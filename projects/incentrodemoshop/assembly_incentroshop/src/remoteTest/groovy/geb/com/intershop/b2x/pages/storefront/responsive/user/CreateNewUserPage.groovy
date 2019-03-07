package geb.com.intershop.b2x.pages.storefront.responsive.user

import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage
import geb.com.intershop.b2x.pages.storefront.responsive.user.modules.CreateNewUserForm

class CreateNewUserPage extends AccountPage
{
    String pageId() {"create-user-page"}

    static content =
    {
        newUserForm {contentSlot.$("form", name:"CreateUserForm").module(CreateNewUserForm)}
    }

    def setUser(User user)
    {
        newUserForm.setUserData(user)
    }

    UsersPage createUser()
    {
        newUserForm.submitButton.click()
        browser.page
    }
}