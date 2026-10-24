package marsops.robotics;

public class RepairRobot extends Robot{

    public RepairRobot(int batteryLevel , String healthStatus , String currentLocation)
    {
        super( batteryLevel ,  healthStatus ,currentLocation);
    }

    @Override 
    public void performTask()
    {
        System.out.println("1-Repairs damaged equipment and operates inside facilities.");
    }
}

