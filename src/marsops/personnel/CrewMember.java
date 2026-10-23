
package marsops.personnel;




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
        this.id = id;
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
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }

    public void setHealthStatus(String healthStatus) {
        if (healthStatus != null && !healthStatus.isBlank()) {
            this.healthStatus = healthStatus;
        }
    }

    public void setCurrentLocation(String currentLocation) {
        if (currentLocation != null && !currentLocation.isBlank()) {
            this.currentLocation = currentLocation;
        }
    }

    public void setFatigueLevel(int fatigueLevel) {
        if (fatigueLevel >= 0 && fatigueLevel <= 100) {
            this.fatigueLevel = fatigueLevel;
        }
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    //--> Abstract method that will be overrided <-- 
    public abstract void performDuty();

}
