package geb.com.intershop.b2x.pages.storefront.responsive.account.address

class AddressSuggestionPage extends AccountAddressesPage
{

    static at =
    {
        waitFor { suggestAddressForm.present && suggestAddressForm.displayed }
    }

    static content =
    {
        suggestAddressForm { $("form", id:"suggested-address-form") }
        waitFor { suggestAddressForm.present }
        selectedAddress { suggestAddressForm.$("input", name: "addressSelection") }
        saveButton(to: SavedAddressesPage, toWait:true) { suggestAddressForm.$("button", type: "submit", name: "update")}
        editAddressButton { suggestAddressForm.$("a", class: "btn-default", href: contains("ViewUserAddressList-Edit"))}
        cancelButton(to: SavedAddressesPage, toWait:true) { suggestAddressForm.$("a", class: "btn-default", href: endsWith("ViewUserAddressList-List"))}
    }

    def setSelectedAddress(String addressID)
    {
        selectedAddress = addressID
    }

    SavedAddressesPage saveAddress()
    {
        saveButton.click()
        browser.page
    }

    SavedAddressesPage cancel()
    {
        cancelButton.click()
        browser.page
    }

    def clickEditAddress()
    {
        editAddressButton.click()
    }
}
