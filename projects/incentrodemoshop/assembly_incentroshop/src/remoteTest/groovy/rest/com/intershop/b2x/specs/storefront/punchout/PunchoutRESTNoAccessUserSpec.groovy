package rest.com.intershop.b2x.specs.storefront.punchout

import static java.net.HttpURLConnection.*
import static org.hamcrest.Matchers.*
import static spock.util.matcher.HamcrestSupport.*

import geb.com.intershop.b2x.testdata.TestDataLoader
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll


@Title("I want to configure my Punchout - with a user without access")

class PunchoutRESTNoAccessUserSpec extends Specification
{
    @Shared
    RESTClient client = new RESTClient()
    
    @Shared
    String baseUri
    
    @Shared 
    def customer
    
    @Shared
    def punchoutUserNoAuthorization

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
        def punchoutUserNoAccess = testData.get("punchout.user.no.access.login")[0]
        def punchoutUserNoAccessPassword = testData.get("punchout.user.no.access.password")[0]
        println "Customer: ${customer} || Punchout-UserNoAccess: ${punchoutUserNoAccess}"
        // user with neither Permission: APP_B2B_MANAGE_PUNCHOUT nor APP_B2B_SEND_OCI_BASKET
        punchoutUserNoAuthorization = 'Basic ' + (punchoutUserNoAccess + ':' + punchoutUserNoAccessPassword).bytes.encodeBase64().toString() 
        // set Authorization for user with neither Permission: APP_B2B_MANAGE_PUNCHOUT nor APP_B2B_SEND_OCI_BASKET
        client.headers['Authorization'] = punchoutUserNoAuthorization
    }

    @Unroll
    def "See which Punchout standards are supported - for user without access"() {
        def restURL = baseUri + "/customers/${customer}/punchouts"
        client.headers['Accept'] = JSON
        client.headers['Content-Type'] = JSON
        when: "Ask for supported Punchout standards - for user without access"
            def response = client.get(uri: restURL,  requestContentType: JSON)

        then: "Test whether access 'Forbidden' for 'no access' user is returned and the response of the RESTClient 'get' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null
    }
    
    @Unroll
    def "See the configuration of punchout OCI - for user without access"() {
        def restURL = baseUri + "/customers/${customer}/punchouts/oci/configurations"
        client.headers['Accept'] = JSON
        client.headers['Content-Type'] = JSON
        
        when: "I try to get Punchout configuration for OCI - with a user which doesn't have access"
            def response = client.get(uri: restURL,  requestContentType: JSON)
            
        then: "Test whether access 'Forbidden' for 'no access' user is returned and the response of the RESTClient 'get' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null
    }
    
    @Unroll
    def "Get an OCI Punchout user's details - for user without access"()
    {
        def ociUserId = testData.get("punchout.user.oci.id")[0]
        def ociUserEmail = testData.get("punchout.user.oci.email")[0]
        def ociUserLogin = testData.get("punchout.user.oci.login")[0]
        def restURL = baseUri + "/customers/${customer}/punchouts/oci/users/${ociUserLogin}"
        client.headers['Accept'] = JSON
        client.headers['Content-Type'] = JSON
        
        when: "Ask for details of an OCI user - with a OCI user which doesn't have access"
            def response = client.get(uri: restURL, requestContentType: JSON)
        then: "Test whether access 'Forbidden' for 'no access' user is returned and the response of the RESTClient 'get' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null
    }
    
    @Unroll
    def "Update the OCI Punchout Configuration details - for user without access"()
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

            
        then: "Test whether access 'Forbidden' for 'no access' user is returned and the response of the RESTClient 'put' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null
    }
    
    @Unroll
    def "Get all OCI punchout users - for user without access"()
    {
        def ociUserEmail = testData.get("punchout.user.oci.email")[0]
        def ociUserLogin = testData.get("punchout.user.oci.login")[0]
        def restURL = baseUri + "/customers/${customer}/punchouts/oci/users"
        client.headers['Accept'] = JSON
        client.headers['Content-Type'] = JSON

        when: "Ask all OCI punchout users"
            def response = client.get(uri: restURL, requestContentType: JSON)
        
        then: "Test whether access 'Forbidden' for 'no access' user is returned and the response of the RESTClient 'get' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null
    }
    
    @Unroll
    def "Create and delete an OCI punchout user - for user without access"()
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

        then: "Test whether access 'Forbidden' for 'no access' user is returned and the response of the RESTClient 'post' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null


        when: "Delete the just created punchout user"
            def deleteResponse = client.delete(uri: restURL+"/"+ociUserLogin, requestContentType: JSON)

        then: "Test whether access 'Forbidden' for 'no access' user is returned and the response of the RESTClient 'delete' request is not defined."
            def ex2 = thrown(Exception)
            assert ex2 != null
            assert ex2.message.contains("Forbidden")
            assert ex2.statusCode == 403
            assert response == null
    }
    
    @Unroll
    def "Update an OCI punchout user - for user without access"()
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

        then: "Test whether access 'Forbidden' for 'no access' user is returned and the response of the RESTClient 'put' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null
    }
    
    @Unroll
    def "Do an OCI background search - for user without access"()
    {
        def restURL = baseUri + "/customers/${customer}/punchouts/oci?searchTerm=Sony"
        client.headers['Accept'] = JSON
        client.headers['Content-Type'] = JSON
        when: "Search for products via REST call"
            def response = client.get(uri: restURL, requestContentType: JSON)

        then: "Test whether access 'Forbidden' for 'no access' user is returned and the response of the RESTClient 'get' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null
    }
    
    @Unroll
    def "Get OCI Punchout item details - for user without access"()
    {
        def restURL = baseUri + "/customers/${customer}/punchouts/oci"
        client.headers['Accept'] = JSON
        client.headers['Content-Type'] = JSON
        when: "Search for products via REST call"
            def response = client.get(uri: restURL, requestContentType: JSON)

        then: "Test whether access 'Forbidden' for 'no access' user is returned and the response of the RESTClient 'get' request is not defined."
            def ex = thrown(Exception)
            assert ex != null
            assert ex.message.contains("Forbidden")
            assert ex.statusCode == 403
            assert response == null
    }

}