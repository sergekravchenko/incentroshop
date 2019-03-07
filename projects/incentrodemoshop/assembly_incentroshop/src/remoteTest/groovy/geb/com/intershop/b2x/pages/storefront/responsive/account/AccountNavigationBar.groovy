package geb.com.intershop.b2x.pages.storefront.responsive.account

import geb.Module
import geb.com.intershop.b2x.pages.storefront.responsive.HomePage
import geb.com.intershop.b2x.pages.storefront.responsive.account.address.SavedAddressesPage
import geb.com.intershop.b2x.pages.storefront.responsive.account.punchout.PunchoutPage
import geb.com.intershop.b2x.pages.storefront.responsive.account.subscriptions.SubscriptionsPage
import geb.com.intershop.b2x.pages.storefront.responsive.approval.ApprovedOrdersPage
import geb.com.intershop.b2x.pages.storefront.responsive.approval.OrdersToApprovePage
import geb.com.intershop.b2x.pages.storefront.responsive.approval.RejectedOrdersPage
import geb.com.intershop.b2x.pages.storefront.responsive.approval.PendingOrdersPage
import geb.com.intershop.b2x.pages.storefront.responsive.approval.MyRejectedOrdersPage
import geb.com.intershop.b2x.pages.storefront.responsive.contract.AccountContractsPage
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.CostCentersPage
import geb.com.intershop.b2x.pages.storefront.responsive.order.OrdersHistoryPage
import geb.com.intershop.b2x.pages.storefront.responsive.user.UsersPage
import geb.com.intershop.b2x.pages.storefront.responsive.user.profile.ProfileSettingsPage

/**
 * Provides navigation links bar available for a logged B2B user in the front store in the Account related pages.
 */
class AccountNavigationBar extends Module
{
    static content =
    {
        purchaseHeader(required : false) {$("li[data-target='#collapsePurchase']").first()}
        notificationHeader(required : false) {$("li[data-target='#collapseNotification']").first()}
        profileHeader(required : false) {$("li[data-target='#collapseProfile']").first()}
        orgHeader(required : false) {$("li[data-target='#collapseOrg']").first()}
        approvalHeader(required : false) {$("li[data-target='#collapseApprovals']").first()}

        logoutLink(required : false, to: HomePage) {$("a", href:endsWith("ViewUserAccount-LogoutUser")).first()}
        changeAddressLink(required : false, to: SavedAddressesPage) {$("a", href:endsWith("ViewUserAddressList-List")).first()}
        profileSettingsLink(required : false, to: ProfileSettingsPage) {$("a", href:endsWith("ViewProfileSettings-ViewProfile")).first()}
        costCentersLink(required : false, to: CostCentersPage) {$("a", href:endsWith("ViewCostCenter-Start")).first()}
        punchoutLink(required : false, to: PunchoutPage) {$("a", href:endsWith("ViewOCISettings-Start")).first()}
        usersLink(required : false, to: UsersPage) {$("a", href:endsWith("ViewUsers-Start")).first()}
        approvedOrdersLink(required : false, to: ApprovedOrdersPage) {$("a", href:endsWith("ViewApprovals-AllApproved")).first()}
        ordersToApproveLink(required : false, to: OrdersToApprovePage) {$("a", href:endsWith("ViewApprovals-AllOpen")).first()}
        rejectedOrdersLink(required : false, to: RejectedOrdersPage) {$("a", href:endsWith("ViewApprovals-AllRejected")).first()}
        pendingOrdersLink(required : false, to: PendingOrdersPage) {$("a", href:endsWith("ViewApprovals-MyOpen")).first()}
        myRejectedOrdersLink(required : false, to: MyRejectedOrdersPage) {$("a", href:endsWith("ViewApprovals-MyRejected")).first()}
        ordersHistoryLink(required : false, to: OrdersHistoryPage) {$("a", href:endsWith("ViewOrders-SimpleOrderSearch")).first()}
        contractsLink (required: false, to: AccountContractsPage) {$('a[data-testing-linkid="account_contracts"]')}
        orderTemplatesLink (required: false) {$('a[data-testing-id="link-order-templates"]')}
        subscriptionsLink(required: false, to: SubscriptionsPage) { $('[data-testing-id="account-subscriptions-link"]') }
    }


    def expandApproval(){
        if(!approvalHeader.next().hasClass('in')) {
            approvalHeader.click()
            waitForSubNavigationIsExpanded(approvalHeader)
        }
    }

    def expandPurchase(){
        if(!purchaseHeader.next().hasClass('in')) {
            purchaseHeader.click()
            waitForSubNavigationIsExpanded(purchaseHeader)
        }
    }

    def expandProfile(){
        if(!profileHeader.next().hasClass('in')) {
            profileHeader.click()
            waitForSubNavigationIsExpanded(profileHeader)
        }
    }

    def expandOrg(){
        if(!orgHeader.next().hasClass('in')) {
            orgHeader.click()
            waitForSubNavigationIsExpanded(orgHeader)
        }
    }

    private waitForSubNavigationIsExpanded(navigationItem) {
        waitFor{ navigationItem.children().each {child -> child.isDisplayed() == true; } }
    }
}
