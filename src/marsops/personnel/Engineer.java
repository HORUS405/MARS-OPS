package marsops.personnel;

import marsops.emergencies.EmergencyType;

public class Engineer extends CrewMember{

    public Engineer(String name, String id, String healthStatus, String currentLocation, int fatigueLevel){
        super(name, id, healthStatus, currentLocation, fatigueLevel);
    }
    
    
    @Override 
    public void performDuty(){
    
        System.out.println("Repairing equipment.");
    }

    @Override
    public boolean canHandle(EmergencyType type) {
        return isUsableAndAvailable()
                && (type == EmergencyType.OXYGEN_LEAK || type == EmergencyType.ROVER_BREAKDOWN);
    }
}
