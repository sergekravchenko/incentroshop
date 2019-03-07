package geb.com.intershop.b2x.pages.storefront.responsive.costcenter.modules

import geb.Module
import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.CostCenterDetailsPage
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.CostCentersPage
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.DeactivateCostCenterConfirmationPage
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.RemoveCostCenterConfirmationPage
import geb.navigator.Navigator

class CostCenterRow extends Module
{
    static content =
    {
        cell {$("div.list-item", it)}
        detailsLink(to: CostCenterDetailsPage) {getLink(cell(0))}
        costCenterID {detailsLink.text()}
        costCenterName {getLink(cell(1)).text()}
        budgetStatistics (required : false)
        {
            def progressBar = $("div", class: "progress")
            (progressBar != null && !progressBar.isEmpty()) ? new BudgetStatistics(progressBar.parent()) : null
        }

        activateLink (to: CostCentersPage, required: false)
        {
            getLink(cell(4)).has("span", title: "Activate")
        }
        
        deactivateLink (required: false, to: DeactivateCostCenterConfirmationPage, toWait: true) 
        {
            getLink(cell(4)).has("span", title: "Deactivate")
        }
        isActive {!deactivateLink.isEmpty()}
        
        removeLink (to: RemoveCostCenterConfirmationPage, toWait: true) {getLink(cell(4),1)}
    }
    
    
    //------------------------------------------------------------
    // Module checks
    //------------------------------------------------------------

    def checkName(name)
    {
        costCenterName == name
    }

    def checkCostCenter(CostCenter costCenter)
    {
        checkName(costCenter.name)
    }

    def openDetails()
    {
        detailsLink.click()
    }

    void clickActivate()
    {
        if(!isActive)
        {
            activateLink.click()
        }
    }

    void clickDeactivate()
    {
        if(isActive)
        {
            deactivateLink.click()
        }
    }
    
    void remove()
    {
        removeLink.click()
    }
    
    private def getLink(cell)
    {
        cell.$("a")
    }
    
    private def getLink(cell,it)
    {
        cell.$("a",it)
    }
    
    static class BudgetStatistics
    {
        final String budgetSpentPercentage
        final String ordersTotal
        final String remainingBudget
        private BudgetStatistics(Navigator progressContainer)
        {
            budgetSpentPercentage = progressContainer.$("span", class: "progress-display").text()
            progressContainer.$("div", class: "progress").jquery.mouseover()
            ordersTotal = progressContainer.$("span", "data-testing-id": "cost-center-orders-total").text()
            remainingBudget = progressContainer.$("span", "data-testing-id": "cost-center-remaining-budget").text()
        }
    }
}
