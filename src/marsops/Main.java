package marsops;

import marsops.personnel.CrewMember;
import marsops.personnel.Engineer;
import marsops.personnel.Scientist;

public class Main {
    public static void main(String[] args) {
        Scientist S = new Scientist("Ahmed", "S20" , "healthy", "location1" , 10);
        CrewMember E = new Engineer("Mohamed", "E20" , "healthy", "location2" , 50);

        S.performDuty();
        E.performDuty();
    }
}
