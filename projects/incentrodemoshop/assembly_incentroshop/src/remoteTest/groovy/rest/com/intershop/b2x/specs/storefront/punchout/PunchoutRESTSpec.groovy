package rest.com.intershop.b2x.specs.storefront.punchout

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

@Title("I want to configure my Punchout")

class PunchoutRESTSpec extends Specification
{
    @Shared
    RESTClient client = new RESTClient()
    
    @Shared
    String baseUri
    
    @Shared 
    def customer
    
    @Shared
    def customerUserAuthorization

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
        def customerUser = testData.get("punchout.user.bboldner.login")[0]
        def customerUserPassword = testData.get("punchout.customer.user.password")[0]
        println "Customer: ${customer} || User: ${customerUser}"
        // customer with Permission: APP_B2B_MANAGE_PUNCHOUT and APP_B2B_VIEW_PUNCHOUT
        customerUserAuthorization = 'Basic ' + (customerUser + ':' + customerUserPassword).bytes.encodeBase64().toString()
        // set common start Authorization for customer with Permission: APP_B2B_MANAGE_PUNCHOUT
        client.headers['Authorization'] = customerUserAuthorization
    }

    @Unroll
    def "See which Punchout standards are supported via #usedContentType"() {
        def restURL = baseUri + "/customers/${customer}/punchouts"
        client.headers['Accept'] = usedContentType
        client.headers['Content-Type'] = usedContentType
        when: "Ask for supported Punchout standards"
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
    def "See the configuration of punchout OCI via #usedContentType"() {
        def restURL = baseUri + "/customers/${customer}/punchouts/oci/configurations"
        client.headers['Accept'] = usedContentType
        client.headers['Content-Type'] = usedContentType
        
        when: "I try to get Punchout configuration for OCI"
            def response = client.get(uri: restURL,  requestContentType: usedContentType)
        then: "The configuration of punchout OCI will be returned"
            with(response) {
                status == 200
                contentType == usedContentType
            }

            with(response) {
                if(usedContentType == XML) {
                    assert data.items.PunchoutConfigurationItem[0].field.text() == "NEW_ITEM-UNIT"
                    assert data.items.PunchoutConfigurationItem[0].transform.text() == ""
                    assert data.items.PunchoutConfigurationItem[0].formatter.text() == ""
                    assert data.items.PunchoutConfigurationItem[1].field.text() == "NEW_ITEM-VENDOR"
                } else {
                    assert data.items[0].type == "PunchoutConfigurationItem"
                    assert data.items[0].field == "NEW_ITEM-UNIT"
                    assert data.items[0].transform == ""
                    assert data.items[0].formatter == ""
                    assert data.items[1].field == "NEW_ITEM-VENDOR"
                }
            }
        where:
            usedContentType << [JSON, XML]
    }
    
	@Unroll
	def "Get an OCI Punchout user's details via #usedContentType"()
	{
		def ociUserId = testData.get("punchout.user.oci.id")[0]
		def ociUserEmail = testData.get("punchout.user.oci.email")[0]
		def ociUserLogin = testData.get("punchout.user.oci.login")[0]
		def restURL = baseUri + "/customers/${customer}/punchouts/oci/users/${ociUserLogin}"
        client.headers['Accept'] = usedContentType
        client.headers['Content-Type'] = usedContentType
        
        when: "Ask for details of an OCI user"
            def response = client.get(uri: restURL, requestContentType: usedContentType)
        then: "the details of the user are sent"
            with(response) {
                status == 200
                contentType == usedContentType
                
                // check payload
                def typeUsed = (usedContentType == XML) ?
                    data.@type.text() :
                    data.type;
    
                expect typeUsed, equalTo("PunchoutUser")
                expect data.id, equalTo(ociUserLogin)
                expect data.email, equalTo(ociUserEmail)
                expect data.login, equalTo(ociUserLogin)
    		}
		where:
            usedContentType << [JSON, XML]
	}
    
    @Unroll
    def "Update the OCI Punchout Configuration details via #usedContentType"()
    {
        // test parameter
        def config1_field     = testData.get("rest.punchout.configuration.put.1.field")[0] // 'NEW_ITEM-MATGROUP'
        def config1_transform = testData.get("rest.punchout.configuration.put.1.transform")[0]
        def config1_formatter = testData.get("rest.punchout.configuration.put.1.formatter")[0]
        def config2_field     = testData.get("rest.punchout.configuration.put.2.field")[0] // 'NEW_ITEM-UNIT'
        def config2_transform = testData.get("rest.punchout.configuration.put.2.transform")[0]
        def config2_formatter = testData.get("rest.punchout.configuration.put.2.formatter")[0]
        // attributes used to clean up - reset configuration for other tests
        def resetConfigurationFlag = false

        def restURL = baseUri + "/customers/${customer}/punchouts/oci/configurations"
        if(usedContentType.equals(CLEANUP))
        {
            usedContentType = 'application/json';
            resetConfigurationFlag = true
        }
        // test parameter
        client.headers['Accept'] = usedContentType
        client.headers['Content-Type'] = usedContentType
        def response = null
        
        /*
         * =====================================================================
         * Set punch-out configuration data request items
         * =====================================================================
         */
        when: "Set punch-out configuration data items"
            def body = ""
            // Add Accept header
            if(usedContentType == XML) {
                def writer = new StringWriter()
                def xml = new groovy.xml.MarkupBuilder(writer).
                PunchoutConfiguration {
                    item {
                        field(config1_field) // 'NEW_ITEM-MATGROUP'
                        transform(config1_transform)
                        formatter(config1_formatter)
                    }
                    item {
                        field(config2_field) // 'NEW_ITEM-UNIT'
                        transform(config2_transform)
                        formatter(config2_formatter)
                    }
                }
                body = writer.toString().replaceAll("'",'"')
            }
            
            if(usedContentType == JSON) {
                def builder = new groovy.json.JsonBuilder()
                builder {
                    items
                    {
                       field config1_field // 'NEW_ITEM-MATGROUP'
                       if(resetConfigurationFlag)
                       {
                           transform ''; formatter ''
                       }
                       else
                       {
                           transform config1_transform
                           formatter config1_formatter
                       }
                    }
                    {
                       field config2_field // 'NEW_ITEM-UNIT'
                       if(resetConfigurationFlag)
                       {
                           transform ''; formatter ''
                       }
                       else
                       {
                           transform config2_transform
                           formatter config2_formatter
                       }
                    }
                }
                body = builder.toString()
            }
            
            response = client.put(
                uri: restURL,
                requestContentType: usedContentType,
                body: body
            )

        then: "Test whether the request is success."
            assert response.success
            assert response.status == 200
            
            // check the responded data for the two Content Types (xml, json)
            if(usedContentType == XML) {
                assert response.data instanceof GPathResult
                assert response.data.@type == "PunchoutConfiguration"
                assert response.data.items != null
                // check if the two configured attributes with their changed values exist in the responds
                def configuredAttrsMap = response.data.items.PunchoutConfigurationItem.findAll {
                    it.field.text() == config1_field || it.field.text() == config2_field
                }
                def configuredMatGroup = configuredAttrsMap.find {it.field.text() == config1_field}
                def configuredUnit = configuredAttrsMap.find {it.field.text() == config2_field}
                assert configuredAttrsMap.size() == 2
                assert configuredMatGroup.transform.text() == config1_transform
                assert configuredMatGroup.formatter.text() == config1_formatter
                assert configuredUnit.transform.text()     == config2_transform
                assert configuredUnit.formatter.text()     == config2_formatter
            }
            
            if (usedContentType == JSON && resetConfigurationFlag == false) {
                assert response.data instanceof Map
                assert response.data.type == "PunchoutConfiguration"
                // check if the two configured attributes with their changed values exist in the responds
                def configuredAttrsMap = response.data.items.findAll {it.field == config1_field || it.field == config2_field}
                def configuredMatGroup = configuredAttrsMap.find {it.field == config1_field}
                def configuredUnit = configuredAttrsMap.find {it.field == config2_field}
                assert configuredAttrsMap.size() == 2
                assert configuredMatGroup.transform == config1_transform
                assert configuredMatGroup.formatter == config1_formatter
                assert configuredUnit.transform     == config2_transform
                assert configuredUnit.formatter     == config2_formatter
            }

        where:
            //ignoring XML, failing on bamboo but not locally
            usedContentType << [JSON, CLEANUP]
            //usedContentType << [JSON, XML, CLEANUP]
    }
	
	@Unroll
	def "Get all OCI punchout users via #usedContentType"()
	{
		def ociUserEmail = testData.get("punchout.user.oci.email")[0]
		def ociUserLogin = testData.get("punchout.user.oci.login")[0]
		def restURL = baseUri + "/customers/${customer}/punchouts/oci/users"
        client.headers['Accept'] = usedContentType
        client.headers['Content-Type'] = usedContentType
        when: "Ask all OCI punchout users"
            def response = client.get(uri: restURL, requestContentType: usedContentType)
        then: "the links to all OCI punchout users are sent"
            with (response) 
			{
                expect status, equalTo(200)
                expect contentType, equalTo(usedContentType)
                
                def decodedUriToCompareWith = (usedContentType == XML) ?
                    URLDecoder.decode(data.elements.Link[0].uri.text(), "UTF-8") :
                    URLDecoder.decode(data.elements[0].uri, "UTF-8");
                def typeUsed = (usedContentType == XML) ?
                    data.elements.Link[0].@type.text() :
                    data.elements[0].type;
                    
                def titleUsed = (usedContentType == XML) ?
                    data.elements.Link[0].title.text() :
                    data.elements[0].title;
                
                expect decodedUriToCompareWith, containsString(ociUserLogin)
                expect typeUsed, equalTo("Link")
                // check if any user is available
                expect titleUsed, containsString(ociUserLogin) 
            }
		where:
            usedContentType << [JSON, XML]
	}
    
    @Unroll
    def "Create and delete an OCI punchout user via #usedContentType"()
    {
        def ociUserEmail = testData.get("punchout.user.oci.email")[0]
        def ociUserLogin = testData.get("punchout.user.oci.login")[0] + (new Date()).getTime()
        def ociUserPassword = testData.get("punchout.user.oci.password")[0]
        def restURL = baseUri + "/customers/${customer}/punchouts/oci/users"
        client.headers['Accept'] = usedContentType
        client.headers['Content-Type'] = usedContentType
        
        def body = ""
        if(usedContentType == XML) {
            def writer = new StringWriter()
            def xml = new groovy.xml.MarkupBuilder(writer).PunchoutUser("type":"PunchoutUser") {
                login(ociUserLogin)
                password(ociUserPassword)
                email(ociUserEmail)
            }
            body = writer.toString().replaceAll("'",'"')
        }
        
        if(usedContentType == JSON) {
            def builder = new groovy.json.JsonBuilder()
            builder {
               type 'PunchoutUser'
               login ociUserLogin
               password ociUserPassword
               email ociUserEmail
            }
            body = builder.toString()
        }
        
        when: "Create a new OCI punchout user"
            def response = client.post(uri: restURL, body: body, requestContentType: usedContentType)
        then: "A link to the created OCI punchout user is returned"
            with(response) {
                status == 201
                contentType == usedContentType
            }
            with(response.data) {
                def decodedUriToCompareWith = (usedContentType == XML) ? 
                    URLDecoder.decode(uri.text(), "UTF-8") : 
                    URLDecoder.decode(uri, "UTF-8");
                expect decodedUriToCompareWith, endsWith(ociUserLogin)
                expect title, equalTo(ociUserLogin)
            }
        when: "Delete the just created punchout user"
            def deleteResponse = client.delete(uri: restURL+"/"+ociUserLogin, requestContentType: usedContentType)
        then: "an empty response is returned"
            with(deleteResponse) {
                status == 204
            }
        where:
            usedContentType << [JSON, XML]
    }
    
    @Unroll
    def "Update an OCI punchout user via #usedContentType"()
    {
        def ociUserLogin = testData.get("punchout.user.oci.login")[0]
        def ociUserEmail = testData.get("punchout.user.oci.email")[0]
        def ociUserPassword = testData.get("punchout.user.oci.password")[0]
        // attributes used to clean up - reset configuration for other tests
        def resetConfigurationFlag = false
        if(usedContentType.equals(CLEANUP)) {
            usedContentType = JSON;
            resetConfigurationFlag = true
        }
        def restURL = baseUri + "/customers/${customer}/punchouts/oci/users/" + ociUserLogin
        client.headers['Accept'] = usedContentType
        client.headers['Content-Type'] = usedContentType
        
        def body = ""
        if(usedContentType == XML) {
            def writer = new StringWriter()
            def xml = new groovy.xml.MarkupBuilder(writer).PunchoutUser("type":"PunchoutUser") {
                password(ociUserPassword)
                email(ociUserEmail)
                active("true")
            }
            body = writer.toString().replaceAll("'",'"')
        }
        
        if(usedContentType == JSON) {
            def builder = new groovy.json.JsonBuilder()
            builder {
               type 'PunchoutUser'
               if(resetConfigurationFlag) {
                   // clean-up conditions
                   password testData.get("punchout.user.password")[0]
               } else {
                   // test conditions
                   password ociUserPassword
               }
               email ociUserEmail
               active true
            }
            body = builder.toString()
        }

        when: "Update a new OCI punchout user"
            def response = client.put(uri: restURL, body: body, requestContentType: usedContentType)
        then: "A link to the created OCI punchout user is returned"
            with(response) {
                status == 200
                contentType == usedContentType
                
                // check payload
                def typeUsed = (usedContentType == XML) ?
                    data.@type.text() :
                    data.type;
    
                expect typeUsed, equalTo("PunchoutUser")
                expect data.id, equalTo(ociUserLogin)
                expect data.email, equalTo(ociUserEmail)
                expect data.login, equalTo(ociUserLogin)
                expect data.active, equalTo(true)
            }
        where:
            usedContentType << [JSON, XML, CLEANUP]
    }
    
    @Unroll
	def "Do an OCI background search via #usedContentType"()
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
    def "Get OCI Punchout item details via #usedContentType"()
    {
        def restURL = baseUri + "/customers/${customer}/punchouts/oci"
        client.headers['Accept'] = usedContentType
        client.headers['Content-Type'] = usedContentType
        when: "Getting oci punchout item header data via REST call"
            def response = client.get(uri: restURL, requestContentType: usedContentType)
        then: "verify the oci punchout item header data sent back"
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
    
 }