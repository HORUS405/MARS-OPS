package marsops.emergencies;

import marsops.colony.MarsColony;
import marsops.facilities.Facility;
import marsops.facilities.Habitat;
import marsops.facilities.ResourceType;
import marsops.personnel.CrewMember;
import marsops.robotics.Robot;

public class RoverBreakdown extends Emergency {

    
    private Robot brokenRover;
    private static final int REQUIRED_SPARE_PARTS = 4;
    private static final int REQUIRED_BATTERY_POWER = 7;
    


    public RoverBreakdown(String location, Severity severity, Robot brokenRover){
        super(location, severity);
        if(brokenRover == null){
            throw new IllegalArgumentException("brokeRobot cannot be null");
        }
        this.brokenRover = brokenRover;
    }


    @Override 
    public void respond(MarsColony colony){

        if (colony == null) {
            throw new IllegalArgumentException("Colony cannot be null");
        }

        brokenRover.setHealthStatus("Damaged");
        brokenRover.setAvailable(false);
        brokenRover.setCurrentLocation(getLocation());


        CrewMember humanresponder = null;
        for (CrewMember member : colony.getCrewMembers()) {
            if (member.canHandle(EmergencyType.ROVER_BREAKDOWN)) {
                humanresponder = member;
                break;
            }
        }
        if (humanresponder == null) {
            setFailureReason("No available human responder.");
            setEmergencyStatus(Status.UN_RESOLVED);
            return;
        }


        Robot robotresponder = null;
        for (Robot member : colony.getRobots()) {
            if (member.canHandle(EmergencyType.ROVER_BREAKDOWN)) {
                robotresponder = member;
                break;
            }
        }
        if (robotresponder == null) {
            setFailureReason("No available robot responder.");
            setEmergencyStatus(Status.UN_RESOLVED);
            return;
        }

        if (!colony.hasSufficientResource(ResourceType.SPARE_PARTS, REQUIRED_SPARE_PARTS)
                || !colony.hasSufficientResource(ResourceType.BATTERY_POWER, REQUIRED_BATTERY_POWER)) {
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
            brokenRover.setAvailable(true);
            brokenRover.setHealthStatus("Healthy");
            setEmergencyStatus(Status.RESOLVED);
        } finally {
            humanresponder.setAvailable(true);
            robotresponder.setAvailable(true);
        }

    }


}
