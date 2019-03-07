package geb.com.intershop.b2x.pages.storefront.responsive.checkout

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage
import geb.com.intershop.b2x.pages.storefront.responsive.modules.OrderAddressSummary

class CheckoutPaymentPage extends StorefrontPage
{

    static at =
    {
        waitFor{contentSlot.size()>0}
    }

    static content =
    {
        contentSlot { $('div[data-testing-id="checkout-payment-page"]') }
        cashOnDeliveryRadio { contentSlot. $("input[data-testing-id='payment-Cash on Delivery-element']") }
        invoiceRadio { contentSlot. $("input[data-testing-id='payment-Invoice-element']") }
        ishDemoOnlinePayRadio { contentSlot. $("input[data-testing-id='payment-ISH Demo Online Pay-element']") }
        creditCardRadio { contentSlot. $("input[data-testing-id='payment-ISH Demo Credit Card-element']") }
        deleteButton(required: false) { contentSlot.$('button',0, name:"deletePayment") }
        continueButton { contentSlot.$('button',name:"continue") }
        error { $("div",class: "alert alert-danger").size()>0 }
        orderAddressSummary { module OrderAddressSummary }
    }

    //------------------------------------------------------------
    // link functions
    //------------------------------------------------------------
    def cashOnDelivery()
    {
        cashOnDeliveryRadio.click()
        continueButton.click()
    }

    def invoice()
    {
        invoiceRadio.click()
        continueButton.click()
    }

    def ishDemoOnlinePay()
    {
        ishDemoOnlinePayRadio.click()
        continueButton.click()
    }

    def creditCard(cardNumber, expDate, type)
    {
        creditCardRadio.click()
        //TODO fill input
        waitFor{ $('input', name:'com.intershop.adapter.payment.demo.internal.creditcard.CreditCardInformation:creditCardNumber').displayed }
        $('input', name:'com.intershop.adapter.payment.demo.internal.creditcard.CreditCardInformation:creditCardNumber').value   cardNumber
        $('input', name:'com.intershop.adapter.payment.demo.internal.creditcard.CreditCardInformation:creditCardExpiryDate').value expDate
        $('select',name:"com.intershop.adapter.payment.demo.internal.creditcard.CreditCardInformation:creditCardType").value   type
        $("input[data-testing-id='payment-saveForLater-checkbox']").value(false)
        continueButton.click()
    }

    def addPromoCode(promoCode)
    {
        when: "I enter the code"
        $("a", href:endsWith("promoentry")).click()
        waitFor { $("input",name:"promotionCode").displayed }
        $("input",name:"promotionCode").value promoCode
        $("button",name:"applyPromotion").click()

        then: "and got my promotion"
        waitFor{
            $("div",class:iContains("alert-success"),text:iContains(
            "Your Promotional Discount has been applied.")).size()>0}
    }

    def deletePayment()
    {
        if ($('button',0, name:"deletePayment").size()>0) {
            deleteButton.click()
            waitFor{contentSlot.size()>0}
        }
    }
}
