package rest.com.intershop.b2x.specs.smc;

import static javax.servlet.http.HttpServletResponse.SC_OK
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED

import rest.com.intershop.b2x.specs.storefront.common.RestSpec
import spock.lang.Shared
import spock.lang.Unroll

public class SMCJobsRestSpec extends RestSpec
{
    @Shared String restServiceUri, adminUserLogin, adminUserPassword
    
    def setupSpec()
    {
        setup:
            adminUserLogin = testData.get("smc.adminUser.login")[0]
            adminUserPassword = testData.get("smc.adminUser.password")[0]

            restServiceUri = testData.get("rest.smc.uri.ish")[0] + "/jobs"
    }

    @Unroll
    def "Check Admin user sees Jobs resource using contentType #contentType"()
    {
        given:
            client.setAuthorizationHeader(adminUserLogin, adminUserPassword)
            client.headers['Accept'] = contentType
        when: "request GET Jobs"
            def response = client.get uri: restServiceUri
        then:
            response.success
            response.status == SC_OK
            assertType response.data, "ResourceCollection", contentType
    where:
        contentType << [JSON, XML]
    }

    @Unroll
    def "Check Anonimous user is denied using contentType #contentType"()
    {
        given: "not using authentication"
            client.clearAuthorizationHeader()
            client.headers['Accept'] = contentType
        when: "request GET Jobs"
            def response = client.get uri: restServiceUri
        then: "unauthorized response is returned as REST resource require authenticated user"
            response.status == SC_UNAUTHORIZED
        where:
            contentType << [JSON, XML]
    }
}
