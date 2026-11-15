package marsops.missions;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import marsops.colony.MarsColony;
import marsops.facilities.ResourceType;
import marsops.personnel.CrewMember;
import marsops.robotics.Robot;

public abstract class Mission {

    
    public static final String EXPLORATION = "Exploration";
    public static final String REPAIR = "Repair";
    public static final String RESEARCH = "Research";
    public static final String RESCUE = "Rescue";

    private String missionId;
    private String missionType;
    private MissionState state;
    private int requiredDuration;
    private int elapsedDuration;
    private String failureReason;
    private boolean agentsReserved;
    private List<CrewMember> assignedCrew;
    private List<Robot> assignedRobots;

    public enum MissionState {
        ASSIGNED,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }

    public Mission(String missionId, String missionType, int requiredDuration) {
        setMissionId(missionId);
        setMissionType(missionType);
        setRequiredDuration(requiredDuration);
        this.state = MissionState.ASSIGNED;
        this.elapsedDuration = 0;
        this.failureReason = "";
        this.agentsReserved = false;
        this.assignedCrew = new ArrayList<>();
        this.assignedRobots = new ArrayList<>();
    }

    // ---------------------------------------------------------------- getters / setters

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        if (missionId == null || missionId.isBlank()) {
            throw new IllegalArgumentException("Mission ID cannot be empty!");
        }
        this.missionId = missionId;
    }

    public String getMissionType() {
        return missionType;
    }

    public void setMissionType(String missionType) {
        if (missionType == null || missionType.isBlank()) {
            throw new IllegalArgumentException("Mission type cannot be empty!");
        }
        this.missionType = missionType;
    }

    public MissionState getState() {
        return state;
    }

    public int getRequiredDuration() {
        return requiredDuration;
    }

    public void setRequiredDuration(int requiredDuration) {
        if (requiredDuration <= 0) {
            throw new IllegalArgumentException("Required duration must be greater than zero!");
        }
        this.requiredDuration = requiredDuration;
    }

    public int getElapsedDuration() {
        return elapsedDuration;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public boolean isFinished() {
        return state == MissionState.COMPLETED || state == MissionState.FAILED;
    }

    public List<CrewMember> getAssignedCrew() {
        return new ArrayList<>(assignedCrew);
    }

    public List<Robot> getAssignedRobots() {
        return new ArrayList<>(assignedRobots);
    }

    // Agents can only be attached before the mission is submitted to the colony.
    public void addCrewMember(CrewMember crew) {
        if (state != MissionState.ASSIGNED) {
            throw new IllegalStateException("Agents can only be added before the mission is submitted!");
        }
        if (crew != null && !assignedCrew.contains(crew)) {
            assignedCrew.add(crew);
        }
    }

    public void addRobot(Robot robot) {
        if (state != MissionState.ASSIGNED) {
            throw new IllegalStateException("Agents can only be added before the mission is submitted!");
        }
        if (robot != null && !assignedRobots.contains(robot)) {
            assignedRobots.add(robot);
        }
    }

    // ---------------------------------------------------------------- lifecycle

    /**
     * Validates the mission and, only if every check passes, starts it.
     * Order: environment -> agents (availability + canPerform) -> mission specific
     * requirements -> resources (all-or-nothing). A mission is validated once only:
     * a mission that is already running or finished is never validated (or retried) again.
     */
    public boolean validateAndPerformChecks(MarsColony colony) {
        if (colony == null) {
            throw new IllegalArgumentException("Colony cannot be null!");
        }
        if (state != MissionState.ASSIGNED) {
            return false;
        }

        String reason = checkEnvironment(colony);
        if (reason == null) {
            reason = checkAgents();
        }
        if (reason == null) {
            reason = checkRequirements(colony);
        }
        if (reason == null) {
            reason = checkResources(colony);
        }
        if (reason != null) {
            failMission(reason);
            return false;
        }

        for (Map.Entry<ResourceType, Integer> need : getResourceRequirements().entrySet()) {
            if (need.getValue() > 0) {
                colony.consumeResource(need.getKey(), need.getValue());
            }
        }
        reserveAgents(getDestination());
        onStart(colony);
        this.state = MissionState.IN_PROGRESS;
        return true;
    }

    // Called once per time unit by the colony. The duration only counts while IN_PROGRESS.
    public void advanceTime(MarsColony colony) {
        if (colony == null) {
            throw new IllegalArgumentException("Colony cannot be null!");
        }
        if (state != MissionState.IN_PROGRESS) {
            return;
        }
        String reason = checkRunningConditions(colony);
        if (reason != null) {
            failMission(reason);
            return;
        }
        elapsedDuration++;
        if (elapsedDuration >= requiredDuration) {
            this.state = MissionState.COMPLETED;
            releaseAgents();
            onComplete(colony);
        }
    }

    // Called by the colony when the environment changes while this mission is running.
    public void onEnvironmentChanged(MarsColony colony) {
        if (state != MissionState.IN_PROGRESS) {
            return;
        }
        String reason = checkRunningConditions(colony);
        if (reason != null) {
            failMission(reason);
        }
    }

    // COMPLETED and FAILED are final states: a finished mission can never change again.
    public void failMission(String reason) {
        if (isFinished()) {
            return;
        }
        this.failureReason = (reason == null || reason.isBlank()) ? "Mission failed." : reason;
        this.state = MissionState.FAILED;
        releaseAgents();
    }

    // Releases only agents this mission actually reserved, so a rejected mission
    // never frees agents that belong to another (active) mission.
    public void releaseAgents() {
        if (!agentsReserved) {
            return;
        }
        for (CrewMember crew : assignedCrew) {
            crew.setAvailable(true);
        }
        for (Robot robot : assignedRobots) {
            robot.setAvailable(true);
        }
        agentsReserved = false;
    }

    // ---------------------------------------------------------------- checks

    private String checkAgents() {
        if (assignedCrew.size() < minimumCrew() || assignedRobots.size() < minimumRobots()) {
            return missionType + " mission requires at least " + minimumCrew() + " crew member(s) and "
                    + minimumRobots() + " robotic unit(s).";
        }
        for (CrewMember crew : assignedCrew) {
            if (!crew.isUsableAndAvailable()) {
                return "Crew member " + crew.getName() + " is unavailable or not in usable condition.";
            }
            if (!crew.canPerform(this)) {
                return "Crew member " + crew.getName() + " does not possess the required capability for a "
                        + missionType + " mission.";
            }
        }
        for (Robot robot : assignedRobots) {
            String label = robot.getClass().getSimpleName();
            if (!robot.isUsableAndAvailable()) {
                return "Robotic unit " + label + " is unavailable or not in usable condition.";
            }
            if (!robot.canPerform(this)) {
                return "Robotic unit " + label + " does not possess the required capability for a "
                        + missionType + " mission.";
            }
        }
        return null;
    }

    private String checkResources(MarsColony colony) {
        for (Map.Entry<ResourceType, Integer> need : getResourceRequirements().entrySet()) {
            if (need.getValue() > 0 && !colony.hasSufficientResource(need.getKey(), need.getValue())) {
                return "Insufficient " + need.getKey() + ": required " + need.getValue() + ", available "
                        + colony.getTotalResourceQuantity(need.getKey()) + ".";
            }
        }
        return null;
    }

    private void reserveAgents(String destination) {
        for (CrewMember crew : assignedCrew) {
            crew.setAvailable(false);
            if (destination != null) {
                crew.setCurrentLocation(destination);
            }
        }
        for (Robot robot : assignedRobots) {
            robot.setAvailable(false);
            if (destination != null) {
                robot.setCurrentLocation(destination);
            }
        }
        agentsReserved = true;
    }

    // ---------------------------------------------------------------- hooks for subclasses

    protected int minimumCrew() {
        return 1;
    }

    protected int minimumRobots() {
        return 0;
    }

    // Where the agents are moved to when the mission starts (null = they stay where they are).
    protected String getDestination() {
        return null;
    }

    // Returns a failure reason, or null if the environment allows the mission to start.
    protected String checkEnvironment(MarsColony colony) {
        return null;
    }

    // Mission specific requirements (equipment, target facility...). Failure reason or null.
    protected String checkRequirements(MarsColony colony) {
        return null;
    }

    protected Map<ResourceType, Integer> getResourceRequirements() {
        return new EnumMap<>(ResourceType.class);
    }

    protected void onStart(MarsColony colony) {
    }

    // Environmental / battery / equipment failure hook while the mission runs. Failure reason or null.
    protected String checkRunningConditions(MarsColony colony) {
        return null;
    }

    protected void onComplete(MarsColony colony) {
    }
}
