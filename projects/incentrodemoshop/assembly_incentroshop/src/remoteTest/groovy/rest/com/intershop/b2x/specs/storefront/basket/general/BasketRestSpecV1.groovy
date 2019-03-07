package rest.com.intershop.b2x.specs.storefront.basket.general

import static rest.com.intershop.b2x.specs.storefront.common.RequestBuilder.toJSON

import java.time.*

import rest.com.intershop.b2x.specs.storefront.basket.AbstractBasketRestSpecV1
import rest.com.intershop.b2x.specs.storefront.common.JsonResponseValidator
import rest.com.intershop.b2x.specs.storefront.common.objects.AddressV1
import rest.com.intershop.b2x.specs.storefront.common.objects.LineItemV1
import spock.lang.*

@Narrative("""As a customer
I want to use the ICM Web Shop REST API
to handle basket operations.""")
@Title("Tests for basket REST resources (v1)")
class BasketRestSpecV1 extends AbstractBasketRestSpecV1 {

    static final String LINE_ITEM_INCLUDE = "lineItems"
    static final String DISCOUNTS_INCLUDE = "discounts"
    static final String PAYMENTS_INCLUDE = "payments"
    static final String ATTRIBUTES_INCLUDE = "attributes"
    static final String COMMON_SHIPPING_METHOD_INCLUDE = "commonShippingMethod"
    static final String BUCKETS_INCLUDE = "buckets"
    static final String INVOICE_TO_ADDRESS_INCLUDE = "invoiceToAddress"
    static final String COMMON_SHIP_TO_ADDRESS_INCLUDE = "commonShipToAddress"

    static final String ALL_INCLUDES = LINE_ITEM_INCLUDE + "," + COMMON_SHIPPING_METHOD_INCLUDE + "," + 
        DISCOUNTS_INCLUDE + "," + BUCKETS_INCLUDE + "," + INVOICE_TO_ADDRESS_INCLUDE + "," + 
        COMMON_SHIP_TO_ADDRESS_INCLUDE + "," + PAYMENTS_INCLUDE + "," + ATTRIBUTES_INCLUDE
             
    static final String ELIGIBLE_ADDRESSES_SUBRESOURCE = "/eligible-addresses"
    static final String CURRENT = "current"
    static final String BASKET_UPDATE_ERROR = "basket.update.error"
    static final String WRONG_ADDRESS_TYPE_INVOICE_CAUSE = "address.wrong_address_type_no_invoice_to_address.error"
    static final String WRONG_ADDRESS_TYPE_SHIP_TO_CAUSE = "address.wrong_address_type_no_ship_to_address.error"
    static final String ADDRESS_NOT_FOUND_CAUSE = "address.not_found.error"
    static final String BASKET_NOT_FOUND_ERROR = "basket.not_found.error"
    static final String BASKET_DELETION_INFO = "basket.deletion.info"
    static final String SHIPPING_METHOD_NOT_FOUND_ERROR = "shipping_method.not_found.error"
    static final String EX_NUMBER = "ex_number"
    static final String BASKET_RECURRENCE_MISSING_START_DATE = "basket.recurrence_missing_start_date.error"
    static final String DEFAULT_SHIPPING_METHOD = "STD_GROUND"
    static final String NEW_SHIPPING_METHOD = "STD_5DAY"

    static final String OPEN_TENDER_SUBRESOURCE = "/payments/open-tender"
    static final String DEFAULT_PAYMENT_METHOD = "ISH_CASH_ON_DELIVERY"

    @Shared
    String productSKU_One

    def setupSpec()
    {
        setup:

        user = getTestValue("rest.basket.user.login")
        password = getTestValue("rest.basket.user.password")
        productSKU_One = getTestValue("rest.basket.productId")
        String sitePath = getTestValue("rest.base.uri")
        basketsURI = "${sitePath}/baskets"
    }

    def "Create empty basket as anonymous user"()
    {
        when: "Send \"POST /baskets\" request with empty initial basket data"
            def response = client.post(uri: basketsURI, requestContentType: JSON, body: "{}")

        then: "basket is created and response with link to the basket is returned"
            responseValidator.assertStatusCreated(response, basketsURI)

        when: "Send \"POST /baskets\" request with empty initial basket data (request includes)"
            response = client.post(uri: basketsURI + INCLUDE_PREFIX + ALL_INCLUDES, requestContentType: JSON, body: "{}")

        then: "basket is created and response with link to the basket is returned, (empty) includes are returned"
            includeValidator.assertStatusCreated( response, [ (LINE_ITEM_INCLUDE) : null, 
                (COMMON_SHIPPING_METHOD_INCLUDE) : null,
                (BUCKETS_INCLUDE) : null,
                (INVOICE_TO_ADDRESS_INCLUDE) : null,
                (COMMON_SHIP_TO_ADDRESS_INCLUDE) : null,
                (DISCOUNTS_INCLUDE) : null,
                (PAYMENTS_INCLUDE) : null,
                (ATTRIBUTES_INCLUDE) : null ] )

        cleanup:
            clearHeaders()

        where:
            responseValidator << new JsonResponseValidator()
            includeValidator << new ResponseWithIncludesHandler()
    }

    def "Create empty basket as registered user"()
    {
        given: "An registered user"
            client.setAuthorizationHeader(user, password)

        when: "Send \"POST /baskets\" request with empty initial basket data"
            def response = client.post(uri: basketsURI, requestContentType: JSON, body: "{}")

        then: "basket is created and response with link to the basket is returned"
            responseValidator.assertStatusCreated(response, basketsURI)

        when: "Send \"POST /baskets\" request with empty initial basket data (request includes)"
            response = client.post(uri: basketsURI + INCLUDE_PREFIX + ALL_INCLUDES, requestContentType: JSON, body: "{}")

        then: "basket is created and response with link to the basket is returned, (empty) includes are returned"
            includeValidator.assertStatusCreated( response, [ (LINE_ITEM_INCLUDE) : null,
                (COMMON_SHIPPING_METHOD_INCLUDE) : null,
                (BUCKETS_INCLUDE) : null,
                //(INVOICE_TO_ADDRESS_INCLUDE) : null, // invoice-to-address is not null for registered user
                (COMMON_SHIP_TO_ADDRESS_INCLUDE) : null,
                (DISCOUNTS_INCLUDE) : null,
                (PAYMENTS_INCLUDE) : null,
                (ATTRIBUTES_INCLUDE) : null ] )

        cleanup:
            clearHeaders()

        where:
            responseValidator << new JsonResponseValidator()
            includeValidator << new ResponseWithIncludesHandler()
    }

    def "Create empty basket as registered user with wrong credentials"()
    {
        given: "An registered user with wrong credentials"
            client.clearAuthorizationHeader()
            client.setAuthorizationHeader(user, "wrongPassword")

        when: "Send \"POST /baskets\" request with empty initial basket data"
            def response = client.post(uri: basketsURI, requestContentType: JSON, body: "{}")

        then: "Request failed"
            responseValidator.assertStatusUnauthorized(response)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new JsonResponseValidator()
    }

    def "Get basket list as anonymous user"()
    {
        given: "An anonymous user with two baskets"
            def expectedData = toJSON {
                basketID1(createBasket())
                basketID2(createBasket())
            }

        when: "Send \"GET /baskets\" request"
            def response = client.get(uri: basketsURI, requestContentType: JSON)

        then: "Basket list is successful retrieved"
            responseValidator.assertStatusOk(response, expectedData.body)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetBasketListResponseHandler()
    }

    def "Get basket list as registered user"()
    {
        given: "A registered user with two baskets"
            client.setAuthorizationHeader(user, password)
            def expectedData = toJSON {
                basketID1(createBasket())
                basketID2(createBasket())
            }

        when: "Send \"GET /baskets\" request"
            def response = client.get(uri: basketsURI, requestContentType: JSON)

        then: "Basket list is successful retrieved"
            responseValidator.assertStatusOk(response, expectedData.body)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetBasketListResponseHandler()
    }

    def "Get basket list as registered user with wrong credentials"()
    {
        given: "A registered user with two baskets"
            client.setAuthorizationHeader(user, password)
            createBasket()
            createBasket()

        when: "Send \"GET /baskets\" request with wrong credentials"
            client.headers.remove("authentication-token")
            client.setAuthorizationHeader(user, "wrongPassword")
            def response = client.get(uri: basketsURI, requestContentType: JSON)

        then: "Request failed"
            responseValidator.assertStatusUnauthorized(response)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetBasketListResponseHandler()
    }

    def "Get basket as anonymous user"()
    {
        given: "An anonymous user with a basket"
            def expectedData = toJSON { basketID(createBasket()) }

        when: "Send \"GET /baskets/<basket-id>\" request"
            def response = client.get(uri: basketURI, requestContentType: JSON)

        then: "Request is successful and basket is returned"
            responseValidator.assertStatusOk(response, expectedData.body)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetBasketItemResponseHandler()
    }

    def "Get current basket as anonymous user"()
    {
        given: "An anonymous user with two baskets"
            createBasket()
            def expectedData = toJSON { basketID(createBasket()) }

        when: "Send \"GET /baskets/current\" request"
            def response = client.get(uri: "${basketsURI}/${CURRENT}", requestContentType: JSON)

        then: "Request is successful and current basket is returned"
            responseValidator.assertStatusOk(response, expectedData.body)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetBasketItemResponseHandler()
    }

    def "Get basket as registered user"()
    {
        given: "A registered user with a basket"
            client.setAuthorizationHeader(user, password)
            def expectedData = toJSON { basketID(createBasket()) }

        when: "Send \"GET /baskets/<basket-id>\" request"
            def response = client.get(uri: "${basketsURI}/${expectedData.body.basketID}", requestContentType: JSON)

        then: "Request is successful and basket is returned"
            responseValidator.assertStatusOk(response, expectedData.body)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetBasketItemResponseHandler()
    }

    def "Get current basket as registered user"()
    {
        given: "A registered user with two baskets"
            client.setAuthorizationHeader(user, password)
            createBasket()
            def expectedData = toJSON { basketID(createBasket()) }

        when: "Send \"GET /baskets/current\" request"
            def response = client.get(uri: "${basketsURI}/${CURRENT}", requestContentType: JSON)

        then: "Request is successful and current basket is returned"
            responseValidator.assertStatusOk(response, expectedData.body)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetBasketItemResponseHandler()
    }

    def "Get basket as registered user with wrong credentials"()
    {
        given: "A registered user with a basket"
            String basketID = createBasket()

        when: "Send \"GET /baskets/<basket-id>\" request with wrong credentials"
            client.headers.remove("authentication-token")
            client.setAuthorizationHeader(user, "wrongPassword")
            def response = client.get(uri: basketURI, requestContentType: JSON)

        then: "Request failed"
            responseValidator.assertStatusUnauthorized(response)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetBasketItemResponseHandler()
    }

    def "Get basket from another user"()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()

        when: "Send \"GET /baskets/<basket-id>\" request as another user"
            client.headers.remove("authentication-token")
            def response = client.get(uri: basketURI, requestContentType: JSON)

        then: "Request failed and an error list is returned which contains an error 404"
            responseValidator.assertStatusNotFound(response, BASKET_NOT_FOUND_ERROR)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetBasketItemResponseHandler()
    }

    def "Get basket by non-existing ID"()
    {
        when: "Send \"GET /baskets/<basket-id>\" request with non-existing ID"
            def response = client.get(uri: "${basketsURI}/nonExistingBasket", requestContentType: JSON)

        then: "Request failed and an error list is returned which contains an error 404"
            responseValidator.assertStatusNotFound(response,BASKET_NOT_FOUND_ERROR)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetBasketItemResponseHandler()
    }

    def "Get current (non-existing) basket"()
    {
        when: "Send \"GET /baskets/current\" request without an existing basket"
            def response = client.get(uri: "${basketsURI}/${CURRENT}", requestContentType: JSON)

        then: "Request failed and an error list is returned which contains an error 404"
            responseValidator.assertStatusNotFound(response, BASKET_NOT_FOUND_ERROR)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetBasketItemResponseHandler()
    }

    def "Get Basket with include data"()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "Send GET request to baskets/<basket-id> with multiple includes"
            AddressV1 address = createBasketAddress()
            client.patch(uri: "${basketURI}", requestContentType: JSON, body: toJSON {invoiceToAddress(address.getURN())}.bodyString)
            client.patch(uri: "${basketURI}", requestContentType: JSON, body: toJSON {commonShipToAddress(address.urn)}.bodyString)
            def response = client.get(uri: basketURI + INCLUDE_PREFIX + ALL_INCLUDES, requestContentType: JSON)

        then: "Request is successful and basket is returned with includes"
            responseValidator.assertStatusOk(response, [ "basketID" : basketID ])
            includeValidator.assertStatusOk( response, [ "basketID" : basketID,
                (LINE_ITEM_INCLUDE) : lineItem, 
                (COMMON_SHIPPING_METHOD_INCLUDE) : DEFAULT_SHIPPING_METHOD,
                (BUCKETS_INCLUDE) : lineItem,
                (INVOICE_TO_ADDRESS_INCLUDE) : address,
                (COMMON_SHIP_TO_ADDRESS_INCLUDE) : address,
                (DISCOUNTS_INCLUDE) : null,
                (PAYMENTS_INCLUDE) : null,
                (ATTRIBUTES_INCLUDE) : null ] )

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetBasketItemResponseHandler()
            includeValidator << new ResponseWithIncludesHandler()
    }

    def "Delete basket as anonymous user"()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()

        when: "Send \"DELETE /baskets/<basket-id>\" request"
            def response = client.delete(uri: basketURI, requestContentType: JSON)

        then: "Request is successful with status 200 and the basket cannot be accessed anymore"
            responseValidator.assertStatusOk(response, BASKET_DELETION_INFO)
            !isBasketExisting(basketID)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new DeleteBasketResponseHandler()
    }

    def "Delete current basket as anonymous user"()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()

        when: "Send \"DELETE /baskets/<basket-id>\" request"
            def response = client.delete(uri: "${basketsURI}/${CURRENT}", requestContentType: JSON)

        then: "Request is successful with status 200 and the basket cannot be accessed anymore"
            responseValidator.assertStatusOk(response, BASKET_DELETION_INFO)
            !isBasketExisting(basketID)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new DeleteBasketResponseHandler()
    }

    def "Delete basket as registered user"()
    {
        given: "An anonymous user with a basket"
            client.setAuthorizationHeader(user, password)
            String basketID = createBasket()

        when: "Send \"DELETE /baskets/<basket-id>\" request"
            def response = client.delete(uri: basketURI, requestContentType: JSON)

        then: "Request is successful with status 200 and the basket cannot be accessed anymore"
            responseValidator.assertStatusOk(response, BASKET_DELETION_INFO)
            !isBasketExisting(basketID)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new DeleteBasketResponseHandler()
    }

    def "Delete current basket as registered user"()
    {
        given: "An anonymous user with a basket"
            client.setAuthorizationHeader(user, password)
            String basketID = createBasket()

        when: "Send \"DELETE /baskets/<basket-id>\" request"
            def response = client.delete(uri: "${basketsURI}/${CURRENT}", requestContentType: JSON)

        then: "Request is successful with status 200 and the basket cannot be accessed anymore"
            responseValidator.assertStatusOk(response, BASKET_DELETION_INFO)
            !isBasketExisting(basketID)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new DeleteBasketResponseHandler()

    }

    def "Delete basket as registered user with wrong credentials"()
    {
        given: "An anonymous user with a basket"
            client.setAuthorizationHeader(user, password)
            String basketID = createBasket()
            String authenticationToken = client.headers."authentication-token"

        when: "Send \"DELETE /baskets/<basket-id>\" request with wrong credentials"
            client.headers.remove("authentication-token")
            client.setAuthorizationHeader(user, "wrongPassword")
            def response = client.delete(uri: basketURI, requestContentType: JSON)

        then: "Request failed and basket is still accessible by original user"
            responseValidator.assertStatusUnauthorized(response)
            isBasketExisting(basketID, authenticationToken)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new DeleteBasketResponseHandler()
    }

    def "Delete basket from another user"()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()
            String authenticationToken = client.headers."authentication-token"

        when: "Send \"DELETE /baskets/<basket-id>\" request as another user"
            client.headers.remove("authentication-token")
            def response = client.delete(uri: basketURI, requestContentType: JSON)

        then: "Request failed, an error list is returned and the basket is still accessible by original user"
            responseValidator.assertStatusNotFound(response, BASKET_NOT_FOUND_ERROR)
            isBasketExisting(basketID, authenticationToken)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new DeleteBasketResponseHandler()
    }

    def "Delete basket by non-existing ID"()
    {
        when: "Send \"DELETE /baskets/<basket-id>\" request with non-existing ID"
            def response = client.delete(uri: "${basketsURI}/nonExistingBasket", requestContentType: JSON)

        then: "Request failed and an error list is returned which contains an error 404"
            responseValidator.assertStatusNotFound(response, BASKET_NOT_FOUND_ERROR)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new DeleteBasketResponseHandler()
    }

    def "Delete current (non-existing) basket"()
    {
        when: "Send \"DELETE /baskets/<basket-id>\" request without an existing basket"
            def response = client.delete(uri: "${basketsURI}/${CURRENT}", requestContentType: JSON)

        then: "Request failed and an error list is returned which contains an error 404"
            responseValidator.assertStatusNotFound(response, BASKET_NOT_FOUND_ERROR)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new DeleteBasketResponseHandler()
    }

    def "Add/Update invoice address to basket"()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()

        when: "add address with invalid urn"
            def requestData = toJSON {invoiceToAddress("invalidURN")}
            def response = client.patch(uri: basketURI, requestContentType: JSON, body: requestData.bodyString)

        then: "Request failed(422) but no changes occurred"
            responseValidator.assertStatusUnprocessable(response, [BASKET_UPDATE_ERROR, ADDRESS_NOT_FOUND_CAUSE])

        when: "add address with invalid usage"
            AddressV1 invalidAddress = createBasketAddress([false, true] as Boolean[])
            requestData = toJSON {invoiceToAddress(invalidAddress.getURN())}
            response = client.patch(uri: basketURI, requestContentType: JSON, body: requestData.bodyString)

        then: "Request failed(422) but no changes occurred"
            responseValidator.assertStatusUnprocessable(response, [BASKET_UPDATE_ERROR, WRONG_ADDRESS_TYPE_INVOICE_CAUSE])

        when: "add address with valid data"
            AddressV1 address = createBasketAddress()
            requestData = toJSON {invoiceToAddress(address.getURN())}
            response = client.patch(uri: basketURI, requestContentType: JSON, body: requestData.bodyString)

        then: "Request succesful(200) => address added"
            responseValidator.assertStatusOk(response, ["invoice", address.getURN()])

        cleanup:
            clearHeaders()

        where:
            responseValidator << new AddBasketAddressResponseHandler()
    }

    def "Add/Update common ship to address to basket"()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()
            addLineItem(new LineItemV1(productSKU_One, "1"))

        when: "add address with invalid urn"
            def requestData = toJSON {commonShipToAddress("invalidURN")}
            def response = client.patch(uri: basketURI, requestContentType: JSON, body: requestData.bodyString)

        then: "Request failed(422) but no changes occurred"
            responseValidator.assertStatusUnprocessable(response, [BASKET_UPDATE_ERROR, ADDRESS_NOT_FOUND_CAUSE])

        when: "add address with invalid usage"
            AddressV1 invalidAddress = createBasketAddress([true, false] as Boolean[])
            requestData = toJSON {commonShipToAddress(invalidAddress.getURN())}
            response = client.patch(uri: basketURI, requestContentType: JSON, body: requestData.bodyString)

        then: "Request failed(422) but no changes occurred"
            responseValidator.assertStatusUnprocessable(response, [BASKET_UPDATE_ERROR, WRONG_ADDRESS_TYPE_SHIP_TO_CAUSE])

        when: "add address with valid data"
            AddressV1 address = createBasketAddress()
            requestData = toJSON {commonShipToAddress(address.getURN())}
            response = client.patch(uri: basketURI, requestContentType: JSON, body: requestData.bodyString)

        then: "Request succesful(200) => address added"
            responseValidator.assertStatusOk(response, ["shipTo", address.getURN()])

        cleanup:
            clearHeaders()

        where:
            responseValidator << new AddBasketAddressResponseHandler()
    }

    def "Add/Update common shipping method to basket"()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "add invalid shipping method  => parameter null"
            def expectedData = toJSON {commonShippingMethod(null)}
            def response = client.patch(uri: basketURI, requestContentType: JSON, body: expectedData.bodyString)

        then: "Request succesful(200) => null value will ignored"
            responseValidator.assertStatusOk(response)

        when: "add invalid shipping method"
            expectedData = toJSON {commonShippingMethod("SpaceDelivery")}
            response = client.patch(uri: basketURI, requestContentType: JSON, body: expectedData.bodyString)

        then: "Request failed(422)"
            responseValidator.assertStatusUnprocessable(response, ["SpaceDelivery", SHIPPING_METHOD_NOT_FOUND_ERROR])

        when: "Add valid shipping method"
            expectedData = toJSON {commonShippingMethod(NEW_SHIPPING_METHOD)}
            response = client.patch(uri: basketURI, requestContentType: JSON, body: expectedData.bodyString)

        then: "Request succesful(200) => not marked as common shipping method in multiple shippment case"
            responseValidator.assertStatusOk(response, NEW_SHIPPING_METHOD)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new AddShippingMethodResponseHandler()
    }

    def "Add/Update external order reference on basket"()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()

        when: "add valid external order reference"
            def requestData = toJSON {externalOrderReference(EX_NUMBER)}
            def response = client.patch(uri: basketURI, requestContentType: JSON, body: requestData.bodyString)

        then: "Request succesful(200)"
            responseValidator.assertStatusOk(response, EX_NUMBER)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new AddExternalOrderReferenceResponseHandler()
    }

    def "Add/Update read only attributes"() {

        given: "An anonymous user with a basket"
            String basketID = createBasket()

        when: "Update read only attribute e.g. customer"
            def requestData = toJSON { customer("Monster AG") }
            def response = client.patch(uri: basketURI, requestContentType: JSON, body: requestData.bodyString)

        then: "Request succesful(200) but no changes occurred"
            responseValidator.assertStatusOk(response)

        cleanup:
            clearHeaders()

        where:
           responseValidator << new AddReadOnlyAttributesResponseHandler()
    }

    def "Add/update recurrence information"()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()

        when: "add invalid recurrence information => required parameter null"
            def expectedData = getBasketRecurrenceInformationWithNullValue()
            def response = client.patch(uri: basketURI, requestContentType: JSON, body: expectedData.bodyString)

        then: "Request failed(422)"
            responseValidator.assertStatusUnprocessable(response, BASKET_RECURRENCE_MISSING_START_DATE)

        when: "add invalid recurrence information => required parameter empty"
            expectedData = getBasketRecurrenceInformationWithEmptyValue()
            response = client.patch(uri: basketURI, requestContentType: JSON, body: expectedData.bodyString)

        then: "Request failed(422)"
            responseValidator.assertStatusUnprocessable(response, BASKET_RECURRENCE_MISSING_START_DATE)

        when: "add valid recurrence information"
            expectedData = getBasketRecurrenceInformation()
            response = client.patch(uri: basketURI, requestContentType: JSON, body: expectedData.bodyString)

        then: "Request is successful"
            responseValidator.assertStatusOk(response, expectedData.body)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new AddRecurrenceInformationResponseHandler()

    }

    def "Add/update company information" ()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()

        when: "add invalid department => null value"
            def response = client.patch(uri: basketURI, requestContentType: JSON, body: getCompanyInformation(null))

        then: "Request succesful(200)"
            companyValidator.assertStatusOk(response, null)

        when: "add invalid department => empty value"
            response = client.patch(uri: basketURI, requestContentType: JSON, body: getCompanyInformation(""))

        then: "Request succesful(200)"
            companyValidator.assertStatusOk(response)

        when: "add valid department"
            response = client.patch(uri: basketURI, requestContentType: JSON, body: getCompanyInformation("central buying"))

        then: "Request succesful(200)"
            companyValidator.assertStatusOk(response, "central buying")

        when: "add invalid taxIdentificationNumber => null value"
            response = client.patch(uri: basketURI, requestContentType: JSON, body: getTaxIDNumberInformation(null))

        then: "Request succesful(200)"
            taxIDValidator.assertStatusOk(response, null)

        when: "add invalid taxIdentificationNumber => empty value"
            response = client.patch(uri: basketURI, requestContentType: JSON, body: getTaxIDNumberInformation(""))

        then: "Request succesful(200)"
            taxIDValidator.assertStatusOk(response)

        when: "add valid taxIdentificationNumber"
            response = client.patch(uri: basketURI, requestContentType: JSON, body: getTaxIDNumberInformation("012345"))

        then: "Request succesful(200)"
            taxIDValidator.assertStatusOk(response, "012345")
		
		cleanup:
            clearHeaders()

        where:
            companyValidator << new AddCompanyInformationResponseHandler()
            taxIDValidator << new AddTaxIDNumberResponseHandler()
    }

    def "Update basket (response with includes)"()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "1")
            lineItem.setID(addLineItem(lineItem))

        when: "Update basket and request includes"
            AddressV1 address = createBasketAddress()
            client.patch(uri: "${basketURI}", requestContentType: JSON, body: toJSON {invoiceToAddress(address.urn)}.bodyString)
            client.patch(uri: "${basketURI}", requestContentType: JSON, body: toJSON {commonShipToAddress(address.urn)}.bodyString)
            client.put(uri: "${basketURI}"+OPEN_TENDER_SUBRESOURCE, requestContentType: JSON, body: toJSON {paymentInstrument(DEFAULT_PAYMENT_METHOD)}.bodyString)
            def response = client.patch(uri: basketURI + INCLUDE_PREFIX + ALL_INCLUDES, requestContentType: JSON, body: "{}")

        then: "Request is successful and basket is returned with includes"
            includeValidator.assertStatusOk( response, [ "basketID" : basketID,
                (LINE_ITEM_INCLUDE) : lineItem, 
                (COMMON_SHIPPING_METHOD_INCLUDE) : DEFAULT_SHIPPING_METHOD,
                (BUCKETS_INCLUDE) : lineItem,
                (INVOICE_TO_ADDRESS_INCLUDE) : address,
                (COMMON_SHIP_TO_ADDRESS_INCLUDE) : address,
                (DISCOUNTS_INCLUDE) : null,
                (PAYMENTS_INCLUDE) : DEFAULT_PAYMENT_METHOD,
                (ATTRIBUTES_INCLUDE) : null ] )

        cleanup:
            clearHeaders()

        where:
            includeValidator = new ResponseWithIncludesHandler()

    }

    def "Create basket with initial data"()
    {
        when: "Create basket with external order reference"
            def ex_Number = toJSON { externalOrderReference("ex_number") }
            def response = client.post(uri: basketsURI, requestContentType: JSON, body: ex_Number.bodyString)

        then: "Request is successful"
            externalOrderReferenceValidator.assertStatusCreated(response, basketsURI, "ex_number")

        when: "add valid recurrence information"
            def expectedData = getBasketRecurrenceInformation()
            response = client.post(uri: basketsURI, requestContentType: JSON, body: expectedData.bodyString)

        then: "Request is successful"
            recurrenceInformationValidator.assertStatusCreated(response, basketsURI, expectedData.body)

        when: "add valid department"
            response = client.post(uri: basketsURI, requestContentType: JSON, body: getCompanyInformation("central buying"))

        then: "Request succesful(201)"
            companyValidator.assertStatusCreated(response, basketsURI, "central buying")

        when: "add valid taxIdentificationNumber"
            response = client.post(uri: basketsURI, requestContentType: JSON, body: getTaxIDNumberInformation("012345"))

        then: "Request succesful(201)"
            taxIDValidator.assertStatusCreated(response, basketsURI, "012345")

        cleanup:
            clearHeaders()

        where:
            externalOrderReferenceValidator << new AddExternalOrderReferenceResponseHandler()
            recurrenceInformationValidator << new AddRecurrenceInformationResponseHandler()
            companyValidator << new AddCompanyInformationResponseHandler()
            taxIDValidator << new AddTaxIDNumberResponseHandler()
    }

    def "Get eligible basket addresses"()
    {
        String invoiceQueryParam = "eligibleInvoiceToAddress"
        String shipToQueryParam = "eligibleShipToAddress"
        String installToQueryParam = "eligibleInstallToAddress"
        String shipFromQueryParam = "eligibleShipFromAddress"
        String serviceToQueryParam = "eligibleServiceToAddress"


        given: "An anonymous user with a basket and six different basket addresses"
            createBasket()
            AddressV1 invoiceAddress = createBasketAddress([true, false] as Boolean[])
            AddressV1 shippingAddress = createBasketAddress([false, true] as Boolean[])
            AddressV1 installToAddress = createBasketAddress([false, false, false, false, true] as Boolean[])
            AddressV1 shipFromAddress = createBasketAddress([false, false, true, false, false] as Boolean[])
            AddressV1 serviceToAddress = createBasketAddress([false, false, false, true, false] as Boolean[])
            AddressV1 addressWithoutLimitations = createBasketAddress([true, true, true, true, true] as Boolean[])

        when: "get eligible invoice addresses"
            def response = client.get(uri: basketURI + ELIGIBLE_ADDRESSES_SUBRESOURCE + "?" + invoiceQueryParam + "=true", requestContentType: JSON)

        then: "Request is successful => two addresses returned"
            responseValidator.assertStatusOk(response, [invoiceAddress,addressWithoutLimitations])

        when: "get eligible shipTo addresses"
            response = client.get(uri: basketURI + ELIGIBLE_ADDRESSES_SUBRESOURCE + "?" + shipToQueryParam + "=true", requestContentType: JSON)

        then: "Request is successful => two addresses returned"
            responseValidator.assertStatusOk(response, [shippingAddress,addressWithoutLimitations])

        when: "get eligible serviceTo addresses"
            response = client.get(uri: basketURI + ELIGIBLE_ADDRESSES_SUBRESOURCE + "?" + serviceToQueryParam + "=true", requestContentType: JSON)

        then: "Request is successful => two addresses returned"
            responseValidator.assertStatusOk(response, [serviceToAddress, addressWithoutLimitations])

        when: "get eligible installTo addresses"
            response = client.get(uri: basketURI + ELIGIBLE_ADDRESSES_SUBRESOURCE + "?" + installToQueryParam + "=true", requestContentType: JSON)

        then: "Request is successful => two addresses returned"
            responseValidator.assertStatusOk(response, [installToAddress,addressWithoutLimitations])

        when: "get eligible shipFrom addresses"
            response = client.get(uri: basketURI + ELIGIBLE_ADDRESSES_SUBRESOURCE + "?" + shipFromQueryParam + "=true", requestContentType: JSON)

        then: "Request is successful => two addresses returned"
            responseValidator.assertStatusOk(response, [shipFromAddress,addressWithoutLimitations])

        when: "get eligible addresses without query parameters"
            response = client.get(uri: basketURI + ELIGIBLE_ADDRESSES_SUBRESOURCE, requestContentType: JSON)

        then: "Request is successful => six addresses returned"
            responseValidator.assertStatusOk(response, [invoiceAddress,shippingAddress,shipFromAddress,installToAddress,serviceToAddress,addressWithoutLimitations])

        when: "get eligible addresses with wrong basket id"
            response = client.get(uri: basketsURI + "/wrongID" + ELIGIBLE_ADDRESSES_SUBRESOURCE, requestContentType: JSON)

        then: "Request failed and an error is returned which contains an error 404"
            responseValidator.assertStatusNotFound(response, BASKET_NOT_FOUND_ERROR)

        when: "get eligible addresses without authentication token"
            client.headers.remove("authentication-token")
            response = client.get(uri: basketsURI + "/wrongID" + ELIGIBLE_ADDRESSES_SUBRESOURCE, requestContentType: JSON)

        then: "Request failed and an error is returned which contains an error 404"
            responseValidator.assertStatusNotFound(response, BASKET_NOT_FOUND_ERROR)

        where:
            responseValidator << new GetEligibleAddressesResponseHandler()
    }

    /**
     * Checks whether the basket with the given ID exists. If an authentication token is passed, it will be set as
     * header before the request is invoked; else the request will be executed for the current authenticated user.
     * @param basketID the ID of the basket
     * @param authenticationToken the (optional) authentication token
     * @return {@code true} if the basket can be accessed; else {@code false}
     */
    private boolean isBasketExisting(String basketID, String authenticationToken = null)
    {
        if (authenticationToken)
        {
            client.headers."authentication-token" = authenticationToken
        }
        def response = client.get(uri: "${basketsURI}/${basketID}", requestContentType: JSON)
        return response.success
    }

    private String getJSONAttribute(String attributeName, String[] value)
    {
        def builder = new groovy.json.JsonBuilder()

        if(attributeName == "customer")
        {
            builder { customer value[0] }
        }
        if(attributeName == "invoiceToAddress")
        {
            builder { invoiceToAddress value[0] }
        }
        if(attributeName == "commonShippingMethod")
        {
            builder {
                if(value != null)
                {
                    commonShippingMethod value[0]
                }
            }
        }
        if(attributeName == "department")
        {
            builder {
                if(value != null)
                {
                    department value[0]
                }
            }
        }
        if(attributeName == "taxIdentificationNumber")
        {
            builder {
                if(value != null)
                {
                    taxIdentificationNumber value[0]
                }
            }
        }
        if(attributeName == "externalOrderReference")
        {
            builder { externalOrderReference value[0] }
        }
        if(attributeName == "recurrence")
        {
            builder.recurrence{
                if(value[0] != null)
                {
                    startDate value[0]
                }
                endDate value[1]
                interval value[2]
                repetitions value[3]
            }
        }

        return builder.toString()
    }
    def getBasketRecurrenceInformation() {
        def year = LocalDateTime.now().getYear()
        toJSON {
            recurrence {
                startDate(String.valueOf(year + 1) + "-05-23T13:23:45+02:00")
                endDate(String.valueOf(year + 2) + "-05-23T13:23:45+02:00")
                interval("P6Y11M27D")
                repetitions("24")
            }
        }
    }

    def getBasketRecurrenceInformationWithNullValue() {
        def year = LocalDateTime.now().getYear()
        toJSON {
            recurrence {
                startDate(null)
                endDate(String.valueOf(year + 2) + "-05-23T13:23:45+02:00")
                interval("P6Y11M27D")
                repetitions("24")
            }
        }
    }

    def getBasketRecurrenceInformationWithEmptyValue() {
        def year = LocalDateTime.now().getYear()
        toJSON {
            recurrence {
                startDate("")
                endDate(String.valueOf(year + 2) + "-05-23T13:23:45+02:00")
                interval("P6Y11M27D")
                repetitions("24")
            }
        }
    }
    
    def String getCompanyInformation(def value)
    {
        return toJSON { department value }.bodyString
    }
        
    def String getTaxIDNumberInformation(def value) {

        return toJSON { taxIdentificationNumber value }.bodyString
    }
}

/**
 *  VALIDATION CLASS IMPLEMENTATIONS
 */
class CreateBasketResponseHandler extends JsonResponseValidator {

    @Override
    void assertStatusNotFound(def response, def expectedData = null)
    {
        hasStatusNotFound(response)
        hasErrorCode(response.data, expectedData)
    }
}

class GetBasketListResponseHandler extends JsonResponseValidator
{
    @Override
    void assertStatusUnauthorized(def response, def expectedData = null) {
        hasStatusUnauthorized(response)
    }

    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        List data = responseData.data
        assert data != null
        assert data.size() >= 2
        assert data.find{it.id.equals(expectedData.basketID2)} != null
        assert data.find{it.id.equals(expectedData.basketID1)} != null
    }
}

class GetBasketItemResponseHandler extends JsonResponseValidator
{
    @Override
    void assertStatusNotFound(def response, def expectedData = null)
    {
        hasStatusNotFound(response)
        hasErrorCode(response.data, expectedData)
    }

    @Override
    void assertStatusUnauthorized(def response, def expectedData = null) {
        hasStatusUnauthorized(response)
    }

    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        hasDataId(responseData, expectedData.basketID)
        if(expectedData.included != null)
        {
            hasIncludes(responseData)
            def include = responseData.included
            if(expectedData.included == BasketRestSpecV1.LINE_ITEM_INCLUDE)
            {
                def lineItem = include.lineItems.get(expectedData.data.id)
                assert lineItem != null
                assert lineItem.product.equals(expectedData.data.sku)
            }
            if(expectedData.included == BasketRestSpecV1.COMMON_SHIPPING_METHOD_INCLUDE)
            {
                def shippingMethod = include.commonShippingMethod.get(expectedData.data)
                assert shippingMethod !=null
                assert shippingMethod.id.equals(expectedData.data)
                assert shippingMethod.name !=null
                assert shippingMethod.description !=null
            }
            if(expectedData.included == BasketRestSpecV1.BUCKETS_INCLUDE)
            {
                assert include.buckets.size() == 1
                def key = include.buckets.keySet().iterator().next()
                def bucket = include.buckets.get(key)
                assert bucket.basket.equals(expectedData.basketID)
                assert bucket.shippingMethod.equals(BasketRestSpecV1.DEFAULT_SHIPPING_METHOD)
                assert bucket.lineItems.contains(expectedData.data.id)
            }
            if(expectedData.included == BasketRestSpecV1.INVOICE_TO_ADDRESS_INCLUDE || expectedData.included == BasketRestSpecV1.COMMON_SHIP_TO_ADDRESS_INCLUDE)
            {
                AddressV1 expectedAddress = expectedData.data
                def address

                if(expectedData.included == BasketRestSpecV1.INVOICE_TO_ADDRESS_INCLUDE)
                {
                    assert include.invoiceToAddress.size() == 1
                    address = include.invoiceToAddress.get(expectedAddress.getURN())
                }
                else
                {
                    assert include.commonShipToAddress.size() == 1
                    address = include.commonShipToAddress.get(expectedAddress.getURN())
                }

                assert address.firstName.equals(expectedAddress.getFirstName())
                assert address.lastName.equals(expectedAddress.getLastName())
                assert address.addressLine1.equals(expectedAddress.getAddressLine1())
                assert address.postalCode.equals(expectedAddress.getPostalCode())
                assert address.city.equals(expectedAddress.getCity())
                assert address.countryCode.equals(expectedAddress.getCountryCode())
            }
        }
    }
}

class AddBasketAddressResponseHandler extends JsonResponseValidator {

    @Override
    void assertStatusUnprocessable(def response, def expectedData = null)
    {
        hasStatusUnprocessable(response)
        hasErrorCode(response.data, expectedData[0])
        hasErrorCause(response.data, expectedData[1])
    }

    @Override
    void checkResponseData(def responseData, def expectedData = null) {

        if(expectedData[0].equals(BasketRestSpecV1.BASKET_UPDATE_ERROR))
        {
            assert responseData.data.invoiceToAddress == null
        }
        else
        {
            if(expectedData[0].equals("invoice"))
            {
                assert responseData.data.invoiceToAddress == expectedData[1]
            }
            else
            {
                assert responseData.data.commonShipToAddress == expectedData[1]
            }
        }
    }
}

class AddShippingMethodResponseHandler extends JsonResponseValidator {

    @Override
    void assertStatusUnprocessable(def responseData, def expectedData = null)
    {
        assert responseData.data.commonShippingMethod != expectedData[0]
        hasErrorCause(responseData.data, expectedData[1])
    }

    @Override
    void checkResponseData(def responseData, def expectedData = null) 
    {
        assert responseData.data.commonShippingMethod == expectedData
    }
}

class DeleteBasketResponseHandler extends JsonResponseValidator
{
    @Override
    void assertStatusNotFound(def response, def expectedData = null)
    {
        hasStatusNotFound(response)
        hasErrorCode(response.data, expectedData)
    }

    @Override
    void assertStatusUnauthorized(def response, def expectedData = null) {
        hasStatusUnauthorized(response)
    }

    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        hasInfos(responseData)
        hasInfoStatus200(responseData)
        hasInfoCode(responseData, expectedData)
        hasNoData(responseData)
    }
}

class AddRecurrenceInformationResponseHandler extends JsonResponseValidator
{
    @Override
    void assertStatusUnprocessable(def responseData, def expectedData = null) 
    {
        hasErrorCause(responseData.data, expectedData)
    }

    @Override
    void checkResponseData(def responseData, def expectedData = null) 
    {
        assert responseData.data.recurrence.startDate == expectedData.recurrence.startDate
        assert responseData.data.recurrence.endDate == expectedData.recurrence.endDate
        assert responseData.data.recurrence.interval == expectedData.recurrence.interval
        assert responseData.data.recurrence.repetitions == Integer.valueOf(expectedData.recurrence.repetitions)
    }
}

class AddCompanyInformationResponseHandler extends JsonResponseValidator 
{
    @Override
    void checkResponseData(def responseData, def expectedData = null) 
    {
        assert responseData.data.department == expectedData
    }
}

class AddTaxIDNumberResponseHandler extends JsonResponseValidator 
{
    @Override
    void checkResponseData(def responseData, def expectedData = null) 
    {
        assert responseData.data.taxIdentificationNumber == expectedData
    }
}


class AddReadOnlyAttributesResponseHandler extends JsonResponseValidator 
{
    @Override
    void checkResponseData(def responseData, def expectedData = null) 
    {
        assert responseData.data.customer == null
    }
}

class AddExternalOrderReferenceResponseHandler extends JsonResponseValidator 
{
    @Override
    void checkResponseData(def responseData, def expectedData = null) 
    {
        assert responseData.data.externalOrderReference == expectedData
    }
}

class GetEligibleAddressesResponseHandler extends JsonResponseValidator
{
    @Override
    void assertStatusNotFound(def response, def expectedData = null)
    {
        hasStatusNotFound(response)
        hasErrorCode(response.data, expectedData)
    }

    @Override
    void checkResponseData(def responseData, def expectedData = null) {

        assert responseData.data.size().equals(expectedData.size())

        for(AddressV1 address: expectedData)
        {
            assert responseData.data.find{it.urn.equals(address.getURN())} != null
        }
    }
}

class ResponseWithIncludesHandler extends JsonResponseValidator
{
    static final String OPEN_TENDER_PAYMENT = "open-tender"
    
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
                
        if(expectedIncludes.containsKey(BasketRestSpecV1.LINE_ITEM_INCLUDE))
        {
            def expectedLineItem = expectedIncludes[BasketRestSpecV1.LINE_ITEM_INCLUDE]
            if(expectedLineItem != null)
            {
                def actualLineItem = actualIncludes.lineItems.get(expectedLineItem.id)
            
                assert actualLineItem != null
                assert actualLineItem.product.equals(expectedLineItem.sku)
            }
            else
            {
                assert actualIncludes.lineItems.size() == 0
            }
        }
        if(expectedIncludes.containsKey(BasketRestSpecV1.COMMON_SHIPPING_METHOD_INCLUDE))
        {
            def expectedShippingMethod = expectedIncludes[BasketRestSpecV1.COMMON_SHIPPING_METHOD_INCLUDE]
            if (expectedShippingMethod != null)
            {
                def actualShippingMethod = actualIncludes.commonShippingMethod.get(expectedShippingMethod)
                assert actualShippingMethod !=null
                assert actualShippingMethod.id.equals(expectedShippingMethod)
                assert actualShippingMethod.name !=null
                assert actualShippingMethod.description !=null
            }
            else
            {
                assert actualIncludes.commonShippingMethod.size() == 0
            }
        }
        if(expectedIncludes.containsKey(BasketRestSpecV1.BUCKETS_INCLUDE))
        {
            def expectedBucket = expectedIncludes[BasketRestSpecV1.BUCKETS_INCLUDE]
            if(expectedBucket != null)
            {
                def basketID = expectedIncludes.basketID
                assert actualIncludes.buckets.size() == 1
                def bucketID = actualIncludes.buckets.keySet().iterator().next()
                def actualBucket = actualIncludes.buckets.get(bucketID)
            
                assert actualBucket.basket.equals(basketID)
                assert actualBucket.shippingMethod.equals(BasketRestSpecV1.DEFAULT_SHIPPING_METHOD)
                assert actualBucket.lineItems.contains(expectedBucket.id)
            }
            else
            {
                assert actualIncludes.buckets.size() == 0
            }
        }
        if(expectedIncludes.containsKey(BasketRestSpecV1.INVOICE_TO_ADDRESS_INCLUDE))
        {
            AddressV1 expectedAddress = expectedIncludes[BasketRestSpecV1.INVOICE_TO_ADDRESS_INCLUDE]
            if (expectedAddress != null)
            {
                assert actualIncludes.invoiceToAddress.size() == 1
                def actualAddress = actualIncludes.invoiceToAddress.get(expectedAddress.getURN())
                assertAddress(actualAddress, expectedAddress)
            }
            else
            {
                assert actualIncludes.invoiceToAddress.size() == 0
            }
        }
        if(expectedIncludes.containsKey(BasketRestSpecV1.COMMON_SHIP_TO_ADDRESS_INCLUDE))
        {
            AddressV1 expectedAddress = expectedIncludes[BasketRestSpecV1.COMMON_SHIP_TO_ADDRESS_INCLUDE]
            if (expectedAddress != null)
            {
                assert actualIncludes.commonShipToAddress.size() == 1
                def actualAddress = actualIncludes.commonShipToAddress.get(expectedAddress.getURN())
                assertAddress(actualAddress, expectedAddress)
            }
            else
            {
                assert actualIncludes.commonShipToAddress.size() == 0
            }
        }
        if(expectedIncludes.containsKey(BasketRestSpecV1.DISCOUNTS_INCLUDE))
        {
            assert actualIncludes.discounts.size() == 0
        }
        if(expectedIncludes.containsKey(BasketRestSpecV1.PAYMENTS_INCLUDE))
        {
            def expectedPaymentMethod = expectedIncludes[BasketRestSpecV1.PAYMENTS_INCLUDE]
            if (expectedPaymentMethod != null)
            {
                def actualPayment = actualIncludes.payments.get(OPEN_TENDER_PAYMENT)
                assert actualPayment != null
                assert actualPayment.id.equals(OPEN_TENDER_PAYMENT)
                assert actualPayment.paymentInstrument.equals(expectedPaymentMethod)
            }
            else
            {
                assert actualIncludes.payments.size() == 0
            }
        }
        if(expectedIncludes.containsKey(BasketRestSpecV1.ATTRIBUTES_INCLUDE))
        {
            //TODO extend tests after attribute handling (in particular adding) was added to Basket REST API v1
            assert actualIncludes.attributes.size() == 0
        }
    }
    
    void assertAddress(def actualAddress, def expectedAddress)
    {
        assert actualAddress.firstName.equals(expectedAddress.getFirstName())
        assert actualAddress.lastName.equals(expectedAddress.getLastName())
        assert actualAddress.addressLine1.equals(expectedAddress.getAddressLine1())
        assert actualAddress.postalCode.equals(expectedAddress.getPostalCode())
        assert actualAddress.city.equals(expectedAddress.getCity())
        assert actualAddress.countryCode.equals(expectedAddress.getCountryCode())
    }
    
}