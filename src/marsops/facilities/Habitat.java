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
    


    public void consumeOxygen(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Enter a valid quantity greater than 0!");
        }

        int remaining = quantity;
        for (Resource resource : getResources()) {
            if (resource.getType() == ResourceType.OXYGEN && remaining > 0) {
                int deducted = Math.min(resource.getQuantity(), remaining);
                resource.setQuantity(resource.getQuantity() - deducted);
                remaining -= deducted;
            }
            if (remaining <= 0) {
                break;
            }
        }
    }

    
}
