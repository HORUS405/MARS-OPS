package marsops;

import marsops.personnel.CrewMember;
import marsops.personnel.Engineer;
import marsops.personnel.MedicalOfficer;
import marsops.personnel.Scientist;

public class Main {
    public static void main(String[] args) {
        CrewMember S = new Scientist("Ahmed", "S20" , "healthy", "location1" , 10);
        CrewMember E = new Engineer("Mohamed", "E20" , "healthy", "location2" , 50);
        CrewMember M = new MedicalOfficer("Mahmoud", "M20" , "healthy", "location3" , 100);
        S.performDuty();
        E.performDuty();
        M.performDuty();

    }
}
