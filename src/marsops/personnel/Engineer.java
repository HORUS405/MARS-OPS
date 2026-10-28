package marsops.personnel;

public class Engineer extends CrewMember{

    public Engineer(String name, String id, String healthStatus, String currentLocation, int fatigueLevel){
        super(name, id, healthStatus, currentLocation, fatigueLevel);
    }
    
    
    @Override 
    public void performDuty(){
    
        System.out.println("Repairing equipment.");
    }
}
