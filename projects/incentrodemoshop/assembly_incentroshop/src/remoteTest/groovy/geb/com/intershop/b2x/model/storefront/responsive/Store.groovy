package geb.com.intershop.b2x.model.storefront.responsive

class Store
{
    private static final String LINE_SEPARATOR = "\n"
    
    private String storeID
    
    private Boolean active
    
    private Address storeAddress
    
    Store(String ID, Boolean isActive, Address address)
    {
        this.storeID = ID
        this.active = isActive
        this.storeAddress = address
    }
    
    Address getAddress()
    {
        return this.storeAddress
    }
    
    String getStoreID()
    {
        storeID
    }
    
    String asText()
    {
        StringBuilder buf = new StringBuilder()
        buf.append(storeID)
        buf.append(LINE_SEPARATOR)
        buf.append(active.toString())
        buf.append(LINE_SEPARATOR)
        this.storeAddress.asText()
        buf.toString()
    }
    
    String toString()
    {
        asText()
    }

}
