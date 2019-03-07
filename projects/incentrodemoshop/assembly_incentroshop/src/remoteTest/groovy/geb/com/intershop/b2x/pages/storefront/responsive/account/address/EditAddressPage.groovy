package geb.com.intershop.b2x.pages.storefront.responsive.account.address

import geb.com.intershop.b2x.model.storefront.responsive.Address
import geb.com.intershop.b2x.pages.storefront.responsive.account.address.modules.AddressForm

/**
 * Geb page representing the Edit Address dialog in My Account/Saved Addresses section
 */
class EditAddressPage extends AccountAddressesPage
{

    static at =
    {
        waitFor { editAddressForm.present }
    }

    static content =
    {
        editAddressContainer { $("div", class:"myAccount-editBox") }
        waitFor { editAddressContainer.present }
        editAddressForm { editAddressContainer.module(AddressForm) }
        waitFor { editAddressForm.present }
        addressIdField {editAddressForm.$("input", name: "AddressID")}
        addressID {addressIdField.@value}
    }

    def setAddress(Address address)
    {
        editAddressForm.setAddress(address)
    }

    AccountAddressesPage saveAddress()
    {
        editAddressForm.clickSaveAddress()
        browser.page
    }

    SavedAddressesPage cancel()
    {
        editAddressForm.clickCancel()
        browser.page
    }
}
