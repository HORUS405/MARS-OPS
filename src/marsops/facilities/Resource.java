package marsops.facilities;



public class Resource {
 
    private ResourceType type;
    private double quantity;

    public Resource(ResourceType type , double quantity){
        if(type == null)
            throw new IllegalArgumentException("Invalid type!");
        else
            this.type = type;
        
        setQuantity(quantity);
    }

    public ResourceType getType()
    {
        return this.type;
    }
    public double getQuantity()
    {
        return this.quantity;
    }

    public void setQuantity(double quantity)
    {
        
        if(quantity < 0.0)
            throw new IllegalArgumentException("Invalid Quantity!");
        else
            this.quantity = quantity;
    }
    

}
