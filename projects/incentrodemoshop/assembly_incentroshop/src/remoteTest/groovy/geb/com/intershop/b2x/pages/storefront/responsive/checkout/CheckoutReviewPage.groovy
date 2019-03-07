package geb.com.intershop.b2x.pages.storefront.responsive.checkout

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage
import geb.com.intershop.b2x.pages.storefront.responsive.modules.OrderInvoiceAddressSlot
import geb.com.intershop.b2x.pages.storefront.responsive.modules.OrderShippingAddressSlot
import geb.module.*

class CheckoutReviewPage extends StorefrontPage
{

    static at=
    {
        waitFor{ contentSlot.displayed }
    }

    static content =
    {
        contentSlot { $("div[data-testing-id='checkout-review-page']") }
        mainForm { $('form', name:'MultipleBucketsForm') }
        agreeCheckBox { $("[id='terms-conditions-agree']").module(Checkbox) }
        submitButton { mainForm.$("button",name:"sendOrder")}
        orderInvoiceAddressSlot { module OrderInvoiceAddressSlot }
        orderShippingAddressSlot { module OrderShippingAddressSlot }

        recurringInformationInfo { $('[data-testing-id="recurring-information-info"]') }
    }

    def submitOrder()
    {
        agreeCheckBox.check()
        submitButton.click()
    }

    def isRecurringInformationInfoDisplayed() {
        return recurringInformationInfo.displayed
    }
}
