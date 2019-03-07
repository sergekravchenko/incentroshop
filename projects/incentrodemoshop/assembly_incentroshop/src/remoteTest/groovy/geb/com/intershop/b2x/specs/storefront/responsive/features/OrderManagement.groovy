package geb.com.intershop.b2x.specs.storefront.responsive.features

import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.com.intershop.b2x.model.storefront.responsive.Order
import geb.com.intershop.b2x.model.storefront.responsive.Product
import geb.com.intershop.b2x.model.storefront.responsive.RecurrenceInformation
import geb.com.intershop.b2x.pages.storefront.responsive.HomePage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CartPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CheckoutPaymentPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CheckoutReceiptPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CheckoutReviewPage
import geb.com.intershop.b2x.pages.storefront.responsive.order.OrderDetailsPage
import geb.com.intershop.b2x.pages.storefront.responsive.shopping.ProductDetailPage

/**
 * Trait used to make an orders for a product in B2B front store.
 * Will be used by the Spock specs.
 */
trait  OrderManagement
{

    /**
     * Makes a subscription for a product when a user is logged in.
     * The subscription is assigned to the provided Cost Center.
     *
     * @param productId - the product to be ordered.
     * ASSUMPTION: The provided product is available in the catalog.
     * @param costCenter - display name of the cost center in Checkout Review Page
     *                     Example: <CostCenterID> - <CostCenterName>
     *                     Could be null in case the buyer is not assigned to any Cost Center
     * @param recurrence - the recurrence information for the subscription
     *                     Could be null and in this case a normal order will be created instead of subscription
     * ASSUMPTION: The buyer user is assigned to the provided cost center.
     */
    Order orderProduct(Product product, CostCenter costCenter, RecurrenceInformation recurrence) {
        when: "I go to the home page"
            to HomePage
        and: "Search for a product..."
            header.search product.id
        then: "product is found and Product Detail Page is displayed."
            at ProductDetailPage
            checkProduct product
        when: "I add it to Cart..."
            addToCart()
        then: "The Shopping Cart page is shown"
            at CartPage
            checkProduct product
        when: "Select Cost Center if provided AND click on the Checkout"
            setCostCenter costCenter
            setRecurrenceData(recurrence)

            checkOut()
        then: "Select payment method on The Checkout Payment page if it is not already defined"
            if ( isAt(CheckoutPaymentPage) ) {
                invoice()
            }
        when: "Submit the order on Checkout Review Page"
               at CheckoutReviewPage
               submitOrder()
        then: "Order Confirmation(Receipt) Page is displayed"
            at CheckoutReceiptPage
            // Return order information including order number
            getOrder()
    }

    /**
     * Makes an order for a product when a user is logged in.
     * The order is assigned to the provided Cost Center.
     *
     * @param productId - the product to be ordered.
     * ASSUMPTION: The provided product is available in the catalog.
     * @param costCenter - display name of the cost center in Checkout Review Page
     *                     Example: <CostCenterID> - <CostCenterName>
     *                     Could be null in case the buyer is not assigned to any Cost Center
     * ASSUMPTION: The buyer user is assigned to the provided cost center.
     */
    Order orderProduct(Product product, CostCenter costCenter) {
      orderProduct(product, costCenter, null)
    }

    /**
     * Makes an order for a product when a user is logged in.
     * ASSUMPTION: the logged in buyer user is not assigned to any Cost Center
     * @param productId - the product to be ordered.
     * ASSUMPTION: The provided product is available in the catalog.
     */
    Order orderProduct(Product product) {
        orderProduct(product, null)
    }

    void cancelOrder(Order order)
    {
        when: "Go to order history page and search for the order"
           clickOrdersHistory()
            .searchOrder order
        then: "Order is available in the list"
            verifyOrderPresent order
        when: "Open order details"
            openOrderDetails order
        then: "Order Details page is displayed and order state is NEW"
            at OrderDetailsPage
            verifyNew()
        when: "Cancel the order"
            clickCancelOrder()
            .clickConfirmCancelOrder()
        then: "Order Details page is displayed and order state is CANCELLED"
            at OrderDetailsPage
            verifyCancelled()
    }
}
