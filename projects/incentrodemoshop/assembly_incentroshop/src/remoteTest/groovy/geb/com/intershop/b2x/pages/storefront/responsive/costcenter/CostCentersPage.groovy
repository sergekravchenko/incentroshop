package geb.com.intershop.b2x.pages.storefront.responsive.costcenter

import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage
import geb.com.intershop.b2x.pages.storefront.responsive.costcenter.modules.CostCenterRow

class CostCentersPage extends AccountPage
{
    String pageId() {"cost-center-page"}

    static content =
    {
        addCostCenterButton (toWait: true, to: CostCenterCreatePage){$("button#open-create-costcenter-modal").first()}
        costCenters { $("div[data-testing-id='cost-center-list'] div.list-item-row-big")*.module(CostCenterRow).collectEntries{[it.costCenterID, it]}}
    }

    CostCenterCreatePage clickCreateCostCenter()
    {
        addCostCenterButton.click()
        browser.page
    }

    CostCenterDetailsPage openCostCenter(CostCenter costCenter)
    {
        verifyPresent(costCenter)
        CostCenterRow row = getCostCenterRow(costCenter)
        row.openDetails()
        browser.page
    }

    RemoveCostCenterConfirmationPage clickRemove(CostCenter costCenter)
    {
        CostCenterRow row = getCostCenterRow(costCenter)
        row.remove()
        browser.page
    }

    DeactivateCostCenterConfirmationPage clickDeactivate(CostCenter costCenter)
    {
        CostCenterRow row = getCostCenterRow(costCenter)
        assert row.isActive
        row.clickDeactivate()
        browser.page
    }

    CostCentersPage clickActivate(CostCenter costCenter)
    {
        CostCenterRow row = getCostCenterRow(costCenter)
        assert !row.isActive
        row.clickActivate()
        browser.page
    }

    CostCentersPage verifyPresent(CostCenter costCenter)
    {
        assert costCenters.containsKey(costCenter.id)
        browser.page
    }

    CostCentersPage verifyNotPresent(CostCenter costCenter)
    {
        assert !costCenters.containsKey(costCenter.id)
        browser.page
    }

    CostCentersPage verifyActive(CostCenter costCenter)
    {
        assert isActive(costCenter)
        browser.page
    }

    CostCentersPage verifyDeactivated(CostCenter costCenter)
    {
        assert !isActive(costCenter)
        browser.page
    }

    private boolean isActive(CostCenter costCenter)
    {
        verifyPresent(costCenter)
        CostCenterRow row = getCostCenterRow(costCenter)
        row.isActive
    }

    private CostCenterRow getCostCenterRow(CostCenter costCenter)
    {
        CostCenterRow row = costCenters[costCenter.id]
        row.checkCostCenter(costCenter)
        row
    }
}