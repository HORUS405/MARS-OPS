package marsops.emergencies;

import java.util.ArrayList;
import marsops.colony.MarsColony;
import marsops.facilities.Equipment;
import marsops.facilities.Facility;
import marsops.facilities.ResourceType;
import marsops.personnel.CrewMember;

public class FireEmergency extends Emergency {

    private static final int FIRE_OXYGEN_LOSS = 5;
    private static final int REQUIRED_WATER = 5;
    private static final int REQUIRED_MEDICAL_SUPPLIES = 1;

    public FireEmergency(String location, Severity severity) {
        super(location, severity);
    }

    @Override
    public void respond(MarsColony colony) {
        if (colony == null) {
            throw new IllegalArgumentException("Colony cannot be null");
        }

        Facility affectedFacility = null;
        for (Facility facility : colony.getFacilities()) {
            if (facility.getFacilityLocation().equalsIgnoreCase(getLocation())
                    && facility.getFacilityOperationalStatus().equalsIgnoreCase("Operational")) {
                affectedFacility = facility;
                break;
            }
        }
        if (affectedFacility == null) {
            setFailureReason("Relevant facility at " + getLocation() + " is not operational or does not exist.");
            setEmergencyStatus(Status.UN_RESOLVED);
            return;
        }

        try {
            if (colony.hasSufficientResource(ResourceType.OXYGEN, FIRE_OXYGEN_LOSS)) {
                colony.consumeResource(ResourceType.OXYGEN, FIRE_OXYGEN_LOSS);
            }
        } catch (IllegalStateException e) {
            System.out.println("Warning: Insufficient oxygen during the fire!");
        }

        affectedFacility.setFacilityOperationalStatus("Unavailable");
        for (Equipment equipment : affectedFacility.getEquipmentList()) {
            equipment.setOperationalStatus("Damaged");
        }

        ArrayList<CrewMember> affectedPersonnel = new ArrayList<>();
        for (CrewMember member : colony.getCrewMembers()) {
            if (member.getCurrentLocation().equalsIgnoreCase(getLocation())
                    && !member.canHandle(EmergencyType.FIRE)
                    && !member.canHandle(EmergencyType.MEDICAL)) {
                member.setHealthStatus("Injured");
                affectedPersonnel.add(member);
            }
        }
        boolean medicalNeeded = !affectedPersonnel.isEmpty();

        CrewMember fireResponder = null;
        for (CrewMember member : colony.getCrewMembers()) {
            if (member.canHandle(EmergencyType.FIRE)) {
                fireResponder = member;
                break;
            }
        }
        if (fireResponder == null) {
            setFailureReason("No available fire responder.");
            setEmergencyStatus(Status.UN_RESOLVED);
            return;
        }

        CrewMember medicalResponder = null;
        if (medicalNeeded) {
            for (CrewMember member : colony.getCrewMembers()) {
                if (member != fireResponder && member.canHandle(EmergencyType.MEDICAL)) {
                    medicalResponder = member;
                    break;
                }
            }
            if (medicalResponder == null) {
                setFailureReason("No available medical responder for personnel injured in the fire.");
                setEmergencyStatus(Status.UN_RESOLVED);
                return;
            }
        }

        if (!colony.hasSufficientResource(ResourceType.WATER, REQUIRED_WATER)) {
            setFailureReason("Insufficient water to extinguish fire.");
            setEmergencyStatus(Status.UN_RESOLVED);
            return;
        }
        if (medicalNeeded && !colony.hasSufficientResource(ResourceType.MEDICAL_SUPPLIES, REQUIRED_MEDICAL_SUPPLIES)) {
            setFailureReason("Insufficient medical supplies for injured personnel.");
            setEmergencyStatus(Status.UN_RESOLVED);
            return;
        }

        setEmergencyStatus(Status.IN_PROGRESS);
        fireResponder.setCurrentLocation(getLocation());
        fireResponder.setAvailable(false);
        if (medicalNeeded && medicalResponder != null) {
            medicalResponder.setCurrentLocation(getLocation());
            medicalResponder.setAvailable(false);
        }

        try {
            colony.consumeResource(ResourceType.WATER, REQUIRED_WATER);
            fireResponder.performDuty();

            if (medicalNeeded && medicalResponder != null) {
                colony.consumeResource(ResourceType.MEDICAL_SUPPLIES, REQUIRED_MEDICAL_SUPPLIES);
                medicalResponder.performDuty();
                for (CrewMember injured : affectedPersonnel) {
                    injured.setHealthStatus("Healthy");
                }
            }

            for (Equipment equipment : affectedFacility.getEquipmentList()) {
                equipment.setOperationalStatus("Operational");
            }
            affectedFacility.setFacilityOperationalStatus("Operational");
            setEmergencyStatus(Status.RESOLVED);
        } finally {
            fireResponder.setAvailable(true);
            if (medicalNeeded && medicalResponder != null) {
                medicalResponder.setAvailable(true);
            }
        }
    }
}
