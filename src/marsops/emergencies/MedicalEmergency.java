package marsops.emergencies;

import marsops.colony.MarsColony;
import marsops.personnel.CrewMember;
import marsops.personnel.MedicalOfficer;

public class MedicalEmergency extends Emergency {
    
    public MedicalEmergency(String location, Severity severity){
        super(location, severity);
    }


    @Override 
    public void respond(MarsColony colony){
        if(colony == null) throw new  IllegalArgumentException("Colony cannot be null");
        boolean medicalOfficerFound = false;
        setEmergencyStatus(Status.IN_PROGRESS);

        for (CrewMember member : colony.getCrewMembers()){
            if( member instanceof MedicalOfficer && member.isAvailable() && member.getHealthStatus().equalsIgnoreCase("Healthy") && member.getFatigueLevel() < 70){
                medicalOfficerFound = true;
                setEmergencyStatus(Status.IN_PROGRESS);
                MedicalOfficer memMedical = (MedicalOfficer) member;
                memMedical.setAvailable(false);
                try {
                    memMedical.performDuty();
                } finally {
                    memMedical.setAvailable(true);
                }

                break;
            }
        }
        if (medicalOfficerFound) {
            setEmergencyStatus(Status.RESOLVED);
        } else {
            setEmergencyStatus(Status.UN_RESOLVED);
        }

    }
}
