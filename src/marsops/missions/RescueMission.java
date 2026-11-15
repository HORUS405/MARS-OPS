package marsops.missions;

import java.util.EnumMap;
import java.util.Map;
import marsops.colony.MarsColony;
import marsops.facilities.ResourceType;

public class RescueMission extends Mission {

    private String rescueLocation;
    private int requiredFuel;
    private int requiredMedicalSupplies;

    public RescueMission(String missionId, int requiredDuration, String rescueLocation, int requiredFuel, int requiredMedicalSupplies) {
        super(missionId, Mission.RESCUE, requiredDuration);
        setRescueLocation(rescueLocation);
        setRequiredFuel(requiredFuel);
        setRequiredMedicalSupplies(requiredMedicalSupplies);
    }

    public String getRescueLocation() {
        return rescueLocation;
    }

    public void setRescueLocation(String rescueLocation) {
        if (rescueLocation == null || rescueLocation.isBlank()) {
            throw new IllegalArgumentException("Rescue location cannot be empty!");
        }
        this.rescueLocation = rescueLocation;
    }

    public int getRequiredFuel() {
        return requiredFuel;
    }

    public void setRequiredFuel(int requiredFuel) {
        if (requiredFuel < 0) {
            throw new IllegalArgumentException("Required fuel cannot be negative!");
        }
        this.requiredFuel = requiredFuel;
    }

    public int getRequiredMedicalSupplies() {
        return requiredMedicalSupplies;
    }

    public void setRequiredMedicalSupplies(int requiredMedicalSupplies) {
        if (requiredMedicalSupplies < 0) {
            throw new IllegalArgumentException("Required medical supplies cannot be negative!");
        }
        this.requiredMedicalSupplies = requiredMedicalSupplies;
    }

    // Needs available personnel and an appropriate vehicle; each agent decides through canPerform.
    @Override
    protected int minimumRobots() {
        return 1;
    }

    @Override
    protected String getDestination() {
        return rescueLocation;
    }

    @Override
    protected String checkEnvironment(MarsColony colony) {
        if (!colony.isCommunicationAvailable()) {
            return "Rescue mission cannot be initiated during communication disruption.";
        }
        return null;
    }

    @Override
    protected Map<ResourceType, Integer> getResourceRequirements() {
        Map<ResourceType, Integer> needs = new EnumMap<>(ResourceType.class);
        needs.put(ResourceType.FUEL, requiredFuel);
        needs.put(ResourceType.MEDICAL_SUPPLIES, requiredMedicalSupplies);
        return needs;
    }

    
    @Override
    protected String checkRunningConditions(MarsColony colony) {
        if (!colony.isCommunicationAvailable()) {
            return "Communication lost: the rescue mission cannot continue during communication disruption.";
        }
        return null;
    }
}
