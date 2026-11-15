package marsops.missions;

import java.util.EnumMap;
import java.util.Map;
import marsops.colony.MarsColony;
import marsops.facilities.Facility;
import marsops.facilities.ResourceType;

public class RepairMission extends Mission {

    private Facility targetFacility;
    private int requiredSpareParts;
    private int requiredBatteryUnits;

    public RepairMission(String missionId, int requiredDuration, Facility targetFacility, int requiredSpareParts, int requiredBatteryUnits) {
        super(missionId, Mission.REPAIR, requiredDuration);
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

    // Needs an engineer and a repair robot; each agent decides through canPerform.
    @Override
    protected int minimumRobots() {
        return 1;
    }

    @Override
    protected String getDestination() {
        return targetFacility == null ? null : targetFacility.getFacilityLocation();
    }

    @Override
    protected String checkRequirements(MarsColony colony) {
        if (targetFacility == null) {
            return "Repair mission requires a target facility.";
        }
        return null;
    }

    @Override
    protected Map<ResourceType, Integer> getResourceRequirements() {
        Map<ResourceType, Integer> needs = new EnumMap<>(ResourceType.class);
        needs.put(ResourceType.SPARE_PARTS, requiredSpareParts);
        needs.put(ResourceType.BATTERY_POWER, requiredBatteryUnits);
        return needs;
    }

    // Runs exactly once, when the mission completes.
    @Override
    protected void onComplete(MarsColony colony) {
        targetFacility.setFacilityOperationalStatus("Operational");
    }
}
