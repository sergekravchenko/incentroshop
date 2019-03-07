package geb.com.intershop.b2x.pages.storefront.responsive.costcenter.modules

import geb.Module
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.CostCenterDetailsPage
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.CostCenterEditBuyerBudgetPage
import geb.com.intershop.b2x.pages.storefront.responsive.user.UserDetailsPage

class CostCenterViewBuyerBudgetRow extends Module
{
    static content =
    {
        personLink(to: UserDetailsPage) {$("a",href:contains("ViewUser-Start"))}
        personName {personLink.text()}
        cell {$("div.list-item", it)}
        approvedOrdersCount {cell(1).text().toInteger()}
        pendingOrdersCount {cell(2).text().toInteger()}
        buyerBudget {cell(3).module(BudgetInfo)}
        editBuyerBudgetButton (toWait: true, to: CostCenterEditBuyerBudgetPage){$("a[data-url*='ViewCostCenter-EditBuyerBudget']", class:"open-costcenter-modal")}
        removeBuyerButton (toWait: true, to: CostCenterDetailsPage){$("a[href*='ViewCostCenter-RemoveCostCenterBuyer']")}
    }
}
