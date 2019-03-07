package rest.com.intershop.b2x.specs.backoffice.batchprocesses

import static javax.servlet.http.HttpServletResponse.SC_OK
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED

import rest.com.intershop.b2x.specs.storefront.common.RestSpec
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Title
import spock.lang.Unroll

@Narrative("""
As a customer
I want the Intershop REST api for managing the import proceses for my organization
""")
@Title("Test the backoffice batchprocesses REST api")
class BatchProcessesRestSpec extends RestSpec
{
    @Shared String restServiceUri, admin, password

    def setupSpec()
    {
        setup:
            // Backoffice REST API require HTTPS
            client.setUseHTTPS(true)

            admin = testData.get("rest.batchprocesses.user")[0]
            password = testData.get("rest.batchprocesses.password")[0]
            restServiceUri = testData.get("rest.batchprocesses.uri.ish")[0]
    }

    @Unroll
    def "test get batchprocesses using contentType #contentType"()
    {
        given: "authenticated with user having required permissions"
            client.setAuthorizationHeader(admin, password)
            client.headers['Accept'] = contentType
        when: "request get batchprocesses"
            def response = client.get(uri: restServiceUri)
        then: "valid response is returned"
            response.status == SC_OK
            response.success
            assertType(response.data, "ResourceCollection", contentType );
        where:
            contentType << [JSON, XML]
    }

    @Unroll
    def "test deny anonimous user using contentType #contentType"()
    {
        given: "not using authentication for specific customer resource"
            client.clearAuthorizationHeader()
            client.headers['Accept'] = contentType
        when: "request get batchprocesses"
            def response = client.get(uri: restServiceUri)
        then: "unauthorized response is returned as REST resource require authenticated user"
            response.status == SC_UNAUTHORIZED
        where:
            contentType << [JSON, XML]
    }
}