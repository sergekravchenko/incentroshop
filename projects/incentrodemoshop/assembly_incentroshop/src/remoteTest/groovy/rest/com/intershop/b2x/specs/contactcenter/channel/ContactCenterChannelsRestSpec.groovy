package rest.com.intershop.b2x.specs.contactcenter.channel

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN
import static javax.servlet.http.HttpServletResponse.SC_OK
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED

import rest.com.intershop.b2x.specs.storefront.common.RestSpec
import spock.lang.Shared
import spock.lang.Unroll

class ContactCenterChannelsRestSpec extends RestSpec
{
    @Shared String restServiceUri, adminUserLogin, adminUserPassword, ordinaryUserLogin, ordinaryUserPassword

    def setupSpec()
    {
        setup:
            adminUserLogin       = testData.get('contactCenter.adminUser.login')[0]
            adminUserPassword    = testData.get('contactCenter.adminUser.password')[0]
            ordinaryUserLogin    = testData.get('contactCenter.ordinaryUser.login')[0]
            ordinaryUserPassword = testData.get('contactCenter.ordinaryUser.password')[0]

            restServiceUri = testData.get("rest.contactCenter.uri.ish")[0] + "/channels"

            String userOrganizationKey  = testData.get('contactCenter.userOrganization.key')[0]
            String userOrganizationName = testData.get('contactCenter.userOrganization.name')[0]
            client.headers[userOrganizationKey] = userOrganizationName
    }

    @Unroll
    def "Check Logged in Admin User Sees Channels using contentType #contentType"()
    {
        given:
            client.setAuthorizationHeader(adminUserLogin, adminUserPassword)
            client.headers['Accept'] = contentType
        when: "request get users"
            def response = client.get(uri: restServiceUri)
        then:
            response.success
            response.status == SC_OK
            assertType(response.data, "ResourceCollection", contentType);
        where:
            contentType << [JSON, XML]
    }

    @Unroll
    def "Check Anonimous user is denied using contentType #contentType"()
    {
        given: "not using authentication"
            client.clearAuthorizationHeader()
            client.headers['Accept'] = contentType
        when: "request get users"
            def response = client.get(uri: restServiceUri)
        then: "unauthorized response is returned as REST resource require authenticated user"
            response.status == SC_UNAUTHORIZED
        where:
            contentType << [JSON, XML]
    }

    @Unroll
    def "Check Logged in user with no Operate Contact Center permission is denied using contentType #contentType"()
    {
        given:
            client.setAuthorizationHeader(ordinaryUserLogin, ordinaryUserPassword)
            client.headers['Accept'] = contentType
        when: "request get users"
            def response = client.get(uri: restServiceUri)
        then:
            response.status == SC_FORBIDDEN
        where:
            contentType << [JSON, XML]
    }
}
