package geb.com.intershop.b2x.specs.storefront.responsive

import geb.com.intershop.b2x.pages.storefront.responsive.*
import geb.com.intershop.b2x.pages.storefront.responsive.account.*
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.*
import geb.com.intershop.b2x.pages.storefront.responsive.shopping.*
import geb.com.intershop.b2x.testdata.TestDataUsage
import geb.spock.GebReportingSpec
import spock.lang.*

/**
 * Storefront tests for Payment API at inSPIRED
 * @author skoch
 *
 */
class PaymentSpec extends GebReportingSpec implements TestDataUsage
{
    // Stack for Test: "existing Lineitems"
    private static existingPLIs = new Stack()

    def cleanup()
    {
        def zeroQuantity = "0"
        if(!existingPLIs.empty)
        {
            when: "I go to the homepage..."
                to HomePage
                at HomePage


            then: "... open basket ..."
                header.showMiniCart()
                header.viewCartMiniCart()

            and: "... and remove all line items"
                at CartPage
                while ( !existingPLIs.empty )
                {
                    def name = existingPLIs.pop()
                    productCartTable(name).removeLink.click()
                }
        }
    }

    /**
     * Execute checkout with redirect before payment method
     *
     */
    def "Redirect before payment checkout"()
    {
        when: "I go to the signIn page and log in..."
        to HomePage
        at HomePage
        pressLogIn()

        then:
        at AccountLoginPage

        when:
        login user,password

        then: "... then I'm logged in."
        at AccountHomePage

        when: "I go to the home page, search for product..."
        to HomePage
        at HomePage
        header.search searchTerm

        then: "... find it at the Detail Page."
        at ProductDetailPage
        lookedForSKU searchTerm

        when: "I add it to Cart..."
        addToCart()

        then: "... and check it."
        at CartPage
        checkProduct productName

        when: "Now I will checkout..."
        selectCostCenter costCenter
        checkOut()

        and: "...choose Payment if asked..."
        at CheckoutPaymentPage
        deletePayment()
        creditCard cardNumber,expDate,type

        then: "... redirect to payment provider ..."
        at RedirectBeforeDummyPaymentProviderPage
        submitProviderRequest()

        when: "... review my Order..."
        at CheckoutReviewPage

        then: "...and submit."
        agreeCheckBox.click()
        submitButton.click()

        and:"... receipt page is displayed ..."
        at CheckoutReceiptPage

        where:
        searchTerm  = testData.get("defaultProduct.default.sku")[0];
        productName = testData.get("defaultProduct.default.name")[0];

        user        =  testData.get("checkoutUser.login.eMail")[0];
        password    =  testData.get("checkoutUser.login.password")[0];
        costCenter = testData.get("checkoutUser.data.costCenter")[0];

        cardNumber = testData.get("paymentMethod.creditCard.number")[0];
        expDate = testData.get("paymentMethod.creditCard.expDate")[0];
        type = testData.get("paymentMethod.creditCard.type")[0];
    }
}
