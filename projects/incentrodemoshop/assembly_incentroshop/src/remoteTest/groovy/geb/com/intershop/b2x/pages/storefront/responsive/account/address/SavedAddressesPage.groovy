package geb.com.intershop.b2x.pages.storefront.responsive.account.address

class SavedAddressesPage extends AccountAddressesPage
{
    static at =
    {
        waitFor { addAddressButton.present && addAddressButton.displayed }
    }

    static content =
    {
        addAddressButton(to: NewAddressPage) {$("a", class:"my-account-add-address")}
    }

    NewAddressPage clickAddNewAddress()
    {
        addAddressButton.click()
        browser.page
    }
}
