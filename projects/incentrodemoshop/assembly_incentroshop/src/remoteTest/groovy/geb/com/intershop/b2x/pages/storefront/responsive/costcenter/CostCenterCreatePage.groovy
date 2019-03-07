package geb.com.intershop.b2x.pages.storefront.responsive.costcenter

import geb.Page
import geb.com.intershop.b2x.model.storefront.responsive.Budget
import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.model.storefront.responsive.Budget.BudgetPeriod
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.modules.CostCenterForm

class CostCenterCreatePage extends CostCenterModalDialogPage
{
    String formAction() {"ViewCostCenter-Create"}
    Class<? extends Page> destination = CostCentersPage

    static content =
    {
        costCenterForm { webForm.module(CostCenterForm)}
    }

    CostCenterCreatePage setCostCenterData(CostCenter costCenter, Budget budget, User manager)
    {
        costCenterForm.setCostCenterData(costCenter, budget, manager)
        browser.page
    }

    CostCentersPage cancel()
    {
        cancelButton.click()
        browser.page
    }

    CostCentersPage save()
    {
        String id = costCenterForm.id.value()
        submit()
        // Wait until form is closed and new cost center is rendered in the cost centers list
        waitFor{$("a", href:contains("ViewCostCenter-Details"), text: id).size()>0}
        browser.page
    }

    CostCenterCreatePage verifyCostCenterPresent(CostCenter costCenter)
    {
        assert costCenterForm.isPresent(costCenter)
        browser.page
    }
}
