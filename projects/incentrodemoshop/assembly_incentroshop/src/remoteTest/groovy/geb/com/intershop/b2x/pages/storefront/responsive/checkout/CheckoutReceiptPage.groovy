package geb.com.intershop.b2x.pages.storefront.responsive.checkout

import geb.com.intershop.b2x.model.storefront.responsive.Order
import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountHomePage
import geb.com.intershop.b2x.pages.storefront.responsive.modules.OrderInvoiceAddressSlot
import geb.com.intershop.b2x.pages.storefront.responsive.modules.OrderShippingAddressSlot
import geb.com.intershop.b2x.pages.storefront.responsive.modules.RecurrenceInfo
import geb.com.intershop.b2x.pages.storefront.responsive.approval.PendingOrdersPage
import geb.com.intershop.b2x.model.storefront.responsive.RecurrenceInformation

class CheckoutReceiptPage extends StorefrontPage
{
    static at =
    {
        waitFor{ contentSlot.displayed }
    }

    static content =
    {
        contentSlot { $("div[data-testing-id='checkout-receipt-page']") }
        orderNumber {contentSlot.$("strong[data-testing-id='order-document-number']").text()}
        totalPrice {contentSlot.$("div[class='cost-summary'] dd[class='total-price']").text().trim().replaceAll(',','').find(/\d+(\.\d+)?/).toBigDecimal()}
        myAccountLink(required: false, to: AccountHomePage) {contentSlot.$("a", href: endsWith("ViewUserAccount-Start"))}
        costCenter(required: false) {contentSlot.$("span[data-testing-id='order-cost-center']")}
        pendingOrdersLink(required: false, to: PendingOrdersPage) {contentSlot.$("a", href: endsWith("ViewApprovals-MyOpen"))}
        orderInvoiceAddressSlot { module OrderInvoiceAddressSlot }
        orderShippingAddressSlot { module OrderShippingAddressSlot }
        recurrenceInfo(required: false) { $("div[data-testing-id='recurring-information-info']").module(RecurrenceInfo) }
    }

    Order getOrder()
    {
        new Order.OrderBuilder(orderNumber)
                 .total(totalPrice)
                 .defaultOrderDate()
                 .recurrence(getRecurrenceInformation())
                 .build()
    }

    boolean isOrderRequest()
    {
        pendingOrdersLink.size() > 0
    }

    PendingOrdersPage clickPendingOrders()
    {
        pendingOrdersLink.click()
        browser.page
    }

    AccountHomePage clickMyAccount()
    {
        myAccountLink.click()
        browser.page
    }

    private RecurrenceInformation getRecurrenceInformation() {
        recurrenceInfo && recurrenceInfo.displayed ? recurrenceInfo.getRecurrenceInformation() : null
    }

}
