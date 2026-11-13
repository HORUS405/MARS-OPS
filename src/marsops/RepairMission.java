package marsops.missions;

import marsops.colony.MarsColony;
import marsops.facilities.Facility;
import marsops.facilities.ResourceType;
import marsops.personnel.CrewMember;
import marsops.robotics.Robot;

public class RepairMission extends Mission {

    private Facility targetFacility;
    private int requiredSpareParts;
    private int requiredBatteryUnits;

    public RepairMission(String missionId, int requiredDuration, Facility targetFacility, int requiredSpareParts, int requiredBatteryUnits) {
        super(missionId, "Repair", requiredDuration);
        this.targetFacility = targetFacility;
        setRequiredSpareParts(requiredSpareParts);
        setRequiredBatteryUnits(requiredBatteryUnits);
    }

    public Facility getTargetFacility() {
        return targetFacility;
    }

    public void setTargetFacility(Facility targetFacility) {
        this.targetFacility = targetFacility;
    }

    public int getRequiredSpareParts() {
        return requiredSpareParts;
    }

    public void setRequiredSpareParts(int requiredSpareParts) {
        if (requiredSpareParts < 0) {
            throw new IllegalArgumentException("Required spare parts cannot be negative!");
        }
        this.requiredSpareParts = requiredSpareParts;
    }

    public int getRequiredBatteryUnits() {
        return requiredBatteryUnits;
    }

    public void setRequiredBatteryUnits(int requiredBatteryUnits) {
        if (requiredBatteryUnits < 0) {
            throw new IllegalArgumentException("Required battery units cannot be negative!");
        }
        this.requiredBatteryUnits = requiredBatteryUnits;
    }

    @Override
    public boolean validateAndPerformChecks(MarsColony colony) {
        if (getAssignedCrew().isEmpty() || getAssignedRobots().isEmpty()) {
            failMission("Repair mission requires an engineer and a repair robot.");
            return false;
        }

        for (CrewMember crew : getAssignedCrew()) {
            if (!crew.isUsableAndAvailable() || !crew.isAvailable()) {
                failMission("Assigned crew member cannot perform repair operations or is unavailable.");
                return false;
            }
        }

        for (Robot robot : getAssignedRobots()) {
            if (!robot.isUsableAndAvailable() || !robot.isAvailable()) {
                failMission("Assigned robot cannot perform repair operations or is unavailable.");
                return false;
            }
        }

        if (!colony.hasSufficientResource(ResourceType.SPARE_PARTS, requiredSpareParts) ||
            !colony.hasSufficientResource(ResourceType.BATTERY_POWER, requiredBatteryUnits)) {
            failMission("Insufficient spare parts or battery power for repair mission.");
            return false;
        }

        if (requiredSpareParts > 0) {
            colony.consumeResource(ResourceType.SPARE_PARTS, requiredSpareParts);
        }
        if (requiredBatteryUnits > 0) {
            colony.consumeResource(ResourceType.BATTERY_POWER, requiredBatteryUnits);
        }

        for (CrewMember crew : getAssignedCrew()) {
            crew.setAvailable(false);
            if (targetFacility != null) {
                crew.setCurrentLocation(targetFacility.getFacilityLocation());
            }
        }
        for (Robot robot : getAssignedRobots()) {
            robot.setAvailable(false);
            if (targetFacility != null) {
                robot.setCurrentLocation(targetFacility.getFacilityLocation());
            }
        }

        setState(MissionState.IN_PROGRESS);
        return true;
    }

    @Override
    public void advanceTime(MarsColony colony) {
        super.advanceTime(colony);
        if (getState() == MissionState.COMPLETED && targetFacility != null) {
            targetFacility.setFacilityOperationalStatus("Operational");
        }
    }
}

