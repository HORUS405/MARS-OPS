package marsops.missions;

import marsops.colony.MarsColony;
import marsops.facilities.Equipment;
import marsops.facilities.Facility;
import marsops.facilities.ResourceType;
import marsops.personnel.CrewMember;
import marsops.robotics.Robot;

public class ExplorationMission extends Mission {

    private String targetRegion;
    private int requiredBattery;
    private String requiredEquipmentName;

    public ExplorationMission(String missionId, int requiredDuration, String targetRegion, int requiredBattery, String requiredEquipmentName) {
        super(missionId, "Exploration", requiredDuration);
        setTargetRegion(targetRegion);
        setRequiredBattery(requiredBattery);
        setRequiredEquipmentName(requiredEquipmentName);
    }

    public String getTargetRegion() {
        return targetRegion;
    }

    public void setTargetRegion(String targetRegion) {
        if (targetRegion == null || targetRegion.isBlank()) {
            throw new IllegalArgumentException("Target region cannot be empty!");
        }
        this.targetRegion = targetRegion;
    }

    public int getRequiredBattery() {
        return requiredBattery;
    }

    public void setRequiredBattery(int requiredBattery) {
        if (requiredBattery < 0) {
            throw new IllegalArgumentException("Required battery cannot be negative!");
        }
        this.requiredBattery = requiredBattery;
    }

    public String getRequiredEquipmentName() {
        return requiredEquipmentName;
    }

    public void setRequiredEquipmentName(String requiredEquipmentName) {
        this.requiredEquipmentName = requiredEquipmentName;
    }

    @Override
    public boolean validateAndPerformChecks(MarsColony colony) {
        if (!colony.canStartSurfaceExploration()) {
            failMission("Surface exploration is not allowed during a dust storm.");
            return false;
        }

        if (getAssignedCrew().isEmpty() || getAssignedRobots().isEmpty()) {
            failMission("Exploration mission requires both scientific personnel and an exploration vehicle.");
            return false;
        }

        for (CrewMember crew : getAssignedCrew()) {
            if (!crew.isUsableAndAvailable() || !crew.isAvailable()) {
                failMission("Assigned crew member lacks required capability or is unavailable.");
                return false;
            }
        }

        for (Robot robot : getAssignedRobots()) {
            if (!robot.isUsableAndAvailable() || !robot.isAvailable() || robot.getBattery() < requiredBattery) {
                failMission("Assigned robotic unit is unavailable, incapable, or has insufficient battery.");
                return false;
            }
        }

        if (requiredEquipmentName != null && !requiredEquipmentName.isBlank()) {
            boolean equipmentFound = false;
            for (Facility facility : colony.getFacilities()) {
                if (facility.getFacilityOperationalStatus().equalsIgnoreCase("Operational")) {
                    for (Equipment eq : facility.getEquipmentList()) {
                        if (eq.getEquipmentName().equalsIgnoreCase(requiredEquipmentName) && eq.getOperationalStatus().equalsIgnoreCase("Operational")) {
                            equipmentFound = true;
                            break;
                        }
                    }
                }
                if (equipmentFound) {
                    break;
                }
            }
            if (!equipmentFound) {
                failMission("Required operational equipment (" + requiredEquipmentName + ") is missing.");
                return false;
            }
        }

        if (!colony.hasSufficientResource(ResourceType.BATTERY_POWER, requiredBattery)) {
            failMission("Insufficient battery power resources in the colony.");
            return false;
        }

        colony.consumeResource(ResourceType.BATTERY_POWER, requiredBattery);

        for (CrewMember crew : getAssignedCrew()) {
            crew.setAvailable(false);
            crew.setCurrentLocation("Surface");
        }
        for (Robot robot : getAssignedRobots()) {
            robot.setAvailable(false);
            robot.setCurrentLocation("Surface");
            robot.setBattery(robot.getBattery() - requiredBattery);
        }

        setState(MissionState.IN_PROGRESS);
        return true;
    }
}