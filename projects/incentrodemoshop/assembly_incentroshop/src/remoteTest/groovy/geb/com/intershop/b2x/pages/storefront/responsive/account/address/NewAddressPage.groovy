package geb.com.intershop.b2x.pages.storefront.responsive.account.address

import geb.com.intershop.b2x.model.storefront.responsive.Address
import geb.com.intershop.b2x.pages.storefront.responsive.account.address.modules.AddressForm

/**
 * Geb page representing the New Address dialog in My Account/Saved Addresses section
 */
class NewAddressPage extends AccountAddressesPage
{

    static at =
    {
        waitFor { newAddressForm.displayed }
    }

    static content =
    {
        newAddressContainer { $("div", class:"my-account-new-address-container") }
        waitFor { newAddressContainer.displayed }
        newAddressForm { newAddressContainer.module(AddressForm) }
    }

    def setAddress(Address address)
    {
        newAddressForm.setAddress(address)
    }
    
    def setAsPreferredInvoiceAddress()
    {
        newAddressForm.setAsPreferredInvoiceAddress()
    }
    
    def setAsPreferredShippingAddress()
    {
        newAddressForm.setAsPreferredShippingAddress()
    }

    AccountAddressesPage saveAddress()
    {
        newAddressForm.clickSaveAddress()
        browser.page
    }

    SavedAddressesPage cancel()
    {
        newAddressForm.clickCancel()
        browser.page
    }
}
