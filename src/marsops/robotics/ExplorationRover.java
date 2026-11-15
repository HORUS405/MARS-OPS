package marsops.robotics;

import marsops.emergencies.EmergencyType;
import marsops.missions.Mission;

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

    @Override
    public boolean canHandle(EmergencyType type) {
        return false; // Scientists do research missions, not emergency responses
    }

    @Override
    public boolean canPerform(Mission mission) {
        return mission != null
                && (Mission.EXPLORATION.equals(mission.getMissionType())
                        || Mission.RESCUE.equals(mission.getMissionType()));
    }
}
