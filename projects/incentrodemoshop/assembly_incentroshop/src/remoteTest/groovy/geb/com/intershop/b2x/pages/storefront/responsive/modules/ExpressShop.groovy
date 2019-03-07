package geb.com.intershop.b2x.pages.storefront.responsive.modules

import geb.Module

class ExpressShop extends Module
{
    static base = { $('div[data-testing-id="section-expressshop-dialog"]') }
    
    static content =
    {
        addToCartButton         (required: false) { $('button', name: "addProduct") }
        addToQuoteButton        (required: false) { $('button[data-testing-class="link-addToQuote"]') }
        addToQuoteLink          (required: false) { $('a[data-testing-class="link-addToQuote"]') }
        priceNotificationButton (required: false) { $('a[data-testing-class="button-addPriceNotification"]') }
        shippingShortInfoField  (required: false) { $('div[data-testing-id="section-productshipping-shortinfo"]') }
        shippingTabField        (required: false) { $('div[data-testing-id="section-shipping-tab"]') }
        returnPolicyTabField    (required: false) { $('div[data-testing-id="section-returnpolicy-tab"]') }
        ownReviewField          (required: false) { $('div[data-testing-id="section-own-review"]') }
        closeButton             (required: false) { $('button[data-testing-id="button-expressshop-close"]') }
    }
}
