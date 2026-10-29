
package marsops.personnel;

import marsops.emergencies.EmergencyType;

public abstract class CrewMember {

    private String name;
    private String id;
    private String healthStatus;
    private String currentLocation;
    private int fatigueLevel;
    private boolean available;

    //constructor
    public CrewMember(String name, String id, String healthStatus, String currentLocation, int fatigueLevel) {
        setName(name);
        setId(id);
        setHealthStatus(healthStatus);
        setCurrentLocation(currentLocation);
        setFatigueLevel(fatigueLevel);
        this.available = true;
    }

    //getters
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public int getFatigueLevel() {
        return fatigueLevel;
    }

    public boolean isAvailable() {
        return available;
    }

    
    //setters
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name Can't be empty!");
        }
        this.name = name;
    }

    public void setHealthStatus(String healthStatus) {
        if (healthStatus == null || healthStatus.isBlank()) {
            throw new IllegalArgumentException("Health Status can't be empty!");
        }
        this.healthStatus = healthStatus;
    }

    public void setCurrentLocation(String currentLocation) {
        if (currentLocation == null || currentLocation.isBlank()) {
            throw new IllegalArgumentException("Location Can't be empty!");
        }
        this.currentLocation = currentLocation;
    }

    public void setFatigueLevel(int fatigueLevel) {
        if (fatigueLevel < 0 || fatigueLevel > 100) {
            throw new IllegalArgumentException("Fatigue Level should be between 0 and 100!");
        }
        this.fatigueLevel = fatigueLevel;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setId(String id) {
        if(id == null || id.isBlank()){
            throw new IllegalArgumentException("Id caan't be empty!");
        }
            this.id = id;
    }
    
    


    //--> Abstract method that will be overridden <-- 
    public abstract void performDuty();

    public boolean isUsableAndAvailable() {
        return isAvailable() && getHealthStatus().equalsIgnoreCase("Healthy") && getFatigueLevel() < 70;
    }

    public abstract boolean canHandle(EmergencyType type);

}
