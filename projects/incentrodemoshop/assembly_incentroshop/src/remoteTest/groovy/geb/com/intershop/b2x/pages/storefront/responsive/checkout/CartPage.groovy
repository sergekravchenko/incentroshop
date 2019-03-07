package geb.com.intershop.b2x.pages.storefront.responsive.checkout

import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.com.intershop.b2x.model.storefront.responsive.Product
import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage
import geb.com.intershop.b2x.pages.storefront.responsive.modules.CartRow
import geb.com.intershop.b2x.model.storefront.responsive.RecurrenceInformation
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.modules.CartSubscriptionForm

class CartPage extends StorefrontPage
{
    static at =
    {
        waitFor{ contentSlot.size()>0}
    }

    static content =
    {
        contentSlot {$("div[data-testing-id='cart-page']")}
        mainForm { contentSlot.$('form', name:'cartForm') }
        updateButton { mainForm.$("button[data-testing-id='button-update-cart']")}
        checkoutButton (required: false) {mainForm.$("button",name:'checkout')}
        productCartTable { term -> module(new CartRow(productTerm: term)) }

        price { $('dt',text:"Subtotal").parent().find("dd",0).text().replaceAll(',','').find(/\d+(\.\d+)?/) as BigDecimal }

        createQuoteLink (required: false) { $('a[id="createQuote"]') }
        cartAsEmailLink (required: false) { $('a[data-testing-id="link-cart-as-email"]') }
        transferOCIButton (required: false) { $('button[name="transferOCI"]') }
        autoSubmitField (required: false) { $('input[name="AUTOSUBMIT"]').value() }
		subscriptionSelection(required: false) {contentSlot.$('[data-testing-id="cartSubscriptionSection"]').module(CartSubscriptionForm)}
    }

    def selectCostCenter(String param)
    {
        mainForm.CostCenterID = param
    }

    def setCostCenter(CostCenter costCenter)
    {
        if (costCenter != null) {
            selectCostCenter(costCenter.id + " - " + costCenter.name)
        }
    }

    /**
     * set the autosubmit parameter (e.g. "true","false") - OCI Punchout
     * 
     */
    def autoSubmit(value) {
        browser.js.exec('document.querySelector(\'input[name="AUTOSUBMIT"]\').value=\''+value+'\';')
    }

    /**
     * check if the autosubmit has been set (OCI Punchout)
     * @param value
     * @return
     */
    def checkAutoSubmit(value) {
        return autoSubmitField == value
    }

    //------------------------------------------------------------
    // Page checks
    //------------------------------------------------------------

    def checkProduct(productName)
    {
        productCartTable(productName).size()>0
    }

    def checkProduct(Product product)
    {
        checkProduct(product.name)
    }

    //------------------------------------------------------------
    // link functions
    //------------------------------------------------------------

    void checkOut()
    {
        if (subscriptionSelection && subscriptionSelection.displayed && subscriptionSelection.isSubscriptionSelected()) {
            $('[data-testing-id="subscription-checkout-btn"]').click()
            println "Clicked on subscription checkout button."
        } else {
            checkoutButton.click()
        }
    }

    void setRecurrenceData(RecurrenceInformation recurrence) {
        if (recurrence != null) {
            subscriptionSelection.setRecurrenceData(recurrence)
        }
    }
}