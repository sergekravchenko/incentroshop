package geb.com.intershop.b2x.model.storefront.responsive.demo

import geb.com.intershop.b2x.model.storefront.responsive.Address

/**
 * ENUM of demo addresses used in tests for B2B inSpired Showcase
 */
enum DemoAddress
{
    USA_ALABAMA("United States", "Agro Net", DemoUser.BUYER.user.getFirstName(), DemoUser.BUYER.user.getLastName(), "Street Name 333", "New York", "Alabama", "12345", "AL"),
    USA_ALABAMA_SUGGESTION("United States", "Agro Net", DemoUser.BUYER.user.getFirstName(), DemoUser.BUYER.user.getLastName(), "street1", "New York", "Alabama", "12345", "AL"),
    USA_NEW_YORK("United States", "Agro Net", DemoUser.BUYER.user.getFirstName(), DemoUser.BUYER.user.getLastName(), "Street Name 333", "New York", "New York", "12345", "NY"),
    BULGARIA_SOFIA("Bulgaria", "Agro Net Bulgaria", DemoUser.BUYER.user.getFirstName(), DemoUser.BUYER.user.getLastName(), "BG Street Name 333", "Sofia", "Sofia", "1309", "23"),
    BULGARIA_SOFIA_UPDATE("Bulgaria", "Agro Net Bulgaria", DemoUser.BUYER.user.getFirstName(), DemoUser.BUYER.user.getLastName(), "BG Street Name1 233", "Sofia1", "Sofia", "1319", "23"),
    ORDER_PREFERRED_INVOICE("United States", "Agro Net Purchase Department", "Purchaser FirstName", "Purchaser LastName", "Invoice Street", "Area51", "Nevada", "28186", "NV"),
    ORDER_PREFERRED_SHIPPING("United States", "Agro Net Stock Department", "Storeman FirstName", "Storeman LastName", "Shipping Street", "Area51", "Nevada", "28186", "NV"),
 
    final Address address
 
    private DemoAddress(String country, String company, String firstName, String lastName, String addressLine, String city, String state, String postCode, String stateCode) {
        this.address = new Address(country, company, firstName, lastName, addressLine, city, state, postCode, stateCode);
    }
 
    Address getAddress() {
       address
    }
}





