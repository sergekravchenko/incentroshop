package geb.com.intershop.b2x.pages.storefront.responsive.approval

import geb.Module
import geb.com.intershop.b2x.model.storefront.responsive.Order
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage
import geb.com.intershop.b2x.pages.storefront.responsive.order.modules.OrderRow
import geb.com.intershop.b2x.pages.storefront.responsive.approval.modules.OrderApprovalRow

abstract class OrdersListPage extends AccountPage
{
    Class<? extends OrderRow> orderRowModule = OrderApprovalRow

    static content =
    {
        ordersTable(required: false) {contentSlot.$("table[data-testing-id='orders-list']")}
        ordersList(required: false) {ordersTable.$("tbody tr")*.module(orderRowModule).each({o -> o.calculateIndexes(ordersTable.$("thead tr"))}).collectEntries{[it.orderNumber, it]}}
        subscriptionsTable(required: false) {contentSlot.$("table[data-testing-id='subscriptions-list']")}
        subscriptionsList(required: false) {subscriptionsTable.$("tbody tr")*.module(OrderApprovalRow).each({o -> o.calculateIndexes(subscriptionsTable.$("thead tr"))}).collectEntries{[it.orderNumber, it]}}
    }

    OrdersListPage verifyOrderPresent(Order order)
    {
        assert getList(order).containsKey(order.orderNumber)
        browser.page
    }

    OrdersListPage verifyOrderNotPresent(Order order)
    {
        assert !getList(order).containsKey(order.orderNumber)
        browser.page
    }

    def openOrderDetails(Order order)
    {
        verifyOrderPresent(order)
        OrderRow row = getOrderRow(order)
        row.orderDetailsLink.click()
        browser.page
    }

    private OrderRow getOrderRow(Order order)
    {
        OrderRow row = getList(order)[order.orderNumber]
        assert row.getOrder().hasSameInfo(order)
        row
    }

    private Map getList(Order order) {
        order.isRecurring() ? subscriptionsList: ordersList
    }
}