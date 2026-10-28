package marsops.emergencies;

import marsops.colony.MarsColony;
import marsops.facilities.CommunicationStation;
import marsops.facilities.Facility;
import marsops.facilities.Habitat;
import marsops.facilities.Laboratory;
import marsops.facilities.PowerStation;
import marsops.facilities.ResourceType;
import marsops.facilities.StorageFacility;
import marsops.personnel.CrewMember;
import marsops.personnel.FireFighter;
import marsops.personnel.MedicalOfficer;

public class FireEmergency extends Emergency {
    

    public FireEmergency(String location, Severity severity){
        super(location, severity);
    }


    @Override 
    public void respond(MarsColony colony){
        if(colony == null) throw new  IllegalArgumentException("Colony cannot be null");
        boolean firefighterFound = false;
        boolean medicalOfficerFound = false;
        boolean facilityFound = false;
        boolean medicalNeeded = false;
        setEmergencyStatus(Status.IN_PROGRESS);
        for (CrewMember member : colony.getCrewMembers()){
            if( member instanceof FireFighter && member.isAvailable() && member.getHealthStatus().equalsIgnoreCase("Healthy") && member.getFatigueLevel() < 70){
                FireFighter memFighter = (FireFighter) member;
                memFighter.setAvailable(false);

                memFighter.performDuty();
                firefighterFound = true;
                break;
            }
        }
        for (Facility facility : colony.getFacilities()){
            if( facility instanceof Laboratory ){
                Laboratory lab = (Laboratory) facility;
                if(lab.getFacilityLocation().equalsIgnoreCase(getLocation())){
                    facilityFound = true;
                    try {
                        colony.consumeResource(ResourceType.OXYGEN, 5.0);
                    } catch (IllegalStateException e) {
                        System.out.println("Warning: Insufficient oxygen during the fire!");
                    }
                    lab.setFacilityOperationalStatus("Unavailable");
                }
                break;
            }
            else if( facility instanceof PowerStation ){
                PowerStation lab = (PowerStation) facility;
                if(lab.getFacilityLocation().equalsIgnoreCase(getLocation())){
                    lab.setFacilityOperationalStatus("Unavailable");
                    facilityFound = true;
                }
            }
            else if( facility instanceof StorageFacility ){
                StorageFacility lab = (StorageFacility) facility;
                if(lab.getFacilityLocation().equalsIgnoreCase(getLocation())){
                    lab.setFacilityOperationalStatus("Unavailable");
                    facilityFound = true;
                }
            }
            else if( facility instanceof Habitat ){
                Habitat lab = (Habitat) facility;
                if(lab.getFacilityLocation().equalsIgnoreCase(getLocation())){
                    lab.setFacilityOperationalStatus("Unavailable");
                    facilityFound = true;
                }
            }
            else if( facility instanceof CommunicationStation ){
                CommunicationStation lab = (CommunicationStation) facility;
                if(lab.getFacilityLocation().equalsIgnoreCase(getLocation())){
                    facilityFound = true;
                    try {
                        colony.consumeResource(ResourceType.OXYGEN, 5.0);
                    } catch (IllegalStateException e) {
                        System.out.println("Warning: Insufficient oxygen during the fire!");
                    }
                    lab.setFacilityOperationalStatus("Unavailable");
                }
            }
        }

        for (CrewMember member : colony.getCrewMembers()) {
            if (member.getCurrentLocation().equalsIgnoreCase(getLocation())) {
                medicalNeeded = true;
                break;
            }
        }


        if(medicalNeeded){
            for (CrewMember member : colony.getCrewMembers()){
                if( member instanceof MedicalOfficer && member.isAvailable() && member.getHealthStatus().equalsIgnoreCase("Healthy") && member.getFatigueLevel() < 70){
                    setEmergencyStatus(Status.IN_PROGRESS);
                    MedicalOfficer memMedical = (MedicalOfficer) member;
                    memMedical.setAvailable(false);

                    memMedical.performDuty();
                    medicalOfficerFound = true;
                    break;
                }
            }
        }
        if (facilityFound && firefighterFound && (!medicalNeeded || medicalOfficerFound)) {
            setEmergencyStatus(Status.RESOLVED);
        } else {
            setEmergencyStatus(Status.UN_RESOLVED);
        }

    }
}
