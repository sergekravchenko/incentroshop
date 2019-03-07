package geb.com.intershop.b2x.pages.storefront.responsive.checkout

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage;

class NewUserAddressPage extends StorefrontPage
{
    static at =
    {
        waitFor { contentSlot.present }
    }

    static content =
    {
        contentSlot     { $("div[data-testing-id='new-user-address-page']") }
        waitFor         { contentSlot.present }
        countrySelector { contentSlot.$('select',id:"billing_CountryCode") }
        companyName     { contentSlot.$('input',id:"billing_CompanyName") }
        fNameInput      { contentSlot.$('input',id:"billing_FirstName") }
        lNameInput      { contentSlot.$('input',id:"billing_LastName") }
        address1Input   { contentSlot.$('input',id:"billing_Address1") }
        address2Input   { contentSlot.$('input',id:"billing_Address2") }
        cityInput       { contentSlot.$('input',id:"billing_City") }
        postalInput     { contentSlot.$('input',id:"billing_PostalCode") }
        eMailInput      { contentSlot.$('input',id:"email_Email") }
        continueButton  { contentSlot.$("button",name:"continue") }
        shipToDifferentAddressButton { contentSlot.$('input', class:"alternate-address") }
        shipToMultipleAddressButton { contentSlot.$('input', id:"shipOption3") }
    }

    //------------------------------------------------------------
    // link functions
    //------------------------------------------------------------
    def fillData(country,company,fName,lName,address,city,postal,user)
    {
        countrySelector.value(country)
        sleepForNSeconds(2)
        fNameInput.value    fName
        lNameInput.value    lName
        address1Input.value address
        cityInput.value     city
        companyName.value company
        postalInput.value   postal
        eMailInput.value    user
    }
    
    def fillShippingData(country,company,fName,lName,address,city,postal)
    {
        contentSlot.$('select',id:"shipping_CountryCode").value(country)
        sleepForNSeconds(2)
        contentSlot.$('input',id:"shipping_CompanyName").value company
        contentSlot.$('input',id:"shipping_FirstName").value fName
        contentSlot.$('input',id:"shipping_LastName").value lName
        contentSlot.$('input',id:"shipping_Address1").value address
        contentSlot.$('input',id:"shipping_City").value city
        contentSlot.$('input',id:"shipping_PostalCode").value postal
        contentSlot.$('button', name:"addNewBilling").click()
    }


}
