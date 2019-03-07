package rest.com.intershop.b2x.specs.storefront.costobject

import groovy.json.JsonBuilder
import groovy.util.slurpersupport.GPathResult
import rest.com.intershop.b2x.specs.storefront.common.RestSpec
import spock.lang.Ignore
import spock.lang.Narrative
import spock.lang.Title
import spock.lang.Unroll

@Narrative("""
As a customer
I want to use the Intershop REST api
to manage the available cost object types
""")
@Title("Test the cost object type management through the REST api")
@Ignore("temporary ignore until required changes are available")
class CostObjectTypeRestSpec extends RestSpec
{
    private static String baseURI
    private static String costObjectTypesResourcePath

    private static final String englishLocaleID = "en_US"
    private static final String germanLocaleID = "de_DE"

    private static final String typeName = "TestObject"

    private static CostObjectType createdType = new CostObjectType(typeName, "Test Object", "Cost Object Type used for testing")

    private static CostObjectType updatedValuesType = new CostObjectType(typeName, "Test Cost Object", "Test specific Cost Object Type")

    private static CostObjectType addedLocalizedDataType = new CostObjectType(typeName, "Testobjeckt", "Kostentraegertyp zum Testen verwendet")

    def setupSpec()
    {
        setup:       
            String customer             = testData.get("rest.costobjecttypes.customer")[0]
            String user                 = testData.get("rest.costobjecttypes.eMail")[0]
            String password             = testData.get("rest.costobjecttypes.password")[0]
            String uriSite              = testData.get("rest.costobjecttypes.uri.site")[0]

            baseURI                     = testData.get("rest.base.uri")[0]

            costObjectTypesResourcePath = "customers/${customer}/costobjecttypes"

            client.setAuthorizationHeader(user, password)
    }

    @Unroll
    def "test cost object type REST operations. Content type: '#request.requestType'"()
    {
        given:
            client.headers['Content-Type'] = request.contentType
            client.headers['Accept'] = responseHandler.contentType
        when: "creating a new cost object type"
            def createResponse = client.post(uri: getURL(null), requestContentType: request.contentType, body: request.createRequestBody)
        then: "the cost object type is created"
            createResponse.success
            createResponse.status == 201
            responseHandler.validateLink(createResponse)
        when: "requesting the new cost object type without locale parameters"
            String newCostObjectTypeLinkTitle = responseHandler.getLinkTitle(createResponse)
            String costObjectTypeURI = getURL(null) + "/${newCostObjectTypeLinkTitle}"
            
            def getResponse = client.get(uri: costObjectTypeURI)
        then: "it is returned without the localized data"
            responseHandler.validateCostObjectType(getResponse, createdType);
        when: "the cost object type is changed"
            String costObjectTypeEnglishURI = getURL(englishLocaleID) + "/${newCostObjectTypeLinkTitle}"
            def updateValuesResponse = client.put(uri: costObjectTypeEnglishURI, requestContentType: request.contentType, body: request.updateValuesRequestBody)
        then: "the cost object type is updated"
            updateValuesResponse.success
            updateValuesResponse.status == 200

        when: "a new GET is performed with locale parameter"
            def getAfterUpdateValuesResponse = client.get(uri: "${costObjectTypeEnglishURI}")

        then: "the updated cost object type is retrieved for the given locale"
            responseHandler.validateCostObjectType(getAfterUpdateValuesResponse, updatedValuesType);

        when: "the cost object type is changed with data for another locale"
            String costObjectTypeGermanURI = getURL(germanLocaleID) + "/${newCostObjectTypeLinkTitle}"
            def updateLocalizedDataResponse = client.put(uri: costObjectTypeGermanURI, requestContentType: request.contentType, body: request.addLocalizedDataRequestBody)

        then: "the cost object type is updated with data for another locale"
            updateLocalizedDataResponse.success
            updateLocalizedDataResponse.status == 200

        when: "a new GET is performed with the updated locale as parameter"
            def getAfterUpdateLocalizedDataResponse = client.get(uri: "${costObjectTypeGermanURI}")

        then: "the updated cost object type is retrieved with data for the updated locale"
            responseHandler.validateCostObjectType(getAfterUpdateLocalizedDataResponse, addedLocalizedDataType);

        when: "The cost object type is deleted"
            def deleteResponse = client.delete(uri: costObjectTypeURI)

        then: "It is no longer available"
            deleteResponse.success
            deleteResponse.status == 204

        where:
            request << [new JSONRequest(), new XMLRequest()]
            responseHandler << [new JSONResponseHandler(), new XMLResponseHandler()]
    }

    private String getURL(String localeID)
    {
        String uriLocale = (localeID == null) ? "" : ";loc=${localeID}"
        "${baseURI}${uriLocale}/${costObjectTypesResourcePath}"
    }

    static class CostObjectType
    {
        String name
        String displayName
        String description
        
        def CostObjectType(String name, String displayName, String description)
        {
            this.name = name
            this.displayName = displayName
            this.description = description
        }
    }
    
    static class JSONRequest
    {
        String contentType = JSON
       
        String createRequestBody = new JsonBuilder(
            {
               name typeName
               displayName createdType.displayName
               description createdType.description
            }).toString()
        
        String updateValuesRequestBody = new JsonBuilder(
            {
               name typeName
               displayName updatedValuesType.displayName
               description updatedValuesType.description
            }).toString()
            
        String addLocalizedDataRequestBody = new JsonBuilder(
            {
               name typeName
               displayName addedLocalizedDataType.displayName
               description addedLocalizedDataType.description
            }).toString()
    }

    static class JSONResponseHandler
    {
        String contentType = JSON
        
        def void validateCostObjectType(def response, CostObjectType type)
        {
            assert response.success
            assert response.status == 200
            
            def data = response.data
            assert data instanceof Map
            
            assert data.name == type.name
            assert data.displayName == type.displayName
            assert data.description == type.description
        }
        
        def void validateLink(def response)
        {
            assert response.data instanceof Map
            
            assert response.data.type == "Link"
            assert response.data.uri != null
        }
        
        String getLinkTitle(def response)
        {
            response.data.title
        }
    }
    
    class XMLRequest
    {
        String contentType = XML
        
        String createRequestBody = 
        '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <CostObjectType>
              <name>''' + typeName + '''</name>
              <displayName>''' + createdType.displayName + '''</displayName>
              <description>''' + createdType.description + '''</description>
            </CostObjectType>
        '''
        
        String updateValuesRequestBody = 
        '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <CostObjectType>
              <name>''' + typeName + '''</name>
              <displayName>''' + updatedValuesType.displayName + '''</displayName>
              <description>''' + updatedValuesType.description + '''</description>
            </CostObjectType>
        '''
                
        String addLocalizedDataRequestBody =
        '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <CostObjectType>
              <name>''' + typeName + '''</name>
              <displayName>''' + addedLocalizedDataType.displayName + '''</displayName>
              <description>''' + addedLocalizedDataType.description + '''</description>
            </CostObjectType>
        '''
    }

    static class XMLResponseHandler
    {
        String contentType = XML

        def void validateCostObjectType(def response, CostObjectType type)
        {
            assert response.success
            assert response.status == 200
            
            def data = response.data
            assert data instanceof GPathResult
            
            assert data.name == type.name
            assert data.displayName == type.displayName
            assert data.description == type.description
        }
        
        def void validateLink(def response)
        {
            def data = response.data
            assert data instanceof GPathResult
            
            assert data.@type == "Link"
            assert data.uri != null
        }
        
        String getLinkTitle(def response)
        {
            response.data.title
        }
    }
}