package geb.com.intershop.b2x.pages.storefront.responsive.costcenter

import geb.Module
import geb.Page
import geb.com.intershop.b2x.model.storefront.responsive.Budget
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.user.UserDetailsPage
import geb.module.Select

class CostCenterEditBuyerBudgetPage extends CostCenterModalDialogPage
{
    String formAction() {"ViewCostCenter-SaveBuyerBudget"}
    Class<? extends Page> destination = CostCenterDetailsPage

    static content =
    {
        personLink(to: UserDetailsPage) {webForm.$("a",href:contains("ViewUser-Start"))}
        personName {personLink.text()}
        userUUID {webForm.$("input", name: "CostCenterEditUserBudgetForm_BuyerID").@value}
        userBudgetField {$("input", id: "CostCenterEditUserBudgetForm_Budget")}
        budgetPeriod {$("select", name: "CostCenterEditUserBudgetForm_BudgetPeriod").module(Select)}
    }

    CostCenterEditBuyerBudgetPage setBudget(Budget budget)
    {
        userBudgetField.value(budget.amount.doubleValue())
        budgetPeriod.selected = budget.period.value
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

    boolean isPresent(User user)
    {
        user.name == personName
    }

    CostCenterEditBuyerBudgetPage verifyUserPresent(User user)
    {
        assert isPresent(user)
        browser.page
    }

    UserDetailsPage clickUserLink()
    {
        personLink.click()
        browser.page
    }
}
