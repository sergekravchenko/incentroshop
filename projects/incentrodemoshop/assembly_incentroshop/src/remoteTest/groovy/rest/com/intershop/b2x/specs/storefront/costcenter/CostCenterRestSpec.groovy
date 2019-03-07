package rest.com.intershop.b2x.specs.storefront.costcenter

import static javax.servlet.http.HttpServletResponse.SC_CREATED
import static javax.servlet.http.HttpServletResponse.SC_OK

import groovy.json.JsonBuilder
import groovyx.net.http.ContentType
import rest.com.intershop.b2x.specs.storefront.common.RestSpec

class CostCenterRestSpec extends RestSpec
{
    private static String userCostCentersRestServiceUri, basketId, basketsURI, costCenterId, authorizedUserCredentials

    def setupSpec()
    {
        setup:

            String customer = testData.get("rest.costcenter.customer")[0]
            String userLogin = testData.get("rest.costcenter.eMail")[0]
            String password = testData.get("rest.costcenter.password")[0]
            String userCostCentersResourcePath = "customers/${customer}/users/${userLogin}/costcenters"

            String baseURI  = testData.get("rest.base.uri")[0]

            userCostCentersRestServiceUri = "${baseURI}/${userCostCentersResourcePath}"
            basketsURI = "${baseURI}/baskets"

            client.setAuthorizationHeader(userLogin, password)
    }

    def "get user cost centers"() {
        given: "authenticated with user having required permissions"
            client.headers['Accept'] = contentType
        when: "request get cost centers"
            def response =  client.get(uri: userCostCentersRestServiceUri)
        then: "valid response is returned"
            response.success
            response.status == SC_OK
            assertType(response.data, "UserCostCenterLinkROCollection", contentType )
        when: "request get cost center details"
            costCenterId = responseHandler.getCostCenterId(response)
            def userCostCenterRestServiceUri = userCostCentersRestServiceUri + "/" + costCenterId
            def costCenterResponse =  client.get(uri: userCostCenterRestServiceUri)
        then: "valid response is returned"
            costCenterResponse.success
            costCenterResponse.status == SC_OK
            assertType(costCenterResponse.data, "UserCostCenter", contentType )
            costCenterResponse.data.id == costCenterId
        where:
            contentType << [JSON , XML]
            responseHandler << [new JSONResponseHandler(), new XMLResponseHandler()]
    }

    def "assign cost center to existing basket"() {
        given: "authenticated with basket id having required permissions"
            client.headers['Accept'] = contentType
            client.headers['Content-Type'] = contentType
        when: "create basket"
            def response = client.post(uri: basketsURI)
        then: "valid response is returned"
            response.success
            response.status == SC_CREATED
            assertType(response.data, "Link", contentType)
        when: "assign cost center to basket"   
            basketId = response.data.title
            def updateBasketURI = basketsURI + "/" + basketId
            def requestBody = request.createRequestBody
            response = client.put(uri: updateBasketURI, requestContentType: request.contentType, body: requestBody)
        then: "valid response is returned"
            response.success
            response.status == SC_OK
            assertType(response.data, "Basket", contentType)
            response.data.costCenterID == costCenterId
            response.data.id == basketId
        where:
            contentType << [JSON , XML]
            request << [new JSONRequest() ,new XMLRequest()]
    }

    static class JSONResponseHandler {
        String getCostCenterId(def response) {
            response.data.elements.id[0]
        }
    }

    static class XMLResponseHandler {

        String getCostCenterId(def response) {
            response.data.elements.UserCostCenterLink.id[0]
        }
    }

    static class JSONRequest
    {        
        ContentType contentType = ContentType.JSON
        String createRequestBody = new JsonBuilder(
            {
               costCenterID costCenterId
             
            }).toString()
    }

   static class XMLRequest
    {        
        ContentType contentType = ContentType.XML
        def writer = new StringWriter()
        def xml = new groovy.xml.MarkupBuilder(writer).Basket("name":"basket", "type":"Basket")
        {
            costCenterID(costCenterId)
        }
        String createRequestBody = writer.toString().replaceAll("'",'"')
    }
}


