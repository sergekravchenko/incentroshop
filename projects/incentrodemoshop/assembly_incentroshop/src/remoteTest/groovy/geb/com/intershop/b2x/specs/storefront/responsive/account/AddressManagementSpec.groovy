package geb.com.intershop.b2x.specs.storefront.responsive.account

import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoAddress
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoUser
import geb.com.intershop.b2x.model.storefront.responsive.util.AddressHelper
import geb.com.intershop.b2x.pages.storefront.responsive.account.address.AddressSuggestionPage
import geb.com.intershop.b2x.pages.storefront.responsive.account.address.SavedAddressesPage
import geb.com.intershop.b2x.specs.storefront.responsive.features.Authentication
import geb.com.intershop.b2x.testdata.TestDataUsage
import geb.spock.GebReportingSpec

public class AddressManagementSpec extends GebReportingSpec implements Authentication , TestDataUsage
{
    def setup()
    {
        // Login as regular byuer user
        logInUser(DemoUser.BUYER.user)
        .clickSavedAddresses()
    }


    def "Create address"()
    {
        given: "I am on the Saved Addresses page and have stored some addresses"
            int addressesCount = getAddressesCount()
        and: "New Address is not present in the list"
            verifyNotPresent(address)
        when: "Click Add New Address"
            clickAddNewAddress()
        and: "Enter address data and save the address"
            setAddress(address)
            saveAddress()
        then: "New address is visible and count of saved addresses increased with 1"
            at SavedAddressesPage
            getAddressesCount() == addressesCount + 1
            verifyPresent(address)
        where:
            address = AddressHelper.getRandomUSAddress(DemoUser.BUYER.user.getFirstName())
    }

    def "Edit address"()
    {
        given: "I am on the Saved Addresses page and create an address"
            createAddress(address)
        and: "after creation count existing addresses"
            int addressesCount = getAddressesCount()
        when: "Click Edit Address link for selected address"
            clickEditAddress(address)
        and: "Enter address data and save the address"
            setAddress(updateAddress)
            saveAddress()
        then: "Edited address is visible and count of saved addresses did not change"
             at SavedAddressesPage
             getAddressesCount() == addressesCount
             verifyPresent(updateAddress)
        where:
            address = AddressHelper.getRandomUSAddress(DemoUser.BUYER.user.getFirstName())
            updateAddress = AddressHelper.getRandomUSAddress(DemoUser.BUYER.user.getFirstName())
    }

    def "Edit address with suggestion"()
    {
        given: "I am on the Saved Addresses page and create an address"
            createAddress(address)
        and: "after creation count existing addresses"
            int addressesCount = getAddressesCount()
        when: "Click Edit Address link for selected address"
            clickEditAddress(address)
        and: "Enter address data and save the address"
            newAddress.firstName = address.firstName
            setAddress(newAddress)
            saveAddress()
        then: "Edited address is visible and count of saved addresses did not change"
            at AddressSuggestionPage
        when: "Save changed Address as is"
            saveAddress()
        then: "Edited Address is present on Saved Adresses page"
            getAddressesCount() == addressesCount
           // verifyPresent(newAddress)
        where:
            address = AddressHelper.getRandomUSAddress(DemoUser.BUYER.user.getFirstName())
            newAddress = DemoAddress.USA_ALABAMA_SUGGESTION.address
    }

    def "Delete address"()
    {
        given: "I am on the Saved Addresses page and create an address"
            createAddress(address)
        and: "after creation count existing addresses"
            int addressesCount = getAddressesCount()
        when: "Click Delete Address link for selected address"
            clickDeleteAddress(address)
        and: "Confirm the deletion"
            confirm()
        then: "Edited address is visible and count of saved addresses is reduced"
            at SavedAddressesPage
            waitFor{getAddressesCount() == addressesCount - 1}
            verifyNotPresent(address)
        where:
            address = AddressHelper.getRandomUSAddress(DemoUser.BUYER.user.getFirstName())
    }
    
    def createAddress(def address2Create)
    {
        at SavedAddressesPage
        clickAddNewAddress()
        setAddress(address2Create)
        saveAddress()
    }
}