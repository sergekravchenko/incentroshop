package rest.com.intershop.b2x.specs.storefront.address

import spock.lang.Ignore
import static groovyx.net.http.ContentType.JSON
import geb.com.intershop.b2x.testdata.TestDataLoader
import groovy.util.slurpersupport.NodeChildren
import groovyx.net.http.RESTClient
import spock.lang.*

@Narrative("""
As a customer
I want to use the Intershop REST API
to manage set my basket addresses
""")
@Title("i want to manage my basket addresses")
class BasketAddressRestTest extends Specification
{
    @Shared
    RESTClient client = new RESTClient()

    private static final String JSON_GET = "JSON/GET"
    private static final String XML_GET = "XML/GET"

    private static Map<String,List> testData
    private static String customer, customerNo, mail, password, resource, uriISH, basketResourceIdentifier, lineItemResourceIdentifier
    private static String firstNameData, secondNameData, lastNameData, secondLastNameData, emailData, faxData, phoneHomeData, phoneMobileData, phoneBusinessData, phoneBusinessDirectData, titleData, jobTitleData, aristocraticTitleData, honorificData, addressline1Data, addressline2Data, addressline3Data, postalCodeData, postBoxData, mainDivisionData, subDivisionData, countryCodeData, cityData, skuData
    private static String uri
    // gradually increasing number which is appended to address data so no "DuplicateAddress" error occurs when creating multiple addresses
    private static long count = System.nanoTime()
    private String body
    private Object response
    
    public static final String ERROR_MESSAGE ="Bad Request (The following attributes are missing: firstName,lastName,countryCode,postalCode)" 
    
    def setupSpec()
    {
        setup:
        testData = TestDataLoader.getTestData()
        
        customer               = testData.get("rest.address.eMail")[0]
        customerNo             = testData.get("rest.address.customerNo")[0]
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

        skuData                 = testData.get("defaultProduct.default.sku")[0]
        
        resource                = testData.get("rest.address.resource")[0]
        uriISH                  = testData.get("rest.uri.ish")[0]
        uri                     = "http://" + System.properties['hostName'] + ":" + System.properties['webserverPort'] + uriISH
        basketResourceIdentifier      = "/baskets/-/"

        // get basket
        client.headers['Accept'] = 'application/json'
        client.headers['Content-Type'] = 'application/json'
        client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
        Object response = client.get(uri: uri + basketResourceIdentifier, requestContentType: JSON)
        String basketid = response.data.id
        basketResourceIdentifier     = "/baskets/" + basketid

        // create line item
        client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
        client.headers['Content-Type'] = 'application/json'
        client.headers['Accept'] = 'application/json'
        Object body = "{\"elements\":[{\"sku\":" + skuData + ",\"quantity\":{\"value\": 1}}]}"
        response = client.post(uri: uri + basketResourceIdentifier + '/items/', requestContentType: JSON, body: body)
        assert response.success
        assert response.status == 201

        // get line item ID
        client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
        client.headers['Content-Type'] = 'application/json'
        client.headers['Accept'] = 'application/json'
        response = client.get(uri: uri + basketResourceIdentifier + '/items/', requestContentType: JSON)
        assert !response.data.elements.empty
        def lineItemID = response.data.elements[0].id
        assert lineItemID
        lineItemResourceIdentifier = basketResourceIdentifier + '/items/' + lineItemID
    }

    def "Create Basket Invoice Address with XML"()
    {        
        /*
         * =====================================================================
         * Create basket addresses request.
         * =====================================================================
         */
        when: "Create basket invoice address request"
            
            setXMLBasketAddressBody()
            client.headers['Content-Type'] = 'text/xml'
            client.headers['Accept'] = 'text/xml'
            client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
            response = client.put(uri: uri + basketResourceIdentifier, requestContentType: 'text/xml', body: body)
                            
        then: "Test the response regarding expected parameters."
            
            validateAddressResponse(XML_GET)
    }

    def "Create Basket Invoice Address with JSON"()
    {
        /*
         * =====================================================================
         * Create basket addresses request.
         * =====================================================================
         */
        when: "Create basket invoice address request"
            
            setJSONBasketAddressBody()
            client.headers['Accept'] = 'application/json'
            client.headers['Content-Type'] = 'application/json'
            client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
            response = client.put(uri: uri + basketResourceIdentifier, requestContentType: JSON, body: body)
                            
        then: "Test the response regarding expected parameters."

            validateAddressResponse(JSON_GET)
    }

    def "Set Basket Invoice Address with URN With XML"() 
    {        
        /*
         * =====================================================================
         * Create customer addresses request.
         * =====================================================================
         */
        when: "Create customer address request."
        
            createCustomerAddress()
        
        then: "Test the response regarding expected parameters."
        
            assert response.status == 201
            assert response.data instanceof Map
            assert response.data.type == "Link"
            assert response.data.uri.contains('/customers/' + customerNo + '/addresses/')
            def urn = response.data.title
            assert urn != null

        /*
         * =====================================================================
         * Create basket addresses request.
         * =====================================================================
         */
        when: "Set basket invoice address request"

            setXMLBasketAddressURNBody()
            client.headers['Content-Type'] = 'text/xml'
            client.headers['Accept'] = 'text/xml'
            client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
            response = client.put(uri: uri + basketResourceIdentifier, requestContentType: 'text/xml', body: body)
            
       then: "Test the response regarding expected parameters."
            
           validateAddressResponse(XML_GET)
           assert response.data.invoiceToAddress.urn == urn
    }

    def "Set Basket Invoice Address with URN With JSON"()
    {
        /*
         * =====================================================================
         * Create customer addresses request.
         * =====================================================================
         */
        when: "Create customer address request."
        
            createCustomerAddress()
        
        then: "Test the response regarding expected parameters."
        
            assert response.status == 201
            assert response.data instanceof Map
            assert response.data.type == "Link"
            assert response.data.uri.contains('/customers/' + customerNo + '/addresses/')
            def urn = response.data.title
            assert urn != null

        /*
         * =====================================================================
         * Create basket addresses request.
         * =====================================================================
         */
        when: "Set basket invoice address request"

            setJSONBasketAddressURNBody()
            client.headers['Content-Type'] = 'application/json'
            client.headers['Accept'] = 'application/json'
            client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
            response = client.put(uri: uri + basketResourceIdentifier, requestContentType: JSON, body: body)
            
       then: "Test the response regarding expected parameters."
            
           validateAddressResponse(JSON_GET)
           assert response.data.invoiceToAddress.get('urn') == urn
    }

    def "Set Basket Invoice Address with an unknown URN With XML"()
    {
        /*
         * =====================================================================
         * Set basket address request.
         * =====================================================================
         */
         when: "Set basket address request."
             
             setXMLBasketAddressURNBody("urn:address:unknown")
             
             client.headers['Accept'] = 'text/xml'
             client.headers['Content-Type'] = 'text/xml'
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + basketResourceIdentifier, requestContentType: 'text/xml', body: body)
                         
         then: "Request failed => status code should be 400"

             assert !response.success
             assert response.status == 400
    }

    def "Set Basket Invoice Address with an unknown URN With JSON"() 
    {
        /*
         * =====================================================================
         * Set basket address request.
         * =====================================================================
         */
         when: "Set basket address request."
             
             setJSONBasketAddressURNBody("invoiceToAddress", "urn:address:unknown")
             
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + basketResourceIdentifier, requestContentType: JSON, body: body)
                         
         then: "Request failed => status code should be 404"

             assert !response.success
             assert response.status == 400
    }
    
    def "Set Store address as Basket Invoice Address With JSON"()
    {
        /*
         * =====================================================================
         * Set basket address request.
         * =====================================================================
         */
         when: "Set basket address request."
             
             def storeUUID = getArbitraryStoreUUID()
             setJSONBasketAddressUUIDBody("invoiceToAddress", storeUUID)
             
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + basketResourceIdentifier, requestContentType: JSON, body: body)
                         
         then: "Request failed => status code should be 404"

             assert !response.success
             assert response.status == 400
    }
	
	@Ignore
    def "Create Line Item Ship-To Address with XML"()
    {
        /*
         * =====================================================================
         * Create line item addresses request.
         * =====================================================================
         */
        when: "Create line item ship-to address request"
            
            setXMLLineItemAddressBody()
            client.headers['Accept'] = 'text/xml'
            client.headers['Content-Type'] = 'text/xml'
            client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
            response = client.put(uri: uri + lineItemResourceIdentifier, requestContentType: 'text/xml', body: body)
                            
        then: "Test the response regarding expected parameters."

            assert response.success
            assert response.status == 200

        /*
         * =====================================================================
         * Get line item data request.
         * =====================================================================
         */
        when: "Get line item data request"
            
            client.headers['Accept'] = 'text/xml'
            client.headers['Content-Type'] = 'text/xml'
            client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
            response = client.get(uri: uri + lineItemResourceIdentifier, requestContentType: 'text/xml')
                            
        then: "Test the response regarding expected parameters."

            validateAddressResponse(XML_GET, "shipToAddress")
    }

    def "Create Line Item Ship-To Address with JSON"()
    {
        /*
         * =====================================================================
         * Create line item addresses request.
         * =====================================================================
         */
        when: "Create line item ship-to address request"
            
            setJSONBasketAddressBody("shipToAddress")
            client.headers['Accept'] = 'application/json'
            client.headers['Content-Type'] = 'application/json'
            client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
            response = client.put(uri: uri + lineItemResourceIdentifier, requestContentType: JSON, body: body)
                            
        then: "Test the response regarding expected parameters."

            assert response.success
            assert response.status == 200

        /*
         * =====================================================================
         * Get line item data request.
         * =====================================================================
         */
        when: "Get line item data request"
            
            client.headers['Accept'] = 'application/json'
            client.headers['Content-Type'] = 'application/json'
            client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
            response = client.get(uri: uri + lineItemResourceIdentifier, requestContentType: JSON)
                            
        then: "Test the response regarding expected parameters."

            validateAddressResponse(JSON_GET, "shipToAddress")
    }

	@Ignore
    def "Set Line Item Ship-To Address with URN with XML"()
    {

        /*
         * =====================================================================
         * Create customer addresses request.
         * =====================================================================
         */
        when: "Create customer address request."
        
            createCustomerAddress()
        
        then: "Test the response regarding expected parameters."
        
            assert response.status == 201
            assert response.data instanceof Map
            assert response.data.type == "Link"
            assert response.data.uri.contains('/customers/' + customerNo + '/addresses/')
            def urn = response.data.title
            assert urn != null

        /*
         * =====================================================================
         * Create line item addresses request.
         * =====================================================================
         */
        when: "Create line item ship-to address request"
            
            setXMLLineItemAddressURNBody(urn)
            client.headers['Accept'] = 'text/xml'
            client.headers['Content-Type'] = 'text/xml'
            client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
            response = client.put(uri: uri + lineItemResourceIdentifier, requestContentType: 'text/xml', body: body)
                            
        then: "Test the response regarding expected parameters."

            assert response.success
            assert response.status == 200

        /*
         * =====================================================================
         * Get line item data request.
         * =====================================================================
         */
        when: "Get line item data request"
            
            client.headers['Accept'] = 'text/xml'
            client.headers['Content-Type'] = 'text/xml'
            client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
            response = client.get(uri: uri + lineItemResourceIdentifier, requestContentType: 'text/xml')
                            
        then: "Test the response regarding expected parameters."

            validateAddressResponse(XML_GET, "shipToAddress")
    }

    def "Set Line Item Ship-To Address with URN with JSON"()
    {

        /*
         * =====================================================================
         * Create customer addresses request.
         * =====================================================================
         */
        when: "Create customer address request."
        
            createCustomerAddress()
        
        then: "Test the response regarding expected parameters."
        
            assert response.status == 201
            assert response.data instanceof Map
            assert response.data.type == "Link"
            assert response.data.uri.contains('/customers/' + customerNo + '/addresses/')
            def urn = response.data.title
            assert urn != null

        /*
         * =====================================================================
         * Create line item addresses request.
         * =====================================================================
         */
        when: "Create line item ship-to address request"
            
            setJSONBasketAddressURNBody("shipToAddress", urn)
            client.headers['Accept'] = 'application/json'
            client.headers['Content-Type'] = 'application/json'
            client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
            response = client.put(uri: uri + lineItemResourceIdentifier, requestContentType: JSON, body: body)
                            
        then: "Test the response regarding expected parameters."

            assert response.success
            assert response.status == 200

        /*
         * =====================================================================
         * Get line item data request.
         * =====================================================================
         */
        when: "Get line item data request"
            
            client.headers['Accept'] = 'application/json'
            client.headers['Content-Type'] = 'application/json'
            client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
            response = client.get(uri: uri + lineItemResourceIdentifier, requestContentType: JSON)
                            
        then: "Test the response regarding expected parameters."

            validateAddressResponse(JSON_GET, "shipToAddress")
    }

    def "Set Line Item Ship-To Address with an unknown URN With XML"()
    {
        /*
         * =====================================================================
         * Set line item ship-to address request.
         * =====================================================================
         */
         when: "Set line item ship-to address request."
             
             setXMLLineItemAddressURNBody("urn:address:unknown")
             
             client.headers['Accept'] = 'text/xml'
             client.headers['Content-Type'] = 'text/xml'
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + lineItemResourceIdentifier, requestContentType: 'text/xml', body: body)
                         
         then: "Request failed => status code should be 400"

             assert !response.success
             assert response.status == 400
    }

    def "Set Line Item Ship-To Address with an unknown URN With JSON"()
    {
        /*
         * =====================================================================
         * Set line item ship-to address request.
         * =====================================================================
         */
         when: "Set line item ship-to address request."
             
             setJSONBasketAddressURNBody("shipToAddress", "urn:address:unknown")
             
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + lineItemResourceIdentifier, requestContentType: JSON, body: body)
                         
         then: "Request failed => status code should be 400"

             assert !response.success
             assert response.status == 400
    }
    
    def "Set Store address as Line Item Ship-To Address With JSON"()
    {
        /*
         * =====================================================================
         * Set line item ship-to address request.
         * =====================================================================
         */
         when: "Set line item ship-to address request."
             
             def storeUUID = getArbitraryStoreUUID()
             setJSONBasketAddressURNBody("shipToAddress", storeUUID)
             
             client.headers['Accept'] = 'application/json'
             client.headers['Content-Type'] = 'application/json'
             client.handler.failure = client.handler.success
             response = client.put(uri: uri + lineItemResourceIdentifier, requestContentType: JSON, body: body)
                         
         then: "Request failed => status code should be 400"

             assert !response.success
             assert response.status == 400
    }

    def setXMLBasketAddressURNBody(urnData = response.data.title)
    {
        def writer = new StringWriter()
        def xml = new groovy.xml.MarkupBuilder(writer).Basket("name":"basket", "type":"Basket")
        {               
                // invoice to address
               invoiceToAddress("type":"Address") {
                    urn(urnData)
                }
        }
        body = writer.toString().replaceAll("'",'"')
    }

    def setXMLLineItemAddressURNBody(urnData = response.data.title)
    {
        def writer = new StringWriter()
        def xml = new groovy.xml.MarkupBuilder(writer).BasketLineItem("name":"basketLineItem", "type":"BasketLineItem")
        {
                // invoice to address
               shipToAddress("type":"Address") {
                    urn(urnData)
                }
        }
        body = writer.toString().replaceAll("'",'"')
    }
    
    def getArbitraryStoreUUID()
    {
        client.headers['Content-Type']  = JSON
        client.headers['Accept']        = JSON
        def response = client.get(uri: uri + "/stores", requestContentType: JSON)
        
        return response.data.elements.iterator().next().uuid
    }
    
    def setXMLBasketAddressBody() 
    {
        String suffix = ++count
        def writer = new StringWriter()
        def xml = new groovy.xml.MarkupBuilder(writer).Basket("name":"basket", "type":"Basket") 
        {                
               invoiceToAddress("type":"Address") {         
                    postalCode(postalCodeData + suffix)
                    city(cityData + suffix)
                    addressLine1(addressline1Data + suffix)
                    countryCode(countryCodeData)
                    firstName(firstNameData + suffix)
                    secondName(secondNameData + suffix)
                    lastName(lastNameData + suffix)
                    secondLastName(secondLastNameData + suffix)
                    phoneHome(phoneHomeData + suffix)
                    phoneMobile(phoneMobileData + suffix)
                    phoneBusiness(phoneBusinessData + suffix)
                    phoneBusinessDirect(phoneBusinessDirectData + suffix)
                    mainDivision(mainDivisionData + suffix)
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
        }
        body = writer.toString().replaceAll("'",'"')
    }

    def setXMLLineItemAddressBody()
    {
        String suffix = ++count
        def writer = new StringWriter()
        def xml = new groovy.xml.MarkupBuilder(writer).BasketLineItem("name":"basketLineItem", "type":"BasketLineItem")
        {
               shipToAddress("type":"Address") {
                    postalCode(postalCodeData + suffix)
                    city(cityData + suffix)
                    addressLine1(addressline1Data + suffix)
                    countryCode(countryCodeData)
                    firstName(firstNameData + suffix)
                    secondName(secondNameData + suffix)
                    lastName(lastNameData + suffix)
                    secondLastName(secondLastNameData + suffix)
                    phoneHome(phoneHomeData + suffix)
                    phoneMobile(phoneMobileData + suffix)
                    phoneBusiness(phoneBusinessData + suffix)
                    phoneBusinessDirect(phoneBusinessDirectData + suffix)
                    mainDivision(mainDivisionData + suffix)
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
        }
        body = writer.toString().replaceAll("'",'"')
    }

    def setJSONBasketAddressURNBody(addressType = "invoiceToAddress", urnData = response.data.title)
    {
        def builder = new groovy.json.JsonBuilder()
        builder
        {
            "$addressType"
            {
                urn urnData
            }
            
        }
        body = builder.toString()
    }
    
    def setJSONBasketAddressUUIDBody(addressType = "invoiceToAddress", uuidData = response.data.title)
    {
        def builder = new groovy.json.JsonBuilder()
        builder
        {
            "$addressType"
            {
                uuid uuidData
            }
            
        }
        body = builder.toString()
    }
    
    def setJSONBasketAddressBody(addressType = "invoiceToAddress")
    {
        String suffix = ++count
        def builder = new groovy.json.JsonBuilder()
        builder 
        {
            "$addressType"
            {
                mainDivision mainDivisionData + suffix
                postalCode postalCodeData + suffix
                city cityData + suffix
                addressLine1 addressline1Data + suffix
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
            
        }
        body = builder.toString()
    }

    def setJSONAddressBody =
    {
        String suffix = ++count
        def builder = new groovy.json.JsonBuilder()
        builder
        {
                mainDivision mainDivisionData + suffix
                postalCode postalCodeData + suffix
                city cityData + suffix
                addressLine1 addressline1Data + suffix
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
                invoiceToAddress "true"
                shipToAddress "true"
        }
          
        body = builder.toString()
    }
    
    def createCustomerAddress = 
    {
        setJSONAddressBody()
                
        // Add Authorization to rest client
        client.headers['Authorization'] = 'Basic ' + (customer + ':' + password).bytes.encodeBase64().toString()
        client.headers['Content-Type'] = 'application/json'
        client.headers['Accept'] = 'application/json'
        response = client.post(uri: uri + '/customers/' + customerNo + '/addresses/', requestContentType: JSON, body: body)
    }
    
    
    def validateAddressResponse(mode, addressType = "invoiceToAddress") 
    {
        assert response.success
        assert response.status == 200
        String suffix = "" + count
        
        switch (mode)
        {
            case "JSON/GET":
                            def element = response.data."$addressType"
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
                            def NodeChildren address = response.data."$addressType"
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
   
}
