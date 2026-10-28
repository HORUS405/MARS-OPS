package marsops.facilities;

public class LifeSupportSystem {
    private boolean airFiltrationOperational;

    public LifeSupportSystem() {
        this.airFiltrationOperational = true;
    }

    public boolean isAirFiltrationOperational() {
        return airFiltrationOperational;
    }

    public void setAirFilterationOperational(boolean airFilterationOperational) {
        this.airFiltrationOperational = airFilterationOperational;
    }

    
}
