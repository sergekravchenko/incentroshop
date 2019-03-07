package rest.com.intershop.b2x.specs.storefront.basket.shipping

import static rest.com.intershop.b2x.specs.storefront.common.RequestBuilder.toJSON

import java.time.*

import rest.com.intershop.b2x.specs.storefront.basket.AbstractBasketRestSpecV1
import rest.com.intershop.b2x.specs.storefront.common.JsonResponseValidator
import rest.com.intershop.b2x.specs.storefront.common.objects.AddressV1
import rest.com.intershop.b2x.specs.storefront.common.objects.LineItemV1
import spock.lang.*

@Narrative("""As a customer
I want to use the ICM Web Shop REST API
to handle shipping buckets.""")
@Title("Tests for shipping bucket REST resources (v1)")
class ShippingBucketRestSpecV1 extends AbstractBasketRestSpecV1 {
    
    public static final String DEFAULT_SHIPPING_METHOD = "STD_GROUND"
    public static final String OTHER_SHIPPING_METHOD = "STD_5DAY"

    public static final String ERROR_SHIPPING_BUCKET_NOT_FOUND = "shipping_bucket.not_found.error";
    public static final String ERROR_SHIPPING_BUCKET_SHIPPING_METHOD_NOT_ELIGIBLE = "shipping_bucket.shipping_method_not_eligible.error";
    public static final String ERROR_COULD_NOT_UPDATE_SHIPPING_BUCKET = "shipping_bucket.could_not_update.error";
    public static final String ERROR_SHIPPING_METHOD_NOT_FOUND = "shipping_method.not_found.error";
    public static final String ERROR_SHIPPING_METHOD_NOT_ELIGIBLE = "basket.shipping_method_not_eligible.error";
    public static final String ERROR_ADDRESS_NOT_FOUND = "address.not_found.error";
    public static final String INFO_SHIPPING_BUCKET_UPDATE = "shipping_bucket.update.info";

    public static final String SHIP_TO_ADDRESS_INCLUDE = "shipToAddress"
    public static final String SHIPPING_METHOD_INCLUDE = "shippingMethod"
    public static final String ALL_INCLUDES = SHIP_TO_ADDRESS_INCLUDE + "," + SHIPPING_METHOD_INCLUDE
    
    public static final String BUCKETS_RESOURCE = "/buckets"
    
    @Shared
    String bucketsURI, productSKU_One, productSKU_Two

    def setupSpec()
    {
        user = getTestValue("rest.basket.user.login")
        password = getTestValue("rest.basket.user.password")

        String host = System.properties["hostName"]
        String port = System.properties["webserverPort"]
        String basketPath = getTestValue("rest.basket.path")

        productSKU_One = getTestValue("rest.basket.productId", 0);
        productSKU_Two = getTestValue("rest.basket.productId", 1);
        basketsURI          = "http://${host}:${port}/${basketPath}"
    }

    def "Get shipping bucket list as anonymous user"()
    {
        given: "A basket with anonymous user"
            createBasket()

        when: "Get shipping bucket list from basket with unknown id"
            def response = client.get(uri: basketsURI + "/wrongBasketID/buckets", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Get empty shipping bucket list"
            response = client.get(uri: bucketsURI, requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response)

        when: "Get shipping bucket list by current user"
            LineItemV1 pli1 = new LineItemV1(productSKU_One, "1"); pli1.shippingMethod = DEFAULT_SHIPPING_METHOD
            LineItemV1 pli2 = new LineItemV1(productSKU_Two, "1"); pli2.shippingMethod = OTHER_SHIPPING_METHOD
            addLineItem(pli1)
            addLineItem(pli2)
            response = client.get(uri: bucketsURI, requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, 2)

        cleanup:
            clearHeaders()
            
         where:
            responseValidator << new GetShippingBucketListResponseHandler()
    }

    def "Get shipping bucket list as registered user"()
    {
        given: "A basket with registered user"
            client.setAuthorizationHeader(user, password)
            createBasket()

        when: "Get shipping bucket list from basket with unknown id"
            def response = client.get(uri: basketsURI + "/wrongBasketID/buckets", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Get empty shipping bucket list by current user"
            response = client.get(uri: bucketsURI, requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response)

        when: "Get shipping bucket list by current user"
            LineItemV1 pli1 = new LineItemV1(productSKU_One, "1"); pli1.setShippingMethod(DEFAULT_SHIPPING_METHOD)
            LineItemV1 pli2 = new LineItemV1(productSKU_Two, "1"); pli2.setShippingMethod(OTHER_SHIPPING_METHOD)
            addLineItem(pli1); addLineItem(pli2);
            getShippingBucket()
            getShippingBucket()
            response = client.get(uri: bucketsURI, requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, 2)

        cleanup:
            clearHeaders()
            
         where:
            responseValidator << new GetShippingBucketListResponseHandler()
    }


    def "Get shipping bucket as anonymous user"()
    {
        given: "A basket with anonymous user"
            createBasket()
            addLineItem(new LineItemV1(productSKU_One, "1"))
            ShippingBucket bucket = getShippingBucket()

        when: "Get shipping bucket from basket with unknown id"
            def response = client.get(uri: basketsURI + "/wrongBasketID/buckets", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Get shipping bucket with unknown id"
            response = client.get(uri: bucketsURI + "/wrongBucketID", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusNotFound(response, ERROR_SHIPPING_BUCKET_NOT_FOUND)

        when: "Get shipping bucket by anonymous user"
            response = client.get(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, bucket)

        cleanup:
            clearHeaders()
            
         where:
            responseValidator << new GetShippingBucketResponseHandler()
    }

    def "Get shipping bucket as registered user"()
    {
        given: "A basket with registered user"
            client.setAuthorizationHeader(user, password)
            createBasket()
            addLineItem(new LineItemV1(productSKU_One, "1"))
            ShippingBucket bucket = getShippingBucket()
            
        when: "Get shipping bucket from basket with unknown id"
            def response = client.get(uri: basketsURI + "/wrongBasketID/items", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response)

        when: "Get shipping bucket with unknown id"
            response = client.get(uri: bucketsURI + "/wrongItemID", requestContentType: JSON)

        then: "Request failed => status code should be 404"
            responseValidator.assertStatusNotFound(response, ERROR_SHIPPING_BUCKET_NOT_FOUND)

        when: "Get shipping bucket by registered user"
            response = client.get(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON)

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, bucket)

        cleanup:
            clearHeaders()
            
         where:
            responseValidator << new GetShippingBucketResponseHandler()
    }

    def "Add/Update shipping address of a shipping bucket as anonymous user"()
    {
        given: "A basket with anonymous user"
            createBasket()
            addLineItem(new LineItemV1(productSKU_One, "1"))
            ShippingBucket bucket = getShippingBucket()
        
        when: "Add shipping address of a shipping bucket"
            AddressV1 address = createBasketAddress()
            def response = client.patch(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON, body: getShippingAddressdEntry(address.urn))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ["shipToAddress", address.urn])
            
        when: "Update shipping address of a shipping bucket"
            AddressV1 address2 = createBasketAddress()
            response = client.patch(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON, body: getShippingAddressdEntry(address2.urn))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ["shipToAddress", address2.urn])

        when: "Update shipping method of a shipping bucket with invalid shipping method"
            response = client.patch(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON, body: getShippingAddressdEntry("noAddress"))

        then: "Request failed => status code should be 422"
            responseValidator.assertStatusUnprocessable(response, [ERROR_COULD_NOT_UPDATE_SHIPPING_BUCKET, ERROR_ADDRESS_NOT_FOUND])

        cleanup:
            clearHeaders()
            
         where:
            responseValidator << new UpdateShippingBucketResponseHandler()
    }
    
    def "Add/Update shipping address of a shipping bucket as registered user"()
    {
        given: "A basket with anonymous user"
            client.setAuthorizationHeader(user, password)
            createBasket()
            addLineItem(new LineItemV1(productSKU_One, "1"))
            ShippingBucket bucket = getShippingBucket()

        when: "Add shipping address of a shipping bucket"
            AddressV1 address = createBasketAddress()
            def response = client.patch(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON, body: getShippingAddressdEntry(address.urn))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ["shipToAddress", address.urn])
            
        when: "Update shipping address of a shipping bucket"
            AddressV1 address2 = createBasketAddress()
            response = client.patch(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON, body: getShippingAddressdEntry(address2.urn))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ["shipToAddress", address2.urn])

        when: "Update shipping method of a shipping bucket with invalid shipping method"
            response = client.patch(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON, body: getShippingAddressdEntry("noAddress"))

        then: "Request failed => status code should be 422"
            responseValidator.assertStatusUnprocessable(response, [ERROR_COULD_NOT_UPDATE_SHIPPING_BUCKET, ERROR_ADDRESS_NOT_FOUND])

        cleanup:
            clearHeaders()
            
        where:
            responseValidator << new UpdateShippingBucketResponseHandler()
    }

    def "Add/Update shipping method of a shipping bucket as anonymous user"()
    {
        given: "A basket with anonymous user"
            createBasket()
            addLineItem(new LineItemV1(productSKU_One, "1"))
            ShippingBucket bucket = getShippingBucket()
        
        when: "Add shipping method of a shipping bucket"
            def response = client.patch(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON, body: getShippingMethodEntry(DEFAULT_SHIPPING_METHOD))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ["shippingMethod", DEFAULT_SHIPPING_METHOD])

        
        when: "Update shipping method of a shipping bucket"
            response = client.patch(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON, body: getShippingMethodEntry(OTHER_SHIPPING_METHOD))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ["shippingMethod", OTHER_SHIPPING_METHOD])

        when: "Update shipping method of a shipping bucket with invalid shipping method"
            response = client.patch(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON, body: getShippingMethodEntry("NoShippingMethod"))

        then: "Request failed => status code should be 422"
            responseValidator.assertStatusUnprocessable(response, [ERROR_COULD_NOT_UPDATE_SHIPPING_BUCKET, ERROR_SHIPPING_METHOD_NOT_FOUND])

        cleanup:
            clearHeaders()
            
        where:
            responseValidator << new UpdateShippingBucketResponseHandler()
    }
    
    def "Add/Update shipping method of a shipping bucket as registered user"()
    {
        given: "A basket with registered user"
            client.setAuthorizationHeader(user, password)
            createBasket()
            addLineItem(new LineItemV1(productSKU_One, "1"))
            ShippingBucket bucket = getShippingBucket()

        when: "Add shipping method of a shipping bucket"
            def response = client.patch(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON, body: getShippingMethodEntry(DEFAULT_SHIPPING_METHOD))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ["shippingMethod", DEFAULT_SHIPPING_METHOD])

        when: "Update shipping method of a shipping bucket"
            response = client.patch(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON, body: getShippingMethodEntry(OTHER_SHIPPING_METHOD))

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, ["shippingMethod", OTHER_SHIPPING_METHOD])

        when: "Update shipping method of a shipping bucket with invalid shipping method"
            response = client.patch(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON, body: getShippingMethodEntry("NoShippingMethod"))

        then: "Request failed => status code should be 422"
            responseValidator.assertStatusUnprocessable(response, [ERROR_COULD_NOT_UPDATE_SHIPPING_BUCKET, ERROR_SHIPPING_METHOD_NOT_FOUND])

        cleanup:
            clearHeaders()
            
        where:
            responseValidator << new UpdateShippingBucketResponseHandler()
    }

    def "Get eligible shipping methods list as registered user"()
    {
        given: "A basket with registered user"
            client.setAuthorizationHeader(user, password)
            createBasket()
            addLineItem(new LineItemV1(productSKU_One, "1"))
            ShippingBucket bucket = getShippingBucket()
            
        when: "Get eligible shipping methods"
            def response = client.get(uri: bucketsURI + "/" + bucket.id + "/eligible-shipping-methods", requestContentType: JSON)

        then: "Request success => status code should be 200"
            new JsonResponseValidator().assertStatusOk(response)

        cleanup:
            clearHeaders()
    }

    def "Get shipping bucket with include data"()
    {
        given: "An anonymous user with a basket"
            String basketID = createBasket()
            addLineItem(new LineItemV1(productSKU_One, "1"))
            ShippingBucket bucket = getShippingBucket()

        when: "Send \"GET /baskets/<basket-id>/buckets/<bucket-id>?include=shipToAddress\" request"
            AddressV1 address = createBasketAddress()
            client.patch(uri: bucketsURI + "/" + bucket.id, requestContentType: JSON, body: getShippingAddressdEntry(address.urn))
            def response = client.get(uri: bucketsURI + "/" + bucket.id + INCLUDE_PREFIX + ALL_INCLUDES, requestContentType: JSON)

        then: "Request is successful and bucket is returned with includes"
            responseValidator.assertStatusOk(response, [ (SHIPPING_METHOD_INCLUDE) : DEFAULT_SHIPPING_METHOD, (SHIP_TO_ADDRESS_INCLUDE) : address])

        cleanup:
            clearHeaders()

        where:
            responseValidator << new ResponseWithIncludesHandler()
    }
    
    def "Update shipping bucket and respond with include data"()
    {
        given: "A basket with anonymous user"
            createBasket()
            addLineItem(new LineItemV1(productSKU_One, "1"))
            ShippingBucket bucket = getShippingBucket()
        
        when: "Add shipping method of a shipping bucket"
            def response = client.patch(uri: bucketsURI + "/" + bucket.id + INCLUDE_PREFIX + ALL_INCLUDES, requestContentType: JSON, body: "{}")

        then: "Request success => status code should be 200 and bucket is returned with includes"
            responseValidator.assertStatusOk(response, [ (SHIPPING_METHOD_INCLUDE) : DEFAULT_SHIPPING_METHOD, (SHIP_TO_ADDRESS_INCLUDE) : null])

        cleanup:
            clearHeaders()
            
        where:
            responseValidator << new ResponseWithIncludesHandler()
    }
    
    def String createBasket()
    {
        super.createBasket()
        bucketsURI = basketURI + BUCKETS_RESOURCE
    }

    /**
     * @return the first shipping bucket
     */
    private ShippingBucket getShippingBucket()
    {
        def response = client.get(uri: bucketsURI, requestContentType: JSON)
        
        ShippingBucket bucket = new ShippingBucket(response.data.data[0].id)
        bucket.basketid = response.data.data[0].basket
        bucket.shippingMethod = response.data.data[0].shippingMethod
        bucket.shipToAddress = response.data.data[0].shipToAddress
        return bucket;
    }

    def String getShippingMethodEntry(String name)
    {
        return toJSON{ shippingMethod name }.bodyString
    }
    
    def String getShippingAddressdEntry(String urn)
    {
        return toJSON{ shipToAddress urn }.bodyString
    }
}

/**
 *  VALIDATION CLASS IMPLEMENTATIONS
 */
class GetShippingBucketListResponseHandler extends JsonResponseValidator
{
    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        def data = responseData.data
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

class GetShippingBucketResponseHandler extends JsonResponseValidator
{
    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        def data = responseData.data
        assert data.id == expectedData.id
        assert data.basket == expectedData.basketid
        assert data.shippingMethod == expectedData.shippingMethod
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
        
        if(expectedIncludes.containsKey(ShippingBucketRestSpecV1.SHIPPING_METHOD_INCLUDE))
        {
            def expectedShippingMethod = expectedIncludes[ShippingBucketRestSpecV1.SHIPPING_METHOD_INCLUDE]
            def shippingMethod = actualIncludes.shippingMethod.get(expectedShippingMethod)
            assert shippingMethod !=null
            assert shippingMethod.id.equals(expectedShippingMethod)
            assert shippingMethod.name !=null
            assert shippingMethod.description !=null
        }
        if(expectedIncludes.containsKey(ShippingBucketRestSpecV1.SHIP_TO_ADDRESS_INCLUDE))
        {
            AddressV1 expectedAddress = expectedIncludes[ShippingBucketRestSpecV1.SHIP_TO_ADDRESS_INCLUDE]

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

class UpdateShippingBucketResponseHandler extends JsonResponseValidator
{
    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        if(expectedData != null) {
            if (expectedData[0] == "shippingMethod") {
                assert responseData.data.shippingMethod == expectedData[1]
            }
            else if (expectedData[0] == "shipToAddress") {
                assert responseData.data.shipToAddress == expectedData[1]
            }
        }
    }

    @Override
    void assertStatusUnprocessable(def response, def expectedData = null)
    {
        hasStatusUnprocessable(response)
        hasErrorCode(response.data, expectedData[0])
        hasErrorCause(response.data, expectedData[1])
    }
}

class ShippingBucket
{
    def id
    def String[] products
    def shippingMethod
    def shipToAddress
    def basketid
    
    public ShippingBucket(String id)
    {
        this.id = id;
    }
}