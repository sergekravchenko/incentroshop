package rest.com.intershop.b2x.specs.storefront.punchout

import spock.lang.Ignore

import static java.net.HttpURLConnection.*
import static org.hamcrest.Matchers.*
import static spock.util.matcher.HamcrestSupport.*

import geb.com.intershop.b2x.testdata.TestDataLoader
import groovy.util.slurpersupport.GPathResult
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

@Ignore
@Title("I want to test requests with OCI Punchout User ")
class PunchoutRESTOciUserSpec extends Specification
{
    @Shared
    RESTClient client = new RESTClient()
    
    @Shared
    String baseUri
    
    @Shared
    String punchoutProductSkuToAdd 
    
    @Shared
    String punchoutProductQtyToAdd 
    
    @Shared 
    def customer
    
    @Shared
    def punchoutUserAuthorization

    final static XML = "text/xml"
    final static JSON = "application/json"
    final static CLEANUP = 'last-step-cleanup-data'
    
    private static Map<String,List> testData
    
    def setupSpec()
    {       
        setup:
        testData = TestDataLoader.getTestData();
        // REST URI setup
        def b2b_URI = testData.get("rest.b2b.uri.ish")[0]
        def hostName = System.properties['hostName']
        def port = System.properties['webserverPort']
        baseUri = "http://${hostName}:${port}/${b2b_URI}"
        // customer and user setup
        customer = testData.get("punchout.customer.oilcorp.id")[0]
        def punchoutUser = testData.get("punchout.user.login")[0]
        def punchoutUserPassword = testData.get("punchout.user.password")[0]
        println "Customer: ${customer} || PunchoutUser: ${punchoutUser}"
        // user with Role: APP_B2B_OCI_USER Permission: APP_B2B_SEND_OCI_BASKET
        punchoutUserAuthorization = 'Basic ' + (punchoutUser + ':' + punchoutUserPassword).bytes.encodeBase64().toString()
        // set client Authorization for user with Role: APP_B2B_OCI_USER Permission: APP_B2B_SEND_OCI_BASKET
        client.headers['Authorization'] = punchoutUserAuthorization
        // punchout product to add to basket
        punchoutProductSkuToAdd = testData.get("punchout.product.productId")[0]
        punchoutProductQtyToAdd = testData.get("punchout.product.quantity")[0]
    }

    @Unroll
    def "See which Punchout standards are supported via #usedContentType - access with punchout user"() {
        def restURL = baseUri + "/customers/${customer}/punchouts"
        client.headers['Accept'] = usedContentType
        client.headers['Content-Type'] = usedContentType
        when: "Ask for supported Punchout standards - access with punchout user"
            def response = client.get(uri: restURL,  requestContentType: usedContentType)
        then: "a list of supported punchout standards will be returned"
            with(response) {
                assert status == 200
                assert contentType == usedContentType
            }
            with(response.data) {
                if (elements instanceof GPathResult) {
                    elements.each { element ->
                        assert element.Link.title == "oci" : "Wrong title returned!"
                        String uri = element.Link.uri
                        assert uri.endsWith("oci") : "Link to punchout standard incorrect!"
                    }
                } else {
                    elements.each { element ->
                        assert element.title == "oci" : "Wrong title returned!"
                        assert element.uri.endsWith("oci") : "Link to punchout standard incorrect!"
                    }
                }
            }
            
        where:
            usedContentType << [JSON, XML]
            
    }
    
    @Unroll
    def "See the configuration of punchout OCI - access with punchout user"() {
        def restURL = baseUri + "/customers/${customer}/punchouts/oci/configurations"
        client.headers['Accept'] = JSON
        client.headers['Content-Type'] = JSON
        
        when: "I try to get Punchout configuration for OCI - with a user which doesn't have access"
            def response = client.get(uri: restURL,  requestContentType: JSON)
            
        then: "Test whether access 'Forbidden' for punchout user is returned and the response of the RESTClient 'get' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null
    }
    
	@Unroll
	def "Get an OCI Punchout user's details - access with punchout user"()
	{
		def ociUserId = testData.get("punchout.user.oci.id")[0]
		def ociUserEmail = testData.get("punchout.user.oci.email")[0]
		def ociUserLogin = testData.get("punchout.user.oci.login")[0]
		def restURL = baseUri + "/customers/${customer}/punchouts/oci/users/${ociUserLogin}"
        client.headers['Accept'] = JSON
        client.headers['Content-Type'] = JSON
        
        when: "Ask for details of an OCI user - with a OCI user which doesn't have access"
            def response = client.get(uri: restURL, requestContentType: JSON)
        then: "Test whether access 'Forbidden' for punchout user is returned and the response of the RESTClient 'get' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null
	}
    
    @Unroll
    def "Update the OCI Punchout Configuration details - access with punchout user"()
    {
        // test parameter
        def config1_field     = testData.get("rest.punchout.configuration.put.1.field")[0] // 'NEW_ITEM-MATGROUP'
        def config1_transform = testData.get("rest.punchout.configuration.put.1.transform")[0]
        def config1_formatter = testData.get("rest.punchout.configuration.put.1.formatter")[0]
        def config2_field     = testData.get("rest.punchout.configuration.put.2.field")[0] // 'NEW_ITEM-UNIT'
        def config2_transform = testData.get("rest.punchout.configuration.put.2.transform")[0]
        def config2_formatter = testData.get("rest.punchout.configuration.put.2.formatter")[0]

        def restURL = baseUri + "/customers/${customer}/punchouts/oci/configurations"
        // test parameter
        client.headers['Accept'] = JSON
        client.headers['Content-Type'] = JSON
        def response = null
        
        /*
         * =====================================================================
         * Set punch-out configuration data request items
         * =====================================================================
         */
        when: "Set punch-out configuration data items"
            // Add Accept header
            def builder = new groovy.json.JsonBuilder()
            builder {
                items
                {
                   field config1_field // 'NEW_ITEM-MATGROUP'
                   transform ''; formatter ''
                }
                {
                   field config2_field // 'NEW_ITEM-UNIT'
                   transform ''; formatter ''
                }
            }
            def body = builder.toString()
            
            response = client.put(
                uri: restURL,
                requestContentType: JSON,
                body: body
            )

            
        then: "Test whether access 'Forbidden' for punchout user is returned and the response of the RESTClient 'put' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null
    }
	
	@Unroll
	def "Get all OCI punchout users - access with punchout user"()
	{
		def ociUserEmail = testData.get("punchout.user.oci.email")[0]
		def ociUserLogin = testData.get("punchout.user.oci.login")[0]
		def restURL = baseUri + "/customers/${customer}/punchouts/oci/users"
        client.headers['Accept'] = JSON
        client.headers['Content-Type'] = JSON

        when: "Ask all OCI punchout users"
            def response = client.get(uri: restURL, requestContentType: JSON)
        
        then: "Test whether access 'Forbidden' for punchout user is returned and the response of the RESTClient 'get' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null
	}
    
    @Unroll
    def "Create and delete an OCI punchout user - access with punchout user"()
    {
        def ociUserEmail = testData.get("punchout.user.oci.email")[0]
        def ociUserLogin = testData.get("punchout.user.oci.login")[0] + (new Date()).getTime()
        def ociUserPassword = testData.get("punchout.user.oci.password")[0]
        def restURL = baseUri + "/customers/${customer}/punchouts/oci/users"
        client.headers['Accept'] = JSON
        client.headers['Content-Type'] = JSON
        
        def builder = new groovy.json.JsonBuilder()
        builder {
           type 'PunchoutUser'
           login ociUserLogin
           password ociUserPassword
           email ociUserEmail
        }
        def body = builder.toString()
        
        when: "Create a new OCI punchout user"
            def response = client.post(uri: restURL, body: body, requestContentType: JSON)

        then: "Test whether access 'Forbidden' for punchout user is returned and the response of the RESTClient 'post' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null


        when: "Delete the just created punchout user"
            def deleteResponse = client.delete(uri: restURL+"/"+ociUserLogin, requestContentType: JSON)

        then: "Test whether access 'Forbidden' for punchout user is returned and the response of the RESTClient 'delete' request is not defined."
            def ex2 = thrown(Exception)
            assert ex2 != null
            assert ex2.message.contains("Forbidden")
            assert ex2.statusCode == 403
            assert response == null
    }
    
    @Unroll
    def "Update an OCI punchout user - access with punchout user"()
    {
        def ociUserLogin = testData.get("punchout.user.oci.login")[0]
        def ociUserEmail = testData.get("punchout.user.oci.email")[0]
        def ociUserPassword = testData.get("punchout.user.oci.password")[0]

        def restURL = baseUri + "/customers/${customer}/punchouts/oci/users/" + ociUserLogin
        client.headers['Accept'] = JSON
        client.headers['Content-Type'] = JSON
        
        def builder = new groovy.json.JsonBuilder()
        builder {
           type 'PunchoutUser'
           // clean-up conditions - password doesn't really matter because user access is 'Forbidden'
           password testData.get("punchout.user.password")[0]
           email ociUserEmail
           active true
        }
        def body = builder.toString()

        when: "Update a new OCI punchout user"
            def response = client.put(uri: restURL, body: body, requestContentType: JSON)

        then: "Test whether access 'Forbidden' for punchout user is returned and the response of the RESTClient 'put' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null
    }
    
    @Unroll
	def "Do an OCI background search via #usedContentType - access with punchout user"()
	{
		def restURL = baseUri + "/customers/${customer}/punchouts/oci?searchTerm=Sony"
        client.headers['Accept'] = usedContentType
        client.headers['Content-Type'] = usedContentType
        when: "Search for products via REST call"
            def response = client.get(uri: restURL, requestContentType: usedContentType)
        then: "a list of products that match the search term is sent back"
            with (response) 
			{
                expect status, equalTo(HTTP_OK)
                expect contentType, equalTo(usedContentType)
				if (contentType == XML)
				{
					expect data.PunchoutData.dataItems.PunchoutDataItem.size(), equalTo(6)
					expect data.PunchoutData.dataItems.PunchoutDataItem[0].PunchoutDataItemField.size(), equalTo(22)
					expect data.PunchoutData.dataItems.PunchoutDataItem[0].PunchoutDataItemField[0].value, notNullValue()
					expect data.PunchoutData.dataItems.PunchoutDataItem[0].PunchoutDataItemField[0].@name, not(empty())
					expect data.PunchoutData.dataItems.PunchoutDataItem[0].PunchoutDataItemField[0].@type, equalTo("PunchoutDataItemField")
					expect data.punchoutType, equalTo("oci")
					expect data.version, equalTo("ver5")
				}
				else
				{
					expect data.punchoutData.dataItems.size(), equalTo(6)
					expect data.punchoutData.dataItems[0].dataItemFields.size(), equalTo(22)
					expect data.punchoutData.dataItems[0].dataItemFields[0].name, not(empty())
					expect data.punchoutData.dataItems[0].dataItemFields[0].type, equalTo("PunchoutDataItemField")
					expect data.punchoutData.dataItems[0].dataItemFields[0].value, notNullValue()
					expect data.punchoutType, equalTo("oci")
					expect data.version, equalTo("ver5")
				}
            }
		where:
            usedContentType << [JSON, XML]
	}
    
    @Unroll
    def "Get OCI Punchout item details via #usedContentType - access with punchout user"()
    {
        def restURL = baseUri + "/customers/${customer}/punchouts/oci"
        client.headers['Accept'] = usedContentType
        client.headers['Content-Type'] = usedContentType
        when: "Getting oci punchout item header data via REST call"
            def response = client.get(uri: restURL, requestContentType: usedContentType)
        then: "then oci punchout item header data is sent back"
            with (response) 
            {
                expect status, equalTo(HTTP_OK)
                expect contentType, equalTo(usedContentType)
                if (contentType == XML)
                {
                    expect data.PunchoutData.dataItems.PunchoutDataItem.size(), equalTo(0)
                    expect data.punchoutType, equalTo("oci")
                    expect data.version, equalTo("ver5")
                }
                else
                {
                    expect data.punchoutData.dataItems, nullValue()
                    expect data.punchoutType, equalTo("oci")
                    expect data.version, equalTo("ver5")
                }
            }
        where:
            usedContentType << [JSON, XML]
    }
    
    @Unroll
    def "Create an OCI basket, add a product and verify basket content #usedContentType"()
    {
        def restUrlPunchout = baseUri + "/customers/${customer}/punchouts/oci" // add {basketID} to get punchout basket response
        def restUrlBasket = baseUri + "/baskets" // GET retrieved active basketId
        def restUrlBasketItemsPrefix = "/items" // POST item in active basketId + GET itemId to verify AddToBasket succeeded
        client.headers['Accept'] = usedContentType
        client.headers['Content-Type'] = usedContentType
        
        when: "Start punchout via REST call"
            def response = client.get(uri: restUrlPunchout, requestContentType: usedContentType)
            
        then: "check oci punchout item header data is sent back"
            with (response)
            {
                expect status, equalTo(HTTP_OK)
                expect contentType, equalTo(usedContentType)
                if (contentType == XML)
                {
                    expect data.PunchoutData.dataItems.PunchoutDataItem.size(), equalTo(0)
                    expect data.punchoutType, equalTo("oci")
                    expect data.version, equalTo("ver5")
                }
                else
                {
                    expect data.punchoutData.dataItems, nullValue()
                    expect data.punchoutType, equalTo("oci")
                    expect data.version, equalTo("ver5")
                }
            }
            
        when: "Get active OCI punchout basket via REST call"
            def responseActiveBasket = client.get(uri: restUrlBasket, requestContentType: usedContentType)

        then: "a list of products that match the search term is sent back"
             expect responseActiveBasket.status, equalTo(HTTP_OK)

             def String activeBasketIdSource
             if (usedContentType == XML) {
                 activeBasketIdSource = responseActiveBasket.data.elements[0].Link
             } else {
                 activeBasketIdSource = responseActiveBasket.data.elements[0].uri
             }
             // extracts the basketId //e.g. 3yAKAM7CzJ8AAAFUOw8baOx9
             String activeBasketId = activeBasketIdSource.substring(activeBasketIdSource.lastIndexOf("/")+1)
             println "OCI User's active basketId: ${activeBasketId}"
             
         //  prepare post data and place the product into the OCIUser's basket             
             def bodyAddToBasket = generateAddToBasketRestCallBody(usedContentType, punchoutProductSkuToAdd, punchoutProductQtyToAdd)
             def restUrlAddToBasket = "${restUrlBasket}/${activeBasketId}${restUrlBasketItemsPrefix}"
         when: "Add Product to OCI User's basket"
             def responseAddToBasket = client.post(uri: restUrlAddToBasket, requestContentType: usedContentType, body: bodyAddToBasket)
         then: "check oci punchout basket post header data is sent back"
             with (responseAddToBasket)
             {
                 expect status, equalTo(HTTP_CREATED)
             }

         when: "Do OCI punchout with basketId via REST call"
             def restUrlPunchoutWithBasketID = "${restUrlPunchout}?basketId=${activeBasketId}"
             // println "restUrlPunchoutWithBasketID: ${restUrlPunchoutWithBasketID}"
             def responsePunchoutWithBasketID = client.get(uri: restUrlPunchoutWithBasketID, requestContentType: usedContentType)
             // TODO the OCIUser's basket should be a historyBasket and the LicenseTransaction type: 'oci' should be created
             // next test a new OCIUser basket should be generated and used (the 'activeBasketId' should change very time this test runs)
             
         then: "check oci punchout item header data is sent back"
             with (responsePunchoutWithBasketID)
             {
                 expect status, equalTo(HTTP_OK)
                 expect contentType, equalTo(usedContentType)
                 if (contentType == XML)
                 {
                    expect data.PunchoutData.dataItems.PunchoutDataItem.size(), equalTo(1)
                    expect data.PunchoutData.dataItems.PunchoutDataItem[0].PunchoutDataItemField.size(), equalTo(22)
                    expect data.PunchoutData.dataItems.PunchoutDataItem[0].PunchoutDataItemField[0].value, notNullValue()
                    expect data.PunchoutData.dataItems.PunchoutDataItem[0].PunchoutDataItemField[0].@name, not(empty())
                    expect data.PunchoutData.dataItems.PunchoutDataItem[0].PunchoutDataItemField[0].@type, equalTo("PunchoutDataItemField")
                    expect data.PunchoutData.dataItems.PunchoutDataItem[0].PunchoutDataItemField.find {it.value == punchoutProductSkuToAdd}, notNullValue()
                    expect data.punchoutType, equalTo("oci")
                    expect data.version, equalTo("ver5")
                }
                else
                {
                    // 1 - is the one product previously placed into OCIUser's basket
                    expect data.punchoutData.dataItems.size(), equalTo(1)
                    // 22 - is the number of all oci punchout attributes
                    expect data.punchoutData.dataItems[0].dataItemFields.size(), equalTo(22) 
                    expect data.punchoutData.dataItems[0].dataItemFields[0].name, not(empty())
                    expect data.punchoutData.dataItems[0].dataItemFields[0].type, equalTo("PunchoutDataItemField")
                    expect data.punchoutData.dataItems[0].dataItemFields[0].value, notNullValue()
                    expect data.punchoutData.dataItems[0].dataItemFields.find {it.value == punchoutProductSkuToAdd}, notNullValue()
                    expect data.punchoutType, equalTo("oci")
                    expect data.version, equalTo("ver5")
                }
             }
 
        where:
            usedContentType << [JSON, XML]
    }
    
    def generateAddToBasketRestCallBody(def contentType, def sku, def qty)
    {
        def body = ""
        if(contentType == XML) {
            def writer = new StringWriter()
            writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") 
            writer.append("<LineItems>") 
              writer.append("<elements>")
                writer.append("<BasketLineItem>")
                  writer.append("<sku>").append(sku).append("</sku>")
                  writer.append("<quantity type=\"Quantity\">")
                    writer.append("<value>").append(qty).append("</value>")
                  writer.append("</quantity>")
                writer.append("</BasketLineItem>")
              writer.append("</elements>")
            writer.append("</LineItems>") 
            body = writer.toString()
        }
        
        if(contentType == JSON) {
            def writer = new StringWriter()
            writer.append("{")
            writer.append("\"elements\":") 
              writer.append("[")
              writer.append("{")
                writer.append("\"sku\":").append("\"").append(sku).append("\",")
                  writer.append("\"quantity\": {")
                    writer.append("\"value\": ").append(qty)
                  writer.append("}") // qty - end
                writer.append("}") // sku + qty - end
              writer.append("]") // elements array - end
            writer.append("}") // json - end
            body = writer.toString()
        }
        return body
    }
}