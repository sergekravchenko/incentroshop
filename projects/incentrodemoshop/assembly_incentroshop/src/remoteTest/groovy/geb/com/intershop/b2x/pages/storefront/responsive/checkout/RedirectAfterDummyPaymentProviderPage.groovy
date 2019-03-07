package geb.com.intershop.b2x.pages.storefront.responsive.checkout

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage;

class RedirectAfterDummyPaymentProviderPage extends StorefrontPage
{
    static at=
    {
        waitFor{contentSlot.size()>0}
    }
    
    static content =
    {
        contentSlot         { $('form[id="OnlinePayAccountForm"]') }
        applyPaymentButton      { contentSlot.$('button',name:"ok") }
        contentSlotPin      { $('form[id="OnlinePayPINForm"]')}
        applyPaymentButtonPin   { contentSlotPin.$('button',name:"ok") }
        contentSlotSubmit      { $('form[name="Form"]')}
        applyPaymentButtonSubmit      { contentSlotSubmit.$('button',name:"back") }
    }
    
    
    def submitProviderRequest()
    {
        applyPaymentButton.click()
        applyPaymentButtonPin.click()
        applyPaymentButtonSubmit.click()
    }
}
