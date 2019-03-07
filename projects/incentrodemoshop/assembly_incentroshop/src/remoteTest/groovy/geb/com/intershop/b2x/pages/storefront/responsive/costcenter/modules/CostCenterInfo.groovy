package geb.com.intershop.b2x.pages.storefront.responsive.costcenter.modules

import geb.Module
import geb.com.intershop.b2x.model.storefront.responsive.CostCenter

/**
 * The read only view of cost center details
 */
class CostCenterInfo extends Module
{
    static content =
    {
        cell {$("dd", it)}
        id {cell(0).text().trim()}
        name {cell(1).text().trim()}
        managerName {cell(2).text().trim()}
        costCenterBudget {cell(3).module(BudgetInfo)}
    }

    boolean isPresent(CostCenter costCenter)
    {
        costCenter.id == this.id && costCenter.name == this.name
    }
}
