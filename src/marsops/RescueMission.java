package marsops.missions;

import marsops.colony.MarsColony;
import marsops.facilities.ResourceType;
import marsops.personnel.CrewMember;
import marsops.robotics.Robot;

public class RescueMission extends Mission {

    private String rescueLocation;
    private int requiredFuel;
    private int requiredMedicalSupplies;

    public RescueMission(String missionId, int requiredDuration, String rescueLocation, int requiredFuel, int requiredMedicalSupplies) {
        super(missionId, "Rescue", requiredDuration);
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

    @Override
    public boolean validateAndPerformChecks(MarsColony colony) {
        if (!colony.isCommunicationAvailable()) {
            failMission("Rescue mission cannot be initiated during communication disruption.");
            return false;
        }

        if (getAssignedCrew().isEmpty() || getAssignedRobots().isEmpty()) {
            failMission("Rescue mission requires available personnel and an appropriate vehicle.");
            return false;
        }

        for (CrewMember crew : getAssignedCrew()) {
            if (!crew.isUsableAndAvailable() || !crew.isAvailable()) {
                failMission("Assigned personnel cannot perform rescue operations or is unavailable.");
                return false;
            }
        }

        for (Robot robot : getAssignedRobots()) {
            if (!robot.isUsableAndAvailable() || !robot.isAvailable()) {
                failMission("Assigned vehicle cannot perform rescue operations or is unavailable.");
                return false;
            }
        }

        if (!colony.hasSufficientResource(ResourceType.FUEL, requiredFuel) ||
            !colony.hasSufficientResource(ResourceType.MEDICAL_SUPPLIES, requiredMedicalSupplies)) {
            failMission("Insufficient fuel or medical supplies for rescue mission.");
            return false;
        }

        if (requiredFuel > 0) {
            colony.consumeResource(ResourceType.FUEL, requiredFuel);
        }
        if (requiredMedicalSupplies > 0) {
            colony.consumeResource(ResourceType.MEDICAL_SUPPLIES, requiredMedicalSupplies);
        }

        for (CrewMember crew : getAssignedCrew()) {
            crew.setAvailable(false);
            crew.setCurrentLocation(rescueLocation);
        }
        for (Robot robot : getAssignedRobots()) {
            robot.setAvailable(false);
            robot.setCurrentLocation(rescueLocation);
        }

        setState(MissionState.IN_PROGRESS);
        return true;
    }
}