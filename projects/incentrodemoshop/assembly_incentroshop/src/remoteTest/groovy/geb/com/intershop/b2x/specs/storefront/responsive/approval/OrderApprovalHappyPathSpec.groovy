package geb.com.intershop.b2x.specs.storefront.responsive.approval

import geb.com.intershop.b2x.model.storefront.responsive.Order
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoProduct
import geb.com.intershop.b2x.pages.storefront.responsive.approval.RequisitionApprovedPage
import geb.com.intershop.b2x.pages.storefront.responsive.approval.RequisitionRejectedPage
import geb.com.intershop.b2x.pages.storefront.responsive.order.OrdersHistoryPage
import geb.com.intershop.b2x.specs.storefront.responsive.features.Authentication
import geb.com.intershop.b2x.specs.storefront.responsive.features.OrderApproval
import geb.com.intershop.b2x.specs.storefront.responsive.features.OrderManagement
import geb.com.intershop.b2x.testdata.TestDataUsage
import geb.spock.GebReportingSpec
import spock.lang.*

/**
 * Spock test for order approval that is needed in case order price exceed buyer order spend limit or buyer budget is exceeded.
 */
@Ignore
@Stepwise
class OrderApprovalHappyPathSpec extends GebReportingSpec implements OrderManagement,  OrderApproval, Authentication {

    def cleanup() {
        logOutUser()
    }

    def "Storefront: Happy path test for Order Approve functionality"()
    {
        /**
         *  STOREFRONT PART BUYER
         */
        given: "As buyer order product that needs approval"
        when: "I log into storefront as buyer"
            User buyer = new User(buyerLogin, buyerPassword, buyerFirstName, buyerLastName)
            logInUser(buyer)
        and: "Make a requisition for a product"
            Order orderRequest = orderProduct(product)
        and: "Go to Pending Orders page"
            clickMyAccount()
            int ordersCount = myOrdersCount
            clickPendingOrders()
        then: "Order is available in the list as it require approval"
            verifyOrderPresent orderRequest

        /**
         *  STOREFRONT PART APPROVER
         */
        when: "Login as approver"
            User approver = new User(approverLogin, approverPassword, approverFirstName, approverLastName)
            logInUser(approver)
        and: "Approve the requisition"
            approveOrder orderRequest
        then: "Approval Details page is opened"
            at RequisitionApprovedPage
// Currently relation from requisition ID to the created order ID is not available in Approval Details page, so next checks are currently not possible
//        when: "Open Approved Orders page"
//            Order order = getOrder() // New order with different number is created after approval
//            clickApprovedOrders()
//        then: "The order is available in the Approved orders list"
//            verifyOrderPresent order
        when: "Open Orders to Approve list"
            clickOrdersToApprove()
        then: "The order request is not available in the list as it is already processed"
            verifyOrderNotPresent orderRequest

        /**
         *  STOREFRONT PART BUYER - Check order is created
         */
        when: "I signIn as a buyer again"
            logInUser(buyer)
        then: "My orders count increased with 1"
            myOrdersCount == ordersCount + 1 // ASSUMPTION: No other orders has been created in parallel for that buyer
        when: "I click on My Orders Link"
            clickMyOrdersLink()
        then: "I am on Orders History Page"
           at OrdersHistoryPage
        //TODO: Check sent mails here

        where:
        buyerLogin      << testData.get("approval.buyer.login")
        buyerPassword   << testData.get("approval.buyer.password")
        buyerFirstName  << testData.get("approval.buyer.firstName")
        buyerLastName   << testData.get("approval.buyer.lastName")
        customerId      << testData.get("approval.customer.id")
        product         << DemoProduct.ACER_PREDATOR_G3_605.product;
        
        approverLogin      << testData.get("approval.approver.login")
        approverPassword   << testData.get("approval.approver.password")
        approverFirstName  << testData.get("approval.approver.firstName")
        approverLastName   << testData.get("approval.approver.lastName")
    }

    def "Reject Requisition"() {

        /**
         *  STOREFRONT PART BUYER
         */
        given: "As buyer order product that needs approval"
        when: "I go to the signIn page and log in..."
            User buyer = new User(buyerLogin, buyerPassword, buyerFirstName, buyerLastName)
            logInUser(buyer)
        and: "Make a requisition for a product"
            Order orderRequest = orderProduct(product)
        and: "Go to Pending Orders page"
            clickMyAccount().clickPendingOrders()
        then: "Order is available in the list as it require approval"
            verifyOrderPresent orderRequest

        /**
         *  STOREFRONT PART APPROVER
         */
        when: "Login as approver"
            User approver = new User(approverLogin, approverPassword, approverFirstName, approverLastName)
            logInUser(approver)
        and: "Reject the requisition"
            rejectOrder orderRequest
        then: "Approval Details page is opened"
            at RequisitionRejectedPage
        when: "Open Rejected Orders page"
            clickRejectedOrders()
        then: "The order is available in the list"
            verifyOrderPresent orderRequest
        when: "Open Orders to Approve page"
            clickOrdersToApprove()
        
        then: "The order request is not available in the list as it is already processed"
            verifyOrderNotPresent orderRequest

        //TODO: Check sent mails here

        where:
        buyerLogin      << testData.get("approval.buyer.login")
        buyerPassword   << testData.get("approval.buyer.password")
        buyerFirstName  << testData.get("approval.buyer.firstName")
        buyerLastName   << testData.get("approval.buyer.lastName")
        customerId      << testData.get("approval.customer.id")
        product         << DemoProduct.ACER_PREDATOR_G3_605.product;

        approverLogin      << testData.get("approval.approver.login")
        approverPassword   << testData.get("approval.approver.password")
        approverFirstName  << testData.get("approval.approver.firstName")
        approverLastName   << testData.get("approval.approver.lastName")
    }
}