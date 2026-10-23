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


    }
}
