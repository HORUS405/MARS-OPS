package marsops.robotics;

import marsops.emergencies.EmergencyType;

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

    @Override
    public boolean canHandle(EmergencyType type) {
        return false; // Scientists do research missions, not emergency responses
    }
}
