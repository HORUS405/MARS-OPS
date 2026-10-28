package marsops.robotics;

public abstract class Robot {

    
    private int batteryLevel;
    private String healthStatus;
    private String currentLocation;
    private boolean available;

    //Constructor
    public Robot(int batteryLevel , String healthStatus , String currentLocation)
    {
        setHealthStatus(healthStatus);
        setBattery(batteryLevel);
        setCurrentLocation(currentLocation);
        this.available = true;

    }


    //Getters
    public int getBattery()
    {
        return this.batteryLevel;
    }
    public String getHealthStatus()
    {
        return this.healthStatus;
    }
    public String getCurrentLocation()
    {
        return this.currentLocation;
    }
    public boolean isAvailable()
    {
        return this.available;
    }


    //Setters
    public void setBattery(int batteryLevel)
    {
        if(batteryLevel < 0 || batteryLevel > 100)
            throw new IllegalArgumentException("Invalid BatteryLevel!");
        this.batteryLevel = batteryLevel;
    }

    public void setHealthStatus(String healthStatus)
    {
        if(healthStatus == null || healthStatus.isBlank())
            throw new IllegalArgumentException("Health Status can't be empty!");
        this.healthStatus = healthStatus;
    }
    public void setCurrentLocation(String currentLocation)
    {
        if(currentLocation == null || currentLocation.isBlank())
            throw new IllegalArgumentException("Location can't be empty!");
        this.currentLocation = currentLocation;
    }
    public void  setAvailable(boolean available)
    {
        this.available = available;
    }

    //abstract method that will be overriden 
    public abstract void performTask();
    
}
