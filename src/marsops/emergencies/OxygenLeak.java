package marsops.emergencies;

import marsops.colony.MarsColony;
import marsops.facilities.Facility;
import marsops.facilities.Habitat;
import marsops.facilities.ResourceType;
import marsops.personnel.CrewMember;
import marsops.personnel.FireFighter;
import marsops.robotics.Robot;

public class OxygenLeak extends Emergency {
    
    private int oxygenloss;
    private static final int REQUIRED_SPARE_PARTS = 2;
    private static final int REQUIRED_BATTERY_POWER = 5;
    


    public OxygenLeak(String location, Severity severity, int oxygenloss){
        super(location, severity);
        if(oxygenloss <= 0){
            throw new IllegalArgumentException("Oxygen loss must be greater than zero");
        }
        this.oxygenloss = oxygenloss;
    }

    public OxygenLeak(String location, Severity severity) {
        this(location, severity, 10); // Default
    }

    public int getOxygenLoss(){
        return oxygenloss;
    }

    @Override 
    public void respond(MarsColony colony){

        if (colony == null) {
            throw new IllegalArgumentException("Colony cannot be null");
        }


        Habitat affectedHabitat = null;
        for (Facility facility : colony.getFacilities()) {
            if (facility.getFacilityLocation().equalsIgnoreCase(getLocation()) && facility instanceof Habitat) {
                affectedHabitat = (Habitat) facility;
                break;
            }
        }
        if (affectedHabitat == null) {
            setFailureReason("No habitat found at location " + getLocation());
            setEmergencyStatus(Status.UN_RESOLVED);
            return;
        }

        affectedHabitat.consumeOxygen(oxygenloss);

        CrewMember humanresponder = null;
        for (CrewMember member : colony.getCrewMembers()) {
            if (member.canHandle(EmergencyType.OXYGEN_LEAK)) {
                humanresponder = member;
                break;
            }
        }
        if (humanresponder == null) {
            affectedHabitat.consumeOxygen(oxygenloss);
            affectedHabitat.setFacilityOperationalStatus("Damaged");
            setFailureReason("No available human responder.");
            setEmergencyStatus(Status.UN_RESOLVED);
            return;
        }


        Robot robotresponder = null;
        for (Robot member : colony.getRobots()) {
            if (member.canHandle(EmergencyType.OXYGEN_LEAK)) {
                robotresponder = member;
                break;
            }
        }
        if (robotresponder == null) {
            affectedHabitat.consumeOxygen(oxygenloss);
            affectedHabitat.setFacilityOperationalStatus("Damaged");
            setFailureReason("No available robot responder.");
            setEmergencyStatus(Status.UN_RESOLVED);
            return;
        }

        if (!colony.hasSufficientResource(ResourceType.SPARE_PARTS, REQUIRED_SPARE_PARTS)
                || !colony.hasSufficientResource(ResourceType.BATTERY_POWER, REQUIRED_BATTERY_POWER)) {
            affectedHabitat.consumeOxygen(oxygenloss);
            affectedHabitat.setFacilityOperationalStatus("Damaged");
            setFailureReason("Insufficient battery power or spare parts.");
            setEmergencyStatus(Status.UN_RESOLVED);
            return;
        }

        setEmergencyStatus(Status.IN_PROGRESS);
        humanresponder.setCurrentLocation(getLocation());
        humanresponder.setAvailable(false);

        setEmergencyStatus(Status.IN_PROGRESS);
        robotresponder.setCurrentLocation(getLocation());
        robotresponder.setAvailable(false);
        try {
            colony.consumeResource(ResourceType.BATTERY_POWER, REQUIRED_BATTERY_POWER);
            colony.consumeResource(ResourceType.SPARE_PARTS, REQUIRED_SPARE_PARTS);
            humanresponder.performDuty();
            robotresponder.performTask();
            affectedHabitat.setFacilityOperationalStatus("Operational");
            setEmergencyStatus(Status.RESOLVED);
        } finally {
            humanresponder.setAvailable(true);
            robotresponder.setAvailable(true);
        }

    }


}
