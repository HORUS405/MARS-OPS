package marsops.personnel;

public class MedicalOfficer extends CrewMember {
    
    public MedicalOfficer(String name, String id, String healthStatus, String currentLocation, int fatigueLevel){
       super(name, id, healthStatus, currentLocation, fatigueLevel);
    }

    @Override 
    public void performDuty()
    {
        System.out.println("Responding To medical emergencies.");
    }

}
