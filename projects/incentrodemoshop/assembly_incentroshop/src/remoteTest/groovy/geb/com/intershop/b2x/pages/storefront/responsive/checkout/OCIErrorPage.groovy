package geb.com.intershop.b2x.pages.storefront.responsive.checkout

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage

class OCIErrorPage extends StorefrontPage
{
    static at =
    {
        waitFor{ contentSlot.size()>0}
    }
    
    def rowNumber

    static content =
    {
        contentSlot {$("[data-testing-id='page-oci-error']")}
        unableToFindMembershipData { $('[data-testing-id="msg-UnableToFindMembershipData"]') }
        missingParameterHookUrl { $('[data-testing-id="msg-MissingParameterHookUrl"]') }
    }
    
}