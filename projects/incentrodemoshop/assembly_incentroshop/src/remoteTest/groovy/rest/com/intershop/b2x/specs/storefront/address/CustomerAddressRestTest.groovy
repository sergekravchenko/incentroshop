package rest.com.intershop.b2x.specs.storefront.address

import static groovyx.net.http.ContentType.JSON

import geb.com.intershop.b2x.testdata.TestDataLoader
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild
import groovy.util.slurpersupport.NodeChildren
import groovyx.net.http.RESTClient
import spock.lang.*

@Narrative("""
As a customer
I want to use the Intershop REST API
to manage my customer addresses
""")
@Title("i want to manage my customer addresses")
class CustomerAddressRestTest extends Specification
{
    @Shared
    RESTClient client = new RESTClient()
    
    private static Map<String,List> testData
    private static String customer1, customer2, customerNo1, customerNo2, businessPartnerNo1, businessPartnerNo2, mail, password, resource, uriISH, resourceIdentifier, resourceIdentifier1, resourceIdentifier2
    private static String firstNameData, secondNameData, lastNameData, secondLastNameData, emailData, faxData, phoneHomeData, phoneMobileData, phoneBusinessData, phoneBusinessDirectData, titleData, jobTitleData, aristocraticTitleData, honorificData, addressline1Data, addressline2Data, addressline3Data, postalCodeData, postBoxData, mainDivisionData, subDivisionData, countryCodeData, cityData
    private static String suggestionCity, suggestionCountryCode, suggestionMainDivision, suggestionAddressline1, suggestionPostalCode  
    private static String uri
    // gradually increasing number which is appended to address data so no "DuplicateAddress" error occurs when creating multiple addresses
    private static long count = System.nanoTime()
    private static final String UPDATE = "update"
    private static final String SUGGESTION = "suggestion"
    private static final String JSON_GET = "JSON/GET"
    private static final String JSON_UPDATE = "JSON/UPDATE"
    private static final String XML_GET = "XML/GET"
    private static final String XML_UPDATE = "XML/UPDATE"
    private String addressBodyXML, addressBodyJSON, addressID, addressURN
    private Object response
    
    public static final String ERROR_MESSAGE ="Bad Request (The following attributes are missing: firstName,lastName,countryCode)" 
    
    def setupSpec()
    {
        setup:
        testData = TestDataLoader.getTestData()
        
        customer1               = testData.get("rest.address.eMail")[0]
        customer2               = testData.get("rest.address.eMail")[1]
        customerNo1             = testData.get("rest.address.customerNo")[0]
        customerNo2             = testData.get("rest.address.customerNo")[1]
        businessPartnerNo1      = testData.get("rest.address.businessPartnerNo")[0]
        businessPartnerNo2      = testData.get("rest.address.businessPartnerNo")[1]
        password                = testData.get("rest.address.password")[0]

        // address Data
        firstNameData           = testData.get("rest.address.firstName")[0]
        secondNameData          = testData.get("rest.address.secondName")[0]
        lastNameData            = testData.get("rest.address.lastName")[0]
        secondLastNameData      = testData.get("rest.address.secondLastName")[0]
        emailData               = testData.get("rest.address.email")[0]
        faxData                 = testData.get("rest.address.fax")[0]
        phoneHomeData           = testData.get("rest.address.phoneHome")[0]
        phoneMobileData         = testData.get("rest.address.phoneMobile")[0]
        phoneBusinessData       = testData.get("rest.address.phoneBusiness")[0]
        phoneBusinessDirectData = testData.get("rest.address.phoneBusinessDirect")[0]
        titleData               = testData.get("rest.address.title")[0]
        jobTitleData            = testData.get("rest.address.jobTitle")[0]
        aristocraticTitleData   = testData.get("rest.address.aristocraticTitle")[0]
        honorificData           = testData.get("rest.address.honorific")[0]
        addressline1Data        = testData.get("rest.address.addressLine1")[0]
        addressline2Data        = testData.get("rest.address.addressLine2")[0]
        addressline3Data        = testData.get("rest.address.addressLine3")[0]
        postalCodeData          = testData.get("rest.address.postalCode")[0]
        postBoxData             = testData.get("rest.address.postBox")[0]
        mainDivisionData        = testData.get("rest.address.mainDivision")[0]
        subDivisionData         = testData.get("rest.address.subDivision")[0]
        countryCodeData         = testData.get("rest.address.countryCode")[0]
        cityData                = testData.get("rest.address.city")[0]
        
        suggestionCity          = testData.get("rest.suggestion.address.city")[0]
        suggestionCountryCode   = testData.get("rest.suggestion.address.countryCode")[0]
        suggestionMainDivision  = testData.get("rest.suggestion.address.mainDivision")[0]
        suggestionPostalCode    = testData.get("rest.suggestion.address.postalCode")[0]
        suggestionAddressline1  = testData.get("rest.suggestion.address.addressLine1")[0]
        
        resource                = testData.get("rest.address.resource")[0]
        uriISH                  = testData.get("rest.uri.ish")[0]
        uri                     = "http://" + System.properties['hostName'] + ":" + System.properties['webserverPort'] + uriISH
        resourceIdentifier      = "/customers/-/" + resource
        resourceIdentifier1     = "/customers/" + customerNo1 + "/" + resource
        resourceIdentifier2     = "/customers/" + customerNo2 + "/" + resource
    }
    
    def "Get Customer Addresses without Authorization"(){
        
        /*
         * =====================================================================
         * Get customer addresses request.
         * =====================================================================
         */
        when: "Get customer address request without basic authorization."
            
            def response = null
            
            client.headers['Accept'] = 'application/json'
            client.headers['Content-Type'] = 'application/json'
            client.handler.failure = client.handler.success
            response = client.get(uri: uri + resourceIdentifier, requestContentType: JSON)
                            
       then: "Request failed => status code should be 401"
           
           assert response.status == 401
    }

    def "Get Customer Addresses with '-' in URI"() {
        
        /*
         * =====================================================================
         * Get customer addresses request.
         * =====================================================================
         */
        when: "Get customer address request."
            
            def response = null
            
            // Add Authorization to rest client
            client.headers['Authorization'] = 'Basic ' + (customer1 + ':' + password).bytes.encodeBase64().toString()
            client.headers['Content-Type'] = 'application/json'
            client.headers['Accept'] = 'application/json'
            response = client.get(uri: uri + resourceIdentifier, requestContentType: JSON)
                
       then: "Test the response regarding expected parameters."
            
            assert response.success
            assert response.status == 200
        
            assert response.data instanceof Map
            assert response.data.type == "ResourceCollection"
            
            def elementMap = response.data.elements
            assert elementMap.size() >= 1
            assert elementMap.get(0).get("uri").contains(resourceIdentifier)
            assert elementMap.get(0).get("title") != null
            assert elementMap.get(0).get("type") == "Link"
    }

    def "Get Customer Addresses of another Customer"() {
        
        /*
         * =====================================================================
         * Get customer addresses request.
         * =====================================================================
         */
        when: "Get customer address request."
            
            def response = null
            
            // Add Authorization to rest client
            client.headers['Authorization'] = 'Basic ' + (customer1 + ':' + password).bytes.encodeBase64().toString()
            client.headers['Content-Type'] = 'application/json'
            client.headers['Accept'] = 'application/json'
            response = client.get(uri: uri + resourceIdentifier2, requestContentType: JSON)
                
       then: "Request failed => status code should be 403"
            
            assert response.status == 403
    }

    def "Get Customer Addresses in JSON Format"() {
        
        /*
         * =====================================================================
         * Get customer addresses request.
         * =====================================================================
         */
        when: "Get customer address request."
            
            def response = null
            
            // Add Authorization to rest client
            client.headers['Authorization'] = 'Basic ' + (customer1 + ':' + password).bytes.encodeBase64().toString()
            client.headers['Content-Type'] = 'application/json'
            client.headers['Accept'] = 'application/json'
            response = client.get(uri: uri + resourceIdentifier1, requestContentType: JSON)
                
       then: "Test the response regarding expected parameters."
            
            assert response.success
            assert response.status == 200
        
            assert response.data instanceof Map
            assert response.data.type == "ResourceCollection"
            
            def elementMap = response.data.elements
            assert elementMap.size() >= 1
            assert elementMap.get(0).get("uri").contains(resourceIdentifier1)
            assert elementMap.get(0).get("title") != null
            assert elementMap.get(0).get("type") == "Link"
    }

    def "Get Customer Addresses in XML Format"() {
        
        /*
         * =====================================================================
         * Get customer addresses request.
         * =====================================================================
         */
        when: "Get customer address request."
            
            def response = null
            
            // Add Authorization to rest client
            client.headers['Authorization'] = 'Basic ' + (customer1 + ':' + password).bytes.encodeBase64().toString()
            client.headers['Content-Type'] = 'text/xml'
            client.headers['Accept'] = 'text/xml'
            response = client.get(uri: uri + resourceIdentifier1, requestContentType: 'text/xml')

       then: "Test the response regarding expected parameters."
            
            assert response.success
            assert response.status == 200
        
            assert response.data instanceof GPathResult
            assert response.data.elements.Link.size() >= 1
            NodeChildren node = response.data.elements.Link
            assert node.title != null
            assert node.uri.text().contains(resourceIdentifier1)
    }
    
     def "Create Customer Address without Authorization"(){
        
        /*
         * =====================================================================
         * Create customer addresses request.
         * =====================================================================
         */
        when: "Create customer address request without basic authorization."
            
            def response = null
            setJSONAddressBody()
            
            client.headers['Accept'] = 'application/json'
            client.headers['Content-Type'] = 'application/json'
            client.headers['Authorization'] = ''
            client.handler.failure = client.handler.success
            response = client.post(uri: uri + resourceIdentifier, requestContentType: JSON, body: addressBodyJSON)
                            
       then: "Request failed => status code should be 401"
           
           assert response.status == 401
    }
    
    def "Create Customer Address with Invalid Values"(){
        
        /*
         * =====================================================================
         * Create customer addresses request.
         * =====================================================================
         */
        when: "Create customer address request with invalid Accept type."
            
            def response = null
            setXMLAddressBody()
            
            client.headers['Accept'] = 'application/json'
            client.headers['Content-Type'] = 'application/json'
            client.headers['Authorization'] = 'Basic ' + (customer2 + ':' + password).bytes.encodeBase64().toString()
            client.handler.failure = client.handler.success
            response = client.post(uri: uri + resourceIdentifier2, requestContentType: JSON, body: addressBodyXML)
                            
        then: "Request failed => status code should be 400"
           
           assert response.status == 400
                      
        when: "Create customer address request with missing mandatory entries."
            setInvalidJSONAddressBody()
            response = client.post(uri: uri + resourceIdentifier2, requestContentType: JSON, body: addressBodyJSON)
        
        then: "Request failed => status code should be 400"
            
            assert response.status == 400
            assert response.data.readLines().contains(ERROR_MESSAGE)
    }

    def "Create Customer Address with '-' in URI"() {
        
        /*
         * =====================================================================
         * Create customer addresses request.
         * =====================================================================
         */
        when: "Create customer address request."
            
            def response = null
            setJSONAddressBody()
            
            // Add Authorization to rest client
            client.headers['Authorization'] = 'Basic ' + (customer2 + ':' + password).bytes.encodeBase64().toString()
            client.headers['Content-Type'] = 'application/json'
            client.headers['Accept'] = 'application/json'
            response = client.post(uri: uri + resourceIdentifier, requestContentType: JSON, body: addressBodyJSON)
                
       then: "Test the response regarding expected parameters."
            
            assert response.status == 201
        
            assert response.data instanceof Map
            assert response.data.type == "Link"
            assert response.data.uri.contains(resourceIdentifier)
            assert response.data.title != null
    }

    def "Create Customer Address for another Customer"() {
        
        /*
         * =====================================================================
         * Create customer addresses request.
         * =====================================================================
         */
        when: "Create customer address request."
            
            def response = null
            setJSONAddressBody()
            
            // Add Authorization to rest client
            client.headers['Authorization'] = 'Basic ' + (customer2 + ':' + password).bytes.encodeBase64().toString()
            client.headers['Content-Type'] = 'application/json'
            client.headers['Accept'] = 'application/json'
            response = client.post(uri: uri + resourceIdentifier1, requestContentType: JSON, body: addressBodyJSON)
                
       then: "Request failed => status code should be 403"
            
            assert response.status == 403
    }
    
    def "Create Customer Address with JSON Format"() {
        
        /*
         * =====================================================================
         * Create customer addresses request.
         * =====================================================================
         */
        when: "Create customer address request."
            
            createAddress()
                
       then: "Test the response regarding expected parameters."
            
            assert response.status == 201
        
            assert response.data instanceof Map
            assert response.data.type == "Link"
            assert response.data.uri.contains(resourceIdentifier)
            assert response.data.title != null
    }
    
    def "Create Customer Address with XML Format"() {
        
        /*
         * =====================================================================
         * Create customer addresses request.
         * =====================================================================
         */
        when: "Create customer address request."
            
            def response = null
            setXMLAddressBody()
            
            // Add Authorization to rest client
            client.headers['Authorization'] = 'Basic ' + (customer2 + ':' + password).bytes.encodeBase64().toString()
            client.headers['Content-Type'] = 'text/xml'
            client.headers['Accept'] = 'text/xml'
           
            response = client.post(uri: uri + resourceIdentifier2, requestContentType: 'text/xml', body: addressBodyXML)
                
       then: "Test the response regarding expected parameters."
           
            assert response.status == 201
        
            assert response.data instanceof GPathResult
            NodeChildren node = response.data.Link
            assert node.title != null
            assert node.uri != null
    }
    
    def "Create Customer Address with Suggestions"() {
        
        /*
         * =====================================================================
         * Create customer addresses request.
         * =====================================================================
         */
        when: "Create customer address request."
            
            createAddress(SUGGESTION)
                
       then: "Test the response regarding expected parameters."
            
            assert response.status == 201
        
            assert addressID.contains(SUGGESTION)
    }
    
    def "Get Customer Address Details without Authorization"(){
        
        /*
         * =====================================================================
         * Get customer address details request.
         * =====================================================================
         */
         when: "Get customer address details request."
             
             createAddress()
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.headers['Authorization'] = ''
             client.handler.failure = client.handler.success
             response = client.get(uri: uri + resourceIdentifier + "/" + addressID, requestContentType: JSON)
                         
         then: "Request failed => status code should be 401"
        
             assert response.status == 401
    }
    
    def "Get Customer Address Details with '-' in URI"() {
        
        /*
         * =====================================================================
         * Get customer address details request.
         * =====================================================================
         */
         when: "Get customer address details request."
             
             createAddress()
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             response = client.get(uri: uri + resourceIdentifier + "/" + addressID, requestContentType: JSON)
                         
         then: "Test the response regarding expected parameters."
        
             assert response.status == 200
    }
    
    def "Get Customer Address Details of another Customer"() {
        
        /*
         * =====================================================================
         * Get customer address details request.
         * =====================================================================
         */
        when: "Get customer address details request."
             
            createAddress()
            client.headers['Accept'] = 'application/json'
            client.headers['Content-Type'] = 'application/json'
            client.headers['Authorization'] = 'Basic ' + (customer2 + ':' + password).bytes.encodeBase64().toString()
            client.handler.failure = client.handler.success
            response = client.get(uri: uri + resourceIdentifier1 + "/" + addressID, requestContentType: JSON)
                             
        then: "Request failed => status code should be 403"
            
            assert response.status == 403
    }
    
    def "Get Customer Address Details with an unknown Address ID"() {
        
        /*
         * =====================================================================
         * Get customer address details request.
         * =====================================================================
         */
        when: "Get customer address details request."
                
            createAddress()
            
            client.headers['Accept'] = 'application/json'
            client.headers['Content-Type'] = 'application/json'
            client.handler.failure = client.handler.success
            response = client.get(uri: uri + resourceIdentifier + "/" + addressID + "unknown", requestContentType: JSON)
                             
        then: "Request failed => status code should be 404"
            
            assert response.status == 404
    }
    
    def "Get Customer Address Details in JSON Format"() {
        
        when: "Get customer address request."
                
            createAddress()
            // Add Authorization to rest client
            client.headers['Authorization'] = 'Basic ' + (customer1 + ':' + password).bytes.encodeBase64().toString()
            client.headers['Content-Type'] = 'application/json'
            client.headers['Accept'] = 'application/json'
            response = client.get(uri: uri + resourceIdentifier1 + "/" + addressID, requestContentType: JSON)
                    
        then: "Test the response regarding expected parameters."
               
            validateAddressResponse(JSON_GET)
    }
    
    def "Get Customer Address Details in XML Format"() {
        
        /*
         * =====================================================================
         * Get customer addresses request.
         * =====================================================================
         */
        when: "Get customer address request."
             
            createAddress()
            // Add Authorization to rest client
            client.headers['Authorization'] = 'Basic ' + (customer1 + ':' + password).bytes.encodeBase64().toString()
            client.headers['Content-Type'] = 'text/xml'
            client.headers['Accept'] = 'text/xml'
            response = client.get(uri: uri + resourceIdentifier1 + "/" + addressID, requestContentType: 'text/xml')
                    
        then: "Test the response regarding expected parameters."
               
            validateAddressResponse(XML_GET)
    }

    def "Set User Preferred Invoice-To Address in JSON Format"() {
        
        when: "Set user preferred addresses."
                
            createAddress()
            setJSONUserPreferredAddressBody('preferredInvoiceToAddress', addressURN, businessPartnerNo1, customer1)
            // Add Authorization to rest client
            client.headers['Authorization'] = 'Basic ' + (customer1 + ':' + password).bytes.encodeBase64().toString()
            client.headers['Content-Type'] = 'application/json'
            client.headers['Accept'] = 'application/json'
            response = client.put(uri: uri + "/customers/" + customerNo1 + "/users/" + customer1, requestContentType: JSON, body: addressBodyJSON)
                    
        then: "Test the response regarding expected parameters."
            validateUserResponse(JSON_GET, 'preferredInvoiceToAddress')
    }

    def "Set User Preferred Invoice-To Address in XML Format"() {
        
        when: "Set user preferred addresses."
                
            createAddress()
            setXMLUserPreferredAddressBody('preferredInvoiceToAddress', addressURN, businessPartnerNo1, customer1)
            // Add Authorization to rest client
            client.headers['Authorization'] = 'Basic ' + (customer1 + ':' + password).bytes.encodeBase64().toString()
            client.headers['Content-Type'] = 'text/xml'
            client.headers['Accept'] = 'text/xml'
            response = client.put(uri: uri + "/customers/" + customerNo1 + "/users/" + customer1, requestContentType: 'text/xml', body: addressBodyXML)
                    
        then: "Test the response regarding expected parameters."
            validateUserResponse(XML_GET, 'preferredInvoiceToAddress')
    }

    def "Set User Preferred Ship-To Address in JSON Format"() {
        
        when: "Set user preferred addresses."
                
            createAddress()
            setJSONUserPreferredAddressBody('preferredShipToAddress', addressURN, businessPartnerNo1, customer1)
            // Add Authorization to rest client
            client.headers['Authorization'] = 'Basic ' + (customer1 + ':' + password).bytes.encodeBase64().toString()
            client.headers['Content-Type'] = 'application/json'
            client.headers['Accept'] = 'application/json'
            response = client.put(uri: uri + "/customers/" + customerNo1 + "/users/" + customer1, requestContentType: JSON, body: addressBodyJSON)
                    
        then: "Test the response regarding expected parameters."
            validateUserResponse(JSON_GET, 'preferredShipToAddress')
    }

    def "Set User Preferred Ship-To Address in XML Format"() {
        
        when: "Set user preferred addresses."
                
            createAddress()
            setXMLUserPreferredAddressBody('preferredShipToAddress', addressURN, businessPartnerNo1, customer1)
            // Add Authorization to rest client
            client.headers['Authorization'] = 'Basic ' + (customer1 + ':' + password).bytes.encodeBase64().toString()
            client.headers['Content-Type'] = 'text/xml'
            client.headers['Accept'] = 'text/xml'
            response = client.put(uri: uri + "/customers/" + customerNo1 + "/users/" + customer1, requestContentType: 'text/xml', body: addressBodyXML)
                    
        then: "Test the response regarding expected parameters."
            validateUserResponse(XML_GET, 'preferredShipToAddress')
    }
    
    def "Update Customer Address without Authorization"(){
        
        /*
         * =====================================================================
         * Update customer address request.
         * =====================================================================
         */
         when: "Update customer address request."
             
             /*
              * =====================================================================
              * Create customer addresses request.
              * =====================================================================
              */
             createAddress()
             setJSONAddressBody(UPDATE)
             
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.headers['Authorization'] = ''
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + resourceIdentifier + "/" + addressID, requestContentType: JSON, body: addressBodyJSON)
                         
         then: "Request failed => status code should be 401"
        
             assert response.status == 401
    }
    
    def "Update Customer Address with '-' in URI"(){
        
        /*
         * =====================================================================
         * Update customer address request.
         * =====================================================================
         */
         when: "Update customer address request."
             
             /*
              * =====================================================================
              * Create customer addresses request.
              * =====================================================================
              */
             createAddress()
             setJSONAddressBody(UPDATE)
             
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + resourceIdentifier + "/" + addressID, requestContentType: JSON, body: addressBodyJSON)
                         
         then: "Request successful => status code should be 200"
        
             assert response.status == 200
    }
    
    def "Update Customer Address of another Customer"() {
        
        
        /*
         * =====================================================================
         * Update customer address request.
         * =====================================================================
         */
         when: "Update customer address request."
             
             createAddress()
             setJSONAddressBody("update")
             
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.headers['Authorization'] = 'Basic ' + (customer2 + ':' + password).bytes.encodeBase64().toString()
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + resourceIdentifier1 + "/" + addressID, requestContentType: JSON, body: addressBodyJSON)
                         
         then: "Request failed => status code should be 403"
        
             assert response.status == 403
    }
    
    def "Update Customer Address with an unknown Address ID"() {
        
        
        /*
         * =====================================================================
         * Update customer address request.
         * =====================================================================
         */
         when: "Update customer address request."
             
             createAddress()
             setJSONAddressBody("update")
             
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + resourceIdentifier + "/" + addressID + "unknown", requestContentType: JSON, body: addressBodyJSON)
                         
         then: "Request failed => status code should be 404"
        
             assert response.status == 404
    }
    
    def "Update Customer Address in JSON Format"() {
        
        /*
         * =====================================================================
         * Update customer address request.
         * =====================================================================
         */
         when: "Update customer address request."
             
             createAddress()
             setJSONAddressBody("update")
             
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + resourceIdentifier1 + "/" + addressID, requestContentType: JSON, body: addressBodyJSON)
                         
         then: "Test the response regarding expected parameters."
        
              validateAddressResponse(JSON_UPDATE)
    }
    
    def "Update Customer Address in XML Format"() {
        
        /*
         * =====================================================================
         * Update customer address request.
         * =====================================================================
         */
         when: "Update customer address request."
             
             createAddress()
             setXMLAddressBody("update")
             
             client.headers['Content-Type'] = 'text/xml'
             client.headers['Accept'] = 'text/xml'
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + resourceIdentifier1 + "/" + addressID, requestContentType: 'text/xml', body: addressBodyXML)
                         
         then: "Test the response regarding expected parameters."
        
              validateAddressResponse(XML_UPDATE)
    }
    
    def "Delete Last Customer Address"(){
        
        /*
         * =====================================================================
         * Delete customer address request.
         * =====================================================================
         */
         when: "Delete customer address request."
             
             /*
              * =====================================================================
              * Create customer addresses list with only one address
              * =====================================================================
              */
             prepareAddressList()
                          
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.headers['Authorization'] = 'Basic ' + (customer1 + ':' + password).bytes.encodeBase64().toString()
             client.handler.failure = client.handler.success
             response = client.delete(uri: uri + resourceIdentifier + "/" + addressID, requestContentType: JSON)
         
         then: "Request failed => status code should be 409"
        
             assert response.status == 409
    }
    
    def "Delete Customer Address without Authorization"(){
        
        /*
         * =====================================================================
         * Delete customer address request.
         * =====================================================================
         */
         when: "Delete customer address request."
             
             /*
              * =====================================================================
              * Create customer addresses request.
              * =====================================================================
              */
             createAddress()
                          
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.headers['Authorization'] = ''
             client.handler.failure = client.handler.success
             response = client.delete(uri: uri + resourceIdentifier + "/" + addressID, requestContentType: JSON)
                         
         then: "Request failed => status code should be 401"
        
             assert response.status == 401
    }
    
    def "Delete Customer Address with '-' in URI"(){
        
        /*
         * =====================================================================
         * Delete customer address request.
         * =====================================================================
         */
         when: "Delete customer address request."
             
             /*
              * =====================================================================
              * Create customer addresses request.
              * =====================================================================
              */
             createAddress()
                          
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.handler.failure = client.handler.success
             response = client.delete(uri: uri + resourceIdentifier + "/" + addressID, requestContentType: JSON)
                         
         then: "status code should be 204"
        
             assert response.status == 204
    }
    
    def "Delete Customer Address of another Customer"(){
        
        /*
         * =====================================================================
         * Delete customer address request.
         * =====================================================================
         */
         when: "Delete customer address request."
             
             /*
              * =====================================================================
              * Create customer addresses request.
              * =====================================================================
              */
             createAddress()
                          
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.headers['Authorization'] = 'Basic ' + (customer2 + ':' + password).bytes.encodeBase64().toString()
             client.handler.failure = client.handler.success
             response = client.delete(uri: uri + resourceIdentifier1 + "/" + addressID, requestContentType: JSON)
                         
         then: "Request failed => status code should be 403"
        
             assert response.status == 403
    }
    
    def "Delete Customer Address with an unknown Address ID"(){
        
        /*
         * =====================================================================
         * Delete customer address request.
         * =====================================================================
         */
         when: "Delete customer address request."
             
             /*
              * =====================================================================
              * Create customer addresses request.
              * =====================================================================
              */
             createAddress()
                          
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.handler.failure = client.handler.success
             response = client.delete(uri: uri + resourceIdentifier + "/" + addressID + "unknown", requestContentType: JSON)
                         
         then: "Request failed => status code should be 404"
        
             assert response.status == 404
    }
    
    def "Get suggested address list without Authorization"(){
        
        /*
         * =====================================================================
         * Get suggested customer address list request.
         * =====================================================================
         */
         when: "Get suggested customer address details request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.headers['Authorization'] = ''
             client.handler.failure = client.handler.success
             response = client.get(uri: uri + resourceIdentifier + "/" + addressID, requestContentType: JSON)
                         
         then: "Request failed => status code should be 401"
         
              assert response.status == 401
    }
    
    def "Get suggested address list with '-' in URI"(){
        
        /*
         * =====================================================================
         * Get suggested customer address list request.
         * =====================================================================
         */
         when: "Get suggested customer address details request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.handler.failure = client.handler.success
             response = client.get(uri: uri + resourceIdentifier + "/" + addressID, requestContentType: JSON)
                         
         then: "Request successful => status code should be 200"
        
             assert response.status == 200
    }
    
    def "Get suggested address list of another Customer"(){
        
        /*
         * =====================================================================
         * Get suggested customer address list request.
         * =====================================================================
         */
         when: "Get suggested customer address details request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.headers['Authorization'] = 'Basic ' + (customer2 + ':' + password).bytes.encodeBase64().toString()
             client.handler.failure = client.handler.success
             response = client.get(uri: uri + resourceIdentifier1 + "/" + addressID, requestContentType: JSON)
         
         then: "Request failed => status code should be 403"
             
             assert response.status == 403
        
    }
    
    def "Get suggested address list for an unknown Address ID"(){
        
        /*
         * =====================================================================
         * Get suggested customer address list request.
         * =====================================================================
         */
         when: "Get suggested customer address details request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             response = client.get(uri: uri + resourceIdentifier1 + "/wrong" + addressID, requestContentType: JSON)
             
         then: "Request failed => status code should be 404"
             
             assert response.status == 404
    }
    
    def "Get suggested address list in JSON Format"(){
        
        /*
         * =====================================================================
         * Get suggested customer address list request.
         * =====================================================================
         */
         when: "Get suggested customer address details request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             response = client.get(uri: uri + resourceIdentifier1 + "/" + addressID, requestContentType: JSON)
                         
         then: "Test the response regarding expected parameters."
        
             assert response.status == 200
             def elementMap = response.data.elements
             assert elementMap.size() >= 2
             assert elementMap.get(0).get("uri").contains(resourceIdentifier1 + "/" + addressID)
             assert elementMap.get(0).get("title") != null
             assert elementMap.get(0).get("type") == "Link"
             
    }
    
    def "Get suggested address list in XML Format"(){
        
        /*
         * =====================================================================
         * Get suggested customer address list request.
         * =====================================================================
         */
         when: "Get suggested customer address details request."
             
             createAddress(SUGGESTION)
             client.headers['Content-Type'] = 'text/xml'
             client.headers['Accept'] = 'text/xml'
             response = client.get(uri: uri + resourceIdentifier1 + "/" + addressID, requestContentType: 'text/xml')
                         
         then: "Test the response regarding expected parameters."
        
             assert response.status == 200
             assert response.data instanceof GPathResult
             assert response.data.elements.Link.size() >= 1
             NodeChildren node = response.data.elements.Link
             assert node.title != null
             assert node.uri.text().contains(resourceIdentifier1)
             
    }
    
    def "Get suggested address details without Authorization"(){
        
        /*
         * =====================================================================
         * Get suggested customer address details request.
         * =====================================================================
         */
         when: "Get suggested customer address details request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.headers['Authorization'] = ''
             client.handler.failure = client.handler.success
             response = client.get(uri: uri + resourceIdentifier + "/" + addressID + "/1", requestContentType: JSON)
                         
         then: "Request failed => status code should be 401"
         
              assert response.status == 401
    }
    
    def "Get suggested address details with '-' in URI"(){
        
        /*
         * =====================================================================
         * Get suggested customer address details request.
         * =====================================================================
         */
         when: "Get suggested customer address details request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.handler.failure = client.handler.success
             response = client.get(uri: uri + resourceIdentifier + "/" + addressID + "/1", requestContentType: JSON)
                         
         then: "Request successful => status code should be 200"
        
             assert response.status == 200
    }
    
    def "Get suggested address details of another Customer"(){
        
        /*
         * =====================================================================
         * Get suggested customer address details request.
         * =====================================================================
         */
         when: "Get suggested customer address details request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.headers['Authorization'] = 'Basic ' + (customer2 + ':' + password).bytes.encodeBase64().toString()
             client.handler.failure = client.handler.success
             response = client.get(uri: uri + resourceIdentifier1 + "/" + addressID + "/1", requestContentType: JSON)
         
         then: "Request failed => status code should be 403"
             
             assert response.status == 403
        
    }
    
    def "Get suggested address details for an unknown suggested Address ID"(){
        
        /*
         * =====================================================================
         * Get suggested customer address details request.
         * =====================================================================
         */
         when: "Get suggested customer address details request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             response = client.get(uri: uri + resourceIdentifier1 + "/wrong" + addressID + "/wrongID", requestContentType: JSON)
             
         then: "Request failed => status code should be 404"
             
             assert response.status == 404
    }
    
    def "Get suggested address details in JSON Format"(){
        
        /*
         * =====================================================================
         * Get suggested customer address details request.
         * =====================================================================
         */
         when: "Get suggested customer address details request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             response = client.get(uri: uri + resourceIdentifier1 + "/" + addressID + "/1", requestContentType: JSON)
                         
         then: "Test the response regarding expected parameters."
        
             assert response.success
             assert response.status == 200
             def suffix = count
             def element = response.data
             assert element.get("firstName") == firstNameData + suffix
             assert element.get("lastName") == lastNameData + suffix
             assert element.get("email") == emailData + suffix
             assert element.get("mainDivision") == suggestionMainDivision
             assert element.get("addressLine1") == "new_street1"
             assert element.get("city") == suggestionCity
             assert element.get("countryCode") == suggestionCountryCode
             assert element.get("postalCode") == suggestionPostalCode
    }
    
    def "Get suggested address details in XML Format"(){
        
        /*
         * =====================================================================
         * Get suggested customer address details request.
         * =====================================================================
         */
         when: "Get suggested customer address details request."
             
             createAddress(SUGGESTION)
             client.headers['Content-Type'] = 'text/xml'
             client.headers['Accept'] = 'text/xml'
             response = client.get(uri: uri + resourceIdentifier1 + "/" + addressID + "/2", requestContentType: 'text/xml')
                         
         then: "Test the response regarding expected parameters."
        
             assert response.success
             assert response.status == 200
             def suffix = count
             def NodeChild address = response.data
             assert address.firstName == firstNameData + suffix
             assert address.lastName == lastNameData + suffix
             assert address.email == emailData + suffix
             assert address.mainDivision == suggestionMainDivision
             assert address.addressLine1 == "new_street2"
             assert address.city == suggestionCity
             assert address.countryCode == suggestionCountryCode
             assert address.postalCode == suggestionPostalCode
    }
    
    def "Update address with suggested address without Authorization"(){
        
        /*
         * =====================================================================
         * Update address with suggested customer address request.
         * =====================================================================
         */
         when: "Update address with suggested customer address request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.headers['Authorization'] = ''
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + resourceIdentifier + "/" + addressID + "/1", requestContentType: JSON)
             
         then: "Request failed => status code should be 401"
             
              assert response.status == 401
    }
    
    def "Update address with suggested address with '-' in URI"(){
        
        /*
         * =====================================================================
         * Update address with suggested customer address request.
         * =====================================================================
         */
         when: "Update address with suggested customer address request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             response = client.put(uri: uri + resourceIdentifier + "/" + addressID + "/1", requestContentType: JSON)
             
         then: "Request successful => status code should be 200"
        
             assert response.status == 200
    }
    
    def "Update address with suggested address of another Customer"(){
        
        /*
         * =====================================================================
         * Update address with suggested customer address request.
         * =====================================================================
         */
         when: "Update address with suggested customer address request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.headers['Authorization'] = 'Basic ' + (customer2 + ':' + password).bytes.encodeBase64().toString()
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + resourceIdentifier1 + "/" + addressID + "/1", requestContentType: JSON)
             
          then: "Request failed => status code should be 403"
             
             assert response.status == 403
    }
    
    def "Update address with suggested address with an unknown suggested Address ID"(){
        
        /*
         * =====================================================================
         * Update address with suggested customer address request.
         * =====================================================================
         */
         when: "Update address with suggested customer address request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + resourceIdentifier + "/" + addressID + "/3", requestContentType: JSON)
             
         then: "Request failed => status code should be 404"
             
             assert response.status == 404
    }
    
    def "Update address with suggested address"(){
        
        /*
         * =====================================================================
         * Update address with suggested customer address request.
         * =====================================================================
         */
         when: "Update address with suggested customer address request."
             
             createAddress(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             response = client.put(uri: uri + resourceIdentifier1 + "/" + addressID + "/1", requestContentType: JSON)
             
        then: "Request successful => status code should be 200"
        
             assert response.status == 200
    }

    def "Update Customer Address with Suggestions"(){
        
        /*
         * =====================================================================
         * Update address with address data which suggestions are found for.
         * =====================================================================
         */
         when: "Update address with data with suggestions request."
             
             createAddress()
             setJSONAddressBody(SUGGESTION)
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             response = client.put(uri: uri + resourceIdentifier1 + "/" + addressID, requestContentType: JSON, body: addressBodyJSON)
             
        then: "Request successful => status code should be 200"
        
             assert response.status == 200
             assert response.data.uri.endsWith(addressID + "/suggestions")
    }
    
    def setXMLAddressBody = {
        
            String suffix = ++count
        
            if(it == UPDATE)
            {
                suffix = suffix + UPDATE
            }
        
            def writer = new StringWriter()
            def xml = new groovy.xml.MarkupBuilder(writer).Address("type":"Address") {
                
                if(it == SUGGESTION)
                {
                    mainDivision(suggestionMainDivision)
                    postalCode(suggestionPostalCode)
                    city(suggestionCity)
                    addressLine1(suggestionAddressline1)
                }
                else
                {
                    mainDivision(mainDivisionData + suffix)
                    postalCode(postalCodeData + suffix)
                    city(cityData + suffix)
                    addressLine1(addressline1Data + suffix)
                }
                countryCode(countryCodeData)
                firstName(firstNameData + suffix)
                secondName(secondNameData + suffix)
                lastName(lastNameData + suffix)
                secondLastName(secondLastNameData + suffix)
                phoneHome(phoneHomeData + suffix)
                phoneMobile(phoneMobileData + suffix)
                phoneBusiness(phoneBusinessData + suffix)
                phoneBusinessDirect(phoneBusinessDirectData + suffix)
                subDivision(subDivisionData + suffix)
                addressLine2(addressline2Data + suffix)
                addressLine3(addressline3Data + suffix)
                postBox(postBoxData + suffix)
                title(titleData + suffix)
                jobTitle(jobTitleData + suffix)
                aristocraticTitle(aristocraticTitleData + suffix)
                honorific(honorificData + suffix)
                email(emailData + suffix)
                fax(faxData + suffix)
            }
             
            addressBodyXML = writer.toString().replaceAll("'",'"')
    }
    
    def setJSONAddressBody = {
        
            def callParameter = it
            String suffix = ++count
            
            if(callParameter == UPDATE)
            {
                suffix = suffix + UPDATE
            }
            
            def builder = new groovy.json.JsonBuilder()
            builder {
                if(callParameter == SUGGESTION)
                {
                    mainDivision suggestionMainDivision
                    postalCode suggestionPostalCode
                    city suggestionCity
                    addressLine1 suggestionAddressline1
                }
                else
                {
                    mainDivision mainDivisionData + suffix
                    postalCode postalCodeData + suffix
                    city cityData + suffix
                    addressLine1 addressline1Data + suffix
                }
                countryCode countryCodeData
                firstName firstNameData + suffix
                secondName secondNameData + suffix
                lastName lastNameData + suffix
                secondLastName secondLastNameData + suffix
                phoneHome phoneHomeData + suffix
                phoneMobile phoneMobileData + suffix
                phoneBusiness phoneBusinessData + suffix
                phoneBusinessDirect phoneBusinessDirectData + suffix
                subDivision subDivisionData + suffix
                addressLine2 addressline2Data + suffix
                addressLine3 addressline3Data + suffix
                postBox postBoxData + suffix
                title titleData + suffix
                jobTitle jobTitleData + suffix
                aristocraticTitle aristocraticTitleData + suffix
                honorific honorificData + suffix
                email emailData + suffix
                fax faxData + suffix
                
            }
            
             
            addressBodyJSON = builder.toString()
    }
    
    def setInvalidJSONAddressBody = {
        
            def builder = new groovy.json.JsonBuilder()
            builder {
                city "'" + testData.get("rest.address.city")[0] + "'"
            }
            addressBodyJSON = builder.toString()
    }

    def setJSONUserPreferredAddressBody(addressType, addressURN, businessPartnerNumber, userEmail) {
        def builder = new groovy.json.JsonBuilder()
        builder {
            businessPartnerNo businessPartnerNumber
            firstName firstNameData
            lastName lastNameData
            email userEmail
            preferredLanguage "en_US"
            "$addressType" {
                urn addressURN
            }
        }
        addressBodyJSON = builder.toString()
    }

    def setXMLUserPreferredAddressBody(addressType, addressURN, businessPartnerNumber, userEmail) {
        def writer = new StringWriter()
        def xml = new groovy.xml.MarkupBuilder(writer).SMBCustomerUser("type":"SMBCustomerUser") {
            businessPartnerNo(businessPartnerNumber)
            firstName(firstNameData)
            lastName(lastNameData)
            email(userEmail)
            preferredLanguage("en_US")
            "$addressType"("type":"Address") {
                urn(addressURN)
            }
        }
        addressBodyXML = writer.toString().replaceAll("'",'"')
    }
    
    def createAddress = {
        
        setJSONAddressBody(it)
                
        // Add Authorization to rest client
        client.headers['Authorization'] = 'Basic ' + (customer1 + ':' + password).bytes.encodeBase64().toString()
        client.headers['Content-Type'] = 'application/json'
        client.headers['Accept'] = 'application/json'
        response = client.post(uri: uri + resourceIdentifier, requestContentType: JSON, body: addressBodyJSON)
        addressID = response.data.uri
        addressID = addressID.substring(addressID.indexOf(resourceIdentifier) + resourceIdentifier.length() + 1)
        addressURN = response.data.title
    }
    
    def validateAddressResponse = {
        
        assert response.success
        assert response.status == 200
        String suffix = count
        if ("JSON/UPDATE".equals(it) || "XML/UPDATE".equals(it)) {
            suffix = suffix + UPDATE
        }
        
        switch ( it )
        {
            case "JSON/GET":
            case "JSON/UPDATE":
                def element = response.data
                assert element.get("title") == titleData + suffix
                assert element.get("jobTitle") == jobTitleData + suffix
                assert element.get("aristocraticTitle") == aristocraticTitleData + suffix
                assert element.get("honorific") == honorificData + suffix
                assert element.get("firstName") == firstNameData + suffix
                assert element.get("secondName") == secondNameData + suffix
                assert element.get("lastName") == lastNameData + suffix
                assert element.get("secondLastName") == secondLastNameData + suffix
                assert element.get("email") == emailData + suffix
                assert element.get("fax") == faxData + suffix
                assert element.get("phoneHome") == phoneHomeData + suffix
                assert element.get("phoneMobile") == phoneMobileData + suffix
                assert element.get("phoneBusiness") == phoneBusinessData + suffix
                assert element.get("phoneBusinessDirect") == phoneBusinessDirectData + suffix
                assert element.get("mainDivision") == mainDivisionData + suffix
                assert element.get("subDivision") == subDivisionData + suffix
                assert element.get("addressLine1") == addressline1Data + suffix
                assert element.get("addressLine2") == addressline2Data + suffix
                assert element.get("addressLine3") == addressline3Data + suffix
                assert element.get("city") == cityData + suffix
                assert element.get("countryCode") == countryCodeData
                assert element.get("postBox") == postBoxData + suffix
                assert element.get("postalCode") == postalCodeData + suffix
                break
            case "XML/GET":
            case "XML/UPDATE":
                def NodeChild address = response.data
                assert address.title == titleData + suffix
                
                assert address.jobTitle == jobTitleData + suffix
                assert address.aristocraticTitle == aristocraticTitleData + suffix
                assert address.honorific == honorificData + suffix
                assert address.firstName == firstNameData + suffix
                assert address.secondName == secondNameData + suffix
                assert address.lastName == lastNameData + suffix
                assert address.secondLastName == secondLastNameData + suffix
                assert address.email == emailData + suffix
                assert address.fax == faxData + suffix
                assert address.phoneHome == phoneHomeData + suffix
                assert address.phoneMobile == phoneMobileData + suffix
                assert address.phoneBusiness == phoneBusinessData + suffix
                assert address.phoneBusinessDirect == phoneBusinessDirectData + suffix
                assert address.mainDivision == mainDivisionData + suffix
                assert address.subDivision == subDivisionData + suffix
                assert address.addressLine1 == addressline1Data + suffix
                assert address.addressLine2 == addressline2Data + suffix
                assert address.addressLine3 == addressline3Data + suffix
                assert address.city == cityData + suffix
                assert address.countryCode == countryCodeData
                assert address.postBox == postBoxData + suffix
                assert address.postalCode == postalCodeData + suffix
                break
        }
        
        return true
    }

    def validateUserResponse(responseType, addressType) {
        
        assert response.success
        assert response.status == 200
        String suffix = count
        
        switch ( responseType )
        {
            case "JSON/GET":
                def element = response.data.get(addressType)
                assert element.get("title") == titleData + suffix
                assert element.get("jobTitle") == jobTitleData + suffix
                assert element.get("aristocraticTitle") == aristocraticTitleData + suffix
                assert element.get("honorific") == honorificData + suffix
                assert element.get("firstName") == firstNameData + suffix
                assert element.get("secondName") == secondNameData + suffix
                assert element.get("lastName") == lastNameData + suffix
                assert element.get("secondLastName") == secondLastNameData + suffix
                assert element.get("email") == emailData + suffix
                assert element.get("fax") == faxData + suffix
                assert element.get("phoneHome") == phoneHomeData + suffix
                assert element.get("phoneMobile") == phoneMobileData + suffix
                assert element.get("phoneBusiness") == phoneBusinessData + suffix
                assert element.get("phoneBusinessDirect") == phoneBusinessDirectData + suffix
                assert element.get("mainDivision") == mainDivisionData + suffix
                assert element.get("subDivision") == subDivisionData + suffix
                assert element.get("addressLine1") == addressline1Data + suffix
                assert element.get("addressLine2") == addressline2Data + suffix
                assert element.get("addressLine3") == addressline3Data + suffix
                assert element.get("city") == cityData + suffix
                assert element.get("countryCode") == countryCodeData
                assert element.get("postBox") == postBoxData + suffix
                assert element.get("postalCode") == postalCodeData + suffix
                break
             case "XML/GET":
                def NodeChild user = response.data
                def address = user."$addressType"
                assert address.title == titleData + suffix
                
                assert address.jobTitle == jobTitleData + suffix
                assert address.aristocraticTitle == aristocraticTitleData + suffix
                assert address.honorific == honorificData + suffix
                assert address.firstName == firstNameData + suffix
                assert address.secondName == secondNameData + suffix
                assert address.lastName == lastNameData + suffix
                assert address.secondLastName == secondLastNameData + suffix
                assert address.email == emailData + suffix
                assert address.fax == faxData + suffix
                assert address.phoneHome == phoneHomeData + suffix
                assert address.phoneMobile == phoneMobileData + suffix
                assert address.phoneBusiness == phoneBusinessData + suffix
                assert address.phoneBusinessDirect == phoneBusinessDirectData + suffix
                assert address.mainDivision == mainDivisionData + suffix
                assert address.subDivision == subDivisionData + suffix
                assert address.addressLine1 == addressline1Data + suffix
                assert address.addressLine2 == addressline2Data + suffix
                assert address.addressLine3 == addressline3Data + suffix
                assert address.city == cityData + suffix
                assert address.countryCode == countryCodeData
                assert address.postBox == postBoxData + suffix
                assert address.postalCode == postalCodeData + suffix
                break
        }
        
        return true
    }
    
    def prepareAddressList ={
        
        def response = null
        
        // Add Authorization to rest client
        client.headers['Authorization'] = 'Basic ' + (customer1 + ':' + password).bytes.encodeBase64().toString()
        client.headers['Content-Type'] = 'application/json'
        client.headers['Accept'] = 'application/json'
        response = client.get(uri: uri + resourceIdentifier1, requestContentType: JSON)
        
        def elementMap = response.data.elements
        
        if(elementMap.size()<2)
        {
            createAddress();
            createAddress();
        }
        
        for(int i=0; i<elementMap.size()-1;i++)
        {
            addressID = elementMap.get(i).get("uri")
            addressID = addressID.substring(addressID.indexOf(resourceIdentifier1) + resourceIdentifier1.length() + 1)
            response = client.delete(uri: uri + resourceIdentifier1 + "/" + addressID, requestContentType: JSON)
        }
        
        response = client.get(uri: uri + resourceIdentifier1, requestContentType: JSON)
        addressID = response.data.elements.get(0).get("uri")
        addressID = addressID.substring(addressID.indexOf(resourceIdentifier1) + resourceIdentifier1.length() + 1)
    }
}
