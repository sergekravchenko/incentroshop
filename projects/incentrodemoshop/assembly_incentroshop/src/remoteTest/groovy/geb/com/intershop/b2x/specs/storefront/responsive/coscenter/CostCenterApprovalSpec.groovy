package geb.com.intershop.b2x.specs.storefront.responsive.coscenter

import geb.com.intershop.b2x.model.storefront.responsive.Order
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoCostCenter
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoProduct
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoUser
import geb.com.intershop.b2x.specs.storefront.responsive.features.Authentication
import geb.com.intershop.b2x.specs.storefront.responsive.features.OrderApproval
import geb.com.intershop.b2x.specs.storefront.responsive.features.OrderManagement
import geb.com.intershop.b2x.testdata.TestDataUsage
import geb.spock.GebReportingSpec
import spock.lang.Ignore
import spock.lang.Unroll

@Ignore
public class CostCenterApprovalSpec extends GebReportingSpec implements OrderManagement, OrderApproval, Authentication, TestDataUsage
{

    def setup()
    { 
        // Login as regular buyer user
        logInUser(DemoUser.BUYER.user)
    }

    /**
     * ASSUMPTIONS:
     * Buyer user is assigned to the cost center used.
     */
    @Unroll
    def "Approve #recurrenceType for cost center"()
    {
        given: "As buyer order product for cost center"
            Order orderRequest = orderProduct(product, costCenter, recurrence)
        when: "Go to Pending Orders page"
            clickMyAccount()
            .clickPendingOrders()
        then: "Order is available in the list as it require approval"
            verifyOrderPresent orderRequest
        when: "Login as approver"
            logOutUser()
            logInUser approver
        and: "Go to Orders to Approve page"
            approveOrder orderRequest
        then: "Cost center is available in Approval Details"
            verifyCostCenterPresent costCenter
        when: "Open Orders to Approve list"
            clickOrdersToApprove()
        then: "The order request is not available in the list as it is already processed"
            verifyOrderNotPresent orderRequest
        where:
            costCenter << [DemoCostCenter.AGRO_NET_NORTH.costCenter] //, DemoCostCenter.AGRO_NET_NORTH.costCenter]
            approver << [DemoUser.ACCOUNT_ADMIN.user] //, DemoUser.ACCOUNT_ADMIN.user]
            product << [DemoProduct.SONY_VLP_SW225.product] //, DemoProduct.SONY_VLP_SW225.product]
            recurrence << [null] //, RecurrenceInformation.eachWeekOneYear()]
            recurrenceType << ["requisition"] //, "subscription"]
    }

    /**
     * ASSUMPTIONS:
     * Buyer user is assigned to the cost center used.
     */
    @Unroll
    def "Reject #recurrenceType for cost center"()
    {
        given: "As buyer order product for cost center"
            Order orderRequest = orderProduct(product, costCenter, recurrence)
        when: "Go to Pending Orders page"
            clickMyAccount()
            .clickPendingOrders()
        then: "Order is available in the list as it require approval"
            verifyOrderPresent orderRequest
        when: "Login as approver"
            logOutUser()
            logInUser approver
        and: "Reject the requisition"
            rejectOrder orderRequest
        then: "Cost center is available in Approval Details"
            verifyCostCenterPresent costCenter
        when: "Open Rejected Orders page"
            clickRejectedOrders()
        then: "The order is available in the list"
            verifyOrderPresent orderRequest
        when: "Open Orders to Approve page"
            clickOrdersToApprove()
        then: "The order request is not available in the list as it is already processed"
            verifyOrderNotPresent orderRequest
        where:
            costCenter << [DemoCostCenter.AGRO_NET_NORTH.costCenter] //, DemoCostCenter.AGRO_NET_NORTH.costCenter]
            approver << [DemoUser.ACCOUNT_ADMIN.user] //, DemoUser.ACCOUNT_ADMIN.user]
            product << [DemoProduct.SONY_VLP_SW225.product] //, DemoProduct.SONY_VLP_SW225.product]
            recurrence << [null] //, RecurrenceInformation.eachWeekOneYear()]
            recurrenceType << ["requisition"] //, "subscription"]
    }
}