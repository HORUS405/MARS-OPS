package marsops.missions;

import java.util.EnumMap;
import java.util.Map;
import marsops.colony.EnvironmentalCondition;
import marsops.colony.MarsColony;
import marsops.facilities.Equipment;
import marsops.facilities.Facility;
import marsops.facilities.ResourceType;
import marsops.robotics.Robot;

public class ExplorationMission extends Mission {

    private String targetRegion;
    private int requiredBattery;
    private String requiredEquipmentName;

    public ExplorationMission(String missionId, int requiredDuration, String targetRegion, int requiredBattery, String requiredEquipmentName) {
        super(missionId, Mission.EXPLORATION, requiredDuration);
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

    // Needs scientific personnel and a suitable vehicle; each agent decides through canPerform.
    @Override
    protected int minimumRobots() {
        return 1;
    }

    // A rover on an exploration mission is on the surface.
    @Override
    protected String getDestination() {
        return "Surface";
    }

    @Override
    protected String checkEnvironment(MarsColony colony) {
        if (!colony.canStartSurfaceExploration()) {
            return "Surface operations are not allowed during a dust storm.";
        }
        return null;
    }

    @Override
    protected String checkRequirements(MarsColony colony) {
        // A rover must keep some battery after the mission has taken its share, so it needs MORE than required.
        for (Robot robot : getAssignedRobots()) {
            if (robot.getBattery() <= requiredBattery) {
                return "Assigned robotic unit has insufficient battery (" + robot.getBattery()
                        + "%, needs more than " + requiredBattery + "%).";
            }
        }
        if (!hasOperationalEquipment(colony)) {
            return "Required operational equipment (" + requiredEquipmentName + ") is missing.";
        }
        return null;
    }

    @Override
    protected Map<ResourceType, Integer> getResourceRequirements() {
        Map<ResourceType, Integer> needs = new EnumMap<>(ResourceType.class);
        needs.put(ResourceType.BATTERY_POWER, requiredBattery);
        return needs;
    }

    @Override
    protected void onStart(MarsColony colony) {
        for (Robot robot : getAssignedRobots()) {
            robot.setBattery(robot.getBattery() - requiredBattery);
        }
    }

    // Failure hooks while the mission runs: environment, battery, equipment.
    @Override
    protected String checkRunningConditions(MarsColony colony) {
        if (colony.getEnvironmentalCondition() == EnvironmentalCondition.DUST_STORMS) {
            return "Dust storm: the active surface exploration mission cannot continue.";
        }
        for (Robot robot : getAssignedRobots()) {
            if (robot.getBattery() <= 0) {
                return "Insufficient battery: an assigned robotic unit is depleted.";
            }
        }
        if (!hasOperationalEquipment(colony)) {
            return "Equipment failure: " + requiredEquipmentName + " is no longer operational.";
        }
        return null;
    }

    private boolean hasOperationalEquipment(MarsColony colony) {
        if (requiredEquipmentName == null || requiredEquipmentName.isBlank()) {
            return true;
        }
        for (Facility facility : colony.getFacilities()) {
            if (facility.getFacilityOperationalStatus().equalsIgnoreCase("Operational")) {
                for (Equipment eq : facility.getEquipmentList()) {
                    if (eq.getEquipmentName().equalsIgnoreCase(requiredEquipmentName)
                            && eq.getOperationalStatus().equalsIgnoreCase("Operational")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
