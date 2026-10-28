package marsops.robotics;

public class Drone extends Robot {

    public Drone(int batteryLevel , String healthStatus , String currentLocation)
    {
        super( batteryLevel ,  healthStatus ,currentLocation);
    }

    @Override 
    public void performTask()
    {
        System.out.println("1-Performs aerial reconnaissance./n2-operates in areas inaccessible to ground vehicles.");
    }
    
}
