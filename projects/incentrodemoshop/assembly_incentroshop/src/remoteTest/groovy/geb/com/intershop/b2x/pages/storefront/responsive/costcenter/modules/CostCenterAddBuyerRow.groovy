package geb.com.intershop.b2x.pages.storefront.responsive.costcenter.modules

import geb.Module
import geb.com.intershop.b2x.model.storefront.responsive.Budget
import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.module.Select
import geb.navigator.Navigator

class CostCenterAddBuyerRow extends Module
{
    static content =
    {
        personName {$("td[class='sorting_1'] label").text()}
        addUserCheckbox {$("input[name='SelectedObjectUUID']")}
        userUUID {addUserCheckbox.@value}
        userBudgetField {$("input", name: "Budget_" + userUUID)}
        budgetPeriod {$("select", name: "BudgetPeriod_" + userUUID).module(Select)}
    }

    def selectUser() {
        addUserCheckbox.value(true)
    }

    def setBudget(Budget budget)
    {
        if (budget.amount)
        {
            userBudgetField.value(budget.amount.doubleValue())
        }
        if (budget.period)
        {
            budgetPeriod.selected = budget.period.value
        }
    }

    boolean isPresent(User user)
    {
        user.name == personName
    }
}
