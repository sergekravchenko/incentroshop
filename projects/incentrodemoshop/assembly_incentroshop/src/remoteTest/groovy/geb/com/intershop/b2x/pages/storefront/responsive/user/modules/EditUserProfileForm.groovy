package geb.com.intershop.b2x.pages.storefront.responsive.user.modules

import geb.Module
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.user.EditUserProfilePage
import geb.com.intershop.b2x.pages.storefront.responsive.user.UserDetailsPage
import geb.module.Select

class EditUserProfileForm extends Module {
    static content = {
        languageSelector {$("select", id:"UpdateProfileForm_LocaleID").module(Select)}
        firstNameInput {$("input", id:"UpdateProfileForm_FirstName")}
        lastNameInput {$("input", id:"UpdateProfileForm_LastName")}

        submitButton(to: [UserDetailsPage, EditUserProfilePage]) {$("button", type: "submit", name:"UpdateProfile")}
        cancelButton(to: UserDetailsPage) {$("a", class: "btn-default", href:contains("ViewUser-Start"))}
    }

    def setUserData(User user) {
        firstNameInput.value user.firstName
        lastNameInput.value user.lastName
        if (user.language != null ) {
            languageSelector.selected = user.language
        }
    }

    def clickSave() {
        submitButton.click()
    }

    def clickCancel() {
        cancelButton.click()
    }
}
