package marsops.emergencies;

import marsops.colony.MarsColony;
import marsops.facilities.Facility;
import marsops.facilities.ResourceType;
import marsops.personnel.CrewMember;
import marsops.personnel.MedicalOfficer;

public class MedicalEmergency extends Emergency {

    private CrewMember patient;
    private static final int REQUIRED_MEDICAL_SUPPLIES = 1 ;
    private static final int REQUIRED_OXYGEN = 1 ;

    public MedicalEmergency(String location, Severity severity, CrewMember patient){
        super(location, severity);
        if(patient == null) throw new IllegalArgumentException("Patient cannot be null");
        this.patient = patient;
    }


    public CrewMember getPatient() {
        return patient;
    }
    @Override
    public void respond(MarsColony colony) {
        if (colony == null) {
            throw new IllegalArgumentException("Colony cannot be null");
        }

        boolean facilityOperational = false;
        for (Facility facility : colony.getFacilities()) {
            if (facility.getFacilityLocation().equalsIgnoreCase(getLocation())
                    && facility.getFacilityOperationalStatus().equalsIgnoreCase("Operational")) {
                facilityOperational = true;
                break;
            }
        }
        if (!facilityOperational) {
            setFailureReason("Relevant facility at " + getLocation() + " is not operational.");
            setEmergencyStatus(Status.UN_RESOLVED);
            return;
        }

        CrewMember responder = null;
        for (CrewMember member : colony.getCrewMembers()) {
            if (member != patient && member.canHandle(EmergencyType.MEDICAL)) {
                responder = member;
                break;
            }
        }
        if (responder == null) {
            setFailureReason("No available medical responder.");
            setEmergencyStatus(Status.UN_RESOLVED);
            return;
        }

        if (!colony.hasSufficientResource(ResourceType.MEDICAL_SUPPLIES, REQUIRED_MEDICAL_SUPPLIES)
                || !colony.hasSufficientResource(ResourceType.OXYGEN, REQUIRED_OXYGEN)) {
            setFailureReason("Insufficient medical supplies or oxygen.");
            setEmergencyStatus(Status.UN_RESOLVED);
            return;
        }

        setEmergencyStatus(Status.IN_PROGRESS);
        responder.setCurrentLocation(getLocation());
        responder.setAvailable(false);
        try {
            colony.consumeResource(ResourceType.MEDICAL_SUPPLIES, REQUIRED_MEDICAL_SUPPLIES);
            colony.consumeResource(ResourceType.OXYGEN, REQUIRED_OXYGEN);
            responder.performDuty();
            patient.setHealthStatus("Healthy");
            setEmergencyStatus(Status.RESOLVED);
        } finally {
            responder.setAvailable(true);
        }
    }
}
