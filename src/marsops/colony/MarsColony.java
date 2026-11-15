package marsops.colony;

import java.util.ArrayList;

import marsops.facilities.Facility;
import marsops.facilities.Resource;
import marsops.facilities.ResourceType;
import marsops.missions.Mission;
import marsops.personnel.CrewMember;
import marsops.robotics.Robot;

public class MarsColony {
    
    private ArrayList<CrewMember> crewMembers;
    private ArrayList<Facility> facilities;
    private ArrayList<Robot> robots;
    private EnvironmentalCondition environmentalCondition;
    private Mission activeMission;


    public MarsColony() {
        crewMembers = new ArrayList<>();
        facilities = new ArrayList<>();
        robots = new ArrayList<>();
        environmentalCondition = EnvironmentalCondition.NORMAL;
    } 

    public EnvironmentalCondition getEnvironmentalCondition() {
        return environmentalCondition;
    }
    public void setEnvironmentalCondition(EnvironmentalCondition environmentalCondition) {
        if (environmentalCondition == null) {
            throw new IllegalArgumentException("Environmental condition cannot be null!");
        }
        this.environmentalCondition = environmentalCondition;

        if (this.environmentalCondition == EnvironmentalCondition.DUST_STORMS) {
            System.out.println("Environmental Update [DUST_STORMS]: Surface exploration missions cannot begin and active rover missions fail.");
        } else if (this.environmentalCondition == EnvironmentalCondition.COMMUNICATION_DISRUPTION) {
            System.out.println("Environmental Update [COMMUNICATION_DISRUPTION]: Remote drones cannot be controlled and communication-dependent operations are restricted.");
        } else {
            System.out.println("Environmental Update [NORMAL]: Normal colony operations resumed.");
        }

        // The active mission decides whether the new condition makes it fail (e.g. dust storm and rover missions).
        if (activeMission != null) {
            activeMission.onEnvironmentChanged(this);
            clearActiveMissionIfFinished();
        }
    }

    // ---------------------------------------------------------------- missions (owned by the colony)

    public Mission getActiveMission() {
        clearActiveMissionIfFinished();
        return activeMission;
    }

    public boolean hasActiveMission() {
        return getActiveMission() != null;
    }

    /**
     * Only one mission can be active at a time. A rejected mission becomes FAILED immediately
     * with a clear reason (see Mission.getFailureReason) and never becomes active.
     * A mission that was already submitted, started or finished is never retried.
     */
    public boolean submitMission(Mission mission) {
        if (mission == null) {
            throw new IllegalArgumentException("Null Mission!");
        }
        clearActiveMissionIfFinished();
        if (mission.getState() != Mission.MissionState.ASSIGNED) {
            return false;
        }
        if (activeMission != null) {
            mission.failMission("Another mission is already active (" + activeMission.getMissionId() + ").");
            return false;
        }
        if (!mission.validateAndPerformChecks(this)) {
            return false;
        }
        activeMission = mission;
        return true;
    }

    // One simulation time unit for the active mission; the mission is cleared once it completes or fails.
    public void advanceMissionTime() {
        clearActiveMissionIfFinished();
        if (activeMission != null) {
            activeMission.advanceTime(this);
            clearActiveMissionIfFinished();
        }
    }

    private void clearActiveMissionIfFinished() {
        if (activeMission != null && activeMission.isFinished()) {
            activeMission = null;
        }
    }


    public boolean canStartSurfaceExploration(){
        if(environmentalCondition == EnvironmentalCondition.DUST_STORMS) return false;
        else return true;
    }


    public boolean canControlRemoteDrones(){
        if(environmentalCondition == EnvironmentalCondition.COMMUNICATION_DISRUPTION ||
            environmentalCondition == EnvironmentalCondition.DUST_STORMS
        ) return false;
        else return true;
    }


    public boolean isCommunicationAvailable(){
        if(environmentalCondition == EnvironmentalCondition.COMMUNICATION_DISRUPTION) return false;
        else return true;
    }


    public ArrayList<CrewMember> getCrewMembers() {
        return new ArrayList<>(crewMembers);
    }



    public ArrayList<Facility> getFacilities() {
        return new ArrayList<>(facilities);
    }



    public ArrayList<Robot> getRobots() {
        return new ArrayList<>(robots);
    }



    public void addCrewMember(CrewMember crewMember)
    {
        if(crewMember == null)
            throw new IllegalArgumentException("Null Member!");
        else crewMembers.add(crewMember);   
    }
    public void addRobot(Robot robot)
    {
        if(robot == null)
            throw new IllegalArgumentException("Null Robot!");
        else robots.add(robot); 
    }
    public void addFacility(Facility facility)
    {
        if(facility == null)
            throw new IllegalArgumentException("Null Facility!");
        else facilities.add(facility);
    }

    public int getTotalResourceQuantity(ResourceType type) {
        if (type == null) {
            throw new IllegalArgumentException("Invalid resource type!");
        }
        int total = 0;

        for (Facility facility : facilities) {
            if (facility.getFacilityOperationalStatus()
                    .equalsIgnoreCase("Operational")) {

                for (Resource resource : facility.getResources()) {
                    if (resource.getType() == type) {
                        total += resource.getQuantity();
                    }
                }
            }
        }

        return total;
    }

    public boolean hasSufficientResource(ResourceType type, int quantity) {
        if (type == null) {
            throw new IllegalArgumentException("Enter a valid resource type!");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative!");
        }
        return getTotalResourceQuantity(type) >= quantity;
    }

    public void consumeResource(ResourceType type, int quantity) {
        if (type == null) {
            throw new IllegalArgumentException("Enter a valid resource type!");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Enter a valid quantity greater than 0!");
        }

        int amount = getTotalResourceQuantity(type);
        if (quantity > amount) {
            throw new IllegalStateException("Insufficient amount of resource: " + type
                    + " (required: " + quantity + ", available: " + amount + ")");
        }

        int remaining = quantity;
        for (Facility facility : facilities) {
            if (facility.getFacilityOperationalStatus()
                    .equalsIgnoreCase("Operational")) {

                for (Resource resource : facility.getResources()) {
                    if (resource.getType() == type && remaining > 0) {
                        int deducted = Math.min(resource.getQuantity(), remaining);
                        resource.setQuantity(resource.getQuantity() - deducted);
                        remaining -= deducted;
                    }
                }
                if (remaining <= 0) {
                    break;
                }
            }
        }
    }

    public void printResourceReport() {
        System.out.println("=== Colony Resource Status Report ===");
        System.out.println("Environmental Condition: " + environmentalCondition);
        for (ResourceType type : ResourceType.values()) {
            System.out.println(" - " + type + ": " + getTotalResourceQuantity(type));
        }
        System.out.println("=====================================");
    }
}
