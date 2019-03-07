package geb.com.intershop.b2x.specs.storefront.responsive.coscenter

import geb.com.intershop.b2x.model.storefront.responsive.Budget
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoBudget
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoCostCenter
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoUser
import geb.com.intershop.b2x.pages.storefront.responsive.HomePage
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage
import geb.com.intershop.b2x.specs.storefront.responsive.features.Authentication
import geb.com.intershop.b2x.specs.storefront.responsive.features.OrderApproval
import geb.com.intershop.b2x.testdata.TestDataUsage
import geb.spock.GebReportingSpec
import spock.lang.Ignore

@Ignore("This spec is not maintainable. Please rewrite")
public class CostCenterManagementSpec extends GebReportingSpec implements Authentication, OrderApproval, TestDataUsage
{

    def setup()
    { 
        given:
            to HomePage
        when:
        // Login as user having granted the "Manage Cost Centers" Permission and go to the "Cost Centers" page
            logInUser(DemoUser.ACCOUNT_ADMIN.user)
        then:
            at AccountPage
        when:
            clickCostCenters()
        then:
            true
    }

    def "Create and delete cost center"()
    {
        given: "Given Cost Center is not available in the Cost Centers page"
           verifyNotPresent(costCenter)
         when: "I add the new cost center"
           clickCreateCostCenter()
           .setCostCenterData(costCenter, budget, manager)
           .verifyCostCenterPresent(costCenter)
           .save()
         then: "New cost center is displayed in the Cost Centers page"
           openCostCenter(costCenter)
           .verifyCostCenterPresent(costCenter)
           .clickBackLink()
           .verifyPresent(costCenter)
         when: "I remove the cost center"
           clickRemove(costCenter)
           .confirm()
         then: "cost center is not displayed in Cost Centers page"
           verifyNotPresent(costCenter)
         where:
            costCenter = DemoCostCenter.AGRO_NET_NEW.costCenter
            manager = DemoUser.ACCOUNT_ADMIN.user
            budget = DemoBudget.MONHTLY_10000_USD.budget
    }

    def "Edit cost center details"()
    {
        given: "I have existing cost center"
          verifyPresent(costCenter)
         when: "Edit Cost Center data"
           openCostCenter(costCenter)
           .clickEditCostCenterDetails()
           .verifyCostCenterPresent(costCenter)
           .save()
         then: "Changed Cost Center data are displayed on Cost Center Details page"
           verifyCostCenterPresent(costCenter)
         where:
            costCenter = DemoCostCenter.AGRO_NET_CENTRAL.costCenter
    }

    def "Assign and unassign buyer to cost center"()
    {
         given: "Buyer is not assigned to cost center"
           openCostCenter(costCenter)
           .verifyBuyerNotPresent(buyer)
         when: "I assign buyer to Cost Center with given budget"
            clickAddBuyers()
           .addBuyer(buyer, budget)
           .save()
         then: "Buyer is available in the Buyers list in Cost Center Details page"
           verifyBuyerPresent(buyer, budget)
         when: "I remove the buyer"
           removeBuyer(buyer)
         then: "Buyer is not available in the Buyers list in Cost Center Details page"
           verifyBuyerNotPresent(buyer)
         where:
            costCenter = DemoCostCenter.AGRO_NET_NORTH.costCenter
            buyer = DemoUser.APPROVER.user
            budget = DemoBudget.MONHTLY_5000_USD.budget
    }

    def "Edit cost center buyer budget"()
    {
         given: "Buyer is assigned to cost center"
           Budget originalBudget = 
           openCostCenter(costCenter)
           .verifyBuyerPresent(buyer)
           .getBuyerBudget(buyer)
         when: "I change the buyer budget"
           clickEditBuyerBudgetLink(buyer)
           .setBudget(newBudget)
           .save()
         then: "New Buyer budget is displayed in the Buyers list in Cost Center Details page"
           verifyBuyerPresent(buyer, newBudget)
         when: "I restore the original buyer budget"
           clickEditBuyerBudgetLink(buyer)
           .setBudget(originalBudget)
           .save()
         then: "Original Buyer budget is displayed in the Buyers list in Cost Center Details page"
          verifyBuyerPresent(buyer, originalBudget)
         where:
            costCenter = DemoCostCenter.AGRO_NET_NORTH.costCenter
            buyer = DemoUser.BUYER.user
            newBudget = DemoBudget.WEEKLY_1000_USD.budget
    }

    def "Deactivate and activate cost center"()
    {
        given: "Given Active Cost Center is available in the Cost Centers page"
           verifyActive(costCenter)
         when: "I deactivate the cost center"
           clickDeactivate(costCenter)
           .confirm()
         then: "Cost center is displayed as deactivated in the Cost Centers page"
           verifyDeactivated(costCenter)
         when: "I activate the cost center again"
           clickActivate(costCenter)
         then: "Cost center is displayed as active in the Cost Centers page"
           verifyActive(costCenter)
         where:
            costCenter = DemoCostCenter.AGRO_NET_CENTRAL.costCenter
    }
}