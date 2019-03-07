package geb.com.intershop.b2x.pages.storefront.responsive.costcenter

import geb.Module
import geb.Page
import geb.com.intershop.b2x.model.storefront.responsive.Budget
import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.modules.CostCenterAddBuyerRow
import geb.module.Select

class CostCenterAddBuyersPage extends CostCenterModalDialogPage
{
    String formAction() {"ViewCostCenter-AddCostCenterBuyer"}
    Class<? extends Page> destination = CostCenterDetailsPage

    static content =
    {
        searchField {webForm.$("input[type='search']")}
        usersList {webForm.$("table[data-table='CostCenterBuyerList'] tbody tr")*.module(CostCenterAddBuyerRow).collectEntries {[it.personName, it]}}
    }

    CostCenterDetailsPage cancel()
    {
        cancelButton.click()
        browser.page
    }

    CostCenterDetailsPage save()
    {
        submit()
        browser.page
    }
    
    CostCenterDetailsPage close()
    {
        this.closeButton.click()
        browser.page
    }

    private def search(User user)
    {
        this.searchField.value(user.getName())
    }

    private def isUserPresent(User user)
    {
        usersList.containsKey(user.name)
    }

    CostCenterAddBuyersPage addBuyer(User buyer, Budget budget)
    {
        search(buyer)

        if (!isUserPresent(buyer))
        {
            throw IllegalArgumentException("Buyer '" + buyer.name + "' not available for selection")
        }

        CostCenterAddBuyerRow buyerBudgetRow = usersList[buyer.getName()]
        buyerBudgetRow.selectUser()
        buyerBudgetRow.setBudget(budget)

        browser.page
    }

}
