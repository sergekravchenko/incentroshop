package geb.com.intershop.b2x.pages.storefront.responsive.account

import geb.Page
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.HomePage
import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage
import geb.com.intershop.b2x.pages.storefront.responsive.account.address.SavedAddressesPage
import geb.com.intershop.b2x.pages.storefront.responsive.approval.ApprovedOrdersPage
import geb.com.intershop.b2x.pages.storefront.responsive.approval.OrdersToApprovePage
import geb.com.intershop.b2x.pages.storefront.responsive.approval.RejectedOrdersPage
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.CostCentersPage
import geb.com.intershop.b2x.pages.storefront.responsive.order.OrdersHistoryPage
import geb.com.intershop.b2x.pages.storefront.responsive.approval.PendingOrdersPage
import geb.com.intershop.b2x.pages.storefront.responsive.account.subscriptions.SubscriptionsPage

abstract class AccountPage extends StorefrontPage
{

    abstract String pageId()

    static at =
    {
        waitFor{contentSlot.size()>0}
    }

    static content =
    {
        mainContainer {$('div[role="main"]')}
        contentSlot {mainContainer.$("div", class: "account-wrapper", "data-testing-id": pageId())}
        accountNavBar {module AccountNavigationBar, mainContainer.$("ul", id:"AccountNav")}
        breadcrumb {module Breadcrumb, mainContainer.$("ol", class:"breadcrumbs-list").first()}
        personName {header.myAccountLink.$("span" ,class: "login-name").text()}
    }

    //------------------------------------------------------------
    // link functions
    //------------------------------------------------------------
    def clickProfileSettings()
    {
        accountNavBar.expandProfile()
        accountNavBar.profileSettingsLink.click()
    }

    CostCentersPage clickCostCenters()
    {
        accountNavBar.expandOrg();
        accountNavBar.costCentersLink.click()
        browser.page
    }

    ApprovedOrdersPage clickApprovedOrders()
    {
        accountNavBar.expandApproval();
        accountNavBar.approvedOrdersLink.click()
        browser.page
    }

    RejectedOrdersPage clickRejectedOrders()
    {
        accountNavBar.expandApproval();
        accountNavBar.rejectedOrdersLink.click()
        browser.page
    }

    OrdersToApprovePage clickOrdersToApprove()
    {
        accountNavBar.expandApproval();
        accountNavBar.ordersToApproveLink.click()
        browser.page
    }

    PendingOrdersPage clickPendingOrders()
    {
        accountNavBar.expandPurchase();
        accountNavBar.pendingOrdersLink.click()
        browser.page
    }

    OrdersHistoryPage clickOrdersHistory()
    {
        accountNavBar.expandPurchase()
        accountNavBar.ordersHistoryLink.click()
        browser.page
    }

    def clickPunchout()
    {
        accountNavBar.expandOrg()
        accountNavBar.punchoutLink.click()
        browser.page
    }

	def clickContracts()
    {
        accountNavBar.expandOrg();
        accountNavBar.contractsLink.click();
        browser.page
    }

    def clickUsers()
    {
        accountNavBar.expandOrg();
        accountNavBar.usersLink.click()
        browser.page
    }

    SubscriptionsPage showSubscriptions()
    {
        accountNavBar.expandPurchase()
        accountNavBar.subscriptionsLink.click()
        browser.page
    }

    SavedAddressesPage clickSavedAddresses()
    {
        accountNavBar.expandOrg();
        accountNavBar.changeAddressLink.click()
        browser.page
    }

    HomePage clickLogout()
    {
        accountNavBar.logoutLink.click()
        browser.page
    }

    void verifyLoggedUser(User user)
    {
        assert user.name.equalsIgnoreCase(personName)
    }
}
