package geb.com.intershop.b2x.pages.storefront.responsive.order

import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.com.intershop.b2x.model.storefront.responsive.Order
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage
import geb.com.intershop.b2x.pages.storefront.responsive.order.modules.OrderInfo

class OrderDetailsPage extends AccountPage
{
    String pageId() {"order-details-page"}

    static content =
    {
        orderInfo {contentSlot.module(OrderInfo)}
        cancelOrderForm(required: false) {contentSlot.$("form", name:"CancelOrderForm")}
        cancelOrderButton(required: false, to: OrderDetailsPage) {cancelOrderForm.$("button", type: "submit", name:"confirmCancel")}
        confirmOrderCancelForm(required: false) {contentSlot.$("form", name:"ConfirmCancelOrderUp")}
        confirmCancellationButton(required: false, to: OrderDetailsPage) {confirmOrderCancelForm.$("button", type: "submit", name:"cancelOrder")}
    }

    Order getOrder() {
        orderInfo.getOrder()
    }

    void verifyCostCenterPresent(CostCenter costCenter)
    {
        orderInfo.verifyCostCenterPresent(costCenter)
    }

    OrderDetailsPage clickCancelOrder()
    {
        cancelOrderButton.click()
        browser.page
    }

    OrderDetailsPage clickConfirmCancelOrder()
    {
        confirmCancellationButton.click()
        browser.page
    }

    OrderDetailsPage verifyCancelled()
    {
        assert orderInfo.orderStatus.equalsIgnoreCase("canceled")
        browser.page
    }

    OrderDetailsPage verifyNew()
    {
        assert orderInfo.orderStatus.equalsIgnoreCase("new")
        browser.page
    }
}