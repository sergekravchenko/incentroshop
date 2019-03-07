package rest.com.intershop.b2x.specs.storefront.basket.lineitem

import static rest.com.intershop.b2x.specs.storefront.common.RequestBuilder.toJSON

import java.time.*

import rest.com.intershop.b2x.specs.storefront.basket.AbstractBasketRestSpecV1
import rest.com.intershop.b2x.specs.storefront.common.JsonResponseValidator
import rest.com.intershop.b2x.specs.storefront.common.objects.AddressV1
import rest.com.intershop.b2x.specs.storefront.common.objects.LineItemV1
import spock.lang.*

@Narrative("""As a customer
I want to use the ICM Web Shop REST API
to handle lineitem operations.""")
@Title("Tests for lineitem REST resources (v1)")
class LineItemRestSpecV1 extends AbstractBasketRestSpecV1 {

    static final String ITEM_PRODUCT_NOT_FOUND_ERROR = "basket.line_item.product_not_found.error"
    static final String SHIPPING_METHOD_NOT_FOUND_ERROR = "shipping_method.not_found.error"
    static final String QUANTITY_VALUE_IS_NULL_ERROR = "basket.line_item.quantity_value_is_null.error"
    static final String WRONG_ADDRESS_TYPE_SHIP_TO_CAUSE = "address.wrong_address_type_no_ship_to_address.error"
    static final String ADDRESS_NOT_FOUND_CAUSE = "address.not_found.error"
    static final String ITEM_CREATED_INFO = "basket.line_item.created.info"
    static final String ITEM_MERGE_INFO = "basket.line_item.merged.info"
    static final String ITEM_DELETE_INFO = "basket.line_item.deletion.info"
    static final String ITEM_UPDATE_INFO = "basket.line_item.update.info"
    static final String ADJUSTED_QUANTITY_INFO = "basket.line_item.adjustment.cause.AddedToNewLineItemWithAdjustedQuantity"
    static final String ITEM_NOT_FOUND_ERROR = "basket.line_item.not_found.error"

    static final String PRODUCT_INCLUDE = "product"
    static final String SHIP_TO_ADDRESS_INCLUDE = "shipToAddress"
    static final String SHIPPING_METHOD_INCLUDE = "shippingMethod"
    static final String ATTRIBUTES_INCLUDE = "attributes"
    static final String ALL_INCLUDES = PRODUCT_INCLUDE + "," + SHIP_TO_ADDRESS_INCLUDE + "," + SHIPPING_METHOD_INCLUDE + "," + ATTRIBUTES_INCLUDE

    static final String DEFAULT_SHIPPING_METHOD = "STD_GROUND"
    static final String REQUESTED_QUANTITY = "1000000000"
    static final String EMPTY_ATTRIBUTES = ""

    @Shared
    String productSKU_One,productSKU_Two,variation1,variation2,otheruser

    def setupSpec()
    {
        user = getTestValue("rest.basket.user.login")
        otheruser = getTestValue("rest.basket.otheruser.login")
        password = getTestValue("rest.basket.user.password")

        String host = System.properties["hostName"]
        String port = System.properties["webserverPort"]
        String basketPath = getTestValue("rest.basket.path")

        productSKU_One = getTestValue("rest.basket.productId", 0);
        productSKU_Two = getTestValue("rest.basket.productId", 1);
        variation1 = getTestValue("rest.basket.product.variations", 0);
        variation2 = getTestValue("rest.basket.product.variations", 1);
        basketsURI = "http://${host}:${port}/${basketPath}"
    }

    def "Get line item list as anonymous user"()
    {
        given: "A basket with anonymous user"
            createBasket()

        when: "Get line item list from basket with unknown id"
            def response = client.get(uri: basketsURI + "/wrongBasketID/items", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Get empty line item list by anonymous user"
            response = client.get(uri: itemsURI, requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response)

        when: "Get line item list by anonymous user"
            addLineItems([new LineItemV1(productSKU_One, "1"),new LineItemV1(productSKU_Two, "1")] as LineItemV1[])
            response = client.get(uri: itemsURI, requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, [productSKU_One, productSKU_Two])

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetLineItemListResponseHandler()
    }

    def "Get line item list as registered user"()
    {
        given: "A basket with registered user"
            client.setAuthorizationHeader(user, password)
            createBasket()

        when: "Get line item list from basket with unknown id"
            def response = client.get(uri: basketsURI + "/wrongBasketID/items", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Get empty line item list by current user"
            response = client.get(uri: itemsURI, requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response)

        when: "Get line item list by current user"
            addLineItems([new LineItemV1(productSKU_One, "1"),new LineItemV1(productSKU_Two, "1")] as LineItemV1[])
            response = client.get(uri: itemsURI, requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, [productSKU_One, productSKU_Two])

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetLineItemListResponseHandler()
    }

    def "Get line item list with forbidden authorization"()
    {
        given: "A basket with registered user"
            client.setAuthorizationHeader(user, password)
            createBasket()

        when: "Get line item list with unknown customer"
            client.setAuthorizationHeader("blubber", "blubber")
            def response = client.get(uri: itemsURI, requestContentType: JSON)

        then: "Request failed => status code should be 401"
            responseValidator.assertStatusUnauthorized(response)

        when: "Get line item list by different user"
            client.setAuthorizationHeader(otheruser, password)
            response = client.get(uri: itemsURI, requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Get line item list without authentication token"
            client.clearAuthorizationHeader()
            client.headers.remove('authentication-token')
            response = client.get(uri: itemsURI, requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetLineItemListResponseHandler()
    }

    def "Get line item as anonymous user"()
    {
        given: "A basket with anonymous user"
            createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "Get line item from basket with unknown id"
            def response = client.get(uri: basketsURI + "/wrongBasketID/items", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Get line item with unknown id"
            response = client.get(uri: itemsURI + "/wrongItemID", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusNotFound(response, ITEM_NOT_FOUND_ERROR)

        when: "Get line item by anonymous user"
            response = client.get(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, lineItem)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetLineItemResponseHandler()
    }

    def "Get line item as registered user"()
    {
        given: "A basket with registered user"
            client.setAuthorizationHeader(user, password)
            createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "Get line item from basket with unknown id"
            def response = client.get(uri: basketsURI + "/wrongBasketID/items", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Get line item with unknown id"
            response = client.get(uri: itemsURI + "/wrongItemID", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusNotFound(response, ITEM_NOT_FOUND_ERROR)

        when: "Get line item by registered user"
            response = client.get(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, lineItem)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetLineItemResponseHandler()
    }
    
    def "Get line item with forbidden authorization"()
    {
        given: "A basket with registered user"
            client.setAuthorizationHeader(user, password)
            createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "Get line item without authorization"
            client.clearAuthorizationHeader()
            client.headers.remove('authentication-token')
            def response = client.get(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Get line item with unknown customer"
            client.setAuthorizationHeader("blubber", "blubber")
            response = client.get(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON)

        then: "Request failed => status code should be 401"
            defaultResponseValidator.assertStatusUnauthorized(response)

        when: "Get line item by different user"
            client.setAuthorizationHeader(otheruser, password)
            response = client.get(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Get line item without authentication token"
            client.clearAuthorizationHeader()
            client.headers.remove('authentication-token')
            response = client.get(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        cleanup:
            clearHeaders()

        where:
            defaultResponseValidator << new JsonResponseValidator()
    }

    def "Get line item with include data"()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "Send \"GET /baskets/<basket-id>/items/<item-id>?include=product\" request"
            AddressV1 address = createBasketAddress()
            client.patch(uri: "${basketURI}", requestContentType: JSON, body: toJSON {commonShipToAddress(address.urn)}.bodyString)
            def response = client.get(uri: itemsURI + "/" + lineItem.getID() + INCLUDE_PREFIX + ALL_INCLUDES, 
                requestContentType: JSON)

        then: "Request is successful and basket is returned"
            responseValidator.assertStatusOk(response, 
                [ (PRODUCT_INCLUDE) : lineItem, (ATTRIBUTES_INCLUDE) : EMPTY_ATTRIBUTES,
                  (SHIPPING_METHOD_INCLUDE) : DEFAULT_SHIPPING_METHOD, (SHIP_TO_ADDRESS_INCLUDE) : address])

        cleanup:
            clearHeaders()

        where:
            responseValidator << new ResponseWithIncludesHandler()
    }

    def "Add line item as anonymous user"()
    {
        given: "A basket with anonymous user"
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "10")
            createBasket()

        when: "Add line item to basket with unknown id"
            def response = client.post(uri: basketsURI + "/wrongBasketID/items", requestContentType: JSON, body: getJSONLineItemsBody(lineItem))

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Add line item by anonymous user"
            response =  client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody(lineItem))

        then: "Request success => status code should be 201"
            responseValidator.assertStatusCreated(response, lineItem)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new AddLineItemResponseHandler()
    }

    def "Add line item and get response with includes"()
    {
        given: "A basket with anonymous user"
            createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")

        when: "Add line item with request parameter 'include'"
            def response =  client.post(uri: itemsURI + INCLUDE_PREFIX + ALL_INCLUDES, 
                requestContentType: JSON, body: getJSONLineItemsBody(lineItem))

        then: "Request success => status code should be 201 and response should contain 'include' section"
            responseValidator.assertStatusCreated(response, 
                [ (PRODUCT_INCLUDE) : lineItem, (SHIPPING_METHOD_INCLUDE) : DEFAULT_SHIPPING_METHOD, 
                  (SHIP_TO_ADDRESS_INCLUDE) : null, (ATTRIBUTES_INCLUDE) : EMPTY_ATTRIBUTES])

        cleanup:
            clearHeaders()

        where:
            responseValidator << new ResponseWithIncludesHandler()
    }
    
    def "Add line item as registered user"()
    {
        given: "A basket with registered user"
            client.setAuthorizationHeader(user, password)
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "10")
            createBasket()

        when: "Add line item to basket with unknown id"
            def response = client.post(uri: basketsURI + "/wrongBasketID/items", requestContentType: JSON, body: getJSONLineItemsBody(lineItem))

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Add line item by registered user"

            response =  client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody(lineItem))

        then: "Request success => status code should be 201"
            responseValidator.assertStatusCreated(response, lineItem)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new AddLineItemResponseHandler()
    }

    def "Add line item with forbidden authorization"()
    {
        given: "A basket with registered user"
            client.setAuthorizationHeader(user, password)
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "10")
            createBasket()

        when: "Add line item list with unknown customer"
            client.setAuthorizationHeader("blubber", "blubber")
            def response = client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody(lineItem))

        then: "Request failed => status code should be 401"
            responseValidator.assertStatusUnauthorized(response)

        when: "Add line item list by different user"
            client.setAuthorizationHeader(otheruser, password)
            response = client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody(lineItem))

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Add line item list without authentication token"
            client.clearAuthorizationHeader()
            client.headers.remove('authentication-token')
            response = client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody(lineItem))

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new AddLineItemResponseHandler()
    }

    def "Add line item with invalid quantity"()
    {
        given: "A basket with anonymous user"
            LineItemV1 lineItem= new LineItemV1(productSKU_One, null)
            createBasket()

        when: "Add line item with quantity value null"
            def response =  client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody(lineItem))
            lineItem.quantityValue = 1

        then: "Request success => status code should be 201"
            responseValidator.assertStatusCreated(response, lineItem)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new AddLineItemResponseHandler()
    }

    def "Add line item twice with merge quantities default setting"()
    {
        given: "A basket with anonymous user"
            createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "Add line item by anonymous user"
            def response =  client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody(lineItem))
            lineItem.quantityValue = 2

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, lineItem)
            assert response.data.infos.find{it.code.contains(ITEM_MERGE_INFO)} != null

        cleanup:
            clearHeaders()

        where:
            responseValidator << new AddLineItemResponseHandler()
    }

    def "Add invalid line item to basket"()
    {
        given: "A basket with anonymous user"
            createBasket()

        when: "Add line item by anonymous user"
            def response =  client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody([new LineItemV1("invalidProduct", "1")] as LineItemV1[]))

        then: "Request failed => status code should be 422"
            responseValidator.assertStatusUnprocessable(response, ITEM_PRODUCT_NOT_FOUND_ERROR)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new AddLineItemResponseHandler()
    }

    def "Add line item to basket with adjustments"()
    {
        given: "A basket with anonymous user"
            LineItemV1 lineItem = new LineItemV1(productSKU_One, REQUESTED_QUANTITY)
            createBasket()

        when: "Add line item by anonymous user"
            def response =  client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody([new LineItemV1(productSKU_One, REQUESTED_QUANTITY)] as LineItemV1[]))
            lineItem.quantityValue=100

        then: "Request success => status code should be 201"
            responseValidator.assertStatusCreated(response, lineItem)
            assert response.data.infos.causes.find{it.code.contains(ADJUSTED_QUANTITY_INFO)} != null
            assert response.data.infos.find{ it.causes.parameters.requested.contains(REQUESTED_QUANTITY)} != null

        cleanup:
            clearHeaders()

        where:
            responseValidator << new AddLineItemResponseHandler()
    }

    def "Add multiple line items to basket"()
    {
        given: "A basket with anonymous user"
            createBasket()

        when: "Add line item by anonymous user"
            def lineItems = [new LineItemV1(productSKU_One, "1"), new LineItemV1(productSKU_Two, REQUESTED_QUANTITY), new LineItemV1("invalidProduct","1")] as LineItemV1[]
            def response =  client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody(lineItems))

        then: "Request success => status code should be 207"
            responseValidator.assertStatusMultiStatus(response, ITEM_CREATED_INFO)
            assert response.data.infos.causes.find{it.code.contains(ADJUSTED_QUANTITY_INFO)} != null
            assert response.data.infos.find{ it.causes.parameters.requested.contains(REQUESTED_QUANTITY)} != null

        cleanup:
            clearHeaders()

        where:
            responseValidator << new AddLineItemResponseHandler()
    }

    def "Delete line item as as anonymous user"()
    {
        given: "A basket with anonymous user"
            createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "Delete line item with wrong basket id"
            def response = client.delete(uri: basketsURI + "/wrongBasketID/items/" + lineItem.getID(), requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Delete line item with wrong basket id"
            response = client.delete(uri: itemsURI + "/wrongLineItemID", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusNotFound(response, ITEM_NOT_FOUND_ERROR)

        when: "Delete line item with"
            response = client.delete(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ITEM_DELETE_INFO)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new DeleteLineItemResponseHandler()
    }

    def "Delete line item as as registered user"()
    {
        given: "A basket with registered user"
            client.setAuthorizationHeader(user, password)
            createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "Delete line item with wrong basket id"
            def response = client.delete(uri: basketsURI + "/wrongBasketID/items/" + lineItem.getID(), requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Delete line item with wrong basket id"
            response = client.delete(uri: itemsURI + "/wrongLineItemID", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusNotFound(response, ITEM_NOT_FOUND_ERROR)

        when: "Delete line item"
            response = client.delete(uri: itemsURI + "/" +  lineItem.getID(), requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ITEM_DELETE_INFO)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new DeleteLineItemResponseHandler()
    }

    def "Delete line item with forbidden authorization"()
    {
        given: "A basket with registered user"
            client.setAuthorizationHeader(user, password)
            createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "Delete line item list with unknown customer"
            client.setAuthorizationHeader("blubber", "blubber")
            def response = client.delete(uri: itemsURI + "/" +  lineItem.getID(), requestContentType: JSON)

        then: "Request failed => status code should be 401"
            responseValidator.assertStatusUnauthorized(response)

        when: "Delete line item list by different user"
            client.setAuthorizationHeader(otheruser, password)
            client.headers.remove('authentication-token')
            response = client.delete(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Delete line item without authentication token"
            client.clearAuthorizationHeader()
            client.headers.remove('authentication-token')
            response = client.delete(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new JsonResponseValidator()
    }

    def "Update ship to address of a line item"()
    {
        given: "A basket with anonymous user"
            createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))
            AddressV1 address = createBasketAddress()
            AddressV1 invalidAddress = createBasketAddress([true, false] as Boolean[])

        when: "add address with invalid urn"
            def requestData = toJSON {shipToAddress("invalidURN")}
            def response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: requestData.bodyString)

        then: "Request failed(422) but no changes occurred"
            responseValidator.assertStatusUnprocessable(response, ADDRESS_NOT_FOUND_CAUSE)

        when: "add address with invalid usage"
            requestData = toJSON {shipToAddress(invalidAddress.getURN())}
            response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: requestData.bodyString)

        then: "Request failed(422) but no changes occurred"
            responseValidator.assertStatusUnprocessable(response, WRONG_ADDRESS_TYPE_SHIP_TO_CAUSE)

        when: "add address with valid data"
            requestData = toJSON {shipToAddress(address.getURN())}
            response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: requestData.bodyString)

        then: "Request succesful(200) => address added"
            responseValidator.assertStatusOk(response, ["shipToAddress", address.getURN()])

        cleanup:
            clearHeaders()

        where:
            responseValidator << new UpdateLineItemResponseHandler()
    }
    
    def "Update a line item and get response with includes"()
    {
        given: "A basket with anonymous user"
            createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "update line item"
            def response = client.patch(uri: itemsURI + "/" + lineItem.getID() + INCLUDE_PREFIX + ALL_INCLUDES, requestContentType: JSON, body: "{}")

        then: "Request succesful(200) => address updated and includes section should be present"
            responseValidator.assertStatusOk(response,
                [ (PRODUCT_INCLUDE) : lineItem, (ATTRIBUTES_INCLUDE) : EMPTY_ATTRIBUTES,
                  (SHIPPING_METHOD_INCLUDE) : DEFAULT_SHIPPING_METHOD, (SHIP_TO_ADDRESS_INCLUDE) : null])

        cleanup:
            clearHeaders()

        where:
            responseValidator << new ResponseWithIncludesHandler()
    }

    def "Update shipping method of a line item"()
    {
        given: "A basket with anonymous user"
            createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "Update shipping method of a line item"
            def response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: getShippingMethodEntry("STD_5DAY"))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ["shippingMethod", "STD_5DAY"])

        when: "Update shipping method of a line item with invalid shipping method"
            response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: getShippingMethodEntry("Blubber"))

        then: "Request failed => status code should be 422"
            responseValidator.assertStatusUnprocessable(response, SHIPPING_METHOD_NOT_FOUND_ERROR)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new UpdateLineItemResponseHandler()
    }

    def "Update quantity of a line item"()
    {
        given: "A basket with anonymous user"
            def newQuantity = "20"
            createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "Update quantity of a line item => value parameter null"
            def response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: getQuantityEntry(null))

        then: "Request failed => status code should be 422"
            responseValidator.assertStatusUnprocessable(response, QUANTITY_VALUE_IS_NULL_ERROR)

        when: "Update quantity of a line item => value parameter is empty"
            response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: getQuantityEntry(""))

        then: "Request failed => status code should be 422"
            responseValidator.assertStatusUnprocessable(response, QUANTITY_VALUE_IS_NULL_ERROR)

        when: "Update quantity of a line item"
            response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: getQuantityEntry(newQuantity))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ["quantity", newQuantity])

        when: "Update quantity of a line item with adjustments"
            response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: getQuantityEntry(REQUESTED_QUANTITY))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ["quantity", 100])

        when: "Update quantity of a line item with negative quantity value"
            response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: getQuantityEntry("-1"))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response)
            assert response.data.infos.code.contains(ITEM_UPDATE_INFO)

        when: "Update quantity of a not existing line item"
            response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: getQuantityEntry("10"))

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusNotFound(response, ITEM_NOT_FOUND_ERROR)

        when: "Update quantity of a line item with zero quantity value"
            lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))
            response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: getQuantityEntry("0"))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response)
            assert response.data.infos.causes.find{it.code.contains(ITEM_DELETE_INFO)} != null

        cleanup:
            clearHeaders()

        where:
            responseValidator << new UpdateLineItemResponseHandler()
    }

    def "Update variation of a line item"() {

        given: "A basket with anonymous user"
            createBasket()
            LineItemV1 lineItem = new LineItemV1(variation1, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "Update variation of a line item with invalid id"
            def response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: getProductEntry("invalid_ID"))

        then: "Request failed => status code should be 422"
            responseValidator.assertStatusUnprocessable(response, ITEM_PRODUCT_NOT_FOUND_ERROR)

        when: "Update variation of a line item"
            response = client.patch(uri: itemsURI + "/" + lineItem.getID(), requestContentType: JSON, body: getProductEntry(variation2))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ["variation", variation2])

        cleanup:
            clearHeaders()

        where:
            responseValidator << new UpdateLineItemResponseHandler()
    }

    def String getProductEntry(String sku)
    {
        return toJSON{ product sku }.bodyString
    }

    def String getQuantityEntry(def quantityValue)
    {
        def json = toJSON {
            quantity { value quantityValue }
        }

        return json.bodyString
    }

    def String getShippingMethodEntry(String name)
    {
        return toJSON{ shippingMethod name }.bodyString
    }
}

/**
 *  VALIDATION CLASS IMPLEMENTATIONS
 */
class GetLineItemListResponseHandler extends JsonResponseValidator
{
    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        def data = responseData.data
        for(String product_sku : expectedData)
        {
            assert data.find{ it.product.contains(product_sku)} != null
        }
    }
}

class GetLineItemResponseHandler extends JsonResponseValidator
{
    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        def data = responseData.data
        assert data.id == expectedData.id
        assert data.product == expectedData.sku
        assert data.quantity.value == Integer.valueOf(expectedData.quantityValue)
    }
}

class AddLineItemResponseHandler extends JsonResponseValidator
{
    static final String ITEM_ADD_ERROR = "basket.line_item.add.error"

    @Override
    void assertStatusUnprocessable(def response, def expectedData = null)
    {
        hasStatusUnprocessable(response)
        hasErrorCode(response.data, ITEM_ADD_ERROR)
        hasErrorCause(response.data, expectedData)
    }

    void assertStatusMultiStatus(def response, def expectedData)
    {
        assert response.success == true
        assert response.status == 207
        hasErrorCode(response.data, ITEM_ADD_ERROR)
        hasInfoCode(response.data, expectedData)
    }

    void assertStatusCreated(def response, def expectedData = null)
    {
        hasStatusCreated(response)
        hasNoErrors(response.data)

        checkResponseData(response.data, expectedData)
    }

    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        def data = responseData.data
        assert data.id != null
        assert data.product.contains(expectedData.sku)
        assert data.quantity.value.contains(Integer.valueOf(expectedData.quantityValue))
    }
}

class ResponseWithIncludesHandler extends JsonResponseValidator
{
    void assertStatusCreated(def response, def expectedIncludes = null)
    {
        hasStatusCreated(response)
        hasNoErrors(response.data)

        checkResponseData(response.data, expectedIncludes)
    }
    
    @Override
    void checkResponseData(def responseData, def expectedIncludes = null)
    {
        hasData(responseData)
        hasIncludes(responseData)
        def actualIncludes = responseData.included
        
        if(expectedIncludes.containsKey(LineItemRestSpecV1.PRODUCT_INCLUDE))
        {
            def expectedProduct = expectedIncludes[LineItemRestSpecV1.PRODUCT_INCLUDE]
            def product = actualIncludes.product.get(expectedProduct.sku)
            assert product != null
            assert product.sku.equals(expectedProduct.sku)
        }
        if(expectedIncludes.containsKey(LineItemRestSpecV1.SHIPPING_METHOD_INCLUDE))
        {
            def expectedShippingMethod = expectedIncludes[LineItemRestSpecV1.SHIPPING_METHOD_INCLUDE]
            def shippingMethod = actualIncludes.shippingMethod.get(expectedShippingMethod)
            assert shippingMethod !=null
            assert shippingMethod.id.equals(expectedShippingMethod)
            assert shippingMethod.name !=null
            assert shippingMethod.description !=null
        }
        if(expectedIncludes.containsKey(LineItemRestSpecV1.SHIP_TO_ADDRESS_INCLUDE))
        {
            AddressV1 expectedAddress = expectedIncludes[LineItemRestSpecV1.SHIP_TO_ADDRESS_INCLUDE]

            if (expectedAddress != null)
            {
                assert actualIncludes.shipToAddress.size() == 1
                def address = actualIncludes.shipToAddress.get(expectedAddress.getURN())

                assert address.firstName.equals(expectedAddress.getFirstName())
                assert address.lastName.equals(expectedAddress.getLastName())
                assert address.addressLine1.equals(expectedAddress.getAddressLine1())
                assert address.postalCode.equals(expectedAddress.getPostalCode())
                assert address.city.equals(expectedAddress.getCity())
                assert address.countryCode.equals(expectedAddress.getCountryCode())
            }
            else
            {
                assert actualIncludes.shipToAddress.size() == 0
            }
        }
    }
}

class DeleteLineItemResponseHandler extends JsonResponseValidator
{
    @Override
    void assertStatusOk(def response, def expectedData = null)
    {
        hasStatusOk(response)
        assert response.data.infos.find{it.code.contains(expectedData)} != null
    }
}

class UpdateLineItemResponseHandler extends JsonResponseValidator
{
    static final String ITEM_UPDATE_ERROR = "basket.line_item.update.error"

    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        if(expectedData != null) {
            if (expectedData[0] == "shippingMethod") {
                assert responseData.data.shippingMethod.contains(expectedData[1])
            }
            if (expectedData[0] == "quantity") {
                assert responseData.data.quantity.value == Integer.valueOf(expectedData[1])
            }
            if(expectedData[0] == "variation") {
                assert responseData.data.product.contains(expectedData[1])
            }
            if(expectedData[0] == "shipToAddress")
            {
                assert responseData.data.shipToAddress.contains(expectedData[1])
            }
        }
    }

    @Override
    void assertStatusUnprocessable(def response, def expectedData = null)
    {
        hasStatusUnprocessable(response)
        hasErrorCode(response.data, ITEM_UPDATE_ERROR)
        hasErrorCause(response.data, expectedData)
    }
}