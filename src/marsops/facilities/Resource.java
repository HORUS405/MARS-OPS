package marsops.facilities;



public class Resource {
 
    private ResourceType type;
    private int quantity;

    public Resource(ResourceType type , int quantity){
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
    public int getQuantity()
    {
        return this.quantity;
    }

    public void setQuantity(int quantity)
    {
        
        if(quantity < 0)
            throw new IllegalArgumentException("Invalid Quantity!");
        else
            this.quantity = quantity;
    }
    

}
