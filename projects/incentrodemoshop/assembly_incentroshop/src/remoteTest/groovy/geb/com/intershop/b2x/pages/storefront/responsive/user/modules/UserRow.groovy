package geb.com.intershop.b2x.pages.storefront.responsive.user.modules

import geb.Module
import geb.com.intershop.b2x.pages.storefront.responsive.user.RemoveUserDialogPage
import geb.com.intershop.b2x.pages.storefront.responsive.user.UserDetailsPage

/**
 * Module that encapsulates User representation on Users page
 */
class UserRow extends Module {
    static content = {
        viewUserLink(to: UserDetailsPage) {$("a", href: contains("ViewUser-Start")).first()}
        deleteUserLink(to: RemoveUserDialogPage) {$("a", href: contains("ViewUsers-ShowDeleteUserDialog")).first()}
        personName {viewUserLink.text().trim()}
        userID {viewUserLink.@href.split("UserID=")[1]}
    }

    def clickViewUser() {
        viewUserLink.click()
    }

    def clickDeleteUser() {
        deleteUserLink.click()
    }
}
