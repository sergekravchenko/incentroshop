package geb.com.intershop.b2x.pages.storefront.responsive.checkout

import org.openqa.selenium.StaleElementReferenceException;

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage;
import geb.com.intershop.b2x.pages.storefront.responsive.modules.OrderAddressSummary;


class MultipleAddressPage extends StorefrontPage
{
    static at =
    {
        waitFor { contentSlot.present }
    }

    static content =
    {
        contentSlot         { $("div[data-testing-id='multiple-shipment-address-page']") }
        waitFor             { contentSlot.present }
        addAddressButton    { contentSlot.$('a',text:"Add address") }
        continueButton      { contentSlot.$('a',name: "continue") }
        orderAddressSummary { module OrderAddressSummary }
    }

    //------------------------------------------------------------
    // link functions
    //------------------------------------------------------------
    def addShippingAddress(country,company,fName,lName,address,city,postal)
    {
        addAddressButton.click()
        waitFor { $("div", "data-testing-id":"multiple-shipping-newaddress").displayed }
        contentSlot.$('select',id:"newaddress_CountryCode").value(country)
        waitFor { $("select", "name":"newaddress_Title").displayed }
        contentSlot.$('input',id:"newaddress_CompanyName").value company
        contentSlot.$('input',id:"newaddress_FirstName").value fName
        contentSlot.$('input',id:"newaddress_LastName").value lName
        contentSlot.$('input',id:"newaddress_Address1").value address
        contentSlot.$('input',id:"newaddress_City").value city
        contentSlot.$('input',id:"newaddress_PostalCode").value postal
        contentSlot.$('button',name:"addNewBilling").click()
        waitFor { !$("div", "data-testing-id":"multiple-shipping-newaddress").displayed }
    }
    
    def setProductAddress(param, fName, lName, address)
    {
        def result = false
        def attempts = 0
        // try multiple times because page is dynamic
        while (attempts < 2)
        {
            try
            {
                $('select[data-testing-id="multiple-address-select-'+param+'"]').value fName+" "+lName+" | "+address
                result = true
                break
            }
            catch(StaleElementReferenceException e)
            {
                // ignore
            }
            attempts++
        }
        return result
    }
    
    def continueClick()
    {
        continueButton.click()
    }

}
