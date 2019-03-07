package geb.com.intershop.b2x.pages.storefront.responsive.account.address.modules

import geb.Module
import geb.com.intershop.b2x.model.storefront.responsive.Address
import geb.com.intershop.b2x.pages.storefront.responsive.account.address.AddressSuggestionPage
import geb.com.intershop.b2x.pages.storefront.responsive.account.address.SavedAddressesPage
import geb.module.Checkbox
import geb.module.Select

/**
 * Module that encapsulates Add/Edit Address web form used in My Account section of B2X inSPIRED demo
 */
class AddressForm extends Module
{
    
    static at = {
        waitFor{ webForm.size()>0 }
    }
    
    static content =
    {
        webForm {$("form")}
        countrySelector {webForm.$("select",id:"address_CountryCode").module(Select)}
        companyNameInput {webForm.$("input", id:"address_CompanyName")}
        companyName2Input {webForm.$("input", id:"address_CompanyName2")}
        firstNameInput {webForm.$("input", id:"address_FirstName")}
        lastNameInput {webForm.$("input",id:"address_LastName")}
        address1Input {webForm.$("input", id:"address_Address1" )}
        address2Input {webForm.$("input", id:"address_Address2" )}
        cityInput {webForm.$("input",id:"address_City")}
        stateSelector(required: false) {webForm.$("select", id:"address_State").module(Select)}
        postCodeInput {webForm.$("input",id:"address_PostalCode")}
        phoneInput {webForm.$("input",id:"address_PhoneHome")}
        preferredInvoiceAddress { $("[name='address_PreferredBillingAddress']").module(Checkbox) }
        preferredShippingAddress { $("[name='address_PreferredShippingAddress']").module(Checkbox) }

        saveAddressButton(to: [SavedAddressesPage, AddressSuggestionPage]) {webForm.$("button[name]",type:"submit")}
        cancelButton(to: SavedAddressesPage) {webForm.$("a", class: "cancel")}
    }

    def setAddress(Address address)
    {
        waitFor { countrySelector.displayed }
        countrySelector.selected = address.country
        sleepForNSeconds(20) // changing the country triggers a reload of the address part, that need to be finished
        companyNameInput.value address.company
        firstNameInput.value address.firstName
        lastNameInput.value address.lastName
        address1Input.value address.addressLine
        cityInput.value address.city
        selectState(address.state)
        postCodeInput.value address.postCode
    }
    
    def setAsPreferredInvoiceAddress()
    {
        preferredInvoiceAddress.click()
    }
    
    def setAsPreferredShippingAddress()
    {
        preferredShippingAddress.click()
    }

    def clickSaveAddress()
    {
        saveAddressButton.click()
    }

    def clickCancel()
    {
        cancelButton.click()
    }
    
    private def sleepForNSeconds(int n)
    {
        def originalMilliseconds = System.currentTimeMillis()
        waitFor(n + 1)
        {
            (System.currentTimeMillis() - originalMilliseconds) > (n * 1000)
        }
    }

    private void selectState(String sName)
    {
        if (sName != null )
        {
            stateSelector.selected = sName
        }
    }
}
