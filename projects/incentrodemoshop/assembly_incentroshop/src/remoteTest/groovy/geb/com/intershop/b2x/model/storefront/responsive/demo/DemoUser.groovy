package geb.com.intershop.b2x.model.storefront.responsive.demo;

import geb.com.intershop.b2x.model.storefront.responsive.User

/**
 * ENUM of demo users used in B2B inSpired Showcase
 */
enum DemoUser
{
    BUYER("ggoosen@test.intershop.de", , "Gerhardt", "Goosen"),
    ONLY_RESERVED_FOR_ORDER_SPEC("abeat@test.intershop.de", "Arnim","Teutoburg-Weiss"),
    COST_CENTER_MANAGER("slamar@test.intershop.de", "Semur", "Lamar"),
    ACCOUNT_ADMIN("fbirdo@test.intershop.de", "Fritz", "Birdo"),
    // the ACCOUNT_ADMIN_PUNCHOUT must have a punchout user (via dbinit) by default
    ACCOUNT_ADMIN_PUNCHOUT("bboldner@test.intershop.de", "Bernhard", "Boldner"),
    APPROVER("educking@test.intershop.de", "Emil", "Ducking"),
	OCIUSER("ociuser@test.intershop.de", "", ""),
    ACCOUNT_ADMIN_OIL_CORP("bboldner@test.intershop.de", "Bernhard", "Boldner"),
    COST_CENTER_MANAGER_OIL_CORP("jlink@test.intershop.de", "Jack", "Link"),
    APPROVER_OIL_CORP("pmiller@test.intershop.de", "Patricia", "Miller")
    
    final User user;
 
    private DemoUser(String email, String firstName, String lastName) {
        this.user = new User(email, firstName, lastName);
    }
 
    User getUser() {
        user
    }
}