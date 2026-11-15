package marsops.personnel;

import marsops.emergencies.EmergencyType;
import marsops.missions.Mission;

public class Scientist extends CrewMember {
    public Scientist(String name, String id, String healthStatus, String currentLocation, int fatigueLevel){
       super(name, id, healthStatus, currentLocation, fatigueLevel);
    
    }

    @Override 
    public void performDuty(){

        System.out.println("Conducting research.");
    }

    @Override
    public boolean canHandle(EmergencyType type) {
        return false; // Scientists do research missions, not emergency responses
    }

    @Override
    public boolean canPerform(Mission mission) {
        return mission != null
                && (Mission.EXPLORATION.equals(mission.getMissionType())
                        || Mission.RESEARCH.equals(mission.getMissionType()));
    }

}
