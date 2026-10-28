package marsops.colony;

import java.util.ArrayList;

import marsops.facilities.Facility;
import marsops.facilities.Resource;
import marsops.facilities.ResourceType;
import marsops.personnel.CrewMember;
import marsops.robotics.Robot;

public class MarsColony {
    
    private ArrayList<CrewMember> crewMembers;
    private ArrayList<Facility> facilities;
    private ArrayList<Robot> robots;
    
    public MarsColony() {
        crewMembers = new ArrayList<>();
        facilities = new ArrayList<>();
        robots = new ArrayList<>();
    } 

    

    public ArrayList<CrewMember> getCrewMembers() {
        return new ArrayList<>(crewMembers);
    }



    public ArrayList<Facility> getFacilities() {
        return new ArrayList<>(facilities);
    }



    public ArrayList<Robot> getRobots() {
        return new ArrayList<>(robots);
    }



    public void addCrewMember(CrewMember crewMember)
    {
        if(crewMember == null)
            throw new IllegalArgumentException("Null Member!");
        else crewMembers.add(crewMember);   
    }
    public void addRobot(Robot robot)
    {
        if(robot == null)
            throw new IllegalArgumentException("Null Robot!");
        else robots.add(robot); 
    }
    public void addFacility(Facility facility)
    {
        if(facility == null)
            throw new IllegalArgumentException("Null Facility!");
        else facilities.add(facility);
    }

    public double getTotalResourceQuantity(ResourceType type) {
    double total = 0;

    for (Facility facility : facilities) {
        if (facility.getFacilityOperationalStatus()
                .equalsIgnoreCase("Operational")) {

            for (Resource resource : facility.getResources()) {
                if (resource.getType() == type) {
                    total += resource.getQuantity();
                }
            }
        }
    }

    return total;
}



    public void consumeResource(ResourceType type, double quantity){
        if(quantity < 0) throw new IllegalArgumentException("Enter a valid quatity greater that 0");
        if(type == null) throw new IllegalArgumentException("Enter a valid resource type");
        double amount = getTotalResourceQuantity(type);
        double remaining = quantity;
        if(quantity > amount) throw new IllegalArgumentException("Insuffcient amount of resource ");

        for (Facility facility : facilities) {
            if (facility.getFacilityOperationalStatus()
                    .equalsIgnoreCase("Operational")) {

                for (Resource resource : facility.getResources()) {
                    if (resource.getType() == type && remaining > 0) {
                        double deduced = Math.min(resource.getQuantity(), remaining);
                        resource.setQuantity(resource.getQuantity()-deduced);
                        remaining -= deduced;
                    }
                }
                if(remaining <= 0) break;
            }
        }
    }


    
}
