package marsops.robotics;

public class ExplorationRover extends Robot {
    
    public ExplorationRover(int batteryLevel , String healthStatus , String currentLocation)
    {
        super( batteryLevel ,  healthStatus ,currentLocation);
    }

    @Override 
    public void performTask()
    {
        System.out.println("1-Travels across the Martian surface.");
        System.out.println("2-Carries equipment.");
        System.out.println("3-Performs exploration.");
    }
}
