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

    public int getTotalResourceQuantity(ResourceType type) {
    int total = 0;

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

    
}
