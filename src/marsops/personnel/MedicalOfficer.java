package marsops.personnel;

import marsops.emergencies.EmergencyType;
import marsops.missions.Mission;

public class MedicalOfficer extends CrewMember {
    
    public MedicalOfficer(String name, String id, String healthStatus, String currentLocation, int fatigueLevel){
       super(name, id, healthStatus, currentLocation, fatigueLevel);
    }

    @Override 
    public void performDuty()
    {
        System.out.println("Responding To medical emergencies.");
    }

    @Override
    public boolean canHandle(EmergencyType type) {
        return isUsableAndAvailable() && (type == EmergencyType.MEDICAL);
    }

    @Override
    public boolean canPerform(Mission mission) {
        return mission != null && Mission.RESCUE.equals(mission.getMissionType());
    }

}
