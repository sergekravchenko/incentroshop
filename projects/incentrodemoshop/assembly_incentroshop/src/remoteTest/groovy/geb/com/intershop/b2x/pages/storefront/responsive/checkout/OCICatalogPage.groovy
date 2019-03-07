package geb.com.intershop.b2x.pages.storefront.responsive.checkout

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage
import geb.com.intershop.b2x.pages.storefront.responsive.modules.OCICatalogRow

class OCICatalogPage extends StorefrontPage
{
    static at =
    {
        waitFor{ contentSlot.size()>0}
    }
    
    static content =
    {
        contentSlot {$("[data-testing-id='oci-basket']")}
        formTag { $("form[name='form']") }
        // number 1..n
        ociCatalog  { number -> module(new OCICatalogRow(rowNumber: number)) }
    }
    
    def checkHookUrl(value) {
        return formTag.@action == value
    }
}