package marsops.missions;

import java.util.EnumMap;
import java.util.Map;
import marsops.colony.MarsColony;
import marsops.facilities.Equipment;
import marsops.facilities.Facility;
import marsops.facilities.ResourceType;

public class ResearchMission extends Mission {

    private Facility targetLaboratory;
    private String requiredEquipmentName;
    private int requiredPower;

    public ResearchMission(String missionId, int requiredDuration, Facility targetLaboratory, String requiredEquipmentName, int requiredPower) {
        super(missionId, Mission.RESEARCH, requiredDuration);
        this.targetLaboratory = targetLaboratory;
        this.requiredEquipmentName = requiredEquipmentName;
        setRequiredPower(requiredPower);
    }

    public Facility getTargetLaboratory() {
        return targetLaboratory;
    }

    public void setTargetLaboratory(Facility targetLaboratory) {
        this.targetLaboratory = targetLaboratory;
    }

    public String getRequiredEquipmentName() {
        return requiredEquipmentName;
    }

    public void setRequiredEquipmentName(String requiredEquipmentName) {
        this.requiredEquipmentName = requiredEquipmentName;
    }

    public int getRequiredPower() {
        return requiredPower;
    }

    public void setRequiredPower(int requiredPower) {
        if (requiredPower < 0) {
            throw new IllegalArgumentException("Required power cannot be negative!");
        }
        this.requiredPower = requiredPower;
    }

    @Override
    protected String getDestination() {
        return targetLaboratory == null ? null : targetLaboratory.getFacilityLocation();
    }

    @Override
    protected String checkRequirements(MarsColony colony) {
        if (targetLaboratory == null || !targetLaboratory.getFacilityOperationalStatus().equalsIgnoreCase("Operational")) {
            return "Target laboratory is missing or not operational.";
        }
        if (requiredEquipmentName != null && !requiredEquipmentName.isBlank()) {
            boolean hasEquipment = false;
            for (Equipment eq : targetLaboratory.getEquipmentList()) {
                if (eq.getEquipmentName().equalsIgnoreCase(requiredEquipmentName)
                        && eq.getOperationalStatus().equalsIgnoreCase("Operational")) {
                    hasEquipment = true;
                    break;
                }
            }
            if (!hasEquipment) {
                return "Required scientific equipment (" + requiredEquipmentName + ") is unavailable in the laboratory.";
            }
        }
        return null;
    }

    @Override
    protected Map<ResourceType, Integer> getResourceRequirements() {
        Map<ResourceType, Integer> needs = new EnumMap<>(ResourceType.class);
        needs.put(ResourceType.BATTERY_POWER, requiredPower);
        return needs;
    }
}
