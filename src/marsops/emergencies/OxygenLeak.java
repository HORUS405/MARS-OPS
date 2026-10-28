package marsops.emergencies;

import marsops.colony.MarsColony;
import marsops.personnel.CrewMember;
import marsops.personnel.FireFighter;

public class OxygenLeak extends Emergency {
    
    public OxygenLeak(String location, Severity severity){
        super(location, severity);
    }


    @Override 
    public void respond(MarsColony colony){
        boolean found = false;
        for (CrewMember member : colony.getCrewMembers()){
            if( member instanceof FireFighter && member.isAvailable() && member.getHealthStatus().equalsIgnoreCase("Healthy") && member.getFatigueLevel() < 70){
                setEmergencyStatus(Status.IN_PROGRESS);
                FireFighter memFighter = (FireFighter) member;
                memFighter.setAvailable(false);
                memFighter.performDuty();
                found = true;
                break;
            }
        }
        if(found){
            setEmergencyStatus(Status.RESOLVED);
        }else{
            setEmergencyStatus(Status.UN_RESOLVED);
        }

    }


}
