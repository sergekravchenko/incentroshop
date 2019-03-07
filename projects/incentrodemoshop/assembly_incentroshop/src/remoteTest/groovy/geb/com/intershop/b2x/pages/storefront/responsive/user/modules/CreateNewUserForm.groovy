package geb.com.intershop.b2x.pages.storefront.responsive.user.modules
import geb.Module
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.user.CreateNewUserPage
import geb.com.intershop.b2x.pages.storefront.responsive.user.UsersPage
import geb.module.Select

class CreateNewUserForm extends Module {
    static content = {        languageSelector {$("select", id:"CreateUserForm_LocaleID").module(Select)}        firstNameInput {$("input", id:"CreateUserForm_FirstName")}        lastNameInput {$("input", id:"CreateUserForm_LastName")}        emailInput {$("input", id:"CreateUserForm_Email" )}        loginInput {$("input", id:"CreateUserForm_Login" )}

        submitButton(to: [UsersPage, CreateNewUserPage]) {$("button", type: "submit", name:"Create")}
    }    def setUserData(User user) {        firstNameInput.value user.firstName
        lastNameInput.value user.lastName
        loginInput.value user.password
        emailInput.value user.email
        if (user.language != null ) {
            languageSelector.selected = user.language
        }
    }
}
