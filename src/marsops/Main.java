package marsops;

import marsops.personnel.*;
import marsops.robotics.*;


public class Main {
    public static void main(String[] args) {
        CrewMember S = new Scientist("Ahmed", "S20" , "healthy", "location1" , 10);
        CrewMember E = new Engineer("Mohamed", "E20" , "healthy", "location2" , 50);
        CrewMember M = new MedicalOfficer("Mahmoud", "M20" , "healthy", "location3" , 100);
        S.performDuty();
        E.performDuty();
        M.performDuty();

        Robot ER = new ExplorationRover(70, "Healthy", "location1");
        Robot RR = new RepairRobot(100, "Healthy", "location2");
        Robot DR = new Drone(50, "Healthy", "location3");
        ER.performTask();
        RR.performTask();
        DR.performTask();

        MarsColony colony = new MarsColony();

        Facility habitat = new Habitat("location1", 10, "Operational");
        Facility storage = new StorageFacility("location2", 20, "Operational");

        habitat.addResource(new Resource(ResourceType.OXYGEN, 5.5));
        storage.addResource(new Resource(ResourceType.OXYGEN, 4.5));

        colony.addFacility(habitat);
        colony.addFacility(storage);

        System.out.println("Total oxygen: "
                + colony.getTotalResourceQuantity(ResourceType.OXYGEN));

        colony.consumeResource(ResourceType.OXYGEN, 7);

        System.out.println("Oxygen after consumption: "
                + colony.getTotalResourceQuantity(ResourceType.OXYGEN));

        try {
            colony.consumeResource(ResourceType.OXYGEN, 5);
        } catch (IllegalArgumentException ex) {
            System.out.println("Consumption failed: " + ex.getMessage());
        }

        System.out.println("Oxygen after failed consumption: "
                + colony.getTotalResourceQuantity(ResourceType.OXYGEN));
    }
}
