package marsops.personnel;

import marsops.emergencies.EmergencyType;
import marsops.missions.Mission;

public class FireFighter extends CrewMember {
  public FireFighter(String name, String id, String healthStatus, String currentLocation, int fatigueLevel){
        super(name, id, healthStatus, currentLocation, fatigueLevel);
    }
    
    
    @Override 
    public void performDuty(){
    
        System.out.println("Controlling the Fire...");
    }
    
    @Override
    public boolean canHandle(EmergencyType type) {
        return isUsableAndAvailable()
                && (type == EmergencyType.FIRE);
    }

    @Override
    public boolean canPerform(Mission mission) {
        return mission != null && Mission.RESCUE.equals(mission.getMissionType());
    }
}
