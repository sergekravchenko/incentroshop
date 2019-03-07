package geb.com.intershop.b2x.pages.storefront.responsive.user

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage

/**
 * This class represents the confirmation dialog after user removal
 */
class UserRemovedConfirmationPage extends StorefrontPage {
    static at = {
        waitFor{modalDialog.size()>0 && modalDialog.displayed && okButton.displayed}
    }

    static content = {
        modalDialog {$("div", class: "modal-dialog")}
        dialogTitle {modalDialog.$("h2", class: "modal-title").text()}
        okButton(to: UsersPage) {modalDialog.$("a", class: "btn-primary", href:endsWith("ViewUsers-Start"))}
    }

    UsersPage clickOK() {
        okButton.click()
        browser.page
    }
}
