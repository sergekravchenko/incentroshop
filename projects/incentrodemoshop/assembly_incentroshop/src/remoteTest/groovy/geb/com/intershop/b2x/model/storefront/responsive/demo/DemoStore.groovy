package geb.com.intershop.b2x.model.storefront.responsive.demo

import geb.com.intershop.b2x.model.storefront.responsive.Address
import geb.com.intershop.b2x.model.storefront.responsive.Store

/**
 * ENUM of demo stores used in tests for B2B inSpired Showcase
 */
enum DemoStore
{
    FACTORY_OUTLET_B5("Factory Outlet B5", "Alter Spandauer Weg 1", "14641", "Wustermark", "Germany"),
    INSPIRED_ESPAGNOL("inSPIRED Espagnol", "Santa Juliana 13S", "28039", "Madrid", "Spain"),
    INSPIRED_ITALIA("inSPIRED Italia", "Via Del Pigneto 050", "00176", "Roma", "Italy"),
    INSPIRED_JENA_PARADIES("inSPIRED Jena-Paradies", "Burgauer Weg 1", "07745", "Jena", "Germany"),
    INSPIRED_RETAIL_CENTER1("inSPIRED Retail Center1", "Cricklewood Lane 221b", "14482", "London", "United Kingdom"),
    INSPIRED_SUPERSTORE("inSPIRED Superstore", "Gneisenaustraße 8", "04105", "Leipzig", "Germany"),
    INSPIRED_VILLAGE("inSPIRED Village", "Potsdamer Platz 7", "23823", "Seedorf", "Germany"),
    INSPIREDOUTLET_FRANCE("inSPIREDOutlet France", "Rue Rivoli 12", "75001", "Paris", "France"),
    OCEANSIDE_PARADIES_STORE("Oceanside Paradies Store", "Wesenberger Chaussee 22", "17252", "Mirow", "Germany"),
    STORE_BERLIN("Store Berlin", "Marlene-Dietrich-Allee 44", "14482", "Potsdam", "Germany"),
 
    final Store store
    final Address address
 
    private DemoStore(String storeID, String addressLine1, String postalCode, String city, String country) {
        
        this.address = new Address(country, null, null, null, addressLine1, city, null, postalCode, null);
        this.store = new Store(storeID, true, address);
    }
 
    Address getAddress() {
       store.getAddress()
    }
    
    Store getStore() {
        store
     }
}