package rest.com.intershop.b2x.specs.storefront.basket.address

import static rest.com.intershop.b2x.specs.storefront.common.RequestBuilder.toJSON

import java.time.*

import rest.com.intershop.b2x.specs.storefront.basket.AbstractBasketRestSpecV1
import rest.com.intershop.b2x.specs.storefront.common.JsonResponseValidator
import rest.com.intershop.b2x.specs.storefront.common.objects.AddressV1
import rest.com.intershop.b2x.specs.storefront.common.objects.LineItemV1
import spock.lang.*

@Narrative("""
As a customer
I want to use the Intershop REST API
to manage my basket addresses
""")
@Title("i want to manage my basket addresses")
class BasketAddressRestSpecV1 extends AbstractBasketRestSpecV1
{
    static final String ADDRESS_CREATION_ERROR = "basket.address.creation.error"
    static final String ADDRESS_UPDATE_ERROR = "basket.address.update.error"
    static final String DUPLICATE_ADDRESS_CAUSE = "basket.address.creation.error.cause.DuplicateAddress"
    static final String DUPLICATE_ADDRESS_UPDATE_CAUSE = "basket.address.update.error.cause.DuplicateAddress"
    static final String ADDRESS_NOT_FOUND_ERROR = "address.not_found.error"

    static final String ATTRIBUTES_INCLUDE = "attributes"
    static final String EMPTY_ATTRIBUTES = ""
    
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

    def "Create basket address as anonymous user"()
    {
        given: "An anonymous user with a basket and address"
            createBasket()
            AddressV1 address = new AddressV1()

        when: "Send \"POST /baskets/<basket-id>/addresses\" request"
            String jsonAddress = getAddressEntry(address)
            def response = client.post(uri: addressesURI, requestContentType: JSON, body: jsonAddress)

        then: "address is created and response returned"
            responseValidator.assertStatusCreated(response, addressesURI, address)

        when: "Send \"POST /baskets/<basket-id>/addresses\" request with duplicated data"
            response = client.post(uri: addressesURI, requestContentType: JSON, body: jsonAddress)

        then: "Request failed => status code should be 422"
            responseValidator.assertStatusUnprocessable(response, [ADDRESS_CREATION_ERROR, DUPLICATE_ADDRESS_CAUSE])

        when: "Send \"GET /baskets/<basket-id>/addresses\" request without authentication-token"
            client.headers.remove('authentication-token')
            response = client.post(uri: addressesURI, requestContentType: JSON, body: jsonAddress)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        cleanup:
            clearHeaders()
            
        where:
            responseValidator << new AddAddressResponseHandler()
    }

    def "Create basket address and get response with includes"()
    {
        given: "An anonymous user with a basket and address"
            createBasket()
            AddressV1 address = new AddressV1()

        when: "Send \"POST /baskets/<basket-id>/addresses?include=attributes\" request"
            String jsonAddress = getAddressEntry(address)
            def response = client.post(uri: addressesURI+INCLUDE_PREFIX+ATTRIBUTES_INCLUDE, requestContentType: JSON, body: jsonAddress)

        then: "address is created and response with attributes include is returned"
            responseValidator.assertStatusCreated(response, addressesURI, [(ATTRIBUTES_INCLUDE) : EMPTY_ATTRIBUTES])
        
        cleanup:
            clearHeaders()
        
        where:
            responseValidator << new ResponseWithIncludesHandler()
    }

    def "Create basket address as registered user"()
    {
        given: "An registered user with a basket and address"
            client.setAuthorizationHeader(user, password)
            createBasket()
            client.headers.remove('authentication-token')
            AddressV1 address = new AddressV1()

        when: "Send \"POST /baskets/<basket-id>/addresses\" request"
            String jsonAddress = getAddressEntry(address)
            def response = client.post(uri: addressesURI, requestContentType: JSON, body: jsonAddress)

        then: "address is created and response returned"
            responseValidator.assertStatusCreated(response, addressesURI, address)

        when: "Send \"POST /baskets/<basket-id>/addresses\" request with duplicated data"
            response = client.post(uri: addressesURI, requestContentType: JSON, body: jsonAddress)

        then: "Request failed => status code should be 422"
            responseValidator.assertStatusUnprocessable(response, [ADDRESS_CREATION_ERROR, DUPLICATE_ADDRESS_CAUSE])

        when: "Send \"GET /baskets/<basket-id>/addresses\" request without authentication-token"
            client.clearAuthorizationHeader()
            response = client.post(uri: addressesURI, requestContentType: JSON, body: jsonAddress)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        cleanup:
            clearHeaders()
            
        where:
            responseValidator << new AddAddressResponseHandler()
    }

    def "Get basket address list as anonymous user"()
    {
        given: "An anonymous user with a basket"
            createBasket()

        when: "Send \"GET /baskets/<basket-id>/addresses\" request"
            def response = client.get(uri: addressesURI, requestContentType: JSON)

        then: "Request success => but address list is empty"
            responseValidator.assertStatusOk(response)

        when: "Send \"GET /baskets/<basket-id>/addresses\" request"
            createBasketAddress()
            createBasketAddress()
            response = client.get(uri: addressesURI, requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, 2)

        when: "Send \"GET /baskets/<basket-id>/addresses\" request without authentication-token"
            client.headers.remove('authentication-token')
            response = client.get(uri: addressesURI, requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetAddressListResponseHandler()
    }

    def "Get basket address list as registered user"()
    {
        given: "An registered user with a basket"
            client.setAuthorizationHeader(user, password)
            createBasket()
            client.headers.remove('authentication-token')

        when: "Send \"GET /baskets/<basket-id>/addresses\" request"
            def response = client.get(uri: addressesURI, requestContentType: JSON)

        then: "Request success => but address list is empty"
            responseValidator.assertStatusOk(response)

        when: "Send \"GET /baskets/<basket-id>/addresses\" request"
            createBasketAddress()
            createBasketAddress()
            response = client.get(uri: addressesURI, requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, 2)

        when: "Send \"GET /baskets/<basket-id>/addresses\" request without authentication-token"
            client.clearAuthorizationHeader()
            response = client.get(uri: addressesURI, requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetAddressListResponseHandler()
    }

    def "Get basket address with include data"()
    {
        given: "An anonymous user with a basket and address"
            createBasket()
            AddressV1 address = createBasketAddress()

        when: "Send \"GET /baskets/<basket-id>/addresses/<address-id>?include=attributes\" request"
            def response = client.get(uri: addressesURI + "/" + address.getID()+INCLUDE_PREFIX+ATTRIBUTES_INCLUDE, requestContentType: JSON)

        then: "Request is successful and address is returned with attributes include"
            responseValidator.assertStatusOk(response, [(ATTRIBUTES_INCLUDE) : EMPTY_ATTRIBUTES])

        cleanup:
            clearHeaders()

        where:
            responseValidator << new ResponseWithIncludesHandler()
    }

    def "Get basket address as anonymous user"()
    {
        given: "An anonymous user with a basket and address"
            createBasket()
            AddressV1 address = createBasketAddress()

        when: "Send \"GET /baskets/<basket-id>/addresses/<address-id>\" request"
            def response = client.get(uri: addressesURI + "/" + address.getID(), requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, address)

        when: "Send \"GET /baskets/<basket-id>/addresses/<address-id>\" request"
            response = client.get(uri: addressesURI + "/wrongID", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusNotFound(response, ADDRESS_NOT_FOUND_ERROR)

        when: "Send \"GET /baskets/<basket-id>/addresses\" request without authentication-token"
            client.headers.remove('authentication-token')
            response = client.get(uri: addressesURI + "/" + address.getID(), requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetAddressItemResponseHandler()
    }

    def "Get basket address as registered user"()
    {
        given: "An registered user with a basket and address"
            client.setAuthorizationHeader(user, password)
            createBasket()
            client.headers.remove('authentication-token')
            AddressV1 address = createBasketAddress()

        when: "Send \"GET /baskets/<basket-id>/addresses/<address-id>\" request"
            def response = client.get(uri: addressesURI + "/" + address.getID(), requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, address)

        when: "Send \"GET /baskets/<basket-id>/addresses/<address-id>\" request"
            response = client.get(uri: addressesURI + "/wrongID", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusNotFound(response, ADDRESS_NOT_FOUND_ERROR)

        when: "Send \"GET /baskets/<basket-id>/addresses\" request without authentication-token"
            client.clearAuthorizationHeader()
            response = client.get(uri: addressesURI + "/" + address.getID(), requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        cleanup:
            clearHeaders()

        where:
            responseValidator << new GetAddressItemResponseHandler()
    }

    def "Delete basket address as anonymous user"()
    {
        given: "An anonymous user with a basket and address"
            createBasket()
            AddressV1 address = createBasketAddress()

        when: "Send \"DELETE /baskets/<basket-id>/addresses/wrongID\" request"
            def response = client.delete(uri: addressesURI + "/wrongID", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusNotFound(response, ADDRESS_NOT_FOUND_ERROR)

        when: "Send \"DELETE /baskets/<basket-id>/addresses/<address-id>\" request"
            response = client.delete(uri: addressesURI + "/" + address.getID(), requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, address)

        when: "Send \"DELETE /baskets/<basket-id>/addresses/<address-id>\" request with address which is already in use"
            address = createBasketAddress()
            addLineItem(new LineItemV1(getTestValue("rest.basket.productId"), "1"))
            client.patch(uri: basketURI, requestContentType: JSON, body: toJSON {commonShipToAddress(address.getURN())}.bodyString)
            response = client.delete(uri: addressesURI + "/" + address.getID(), requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response)

        when: "Send \"GET /baskets/<basket-id>\" request"
            response = client.get(uri: basketURI, requestContentType: JSON)

        then: "Request success => status code should be 200 and commonShipToAddress is empty"
            responseValidator.assertStatusOk(response)
            assert response.data.commonShipToAddress == null

        when: "Send \"DELETE /baskets/<basket-id>/addresses/<address-id>\" request without authentication-token"
            address = createBasketAddress()
            client.headers.remove('authentication-token')
            response = client.delete(uri: addressesURI + "/" + address.getID(), requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        cleanup:
            clearHeaders()
            
        where:
            responseValidator << new JsonResponseValidator()
    }

    def "Delete basket address as registered user"()
    {
        given: "An registered user with a basket and address"
            client.setAuthorizationHeader(user, password)
            createBasket()
            client.headers.remove('authentication-token')
            AddressV1 address = createBasketAddress()

        when: "Send \"DELETE /baskets/<basket-id>/addresses/wrongID\" request"
            def response = client.delete(uri: addressesURI + "/wrongID", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusNotFound(response, ADDRESS_NOT_FOUND_ERROR)

        when: "Send \"DELETE /baskets/<basket-id>/addresses/<address-id>\" request"
            response = client.delete(uri: addressesURI + "/" + address.getID(), requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response)

        when: "Send \"DELETE /baskets/<basket-id>/addresses/<address-id>\" request without authentication-token"
            address = createBasketAddress()
            client.clearAuthorizationHeader()
            response = client.delete(uri: addressesURI + "/" + address.getID(), requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Send \"DELETE /baskets/<basket-id>/addresses/<address-id>\" request with address which is already in use"
            client.setAuthorizationHeader(user, password)
            addLineItem(new LineItemV1(getTestValue("rest.basket.productId"), "1"))
            client.patch(uri: basketURI, requestContentType: JSON, body: toJSON {commonShipToAddress(address.getURN())}.bodyString)
            response = client.delete(uri: addressesURI + "/" + address.getID(), requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response)

        when: "Send \"GET /baskets/<basket-id>\" request"
            response = client.get(uri: basketURI, requestContentType: JSON)

        then: "Request success => status code should be 200 and commonShipToAddress is empty"
            responseValidator.assertStatusOk(response)
            assert response.data.commonShipToAddress == null

        cleanup:
            clearHeaders()
        
        where:
            responseValidator << new JsonResponseValidator()
    }

    def "Update basket address as anonymous user"()
    {
        given: "An anonymous user with a basket and address"
            createBasket()
            AddressV1 address = createBasketAddress()

        when: "Send \"PATCH /baskets/<basket-id>/addresses/wrongID\" request"
            def response = client.patch(uri: addressesURI + "/wrongID", requestContentType: JSON, body: "")

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusNotFound(response, ADDRESS_NOT_FOUND_ERROR)

        when: "Send \"PATCH /baskets/<basket-id>/addresses/<address-id>\" request"
            AddressV1 newAddress = new AddressV1([false, false] as Boolean[])
            response = client.patch(uri: addressesURI + "/" + address.getID(), requestContentType: JSON, body: getAddressEntry(newAddress))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, newAddress)

        cleanup:
            clearHeaders()
        
        where:
            responseValidator << new UpdateAddressItemResponseHandler()
    }

    def "Update basket address and get response with includes"()
    {
        given: "An user with a basket and address"
            createBasket()
            AddressV1 address = createBasketAddress()
            String jsonAddress = getAddressEntry(address)
                   
        when: "Send \"PATCH /baskets/<basket-id>/addresses/<address-id>?include=attributes\" request"
            def response = client.patch(uri: addressesURI + "/" + address.getID() + INCLUDE_PREFIX + ATTRIBUTES_INCLUDE, 
                requestContentType: JSON, body: jsonAddress)
            
        then: "address is patched and response with attributes include is returned"
            responseValidator.assertStatusOk(response, [(ATTRIBUTES_INCLUDE) : EMPTY_ATTRIBUTES])
            
        cleanup:
            clearHeaders()
        
        where:
            responseValidator << new ResponseWithIncludesHandler()
    }

    def "Update basket address as registered user"()
    {
        given: "An registered user with a basket and address"
            client.setAuthorizationHeader(user, password)
            createBasket()
            client.headers.remove('authentication-token')
            AddressV1 address = createBasketAddress()

        when: "Send \"PATCH /baskets/<basket-id>/addresses/wrongID\" request"
            def response = client.patch(uri: addressesURI + "/wrongID", requestContentType: JSON, body: "")

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusNotFound(response, ADDRESS_NOT_FOUND_ERROR)

        when: "Send \"PATCH /baskets/<basket-id>/addresses/<address-id>\" request"
            AddressV1 newAddress = new AddressV1([false, false] as Boolean[])
            response = client.patch(uri: addressesURI + "/" + address.getID(), requestContentType: JSON, body: getAddressEntry(newAddress))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, newAddress)

        cleanup:
            clearHeaders()
            
        where:
            responseValidator << new UpdateAddressItemResponseHandler()
    }

    def "Update basket address up to duplicated address"()
    {
        given: "An anonymous user with a basket and address"
            createBasket()
            AddressV1 address = createBasketAddress()
            AddressV1 duplicatedAddress = createBasketAddress()

        when: "Send \"PATCH /baskets/<basket-id>/addresses/wrongID\" request"
            def response = client.patch(uri: addressesURI + "/" + address.getID(), requestContentType: JSON, body: getAddressEntry(duplicatedAddress))

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusUnprocessable(response, [ADDRESS_UPDATE_ERROR, DUPLICATE_ADDRESS_UPDATE_CAUSE])

        cleanup:
            clearHeaders()
            
        where:
            responseValidator << new UpdateAddressItemResponseHandler()
    }
}

/**
 *  VALIDATION CLASS IMPLEMENTATIONS
 */
class GetAddressListResponseHandler extends JsonResponseValidator
{
    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        if(expectedData == null)
        {
            hasData(responseData)
            assert responseData.data.size() == 0
        }
        else
        {
            assert responseData.data.size().equals(expectedData)
        }
    }
}

class GetAddressItemResponseHandler extends JsonResponseValidator
{
    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        AddressV1 address = expectedData
        assert responseData.data.urn.equals(address.getURN())
        assert responseData.data.id.equals(address.getID())
        assert responseData.data.firstName.equals(address.getFirstName())
        assert responseData.data.lastName.equals(address.getLastName())
        assert responseData.data.addressLine1.equals(address.getAddressLine1())
        assert responseData.data.city.equals(address.getCity())
        assert responseData.data.postalCode.equals(address.getPostalCode())
        assert responseData.data.mainDivision.equals(address.getMainDivision())
        assert responseData.data.countryCode.equals(address.getCountryCode())
        assert responseData.data.eligibleInvoiceToAddress.equals(Boolean.valueOf(address.getEligibleInvoiceToAddress()))
        assert responseData.data.eligibleShipToAddress.equals(Boolean.valueOf(address.getEligibleShipToAddress()))
    }
}

class AddAddressResponseHandler extends JsonResponseValidator
{
    @Override
    void assertStatusUnprocessable(def response, def expectedData = null)
    {
        hasStatusUnprocessable(response)
        hasErrorCode(response.data, expectedData[0])
        hasErrorCause(response.data, expectedData[1])
    }

    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        def data = responseData.data
        assert data.id != null
        assert data.firstName.equals(expectedData.firstName)
    }
}

class ResponseWithIncludesHandler extends JsonResponseValidator
{
    @Override
    void checkResponseData(def responseData, def expectedIncludes = null)
    {
        hasData(responseData)
        hasIncludes(responseData)
        def actualIncludes = responseData.included
        
        if(expectedIncludes.containsKey(BasketAddressRestSpecV1.ATTRIBUTES_INCLUDE))
        {
            def attribute = actualIncludes.attributes
            //TODO extend tests after attribute handling (in particular adding) was added to Basket REST API v1
            assert attribute != null
        }
    }
}

class UpdateAddressItemResponseHandler extends JsonResponseValidator
{
    @Override
    void assertStatusUnprocessable(def response, def expectedData = null)
    {
        hasStatusUnprocessable(response)
        hasErrorCode(response.data, expectedData[0])
        hasErrorCause(response.data, expectedData[1])
    }

    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        AddressV1 address = expectedData
        assert responseData.data.firstName.equals(address.getFirstName())
        assert responseData.data.lastName.equals(address.getLastName())
        assert responseData.data.addressLine1.equals(address.getAddressLine1())
        assert responseData.data.city.equals(address.getCity())
        assert responseData.data.postalCode.equals(address.getPostalCode())
        assert responseData.data.mainDivision.equals(address.getMainDivision())
        assert responseData.data.countryCode.equals(address.getCountryCode())
        assert responseData.data.eligibleInvoiceToAddress.equals(Boolean.valueOf(address.getEligibleInvoiceToAddress()))
        assert responseData.data.eligibleShipToAddress.equals(Boolean.valueOf(address.getEligibleShipToAddress()))
    }
}
