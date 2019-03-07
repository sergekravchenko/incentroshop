package geb.com.intershop.b2x.pages.storefront.responsive.account

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage
import geb.com.intershop.b2x.pages.storefront.responsive.order.OrdersHistoryPage

class AccountHomePage extends AccountPage
{
    static url = StorefrontPage.url + "ViewUserAccount-Start"

    String pageId() {"account-page"}

    static content=
    {
        myOrdersLink(to: OrdersHistoryPage) {$("a", class:"circle-icon", href:endsWith("ViewOrders-MyOrderSearch"))}
        myOrdersCount {myOrdersLink.text().trim().toInteger()}
        myApprovalsSection(required : false) {mainContainer.$("div", class: "section", "data-testing-id": "my-approvals-section")}
        giftCardBalance {$("a",href:contains("ViewGiftCertificatesBalance-Show"),1)}
    }

    OrdersHistoryPage clickMyOrdersLink() {
        myOrdersLink.click()
        browser.page
    }
}

