package geb.com.intershop.b2x.pages.storefront.responsive.checkout

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage;
import geb.com.intershop.b2x.pages.storefront.responsive.modules.OrderAddressSummary

class CheckoutShippingPage extends StorefrontPage
{
    static at=
    {
        waitFor{contentSlot.size()>0}
        checkoutShippingMethods.size()>0
    }

    static content =
    {
        contentSlot { $('div[data-testing-id="checkout-shipping-page"]') }
        checkoutShippingMethods {$('div',class:'shipping-methods')}
        continueButton { contentSlot.$('button',name: "continue") }
        orderAddressSummary { module OrderAddressSummary }
    }
    //------------------------------------------------------------
    // link functions
    //------------------------------------------------------------
    def continueClick()
    {
        continueButton.click()
    }


}
