package marsops.missions;

import marsops.colony.MarsColony;
import marsops.facilities.Equipment;
import marsops.facilities.Facility;
import marsops.facilities.ResourceType;
import marsops.personnel.CrewMember;

public class ResearchMission extends Mission {

    private Facility targetLaboratory;
    private String requiredEquipmentName;
    private int requiredPower;

    public ResearchMission(String missionId, int requiredDuration, Facility targetLaboratory, String requiredEquipmentName, int requiredPower) {
        super(missionId, "Research", requiredDuration);
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
    public boolean validateAndPerformChecks(MarsColony colony) {
        if (targetLaboratory == null || !targetLaboratory.getFacilityOperationalStatus().equalsIgnoreCase("Operational")) {
            failMission("Target laboratory is missing or not operational.");
            return false;
        }

        if (getAssignedCrew().isEmpty()) {
            failMission("Research mission requires at least one scientist.");
            return false;
        }

        for (CrewMember crew : getAssignedCrew()) {
            if (!crew.isUsableAndAvailable() || !crew.isAvailable()) {
                failMission("Assigned personnel lacks scientific research capability or is unavailable.");
                return false;
            }
        }

        if (requiredEquipmentName != null && !requiredEquipmentName.isBlank()) {
            boolean hasEquipment = false;
            for (Equipment eq : targetLaboratory.getEquipmentList()) {
                if (eq.getEquipmentName().equalsIgnoreCase(requiredEquipmentName) && eq.getOperationalStatus().equalsIgnoreCase("Operational")) {
                    hasEquipment = true;
                    break;
                }
            }
            if (!hasEquipment) {
                failMission("Required scientific equipment (" + requiredEquipmentName + ") is unavailable in the laboratory.");
                return false;
            }
        }

        if (!colony.hasSufficientResource(ResourceType.BATTERY_POWER, requiredPower)) {
            failMission("Insufficient power resources for research mission.");
            return false;
        }

        if (requiredPower > 0) {
            colony.consumeResource(ResourceType.BATTERY_POWER, requiredPower);
        }

        for (CrewMember crew : getAssignedCrew()) {
            crew.setAvailable(false);
            crew.setCurrentLocation(targetLaboratory.getFacilityLocation());
        }

        setState(MissionState.IN_PROGRESS);
        return true;
    }
}
