package geb.com.intershop.b2x.pages.storefront.responsive.user

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage

/**
 * This class represents the confirmation modal dialog before user removal
 */
class RemoveUserDialogPage extends StorefrontPage {

    static at = {
        waitFor{modalDialog.size()>0 && modalDialog.displayed && submitButton.displayed}
    }

    static content = {
        modalDialog {$("div", class: "modal-dialog")}
        dialogTitle {modalDialog.$("h2", class: "modal-title").text()}
        form {modalDialog.$("form", action: endsWith("ViewUsers-DeleteUser"))}
        userID {form.$("input", name: "UserID").@value}
        cancelButton(to: UsersPage) {form.$("button", class: "btn-default", name:"cancel")}
        submitButton(to: UserRemovedConfirmationPage, toWait: true) {form.$("button", type: "submit", class: "btn-primary")}
    }

    UserRemovedConfirmationPage clickConfirm() {
        submitButton.click()
        browser.page
    }

    UsersPage clickCancel() {
        cancelButton.click()
        browser.page
    }
}
