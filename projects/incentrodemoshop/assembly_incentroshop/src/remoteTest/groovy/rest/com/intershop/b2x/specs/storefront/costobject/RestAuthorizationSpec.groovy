package rest.com.intershop.b2x.specs.storefront.costobject

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN
import static javax.servlet.http.HttpServletResponse.SC_OK
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED

import rest.com.intershop.b2x.specs.storefront.common.RestSpec
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Title
import spock.lang.Unroll

@Narrative("""
As a customer
I want the Intershop REST api for managing the available cost object types for my organization
to be accessible only for users with assigned specific permissions
""")
@Title("Test the permission based authorization of REST api")

class RestAuthorizationSpec extends RestSpec
{
    @Shared String restServiceUri, currentCustomerRestServiceUri, admin, buyer, otherCustomerAdmin, password

    def setupSpec()
    {
        setup:
            String customer = testData.get("rest.costobjecttypes.customer")[0]
            admin = testData.get("rest.costobjecttypes.eMail")[0]
            buyer = testData.get("rest.costobjecttypes.buyer.eMail")[0]
            otherCustomerAdmin = testData.get("rest.costobjecttypes.other.eMail")[0]
            password = testData.get("rest.costobjecttypes.password")[0]

            String baseURI = testData.get("rest.base.uri")[0]

            restServiceUri = "${baseURI}/customers/${customer}/costobjecttypes"
            currentCustomerRestServiceUri = "${baseURI}/customers/-/costobjecttypes"
    }

    @Unroll
    def "test authorized user using contentType #contentType"()
    {
        given: "authenticated with user having required permissions"
            client.setAuthorizationHeader(admin, password)
            client.headers['Accept'] = contentType
        when: "request get cost object types"
            def response = client.get(uri: restServiceUri)
        then: "valid response is returned"
            response.success
            response.status == SC_OK
            assertType(response.data, "ResourceCollection", contentType );
            assertName(response.data, "costobjecttypes", contentType );
        where:
            contentType << [JSON, XML]
    }

    @Unroll
    def "test not authorized user using contentType #contentType"()
    {
        given: "authenticated with user not having required permissions"
            client.setAuthorizationHeader(buyer, password)
            client.headers['Accept'] = contentType
        when: "request get cost object types"
            def response = client.get(uri: restServiceUri)
        then: "forbidden response is returned"
            response.status == SC_FORBIDDEN
        where:
            contentType << [JSON, XML]
    }

	@Unroll
    def "test anonimous user using contentType #contentType"()
    {
        given: "not using authentication for specific customer resource"
            client.clearAuthorizationHeader()
            client.headers['Accept'] = contentType
        when: "request get cost object types"
            def response = client.get(uri: restServiceUri)
        then: "not found response is returned as customer with given id not found"
            response.status == SC_UNAUTHORIZED
        where:
            contentType << [JSON, XML]
    }

    @Unroll
    def "test current customer anonimous user using contentType #contentType"()
    {
        given: "not using authentication for current customer(-) resource"
            client.clearAuthorizationHeader()
            client.headers['Accept'] = contentType
        when: "request get cost object types"
            def response = client.get(uri: currentCustomerRestServiceUri)
        then: "not authorized response is returned as the REST resource require authentication"
            response.status == SC_UNAUTHORIZED
        where:
            contentType << [JSON, XML]
    }

    @Unroll
    def "test other customer admin user using contentType #contentType"()
    {
        given: "authenticated with user having required permissions but not belonging to the requested customer"
            client.setAuthorizationHeader(otherCustomerAdmin, password)
            client.headers['Accept'] = contentType
        when: "request get cost object types"
            def response = client.get(uri: restServiceUri)
        then: "forbidden response is returned"
            response.status == SC_FORBIDDEN
        where:
            contentType << [JSON, XML]
    }

    @Unroll
    def "test wrong credentials using contentType #contentType"()
    {
        given: "authenticated with not existing user"
            client.setAuthorizationHeader("aaa@aaa.aaa", password)
            client.headers['Accept'] = contentType
        when: "request get cost object types"
            def response = client.get(uri: restServiceUri)
        then: "not authorized response is returned"
            response.status == SC_UNAUTHORIZED
        where:
            contentType << [JSON, XML]
    }
}