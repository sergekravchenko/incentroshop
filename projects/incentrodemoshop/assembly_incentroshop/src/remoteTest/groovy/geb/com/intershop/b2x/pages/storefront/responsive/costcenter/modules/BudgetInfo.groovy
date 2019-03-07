package geb.com.intershop.b2x.pages.storefront.responsive.costcenter.modules

import geb.Module
import geb.com.intershop.b2x.model.storefront.responsive.Budget
import geb.com.intershop.b2x.model.storefront.responsive.Budget.BudgetPeriod

/**
 * Module that encapsulates Budget read-only representation
 */
class BudgetInfo extends Module
{
    static content =
    {
        budgetText {$().text().trim()}
        budgetAmount {budgetText.split(" / ")[0].replaceAll(',','').find(/\d+(\.\d+)?/).toBigDecimal()}
        budgetPeriod {BudgetPeriod.getPeriod(budgetText.split(" / ")[1].tokenize(' ').last())}
    }

    Budget getBudget()
    {
        new Budget(budgetAmount, budgetPeriod)
    }
}
