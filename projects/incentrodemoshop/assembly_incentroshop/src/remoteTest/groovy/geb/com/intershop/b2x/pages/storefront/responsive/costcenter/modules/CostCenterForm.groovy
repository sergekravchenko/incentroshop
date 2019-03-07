package geb.com.intershop.b2x.pages.storefront.responsive.costcenter.modules

import geb.Module
import geb.com.intershop.b2x.model.storefront.responsive.Budget
import geb.com.intershop.b2x.model.storefront.responsive.Budget.BudgetPeriod
import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage
import geb.module.Select

class CostCenterForm extends Module
{
    static content =
    {
        id {$("input", name: "CostCenterForm_Number")}
        costCenterName {$("input", name: "CostCenterForm_Name")}
        budget {$("input", name: "CostCenterForm_Budget")}
        budgetPeriod {$("select", name: "CostCenterForm_BudgetPeriod").module(Select)}
        manager {$("select", name: "CostCenterForm_Manager").module(Select)}
    }
 
    def setCostCenterData(String id, String name, BigDecimal budget, BudgetPeriod period, String manager)
    {
        if (id != null)
        {
            this.id.value(id)
        }

        if (name != null)
        {
            this.costCenterName.value(name)
        }

        if (budget != null)
        {
            this.budget.value(budget.doubleValue())
        }

        if (period != null)
        {
          this.budgetPeriod.selected = period.value
        }

        if (manager != null)
        {
            this.manager.selected = manager
        }
    }

    def setCostCenterData(String id, String name, int budget, BudgetPeriod period, String manager)
    {
        this.setCostCenterData(id, name, BigDecimal.valueOf(budget), period, manager)
    }

    def setCostCenterData(CostCenter costCenter, Budget costCenterBudget, User costCenterManager)
    {
        this.setCostCenterData(costCenter.id, costCenter.name, costCenterBudget.amount, costCenterBudget.period, costCenterManager.name)
    }

    boolean isPresent(CostCenter costCenter)
    {
        costCenter.id.equals(this.id.value()) && costCenter.name.equals(this.costCenterName.value())
    }
}
