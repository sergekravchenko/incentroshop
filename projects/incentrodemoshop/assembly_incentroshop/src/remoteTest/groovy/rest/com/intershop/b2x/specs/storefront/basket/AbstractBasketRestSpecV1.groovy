package rest.com.intershop.b2x.specs.storefront.basket

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND
import static rest.com.intershop.b2x.specs.storefront.common.RequestBuilder.toJSON

import java.time.*

import rest.com.intershop.b2x.specs.storefront.common.RestSpec
import rest.com.intershop.b2x.specs.storefront.common.objects.AddressV1
import rest.com.intershop.b2x.specs.storefront.common.objects.LineItemV1
import spock.lang.*

/**
 * class to extend from for REST Tests of the V1 Basket API
 * 
 * Contains reusable methods. 
 */
class AbstractBasketRestSpecV1 extends RestSpec {

    static final String BASKET_NOT_FOUND_ERROR = "basket.not_found.error"
    static final String ACCEPT = "application/vnd.intershop.basket.v1+json"
    static final String ADDRESSES_RESOURCE = "/addresses"
    static final String INCLUDE_PREFIX = "?include="
    
    @Shared
    String user, password, basketsURI, basketURI, itemsURI, addressesURI;
    
    def setup()
    {
        client.headers."Accept" = ACCEPT
        // handle failure like success (i. e. don't throw exception)
        client.handler.failure = client.handler.success
        // register parser for custom "Accept" content type
        def parser = client.parser
        parser.putAt(ACCEPT, parser.getAt(JSON))
    }
    
    /**
     * Creates a new basket for the current authenticated user. The returned authentication token is set as client
     * header for subsequent requests.
     * Also sets sub resource URIs for items and addresses.
     * @return basket ID of the newly created basket
     */
    def String createBasket()
    {
        def response = client.post(uri: basketsURI, requestContentType: JSON, body: "{}")

        assert response.status == 201
        String basketID = response.data.data.id
        String authenticationToken = response.headers."authentication-token"
        assert authenticationToken != null
        client.headers."authentication-token" = authenticationToken
        basketURI = basketsURI + "/" + basketID;
        itemsURI = basketURI + "/items"
        addressesURI = basketURI + ADDRESSES_RESOURCE
        return basketID
    }
    
    /**
     * Asserts the response for a BASKET_NOT_FOUND_ERROR
     * @param response the response of the request
     */
    def void assertBasketNotFound(def response)
    {
        assert response.status == SC_NOT_FOUND
        assert response.data.errors.find{it.code.contains(BASKET_NOT_FOUND_ERROR)} != null
    }
    
    /**
     * Creates an item json. 
     * @param lineItem the item of which to create the json from
     * @return the item json
     */
    def String getJSONLineItemEntry(LineItemV1 lineItem)
    {
        def builder = new groovy.json.JsonBuilder()
        builder{
            product lineItem.sku
            quantity{
                value lineItem.quantityValue
            }
            if (lineItem.getShippingMethod() != null)
                shippingMethod lineItem.getShippingMethod()
            if (lineItem.getShipToAddress() != null)
                shipToAddress lineItem.getShipToAddress()
        }
        return builder.toString()
    }
    
    /**
     * Creates an items json.
     * @param items the items of which to create the json from
     * @return the items json
     */
    def String getJSONLineItemsBody(LineItemV1[] items)
    {
        String body = "["
        items.each{ LineItemV1 lineItem -> body = body + getJSONLineItemEntry(lineItem) + ","}
        return body.substring(0, body.length() -1 ).concat("]")
    }

    /**
     * Adds several products to the basket.
     * @param products the products to be added to the basket
     */
    def void addLineItems(LineItemV1[] products)
    {
        def response = client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody(products))
    }
    
    /**
     * Adds a product to the basket. 
     * @param product the product to be added to the basket
     * @return item id of the new line item
     */
    def String addLineItem(LineItemV1 product)
    {
        def response = client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody(product))
        return response.data.data.find{it.product.equals(product.sku)}.id
    }

    /**
     * Clears the headers. 
     */
    def void clearHeaders()
    {
        client.headers.remove('Content-Type')
        client.headers.remove('Accept')
        client.clearAuthorizationHeader()
    }

    
    /**
     * Creates a basket address. 
     * @param usage the usage of the new address.
     * @return the newly created address
     */
    def AddressV1 createBasketAddress(Boolean[] usage)
    {
        AddressV1 address = new AddressV1(usage);
        def response = client.post(uri: addressesURI, requestContentType: JSON, body: getAddressEntry(address))
        address.setURN(response.data.data.urn)
        address.setID(response.data.data.id)
        return address
    }
    
    /**
     * Creates an address json.
     * @param address address of which to create the json from
     * @return the created json
     */
    def String getAddressEntry(AddressV1 address)
    {
        return toJSON{
            firstName address.getFirstName()
            lastName address.getLastName()
            addressLine1 address.getAddressLine1()
            postalCode address.getPostalCode()
            city address.getCity()
            mainDivision address.getMainDivision()
            countryCode address.getCountryCode()
            eligibleInvoiceToAddress address.getEligibleInvoiceToAddress()
            eligibleShipToAddress address.getEligibleShipToAddress()
            eligibleShipFromAddress address.getEligibleShipFromAddress()
            eligibleServiceToAddress address.getEligibleServiceToAddress()
            eligibleInstallToAddress address.getEligibleInstallToAddress()
        }.bodyString
    }
}