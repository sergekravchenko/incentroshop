package rest.com.intershop.b2x.specs.storefront.basket.payment

import static rest.com.intershop.b2x.specs.storefront.common.RequestBuilder.toJSON

import java.time.*

import rest.com.intershop.b2x.specs.storefront.basket.AbstractBasketRestSpecV1
import rest.com.intershop.b2x.specs.storefront.common.JsonResponseValidator
import rest.com.intershop.b2x.specs.storefront.common.objects.LineItemV1
import spock.lang.*

@Narrative("""
As a customer
I want to use the Intershop Webshop REST API
to manage my payments of a basket
""")
@Title("I want to handle payments of a basket")
class BasketPaymentRestSpecV1 extends AbstractBasketRestSpecV1 {

    static final String ELIGIBLE_PAYMENT_METHODS_RESOURCE = "/eligible-payment-methods";
    static final String PAYMENTS_RESOURCE = "/payments";
    static final String OPEN_TENDER_PAYMENT_RESOURCE = "/open-tender";
    static final String INSTRUMENT_INCLUDE = "paymentInstrument"
    static final String INSTRUMENTS_INCLUDE = "paymentInstruments"

    @Shared
    String productSKU_One;

    def setupSpec()
    {
        setup:

        user = getTestValue("rest.basket.user.login");
        password = getTestValue("rest.basket.user.password");
        productSKU_One = getTestValue("rest.basket.productId");
        String sitePath = getTestValue("rest.base.uri");
        basketsURI = "${sitePath}/baskets";
    }

    def "Get eligible payment method as anonymous user"()
    {
        given: "A basket with anonymous user"
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "10");
            createBasket();

        when: "Get eligible payment methods with wrong basket id"
            def response = client.get(uri: basketsURI + "/wrongBasketID" + ELIGIBLE_PAYMENT_METHODS_RESOURCE, requestContentType: JSON);

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response);

        when: "Get eligible payment methods with basket id"
            response = client.get(uri: basketURI + ELIGIBLE_PAYMENT_METHODS_RESOURCE, requestContentType: JSON);

        then: "Request success => status code should be 200 and contain no payment method"
            responseValidator.assertStatusOk(response, null);
            
        when: "Add a line item by anonymous user"
            response = client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody(lineItem));

        then: "Request success => status code should be 201"
            responseValidator.assertStatusCreated(response, lineItem);
            
        when: "Get eligible payment methods with basket id"
            response = client.get(uri: basketURI + ELIGIBLE_PAYMENT_METHODS_RESOURCE, requestContentType: JSON);

        then: "Request success => status code should be 200 and contains 7 demo payment method" 
            responseValidator.assertStatusOk(response, ["ISH_CREDITCARD", "ISH_ONLINEPAY", "ISH_CASH_ON_DELIVERY", 
                "ISH_INVOICE", "ISH_GIFT_CARD", "ISH_CASH_IN_ADVANCE", "ISH_FASTPAY"]); // all demo payments, but not ISH_DEBIT_TRANSFER
            
        when: "Get eligible payment methods and instruments with basket id"
            response = client.get(uri: basketURI + ELIGIBLE_PAYMENT_METHODS_RESOURCE + INCLUDE_PREFIX + INSTRUMENTS_INCLUDE, requestContentType: JSON);

        then: "Request success => status code should be 200 and contains 7 demo payment method" 
            responseValidator.assertStatusOk(response, ["ISH_CREDITCARD", "ISH_ONLINEPAY", "ISH_CASH_ON_DELIVERY", 
                "ISH_INVOICE", "ISH_GIFT_CARD", "ISH_CASH_IN_ADVANCE", "ISH_FASTPAY"]);
            includeValidator.assertStatusOk(response, [ (INSTRUMENTS_INCLUDE) : ["ISH_ONLINEPAY", "ISH_CASH_ON_DELIVERY", 
                "ISH_INVOICE", "ISH_CASH_IN_ADVANCE", "ISH_FASTPAY"]] )

        cleanup:
            clearHeaders();

        where:
            responseValidator << new GetEligiblePaymentMethodsResponseHandler();
            includeValidator << new ResponseWithIncludesHandler();    
    }
    
    def "Get eligible payment method as registered user"()
    {
        given: "A registered user"
            client.setAuthorizationHeader(user, password);
            LineItemV1 lineItem = new LineItemV1(productSKU_One, "10");
            createBasket();

        when: "Get eligible payment methods with wrong basket id"
            def response = client.get(uri: basketsURI + "/wrongBasketID" + ELIGIBLE_PAYMENT_METHODS_RESOURCE, requestContentType: JSON);

        then: "Request failed => status code should be 404"
            assertBasketNotFound(response);

        when: "Get eligible payment methods with basket id"
            response = client.get(uri: basketURI + ELIGIBLE_PAYMENT_METHODS_RESOURCE, requestContentType: JSON);

        then: "Request success => status code should be 200 and contain not payment method"
            responseValidator.assertStatusOk(response, null);
            
        when: "Add a line item by anonymous user"
            response =  client.post(uri: itemsURI, requestContentType: JSON, body: getJSONLineItemsBody(lineItem));

        then: "Request success => status code should be 201"
            responseValidator.assertStatusCreated(response, lineItem);
            
        when: "Get eligible payment methods with basket id"
            response = client.get(uri: basketURI + ELIGIBLE_PAYMENT_METHODS_RESOURCE + INCLUDE_PREFIX + INSTRUMENTS_INCLUDE, requestContentType: JSON);

        then: "Request success => status code should be 200 and contains all 8 demo payment method"
            responseValidator.assertStatusOk(response, ["ISH_CREDITCARD", "ISH_ONLINEPAY", "ISH_CASH_ON_DELIVERY", "ISH_INVOICE", 
                "ISH_GIFT_CARD", "ISH_CASH_IN_ADVANCE", "ISH_FASTPAY", "ISH_DEBIT_TRANSFER"]);
            includeValidator.assertStatusOk(response, [ (INSTRUMENTS_INCLUDE) : ["ISH_ONLINEPAY", "ISH_CASH_ON_DELIVERY",
                "ISH_INVOICE", "ISH_CASH_IN_ADVANCE", "ISH_FASTPAY"]] )

        cleanup:
            clearHeaders();

        where:
            responseValidator << new GetEligiblePaymentMethodsResponseHandler();
            includeValidator << new ResponseWithIncludesHandler();
    }

    def "Create open tender payment as anonymous user"()
    {
        given: "A basket with anonymous user"
            createBasket();
            addLineItem(new LineItemV1(productSKU_One, "10"));
            
        when: "Create open tender payment"
            def response = client.put(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE + INCLUDE_PREFIX + INSTRUMENT_INCLUDE, requestContentType: JSON, body: getPaymentInstrument("ISH_INVOICE"));

        then: "Request success => status code should be 201"
            responseValidator.assertStatusCreated(response, "ISH_INVOICE");
            includeValidator.assertStatusCreated(response, [ (INSTRUMENT_INCLUDE) : "ISH_INVOICE"] )
            
        when: "Set new open tender payment"
            response = client.put(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE + INCLUDE_PREFIX + INSTRUMENT_INCLUDE, requestContentType: JSON, body: getPaymentInstrument("ISH_CASH_ON_DELIVERY"));

        then: "Request success => status code should be 201"
            responseValidator.assertStatusCreated(response, "ISH_CASH_ON_DELIVERY");
            includeValidator.assertStatusCreated(response, [ (INSTRUMENT_INCLUDE) : "ISH_CASH_ON_DELIVERY"] )
            
        when: "Try to set invalid new open tender payment"
            response = client.put(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE, requestContentType: JSON, body: getPaymentInstrument("INVALID"));

        then: "Request failure => status code should be 422"
            responseValidator.assertStatusUnprocessable(response, "payment.payment_instrument_not_found.error");
            
        cleanup:
            clearHeaders();

        where:
            responseValidator << new PutOpenTenderPaymentResponseHandler();
            includeValidator << new ResponseWithIncludesHandler();
    }
    
    def "Create open tender payment as registered user"()
    {
        given: "A registered user"
            client.setAuthorizationHeader(user, password);
            createBasket();
            addLineItem(new LineItemV1(productSKU_One, "10"));
            
        when: "Create open tender payment"
            def response = client.put(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE + INCLUDE_PREFIX + INSTRUMENT_INCLUDE, requestContentType: JSON, body: getPaymentInstrument("ISH_INVOICE"));

        then: "Request success => status code should be 201"
            responseValidator.assertStatusCreated(response, "ISH_INVOICE");
            includeValidator.assertStatusCreated(response, [ (INSTRUMENT_INCLUDE) : "ISH_INVOICE"] )
            
        when: "Set new open tender payment"
            response = client.put(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE + INCLUDE_PREFIX + INSTRUMENT_INCLUDE, requestContentType: JSON, body: getPaymentInstrument("ISH_CASH_ON_DELIVERY"));

        then: "Request success => status code should be 201"
            responseValidator.assertStatusCreated(response, "ISH_CASH_ON_DELIVERY");
            includeValidator.assertStatusCreated(response, [ (INSTRUMENT_INCLUDE) : "ISH_CASH_ON_DELIVERY"] )
            
        when: "Try to set invalid new open tender payment"
            response = client.put(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE, requestContentType: JSON, body: getPaymentInstrument("INVALID"));

        then: "Request failure => status code should be 422"
            responseValidator.assertStatusUnprocessable(response, "payment.payment_instrument_not_found.error");
            
        cleanup:
            clearHeaders();

        where:
            responseValidator << new PutOpenTenderPaymentResponseHandler();
            includeValidator << new ResponseWithIncludesHandler();
    }
    
    def "Delete open tender payment as anonymous user"()
    {
        given: "A basket with anonymous user"
            createBasket();
            addLineItem(new LineItemV1(productSKU_One, "10"));
            
        when: "Create open tender payment"
            def response = client.put(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE, requestContentType: JSON, body: getPaymentInstrument("ISH_INVOICE"));

        then: "Request success => status code should be 201"
            responseValidator.assertStatusCreated(response, "ISH_INVOICE");
            
        when: "Delete the open tender payment"
            response = client.delete(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE, requestContentType: JSON);

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response);
            
        when: "Get the payments after deletion"
            response = client.get(uri: basketURI + PAYMENTS_RESOURCE, requestContentType: JSON);

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, []);
            
        when: "Delete the open tender payment"
            response = client.delete(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE + "-invalid", requestContentType: JSON);

        then: "Request failure => status code should be 404"
            responseValidator.assertStatusNotFound(response, "payment.not_found.error");

        cleanup:
            clearHeaders();

        where:
            responseValidator << new DeleteOpenTenderPaymentResponseHandler();
    }
    
    def "Delete open tender payment as registered user"()
    {
        given: "A registered user"
            client.setAuthorizationHeader(user, password);
            createBasket();
            addLineItem(new LineItemV1(productSKU_One, "10"));
            
        when: "Create open tender payment"
            def response = client.put(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE, requestContentType: JSON, body: getPaymentInstrument("ISH_INVOICE"));

        then: "Request success => status code should be 201"
            responseValidator.assertStatusCreated(response, "ISH_INVOICE");
            
        when: "Delete the open tender payment"
            response = client.delete(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE, requestContentType: JSON);

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response);
            
        when: "Get the payments after deletion"
            response = client.get(uri: basketURI + PAYMENTS_RESOURCE, requestContentType: JSON);

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, []);
            
        when: "Delete the open tender payment"
            response = client.delete(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE + "-invalid", requestContentType: JSON);

        then: "Request failure => status code should be 404"
            responseValidator.assertStatusNotFound(response, "payment.not_found.error");

        cleanup:
            clearHeaders();

        where:
            responseValidator << new DeleteOpenTenderPaymentResponseHandler();
    }
    
    def "Get payments as anonymous user"()
    {
        given: "A basket with anonymous user"
            createBasket();
            addLineItem(new LineItemV1(productSKU_One, "10"));
            
        when: "Get the payments before setting one"
            def response = client.get(uri: basketURI + PAYMENTS_RESOURCE, requestContentType: JSON);

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, null);
            
        when: "Create open tender payment"
            response = client.put(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE, requestContentType: JSON, body: getPaymentInstrument("ISH_INVOICE"));

        then: "Request success => status code should be 201"
            otResponseValidator.assertStatusCreated(response, "ISH_INVOICE");
            
        when: "Get the assigned payments"
            response = client.get(uri: basketURI + PAYMENTS_RESOURCE + INCLUDE_PREFIX + INSTRUMENT_INCLUDE, requestContentType: JSON);

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, "ISH_INVOICE");
            includeValidator.assertStatusOk(response, [ (INSTRUMENT_INCLUDE) : "ISH_INVOICE" ]);
            
            
        cleanup:
            clearHeaders();

        where:
            otResponseValidator << new PutOpenTenderPaymentResponseHandler();
            responseValidator << new GetPaymentsResponseHandler();
            includeValidator << new ResponseWithIncludesHandler();
    }

    
    def "Get payments as registered user"()
    {
        given: "Create a basket for a registered user"
            client.setAuthorizationHeader(user, password);
            createBasket();
            addLineItem(new LineItemV1(productSKU_One, "10"));
            
        when: "Get the payments before setting one"
            def response = client.get(uri: basketURI + PAYMENTS_RESOURCE, requestContentType: JSON);

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, null);
            
        when: "Create open tender payment"
            response = client.put(uri: basketURI + PAYMENTS_RESOURCE + OPEN_TENDER_PAYMENT_RESOURCE, requestContentType: JSON, body: getPaymentInstrument("ISH_INVOICE"));

        then: "Request success => status code should be 201"
            otResponseValidator.assertStatusCreated(response, "ISH_INVOICE");
            
        when: "Get the assigned payments with includes"
            response = client.get(uri: basketURI + PAYMENTS_RESOURCE + INCLUDE_PREFIX + INSTRUMENT_INCLUDE, requestContentType: JSON);

        then: "Request success => status code should be 200"
            responseValidator.assertStatusOk(response, "ISH_INVOICE");
            includeValidator.assertStatusOk(response, [ (INSTRUMENT_INCLUDE) : "ISH_INVOICE" ])

        cleanup:
            clearHeaders();

        where:
            otResponseValidator << new PutOpenTenderPaymentResponseHandler();
            responseValidator << new GetPaymentsResponseHandler();
            includeValidator << new ResponseWithIncludesHandler();
    }
    
    def String getPaymentInstrument(String id)
    {
        return toJSON{ paymentInstrument id }.bodyString;
    }
    
    class GetEligiblePaymentMethodsResponseHandler extends JsonResponseValidator
    {
        @Override
        void checkResponseData(def responseData, def expectedData = null)
        {
            if(expectedData == null)
            {
                // assert an empty data section
                hasData(responseData)
                assert responseData.data.size() == 0
            }
            else
            {
                assert expectedData.size() == responseData.data.size();
                for(String pm : expectedData)
                {
                    assert responseData.data.id.contains(pm);
                }
            }
        }
        
        void assertStatusCreated(def response, def expectedData = null)
        {
            hasStatusCreated(response);
            hasNoErrors(response.data);
        }
    }

    class PutOpenTenderPaymentResponseHandler extends JsonResponseValidator
    {
        @Override
        void checkResponseData(def responseData, def expectedData = null)
        {
            def data = responseData.data;
            if(expectedData != null)
            {
                assert data.id == "open-tender";
                assert data.paymentInstrument == expectedData;
            }
        }
        
        void assertStatusCreated(def response, def expectedData = null)
        {
            hasStatusCreated(response);
            hasNoErrors(response.data);
            checkResponseData(response.data, expectedData)
        }
        
        @Override
        void assertStatusUnprocessable(def response, def expectedData = null)
        {
            hasStatusUnprocessable(response)
            hasErrorCause(response.data, "payment.payment_instrument_not_found.error")
        }
    }
    
    class DeleteOpenTenderPaymentResponseHandler extends PutOpenTenderPaymentResponseHandler
    {
        void assertStatusOk(def response, def expectedData = null)
        {
            hasStatusOk(response)
            hasAuthenticationToken(response)
            hasNoErrors(response.data)
        }
    }
    
    class GetPaymentsResponseHandler extends PutOpenTenderPaymentResponseHandler
    {
        @Override
        void checkResponseData(def responseData, def expectedData = null)
        {
            if(expectedData == null)
            {
                // assert an empty data section
                hasData(responseData)
                assert responseData.data.size() == 0
            }
            else
            {
                assert responseData.data.paymentInstrument.contains(expectedData);
            }
        }
        
        void assertStatusOk(def response, def expectedData = null)
        {
            hasStatusOk(response)
            hasAuthenticationToken(response)
            hasNoErrors(response.data)
            
            checkResponseData(response.data, expectedData)
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
            
            if(expectedIncludes.containsKey(BasketPaymentRestSpecV1.INSTRUMENT_INCLUDE))
            {
                def expectedInstrument = expectedIncludes[BasketPaymentRestSpecV1.INSTRUMENT_INCLUDE]
                def actualInstrumentInclude = actualIncludes[BasketPaymentRestSpecV1.INSTRUMENT_INCLUDE]
                
                assert actualInstrumentInclude.containsKey(expectedInstrument)
                
                def actualInstrument = actualInstrumentInclude[expectedInstrument]
                
                assert(actualInstrument != null)
                assert(actualInstrument.id.equals(expectedInstrument))
            }
            if(expectedIncludes.containsKey(BasketPaymentRestSpecV1.INSTRUMENTS_INCLUDE))
            {
                def expectedInstruments = expectedIncludes[BasketPaymentRestSpecV1.INSTRUMENTS_INCLUDE]
                def actualInstruments = actualIncludes[BasketPaymentRestSpecV1.INSTRUMENTS_INCLUDE]
                
                for(String instrument : expectedInstruments)
                {
                    assert actualInstruments.containsKey(instrument)
                    assert actualInstruments[instrument].id.equals(instrument)
                }
            }
        }
    }
}