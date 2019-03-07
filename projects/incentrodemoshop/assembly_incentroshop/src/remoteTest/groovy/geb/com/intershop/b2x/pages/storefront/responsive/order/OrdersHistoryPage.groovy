package geb.com.intershop.b2x.pages.storefront.responsive.order

import geb.Module
import geb.Page
import geb.com.intershop.b2x.model.storefront.responsive.Order
import geb.com.intershop.b2x.pages.storefront.responsive.approval.OrdersListPage

class OrdersHistoryPage extends OrdersListPage
{
    String pageId() {"orders-history-page"}

    static content =
    {
        searchForm {contentSlot.$("form", name: "SortOrdersByForm")}
        searchField {searchForm.$("input", id: "OrderSearchTerm", type:"text")}
        searchButton(to: OrdersHistoryPage) {searchForm.$("button", type: "submit", name:"search")}
    }

    OrdersHistoryPage searchOrder(String searchTerm)
    {
        searchField.value(searchTerm)
        searchButton.click()
        browser.page
    }

    OrdersHistoryPage searchOrder(Order order)
    {
        searchOrder(order.orderNumber)
    }
}