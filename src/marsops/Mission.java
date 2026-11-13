package marsops.missions;

import java.util.ArrayList;
import java.util.List;
import marsops.colony.MarsColony;
import marsops.personnel.CrewMember;
import marsops.robotics.Robot;

public abstract class Mission {

    private String missionId;
    private String missionType;
    private MissionState state;
    private int requiredDuration;
    private int elapsedDuration;
    private String failureReason;
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
        this.assignedCrew = new ArrayList<>();
        this.assignedRobots = new ArrayList<>();
    }

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

    public void setState(MissionState state) {
        this.state = state;
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

    public void failMission(String reason) {
        this.state = MissionState.FAILED;
        this.failureReason = reason;
        releaseAgents();
    }

    public List<CrewMember> getAssignedCrew() {
        return new ArrayList<>(assignedCrew);
    }

    public List<Robot> getAssignedRobots() {
        return new ArrayList<>(assignedRobots);
    }

    public void addCrewMember(CrewMember crew) {
        if (crew != null) {
            assignedCrew.add(crew);
        }
    }

    public void addRobot(Robot robot) {
        if (robot != null) {
            assignedRobots.add(robot);
        }
    }

    public void releaseAgents() {
        for (CrewMember crew : assignedCrew) {
            crew.setAvailable(true);
        }
        for (Robot robot : assignedRobots) {
            robot.setAvailable(true);
        }
    }

    public abstract boolean validateAndPerformChecks(MarsColony colony);

    public void advanceTime(MarsColony colony) {
        if (state == MissionState.IN_PROGRESS) {
            elapsedDuration++;
            if (elapsedDuration >= requiredDuration) {
                state = MissionState.COMPLETED;
                releaseAgents();
            }
        }
    }
}

