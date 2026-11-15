package marsops.robotics;

import marsops.emergencies.EmergencyType;
import marsops.missions.Mission;

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

    @Override
    public boolean canHandle(EmergencyType type) {
        return isUsableAndAvailable()
                && (type == EmergencyType.OXYGEN_LEAK || type == EmergencyType.ROVER_BREAKDOWN);
    }

    @Override
    public boolean canPerform(Mission mission) {
        return mission != null && Mission.REPAIR.equals(mission.getMissionType());
    }
}

