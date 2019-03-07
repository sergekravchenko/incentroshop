package geb.com.intershop.b2x.pages.storefront.responsive.costcenter

import geb.Page
import geb.com.intershop.b2x.model.storefront.responsive.Budget
import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.model.storefront.responsive.Budget.BudgetPeriod
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.modules.CostCenterForm

class CostCenterEditPage extends CostCenterModalDialogPage
{
    String formAction() {"ViewCostCenter-Update"}
    Class<? extends Page> destination = CostCenterDetailsPage

    static content =
    {
        costCenterForm { webForm.module(CostCenterForm)}
    }

    CostCenterEditPage setCostCenterData(String id, String name, int budget, BudgetPeriod period, String manager)
    {
        costCenterForm.setCostCenterData(id, name, BigDecimal.valueOf(budget), period, manager)
        browser.page
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

    CostCenterEditPage verifyCostCenterPresent(CostCenter costCenter)
    {
        assert costCenterForm.isPresent(costCenter)
        browser.page
    }
}
