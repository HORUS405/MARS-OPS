package marsops.personnel;

public class FireFighter extends CrewMember {
  public FireFighter(String name, String id, String healthStatus, String currentLocation, int fatigueLevel){
        super(name, id, healthStatus, currentLocation, fatigueLevel);
    }
    
    
    @Override 
    public void performDuty(){
    
        System.out.println("Controlling the Fire...");
    }
}
