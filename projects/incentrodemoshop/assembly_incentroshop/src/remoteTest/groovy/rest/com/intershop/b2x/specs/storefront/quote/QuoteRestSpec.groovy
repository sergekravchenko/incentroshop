package rest.com.intershop.b2x.specs.storefront.quote

import rest.com.intershop.b2x.specs.storefront.common.RestSpec
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Title
import spock.lang.Unroll

@Narrative("""
As a customer
I want to use the Intershop REST API
to manage my quotes
""")
@Title("i want to see my quotes")
class QuoteRestSpec extends RestSpec {

	@Shared
    String baseUri, quoteRequestListURI, quoteRequestURI, quoteRequestLineItemListURI, quoteRequestLineItemURI, quotesURI

	@Shared
	String quoteRequestID, newLineItemID

	@Shared
	String productSKU1, productSKU2

    def setupSpec()
    {
        setup:

            productSKU1             = testData.get("rest.quotes.products")[0]
            productSKU2             = testData.get("rest.quotes.products")[1]

            def customer            = testData.get("rest.quotes.customer")[0]
            def user                = testData.get("rest.quotes.eMail")[0]
            def password            = testData.get("rest.quotes.password")[0]

            def restUri             = testData.get("rest.base.uri")[0] + "/customers/${customer}/users/${user}"

            quoteRequestListURI     = restUri + "/quoterequests"
            quotesURI               = restUri + "/quotes"

            client.setAuthorizationHeader(user, password)

			client.setAuthorizationHeader(user, password)
    }

    @Unroll
    def "Happy path test QuoteRequest via #contentType"() {
		given:
			client.headers['Content-Type'] = contentType
            client.headers['Accept'] = contentType
		when: "Create a new quote request."
			def body = ""
            if (contentType == XML) 
			{ 
                def writer = new StringWriter()
                def xml = new groovy.xml.MarkupBuilder(writer).NewQuoteRequest("type":"NewQuoteRequest")
                body = writer.toString().replaceAll("'",'"')
			}

			def response = client.post(uri: quoteRequestListURI, requestContentType: contentType, body: body)
        then: "Test if the created quote request id is available in the response."
            with(response) 
			{
				assert success
				assert status == 201
                assert data.title != null
                assert data.uri != null
				assertType(data, "Link", contentType )
			}

			// Save QuoteRequestID for further usage
			// Assignments have to be enclosed by this useless if-statement otherwhise the compiler fails
			if (response.data.title != null) 
			{
				quoteRequestID = response.data.title
				quoteRequestURI = quoteRequestListURI + "/" + quoteRequestID
				quoteRequestLineItemListURI = quoteRequestURI + "/items"
			} 
			
		when: "Set quote request line items"
            body = ""
            if (contentType == XML) 
			{ 
                def writer = new StringWriter()
                def xml = new groovy.xml.MarkupBuilder(writer).QuoteRequestLineItemCollection("type":"QuoteRequestLineItemCollection") 
				{
                    elements("type":"QuoteRequestLineItem") 
					{
                        QuoteRequestLineItem("type":"QuoteRequestLineItem") 
						{
                            productSKU(productSKU1)
                            quantity("type":"Quantity") 
							{
                                value("2")
                                unit("piece")
                            }
                        }
                        QuoteRequestLineItem("type":"QuoteRequestLineItem") 
						{
                            productSKU(productSKU2)
                            quantity("type":"Quantity") 
							{
                                value("3")
                                unit("piece")
                            }
                        }
                    }
                }
                body = writer.toString().replaceAll("'",'"')
            }
            
            if (contentType == JSON) 
			{ 
				def builder = new groovy.json.JsonBuilder()
                builder 
				{
                    elements 
                        {
                           productSKU productSKU1
                           quantity (
                               value: '2'
                           )
                        }
                        {
                            productSKU productSKU2
                            quantity (
                                value: '3'
                            )
                        }
                }
                body = builder.toString()
            }

            response = client.put(uri: quoteRequestLineItemListURI, requestContentType: contentType, body: body, debugMessage: "Set quote request line items")

        then: "Test if the lineitems are successfully created."
        
			with(response) 
			{
				assert success
				assert status == 200
				assert data.status  == "SUCCESS"
				assert data.failedProductSKUs != null
				assertType(data, "QuoteLineItemResult", contentType )
			}

        when: "Create a line item for quote request"
            body = ""
            // Add Accept header
            if (contentType == XML) 
			{
                def writer = new StringWriter()
                def xml = new groovy.xml.MarkupBuilder(writer).QuoteRequestLineItem("type":"QuoteRequestLineItem") 
				{
                    productSKU(productSKU1)
                    quantity("type":"Quantity") 
					{
                        value("2")
                        unit("piece")
                    }
                }
                body = writer.toString().replaceAll("'",'"')
            }
            
            if (contentType == JSON) {
                def builder = new groovy.json.JsonBuilder()
                builder 
				{
                   productSKU productSKU1
                   quantity (
                       value: '2'
                   )
                }
                body = builder.toString()
            }
			response = client.post(uri: quoteRequestLineItemListURI, requestContentType: contentType, body: body, debugMessage: "Create a line item for quote request")

        then: "Test if the created line item ids is available within the response."
        
			with(response) 
			{
				assert success
				assert status == 201
				assert data.title != null
				assert data.uri != null
				assertType(data, "Link", contentType )
			}

			// Save line item for further usage
			if (response.data.title != null) {
				newLineItemID = response.data.title
				quoteRequestLineItemURI = quoteRequestLineItemListURI + "/" + newLineItemID
			}

        when: "Get a list of quote requests"
			response = client.get(uri: quoteRequestListURI, debugMessage: "Get a list of quote requests")
        then: "Test if the created quote request id is available within the response."
            
			with(response) 
			{
				assert success
				assert status == 200
				assert data.elements != null
				assertType(data, "ResourceCollection", contentType );
				assertName(data, "quoteRequests", contentType );

	            if (contentType == XML) 
				{
					assert data.elements.'**'.grep {
						it.title == quoteRequestID
					}.size() == 1
				}
				if (contentType == JSON)
				{
					assert data.elements.grep { 
						it.title == quoteRequestID
					}.size() == 1
				}
			}
			
        when: "Get list of line items of a quote request"
			response = client.get(uri: quoteRequestLineItemListURI, debugMessage: "Get list of line items of a quote request")
        then: "Test whether the created quote items are available within the response."
			with(response) 
			{
				assert success
				assert status == 200
				assert data.elements != null
				assertType(data, "ResourceCollection", contentType );
				assertName(data, "items", contentType );

				if (contentType == XML) 
				{
					assert data.elements.'*'.grep {[
						it.attributes.ResourceAttribute.value == productSKU1,
						it.attributes.ResourceAttribute.value == productSKU2
					]}.size() == 2
					
				}
				if (contentType == JSON) 
				{
					assert data.elements.grep {[
						it.title == productSKU1,
						it.title == productSKU2
					]}.size() == 2
				}
			}

        when: "Get the quote request details"
			response = client.get(uri: quoteRequestURI, debugMessage: "Get the quote request details")
        then: "Test if the quote request details are available within the response."
			with(response) 
			{
				assert success
				assert status == 200
				assert data.displayName != null
				assert data.id == quoteRequestID
				assertType(data, "QuoteRequest", contentType );
			}

        when: "Update data of a quote request"
            body = ""
            // Add Accept header
            if (contentType == XML) 
			{
                def writer = new StringWriter()
                def xml = new groovy.xml.MarkupBuilder(writer).QuoteRequest("type":"QuoteRequest") 
				{
                    displayName("new name...")
                    description("new description...")
                }
                body = writer.toString().replaceAll("'",'"')
            }
            
            if (contentType == JSON) 
			{
                
                def builder = new groovy.json.JsonBuilder()
                builder 
				{
                   displayName 'new name...'
                   description 'new description...'
                }
                body = builder.toString()
            }

			response = client.put(uri: quoteRequestURI, requestContentType: contentType, body: body, debugMessage: "Update data of a quote request")

        then: "Test if quote request is successfully updated"
			with(response) 
			{
				assert success
				assert status == 200
				assert data.id == quoteRequestID
				assert data.displayName == 'new name...'
				assert data.description == 'new description...'
				assertType(data, "QuoteRequest", contentType );
			}

        when: "Update a line item of a quote request"
            body = ""
            // Add Accept header
            if (contentType == XML) 
			{
                def writer = new StringWriter()
                def xml = new groovy.xml.MarkupBuilder(writer).QuoteRequestLineItem("type":"QuoteRequestLineItem") 
				{
                    quantity("type":"Quantity") 
					{
                        value("30")
                        unit("piece")
                    }
                }
                body = writer.toString().replaceAll("'",'"')
            }
            
            if (contentType == JSON) {
                def builder = new groovy.json.JsonBuilder()
                builder {
                   type "QuoteRequestLineItem"
                   quantity {
                       type "Quantity"
                       value "30"
                       unit "piece"                       
                   }
                }
                body = builder.toString()
            }

			response = client.put(uri: quoteRequestLineItemURI, requestContentType: contentType, body: body, debugMessage: "Update a line item of a quote request")
        then: "Test if the line item is changed successfully."
			with(response) 
			{
				assert success
				assert status == 200
				assert data.quantity != null
				assert data.quantity.value == 30
				assert data.quantity.unit == "piece"
				assert data.productSKU == productSKU1
				assertType(data, "QuoteRequestLineItem", contentType );
			}
        when: "Get a line item of a quote request"
			response = client.get(uri: quoteRequestLineItemURI, debugMessage: "Get a line item of a quote request")
        then: "Test if the line item is available in response."
			with(response) 
			{
				assert success
				assert status == 200
				assert data.quantity != null
				assert data.quantity.value == 30
				assert data.quantity.unit == "piece"
				assert data.productSKU == productSKU1
				assertType(data, "QuoteRequestLineItem", contentType );
			}
        
        when: "Delete a line item of a quote request"
			response = client.delete(uri: quoteRequestLineItemURI, debugMessage: "Delete a line item of a quote request")
        then: "Test if the quote line item has been successfully deleted"
			with(response) 
			{
				assert success
				assert status == 204
			}
        when: "Submit a quote request"
            body = ""
            // Add Accept header
            if (contentType == XML) 
			{
                def writer = new StringWriter()
                def xml = new groovy.xml.MarkupBuilder(writer).QuoteRequest("type":"QuoteRequest") 
				{
                    quoteRequestID(quoteRequestID)
                }
                body = writer.toString().replaceAll("'",'"')
            }
            
            if (contentType == JSON) 
			{
                def builder = new groovy.json.JsonBuilder()
                builder 
				{
                   quoteRequestID quoteRequestID
                }
                body = builder.toString()
            }

			response = client.post(uri: quotesURI, requestContentType: contentType, body: body, debugMessage: "Submit a quote request")
        then: "Test if the quote request is successfully submitted"
            with(response) 
			{
				assert success
				assert status == 202
			}
        when: "Delete a quote request"
			response = client.delete(uri: quoteRequestURI, debugMessage: "Delete a quote request")   
        then: "Test if the quote has been successfully deleted"
			with(response) 
			{
				assert success
				assert status == 204
			}
            assert response.success
            assert response.status == 204
		where:
			contentType << [JSON, XML]            
    }
}
