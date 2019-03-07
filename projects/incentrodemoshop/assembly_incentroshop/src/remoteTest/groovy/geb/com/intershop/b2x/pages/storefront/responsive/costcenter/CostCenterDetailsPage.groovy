package geb.com.intershop.b2x.pages.storefront.responsive.costcenter

import geb.com.intershop.b2x.model.storefront.responsive.Budget
import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.com.intershop.b2x.model.storefront.responsive.Order
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.modules.CostCenterInfo
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.modules.CostCenterViewBuyerBudgetRow
import geb.com.intershop.b2x.pages.storefront.responsive.order.modules.OrderRow
import geb.com.intershop.b2x.pages.storefront.responsive.user.UserDetailsPage

class CostCenterDetailsPage extends AccountPage
{
    String pageId() {"cost-center-details-page"}

    static content =
    {
        costCenterInfo {contentSlot.$("dl", "data-testing-id": "cost-center-details").module(CostCenterInfo)}
        editCostCenterButton (toWait: true, to: CostCenterEditPage){contentSlot.$("a[data-url*='ViewCostCenter-ShowForm']", class:"open-costcenter-modal")}
        addBuyersButton (toWait: true, to: CostCenterAddBuyersPage){contentSlot.$("button[data-url*='ViewCostCenter-ShowAddBuyerForm']", class:"open-costcenter-modal")}
        buyersList {contentSlot.$("div[data-testing-id='cost-center-buyers-list'] div.list-item-row-big")*.module(CostCenterViewBuyerBudgetRow).collectEntries {[it.personName, it]}}
        ordersTable(required: false) {contentSlot.$("table[data-testing-id='orders-list']")}
        ordersList(required: false) {ordersTable.$("tbody tr")*.module(OrderRow).each({o -> o.calculateIndexes(ordersTable.$("thead tr"))}).collectEntries{[it.orderNumber, it]}}
		searchOrderField(required: false) {contentSlot.$("input", type:"search")}
        backLink (to: CostCentersPage){contentSlot.$("a[data-testing-id='back-link']")}
    }

    CostCenterEditPage clickEditCostCenterDetails()
    {
        this.editCostCenterButton.click()
        browser.page
    }

    CostCenterAddBuyersPage clickAddBuyers()
    {
        this.addBuyersButton.click()
        browser.page
    }
 
    private boolean isBuyerPresent(User buyer)
    {
        buyersList.containsKey(buyer.name)
    }

    CostCenterDetailsPage verifyBuyerPresent(User buyer)
    {
        assert isBuyerPresent(buyer)
        browser.page
    }

    CostCenterDetailsPage verifyCostCenterPresent(CostCenter costCenter)
    {
        assert costCenter.id.equals(costCenterInfo.id) && costCenter.name.equals(costCenterInfo.name)
        browser.page
    }

    CostCenterDetailsPage verifyBuyerPresent(User buyer, Budget budget)
    {
        assert isBuyerPresent(buyer)
        CostCenterViewBuyerBudgetRow buyerInfo = buyersList[buyer.name]
        Budget displayedBudget = buyerInfo.buyerBudget.getBudget()
        assert budget.amount.equals(displayedBudget.amount) && budget.period == displayedBudget.period
        browser.page
    }

    CostCenterDetailsPage verifyBuyerNotPresent(User buyer)
    {
        assert !isBuyerPresent(buyer)
        browser.page
    }

    CostCenterDetailsPage removeBuyer(User buyer)
    {
        assert isBuyerPresent(buyer)
        buyersList[buyer.name].removeBuyerButton.click()
        browser.page
    }

    CostCenterEditBuyerBudgetPage clickEditBuyerBudgetLink(User buyer)
    {
        assert isBuyerPresent(buyer)
        buyersList[buyer.name].editBuyerBudgetButton.click()
        browser.page
    }
    
    Budget getBuyerBudget(User buyer)
    {
        assert isBuyerPresent(buyer)
        CostCenterViewBuyerBudgetRow buyerInfo = buyersList[buyer.name]
        buyerInfo.buyerBudget.getBudget()
    }
    
    UserDetailsPage clickUserLink(User buyer)
    {
        buyersList[buyer.name].personLink.click()
        browser.page
    }

    CostCentersPage clickBackLink()
    {
        backLink.click()
        browser.page
    }

    CostCenterDetailsPage searchOrder(Order order)
    {
        searchOrderField.value(order.orderNumber)
        browser.page
    }

    CostCenterDetailsPage verifyOrderPresent(Order order)
    {
        assert ordersList.containsKey(order.orderNumber)
        browser.page
    }

    CostCenterDetailsPage verifyOrderNotPresent(Order order)
    {
        assert !ordersList.containsKey(order.orderNumber)
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
        OrderRow row = ordersList[order.orderNumber]
        assert row.getOrder().hasSameInfo(order)
        row
    }
}