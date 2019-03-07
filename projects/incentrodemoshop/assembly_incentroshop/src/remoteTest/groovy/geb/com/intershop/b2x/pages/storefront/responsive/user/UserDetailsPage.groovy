package geb.com.intershop.b2x.pages.storefront.responsive.user

import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage

class UserDetailsPage extends AccountPage
{
    String pageId() {"user-details-page"}

    static content =
    {
        editProfileButton(to: EditUserProfilePage) {$("a", class: "btn-tool", href:contains("ViewUserProfile-View"))}
        editRolesAndBudgetButton (to: UserRolesPage){contentSlot.$("a", href:contains("ViewUserRole-View")).first()}
    }

    EditUserProfilePage clickEditProfile() {
        editProfileButton.click()
        browser.page
    }
    
    UserRolesPage clickEditRolesAndBudget() {
        editRolesAndBudgetButton.click()
        browser.page
    }
}