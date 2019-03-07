package geb.com.intershop.b2x.specs.storefront.responsive.features

import geb.com.intershop.b2x.model.storefront.responsive.Product
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CartPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CheckoutPaymentPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CheckoutReceiptPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CheckoutReviewPage
import geb.com.intershop.b2x.pages.storefront.responsive.shopping.ProductDetailPage

/**
 * Trait used to checkout a given product and a given Payment method.
 */
trait CheckoutProduct
{

    /**
     * Approve the provided requisition/subscription.
     * ASSUMPTION: The currently logged user has permissions to approve orders for the buyer that created the provided requisition.
     * ASSUMPTION: The currently logged user is currently on a page within My Account section.
     * After executing the method the last visited page will be ApprovalDetails page.
     * @param orderRequest - the requisition to be approved. In case it represents subscription recurrenceInformation field must be not null.
     */
    void checkoutProduct(Product aProduct) {
        when: "Search for a product"
            searchProduct(aProduct.sku)
        then: "I'm on the details page"
            at ProductDetailPage
        when: "I add the product to the Cart..."
            addToCart()
        and: " start checkout process"
            at CartPage
            checkOut()
        and: " select Invoice as Payment Method "
            at CheckoutPaymentPage
            invoice()
        and: "...agree T&C and submit."
            at CheckoutReviewPage
            submitOrder()
        //cost center would lead to "approval required"
        then: "I'm on the order confirmation page."
            at CheckoutReceiptPage
    }
}