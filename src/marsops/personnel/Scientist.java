package marsops.personnel;

public class Scientist extends CrewMember {
    public Scientist(String name, String id, String healthStatus, String currentLocation, int fatigueLevel){
       super(name, id, healthStatus, currentLocation, fatigueLevel);
    
    }

    @Override 
    public void performDuty(){

        System.out.println("Conducting research.");
    }


}
