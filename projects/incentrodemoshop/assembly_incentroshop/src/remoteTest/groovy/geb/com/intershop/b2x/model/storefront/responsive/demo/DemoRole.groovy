package geb.com.intershop.b2x.model.storefront.responsive.demo

import geb.com.intershop.b2x.model.storefront.responsive.Role

/**
 * ENUM of demo roles used for testing in B2B inSpired Showcase
 */
enum DemoRole
{
    APP_B2B_BUYER,
    APP_B2B_ACCOUNT_OWNER,
    APP_B2B_COSTCENTER_OWNER,
    APP_B2B_APPROVER,
    APP_B2B_COSTOBJECT_MANAGER,
    APP_B2B_OCI_USER

    final Role role;
 
    private DemoRole() {
        this.role = new Role(this.name);
    }
 
    Role getRole() {
        role
    }
}





