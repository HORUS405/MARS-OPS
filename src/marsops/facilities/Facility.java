package marsops.facilities;

import java.util.ArrayList;

public abstract class Facility {
    private String facilityLocation;
    private int capacity;
    private String facilityOperationalStatus;
    private ArrayList<Resource> resources;
    private ArrayList<Equipment> equipmentList;
    
    public Facility(String facilityLocation, int capacity, String facilityOperationalStatus) {
        setFacilityLocation(facilityLocation);
        setCapacity(capacity);
        setFacilityOperationalStatus(facilityOperationalStatus);
        this.resources = new ArrayList<>();
        this.equipmentList = new ArrayList<>();
    }

    public String getFacilityLocation() {
        return facilityLocation;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getFacilityOperationalStatus() {
        return facilityOperationalStatus;
    }

    public ArrayList<Resource> getResources() {
    return new ArrayList<>(resources);
    }

    public ArrayList<Equipment> getEquipmentList() {
        return new ArrayList<>(equipmentList);
    }

    public void setFacilityLocation(String facilityLocation) {
        if(facilityLocation == null || facilityLocation.isBlank())
            throw new IllegalArgumentException("Facility Location can't be empty!");
        else this.facilityLocation = facilityLocation;
    }

    public void setCapacity(int capacity) {
       if (capacity <= 0)
            throw new IllegalArgumentException("Invalid Capacity!");
        else this.capacity = capacity;
    }

    public void setFacilityOperationalStatus(String facilityOperationalStatus) {
        if(facilityOperationalStatus == null || facilityOperationalStatus.isBlank())
            throw new IllegalArgumentException("Facility Operational Status can't be empty");
        this.facilityOperationalStatus = facilityOperationalStatus;
    }

    public void addResource(Resource resource) {
        if(resource == null)
            throw new IllegalArgumentException("Rescource Can't be empty!");
        else this.resources.add(resource);
    }

    public void addEquipment(Equipment equipment) {
        if(equipment == null)
            throw new IllegalArgumentException("Equimpent Can't be empty!");
        else this.equipmentList.add(equipment);
    }



}
