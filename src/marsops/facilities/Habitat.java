package marsops.facilities;

public class Habitat extends Facility {
    private final LifeSupportSystem lifeSupport;

    public Habitat(String facilityLocation, int capacity, String facilityOperationalStatus){
        super(facilityLocation, capacity, facilityOperationalStatus);
        this.lifeSupport = new LifeSupportSystem();
    }
    
    public boolean isAirFiltrationOperational() {
    return lifeSupport.isAirFiltrationOperational();
    }
    

    
}
