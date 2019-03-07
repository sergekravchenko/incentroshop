package geb.com.intershop.b2x.model.storefront.responsive.demo

import geb.com.intershop.b2x.model.storefront.responsive.CostCenter

/**
 * ENUM of demo cost centers used in B2B inSpired Showcase
 */
enum DemoCostCenter
{
    AGRO_NET_CENTRAL("511288", "Agro Net Central"),
    AGRO_NET_NORTH("511388", "Agro Net North"),
    AGRO_NET_SOUTH("511488", "Agro Net South"),
    AGRO_NET_NEW("111111", "Agro Net NEW COST CENTER")
 
    final CostCenter costCenter
 
    private DemoCostCenter(String id, String name)
    {
        this.costCenter = new CostCenter(id, name)
    }
 
    CostCenter getCostCenter()
    {
        costCenter
    }
}





