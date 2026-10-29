package marsops;

import marsops.colony.EnvironmentalCondition;
import marsops.colony.MarsColony;
import marsops.emergencies.FireEmergency;
import marsops.emergencies.MedicalEmergency;
import marsops.emergencies.OxygenLeak;
import marsops.emergencies.RoverBreakdown;
import marsops.emergencies.Severity;
import marsops.facilities.Equipment;
import marsops.facilities.Facility;
import marsops.facilities.Habitat;
import marsops.facilities.Laboratory;
import marsops.facilities.Resource;
import marsops.facilities.ResourceType;
import marsops.facilities.StorageFacility;
import marsops.personnel.CrewMember;
import marsops.personnel.Engineer;
import marsops.personnel.FireFighter;
import marsops.personnel.MedicalOfficer;
import marsops.personnel.Scientist;
import marsops.robotics.Drone;
import marsops.robotics.ExplorationRover;
import marsops.robotics.RepairRobot;
import marsops.robotics.Robot;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("1. INITIALIZING COLONY PERSONNEL, ROBOTS & FACILITIES");
        System.out.println("==================================================");

        CrewMember scientist = new Scientist("Ahmed", "S20", "Healthy", "Lab-1", 10);
        CrewMember engineer = new Engineer("Mohamed", "E20", "Healthy", "Habitat-A", 30);
        CrewMember medic = new MedicalOfficer("Mahmoud", "M20", "Healthy", "Habitat-A", 20);
        CrewMember firefighter = new FireFighter("Omar", "F20", "Healthy", "Storage-1", 15);

        Robot rover = new ExplorationRover(80, "Healthy", "Surface");
        Robot repairBot = new RepairRobot(90, "Healthy", "Habitat-A");
        Robot drone = new Drone(75, "Healthy", "Storage-1");

        MarsColony colony = new MarsColony();
        colony.addCrewMember(scientist);
        colony.addCrewMember(engineer);
        colony.addCrewMember(medic);
        colony.addCrewMember(firefighter);

        colony.addRobot(rover);
        colony.addRobot(repairBot);
        colony.addRobot(drone);

        Habitat habitatA = new Habitat("Habitat-A", 10, "Operational");
        StorageFacility storage = new StorageFacility("Storage-1", 20, "Operational");
        Laboratory lab1 = new Laboratory("Lab-1", 8, "Operational");
        lab1.addEquipment(new Equipment("Spectrometer", "Operational"));

        // Add resources across multiple facilities
        habitatA.addResource(new Resource(ResourceType.OXYGEN, 30));
        storage.addResource(new Resource(ResourceType.OXYGEN, 20));
        storage.addResource(new Resource(ResourceType.WATER, 20));
        storage.addResource(new Resource(ResourceType.FOOD, 25));
        storage.addResource(new Resource(ResourceType.FUEL, 15));
        storage.addResource(new Resource(ResourceType.BATTERY_POWER, 20));
        storage.addResource(new Resource(ResourceType.MEDICAL_SUPPLIES, 5));
        storage.addResource(new Resource(ResourceType.SPARE_PARTS, 10));

        colony.addFacility(habitatA);
        colony.addFacility(storage);
        colony.addFacility(lab1);

        colony.printResourceReport();

        System.out.println("\n==================================================");
        System.out.println("2. TESTING RESOURCE MANAGEMENT (MULTI-SOURCE & ALL-OR-NOTHING)");
        System.out.println("==================================================");
        System.out.println("Initial Colony Oxygen (30 in Habitat-A + 20 in Storage-1): "
                + colony.getTotalResourceQuantity(ResourceType.OXYGEN));

        // Consume 35 Oxygen (draws 30 from Habitat-A and 5 from Storage-1)
        colony.consumeResource(ResourceType.OXYGEN, 35);
        System.out.println("Oxygen after consuming 35 units across facilities: "
                + colony.getTotalResourceQuantity(ResourceType.OXYGEN));

        // Attempt to consume 25 Oxygen when only 15 remain (All-or-Nothing rejection)
        try {
            colony.consumeResource(ResourceType.OXYGEN, 25);
        } catch (IllegalStateException ex) {
            System.out.println("All-or-Nothing Check Passed -> Rejected: " + ex.getMessage());
        }
        System.out.println("Oxygen after rejected consumption (unchanged): "
                + colony.getTotalResourceQuantity(ResourceType.OXYGEN));

        // Replenish Habitat-A oxygen for emergency tests
        habitatA.addResource(new Resource(ResourceType.OXYGEN, 40));

        System.out.println("\n==================================================");
        System.out.println("3. TESTING EMERGENCY 1: OXYGEN LEAK (RESOLVED & UNRESOLVED)");
        System.out.println("==================================================");
        System.out.println("Oxygen before resolved leak: " + colony.getTotalResourceQuantity(ResourceType.OXYGEN));
        OxygenLeak leakResolved = new OxygenLeak("Habitat-A", Severity.HIGH, 10);
        leakResolved.respond(colony);
        System.out.println("OxygenLeak Status: " + leakResolved.getStatus()
                + " | Colony Oxygen (1x loss = -10): " + colony.getTotalResourceQuantity(ResourceType.OXYGEN));

        // Make repair robot unavailable to test UN_RESOLVED oxygen leak (2x oxygen loss, 0 spare parts used)
        repairBot.setAvailable(false);
        int sparePartsBefore = colony.getTotalResourceQuantity(ResourceType.SPARE_PARTS);
        OxygenLeak leakUnresolved = new OxygenLeak("Habitat-A", Severity.CRITICAL, 10);
        leakUnresolved.respond(colony);
        System.out.println("OxygenLeak (No Robot) Status: " + leakUnresolved.getStatus()
                + " | Reason: " + leakUnresolved.getFailureReason());
        System.out.println("Spare parts unchanged: " + (sparePartsBefore == colony.getTotalResourceQuantity(ResourceType.SPARE_PARTS)));
        // Restore habitat & robot for subsequent tests
        habitatA.setFacilityOperationalStatus("Operational");
        repairBot.setAvailable(true);

        System.out.println("\n==================================================");
        System.out.println("4. TESTING EMERGENCY 2: MEDICAL EMERGENCY (RESOLVED & UNRESOLVED)");
        System.out.println("==================================================");
        scientist.setHealthStatus("Injured");
        System.out.println("Patient health before treatment: " + scientist.getHealthStatus());
        MedicalEmergency medResolved = new MedicalEmergency("Lab-1", Severity.HIGH, scientist);
        medResolved.respond(colony);
        System.out.println("MedicalEmergency Status: " + medResolved.getStatus()
                + " | Patient health after treatment: " + scientist.getHealthStatus());

        // Test unresolved when MedicalOfficer is unavailable
        scientist.setHealthStatus("Injured");
        medic.setAvailable(false);
        MedicalEmergency medUnresolved = new MedicalEmergency("Lab-1", Severity.HIGH, scientist);
        medUnresolved.respond(colony);
        System.out.println("MedicalEmergency (No Medic) Status: " + medUnresolved.getStatus()
                + " | Reason: " + medUnresolved.getFailureReason()
                + " | Patient health still: " + scientist.getHealthStatus());
        medic.setAvailable(true);
        scientist.setHealthStatus("Healthy");

        System.out.println("\n==================================================");
        System.out.println("5. TESTING EMERGENCY 3: FIRE EMERGENCY (RESOLVED & UNRESOLVED)");
        System.out.println("==================================================");
        scientist.setCurrentLocation("Lab-1");
        FireEmergency fireResolved = new FireEmergency("Lab-1", Severity.CRITICAL);
        fireResolved.respond(colony);
        System.out.println("FireEmergency Status: " + fireResolved.getStatus()
                + " | Lab-1 Status: " + lab1.getFacilityOperationalStatus()
                + " | Scientist Health: " + scientist.getHealthStatus());

        // Test unresolved fire when firefighter is unavailable
        firefighter.setAvailable(false);
        FireEmergency fireUnresolved = new FireEmergency("Lab-1", Severity.CRITICAL);
        fireUnresolved.respond(colony);
        System.out.println("FireEmergency (No FireFighter) Status: " + fireUnresolved.getStatus()
                + " | Reason: " + fireUnresolved.getFailureReason()
                + " | Lab-1 Status: " + lab1.getFacilityOperationalStatus());
        firefighter.setAvailable(true);
        lab1.setFacilityOperationalStatus("Operational");

        System.out.println("\n==================================================");
        System.out.println("6. TESTING EMERGENCY 4: ROVER BREAKDOWN (RESOLVED & UNRESOLVED)");
        System.out.println("==================================================");
        RoverBreakdown breakdownResolved = new RoverBreakdown("Surface", Severity.MEDIUM, rover);
        breakdownResolved.respond(colony);
        System.out.println("RoverBreakdown Status: " + breakdownResolved.getStatus()
                + " | Rover Health: " + rover.getHealthStatus()
                + " | Rover Available: " + rover.isAvailable());

        // Test unresolved breakdown when engineer is unavailable
        engineer.setAvailable(false);
        RoverBreakdown breakdownUnresolved = new RoverBreakdown("Surface", Severity.HIGH, rover);
        breakdownUnresolved.respond(colony);
        System.out.println("RoverBreakdown (No Engineer) Status: " + breakdownUnresolved.getStatus()
                + " | Reason: " + breakdownUnresolved.getFailureReason()
                + " | Rover Health: " + rover.getHealthStatus()
                + " | Rover Available: " + rover.isAvailable());
        engineer.setAvailable(true);

        System.out.println("\n==================================================");
        System.out.println("7. TESTING ENVIRONMENTAL EFFECTS");
        System.out.println("==================================================");
        System.out.println("Condition: " + colony.getEnvironmentalCondition()
                + " | Can Start Exploration: " + colony.canStartSurfaceExploration()
                + " | Can Control Drones: " + colony.canControlRemoteDrones());

        colony.setEnvironmentalCondition(EnvironmentalCondition.DUST_STORMS);
        if (!colony.canStartSurfaceExploration()) {
            System.out.println("Exploration Mission cannot start.");
            System.out.println("Reason: Surface operations are not allowed during a dust storm.");
        }

        colony.setEnvironmentalCondition(EnvironmentalCondition.COMMUNICATION_DISRUPTION);
        System.out.println("Condition: " + colony.getEnvironmentalCondition()
                + " | Can Control Remote Drones: " + colony.canControlRemoteDrones()
                + " | Communication Available: " + colony.isCommunicationAvailable());

        colony.setEnvironmentalCondition(EnvironmentalCondition.NORMAL);
        System.out.println();
        colony.printResourceReport();
    }
}
