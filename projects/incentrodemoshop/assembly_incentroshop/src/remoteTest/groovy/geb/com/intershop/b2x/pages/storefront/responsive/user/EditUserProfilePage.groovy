package geb.com.intershop.b2x.pages.storefront.responsive.user

import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage
import geb.com.intershop.b2x.pages.storefront.responsive.user.modules.EditUserProfileForm

class EditUserProfilePage extends AccountPage {
    String pageId() {"edit-user-profile-page"}

    static content = {
        editUserProfileForm {contentSlot.$("form", name:"UpdateProfileForm").module(EditUserProfileForm)}
    }

    def setUserData(User user) {
        editUserProfileForm.setUserData(user)
    }

    UserDetailsPage clickSave() {
        editUserProfileForm.clickSave()
        browser.page
    }
}
