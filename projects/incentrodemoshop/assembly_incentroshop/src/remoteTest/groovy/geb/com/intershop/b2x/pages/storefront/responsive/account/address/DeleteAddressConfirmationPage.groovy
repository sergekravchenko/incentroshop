package geb.com.intershop.b2x.pages.storefront.responsive.account.address

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage

/**
 * This class represents Delete Address confirmation modal dialog
 */
class DeleteAddressConfirmationPage extends StorefrontPage
{

    static at =
    {
        waitFor { modalDialog.present && modalDialog.displayed }
    }

    static content =
    {
        modalDialog { $("div", class: "modal-dialog") }
        waitFor { modalDialog.present }
        dialogTitle {modalDialog.$("h2", class: "modal-title").text()}
        addressID {modalDialog.$("input", name: "AddressID").@value}
        cancelButton(to: SavedAddressesPage) {modalDialog.$("a", class: "btn-default")}
        okButton(to: SavedAddressesPage) {modalDialog.$("button", class: "btn-primary")}
    }

    SavedAddressesPage confirm()
    {
        okButton.click()
        browser.page
    }

    SavedAddressesPage cancel()
    {
        cancelButton.click()
        browser.page
    }
}
