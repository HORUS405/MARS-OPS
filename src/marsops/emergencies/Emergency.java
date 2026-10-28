package marsops.emergencies;
import java.util.UUID;

import marsops.colony.MarsColony;

public abstract class Emergency {

    private String EmergencyID;
    private String location;
    private Severity severity;
    private Status status;
    
    
    public Emergency(String location, Severity severity){
        UUID uuid = UUID.randomUUID();    
        String uuidString = uuid.toString();
        this.EmergencyID = uuidString;
        this.location = location;
        this.severity = severity;
        this.status = status.REPORTED;

    }


    public String getEmergencyID() {
        return EmergencyID;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Status getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEmergencyStatus(Status status) {
        this.status = status;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public abstract void respond(MarsColony colony);

}
