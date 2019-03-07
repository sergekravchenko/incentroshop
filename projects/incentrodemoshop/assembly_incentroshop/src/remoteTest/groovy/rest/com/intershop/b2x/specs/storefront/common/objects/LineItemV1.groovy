package rest.com.intershop.b2x.specs.storefront.common.objects

import spock.lang.*

class LineItemV1
{
    def id
    def sku
    def quantityValue
    private String shippingMethod
    private String shipToAddress

    public LineItemV1(def sku, def quantityValue)
    {
        this.sku = sku
        this.quantityValue = quantityValue
    }

    public String getID()
    {
        return id
    }

    public setID(String id)
    {
        this.id = id
    }
    
    public String getShippingMethod()
    {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod)
    {
        this.shippingMethod = shippingMethod;
    }

    public String getShipToAddress()
    {
        return shipToAddress;
    }

    public void setShipToAddress(String shipToAddress)
    {
        this.shipToAddress = shipToAddress;
    }
}