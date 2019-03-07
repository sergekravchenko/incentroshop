package geb.com.intershop.b2x.pages.storefront.responsive.account.address

import geb.com.intershop.b2x.model.storefront.responsive.Address
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage
import geb.com.intershop.b2x.pages.storefront.responsive.account.address.modules.AddressTile

/**
 * Parent class for all My Account Addresses related Geb pages
 * Encapsulates the common logic for all Addresses related pages
 */
class AccountAddressesPage extends AccountPage
{
    String pageId() {"account-addresses-page"}
    
    static at=
    {
        waitFor{contentSlot.size()>0}
    }

    static content =
    {
        contentSlot { $("div[data-testing-id='"+pageId()+"']") }
        savedAddresses { $("div[data-testing-id='account-address-details']")*.module(AddressTile).collectEntries{[it.addressID, it]}}
    }

    int getAddressesCount()
    {
        savedAddresses.size()
    }

    AccountAddressesPage verifyPresent(Address address)
    {
        List addresses = savedAddresses.values().collect {it.addressText}
        assert addresses.contains(address.asText())
        browser.page
    }

    AccountAddressesPage verifyNotPresent(Address address)
    {
        List addresses = savedAddresses.values().collect {it.addressText}
        assert !addresses.contains(address.asText())
        browser.page
    }
    
    EditAddressPage clickEditAddress(Address address)
    {
        AddressTile addressTile = findAddress(address)
        addressTile.editAddressLink.click(EditAddressPage)
        browser.page
    }

    DeleteAddressConfirmationPage clickDeleteAddress(Address address)
    {
        AddressTile addressTile = findAddress(address)
        addressTile.deleteAddressLink.click(DeleteAddressConfirmationPage)
        browser.page
    }

    private AddressTile findAddress(Address address)
    {
        List filteredAddresses = savedAddresses.values().findAll {it.addressText == address.asText()}
        assert !filteredAddresses.empty
        filteredAddresses.get(0)
    }
}
