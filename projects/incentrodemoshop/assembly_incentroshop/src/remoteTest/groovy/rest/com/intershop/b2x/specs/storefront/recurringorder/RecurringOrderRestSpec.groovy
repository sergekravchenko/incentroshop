package rest.com.intershop.b2x.specs.storefront.recurringorder

import rest.com.intershop.b2x.specs.storefront.common.RestSpec
import spock.lang.*

@Narrative("""
As a customer
I want to use the Intershop Webshop REST API
to manage my recurring orders (subscriptions)
""")
@Title("I want to order a recurring order and manage it (subscriptions)")
@Ignore("Temporary ignored until required changes are available")
class RecurringOrderRestSpec extends RestSpec
{
	@Shared
    String basketsURI, ordersURI, recurringordersURI, basketURI, basketLineItemsURI, basketPaymentsURI, productSKU, basketID

    def setupSpec()
    {
        setup:

			productSKU 			    = testData.get("rest.recurringorders.productId")[0];

			def customer            = testData.get("rest.recurringorders.customer")[0];
			def user                = testData.get("rest.recurringorders.eMail")[0];
			def password            = testData.get("rest.recurringorders.password")[0];

			basketsURI 	            = testData.get("rest.base.uri")[0] + "/baskets";
			ordersURI               = testData.get("rest.base.uri")[0] + "/orders";
            recurringordersURI      = testData.get("rest.base.uri")[0] + "/customers/" + customer + "/recurringorders";

            client.setAuthorizationHeader(user, password)
    }

    @Unroll
    def "Happy path test RecurringOrderCreation via #contentType"()
    {
		given:
            client.headers['Content-Type'] = contentType
            client.headers['Accept'] = contentType
		when: "Create a new basket."
			def body = ""

            if (contentType == XML) 
			{ 
                def writer = new StringWriter()
                def xml = new groovy.xml.MarkupBuilder(writer).Basket("type":"Basket")
                body = writer.toString().replaceAll("'",'"')
			}

            def response = client.post(uri: basketsURI, requestContentType: contentType, body: body, debugMessage: "Create a new basket")
        then: "Test if the created basket id is available in the response."
            with(response) 
			{
				assert success
				assert status == 201
                assert data.title != null
                assert data.uri != null
				assertType(data, "Link", contentType );
			}

			// Save basketID for further usage
			// Assignments have to be enclosed by this useless if-statement otherwise the compiler fails
			if (response.data.title != null) 
			{
				basketID = response.data.title
				basketURI = basketsURI + "/" + basketID;
				basketLineItemsURI = basketURI + "/items"
                basketPaymentsURI   = basketURI + "/payments"
			} 

        when: "Add a line item to basket"
            body = ""
            
            // Add Accept header
            if (contentType == XML)
            {
                def writer = new StringWriter()
                def xml = new groovy.xml.MarkupBuilder(writer).LineItems("type":"LineItems")
                {
                    elements("type":"BasketLineItem")              
                    {
                        BasketLineItem("type":"BasketLineItem")
                        {
                            sku(productSKU)
                            quantity("type":"Quantity")
                            {
                                value("5")
                            }
                        }
                        BasketLineItem("type":"BasketLineItem")
                        {
                            sku(productSKU)
                            quantity("type":"Quantity")
                            {
                                value("3")
                            }
                        }
                    }
                }
                body = writer.toString().replaceAll("'",'"')
            }            
                 
            if (contentType == JSON) {
                def builder = new groovy.json.JsonBuilder()
                builder
                {
                    elements
                    {                                        
                        sku productSKU
                        quantity (
                            value: '5'
                        )
                    }
                    {
                        sku productSKU
                        quantity (
                            value: '3'
                        )
                    }
                }               
                body = builder.toString()
            }

            response = client.post(uri: basketLineItemsURI, requestContentType: contentType, body: body, debugMessage: "Add a line item to basket")

        then: "Test if the created line item id is available within the response."
        
            with(response)
            {
                assert success
                assert status == 201
                assert data.uri != null
                assertType(data, "Link", contentType );
            }

        when: "Set basket to recurrence"
            body = ""
            def date = new Date(System.currentTimeMillis())

            if (contentType == XML)
            {                
                def writer = new StringWriter()
                def xml = new groovy.xml.MarkupBuilder(writer).Basket("type":"Basket")
                {                    
                    recurrence("type":"Recurrence", "startDate":date.format("yyyy-MM-dd'T'HH:mm:ssXXX"), "interval":"P2W")                   
                }
                body = writer.toString().replaceAll("'",'"')
            }
            
            if (contentType == JSON)
            {
                def builder = new groovy.json.JsonBuilder()
                builder
                {
                    recurrence(
                       startDate: date,
                       interval: 'P2W'
                    )
                }                
                body = builder.toString()
            }

            response = client.put(uri: basketURI, requestContentType: contentType, body: body, debugMessage: "Mark basket as recurrent")

        then: "Test if the basket is successfully set to recurrence."
            with(response)
            {

                assert success
                assert status == 200
                assertType(data, "Basket", contentType );
            }

        when: "Add payment method to the basket."
            body = ""
            if (contentType == XML)
            {
                def writer = new StringWriter()
                def xml = new groovy.xml.MarkupBuilder(writer).Payment("type":"Payment", "name":"ISH_INVOICE")
                {
                   
                }
                body = writer.toString().replaceAll("'",'"')
            }

            if (contentType == JSON)
            {
                def builder = new groovy.json.JsonBuilder()
                builder
                {
                    name 'ISH_INVOICE'
                    type 'Payment'                    
                }
                body = builder.toString()
            }

            response = client.post(uri: basketPaymentsURI, requestContentType: contentType, body: body, debugMessage: "Add payment method to the basket")

        then: "Test if the payment method is successfully added to the basket."
            with(response)
            {
                assert success
                assert status == 201
                assert data.uri != null
                assertType(data, "Link", contentType );
            }

        // ============================================================================================================
        //  
        //  Recurring orders REST test for B2B is not complete because ...
        //  - there are not exists REST resources to handle the approval and
        //  - there are not exists REST resources to create a cost center as well as to assign a cost center to a basket
        //
        //  1. To create a new recurring order a cost center have to be assigned to the basket.
        //  2. To find the recurring order an approval step its needed before
        //    
        // ============================================================================================================

        when: "Create an order of the basket with recurrence"
            body = ""

            if (contentType == XML)
            {
                def writer = new StringWriter()
                def xml = new groovy.xml.MarkupBuilder(writer).OrderDescription("type":"OrderDescription")
                {
                    basketID(basketID)
                    acceptTermsAndConditions("true")
                }
                body = writer.toString().replaceAll("'",'"')
            }

            if (contentType == JSON) {
                def builder = new groovy.json.JsonBuilder()
                builder
                {
                   basketID basketID
                   acceptTermsAndConditions 'true'
                }
                body = builder.toString()
            }
            response = client.post(uri: ordersURI, requestContentType: contentType, body: body, debugMessage: "Create an order of the basket with recurrence")
        then: "Test if the created order id is available within the response."
            with(response)
            {
                assert success
                assert status == 202                
                assertType(data, "OrderValidation", contentType );
            }
        when: "Find the created recurring order"
            def recurringorderURI = recurringordersURI + "/" + basketID
            response = client.get(uri: recurringorderURI, requestContentType: contentType, debugMessage: "Get the created recurring order")
        then: "Test if the created recurring order id is available within the response."
            with(response)
            {
                log(DEBUG, "Recurring order found with id: " + data.id + "(" + basketID + ")")
                assert data.id == basketID
            } 
        when: "Search the created recurring order with list resource"
            response = client.get(uri: recurringordersURI, requestContentType: contentType, debugMessage: "Find the created recurring order with list resource")
        then: "Test if the created recurring order id is available within the response."
            with(response)
            {
                assert data.elements.find {
                    it.link.title == basketID
                } != null
            }
        where:
            contentType << [XML, JSON]
    }
}