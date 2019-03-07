package rest.com.intershop.b2x.specs.storefront.common

import static javax.servlet.http.HttpServletResponse.SC_OK
import static javax.servlet.http.HttpServletResponse.SC_CREATED
import groovyx.net.http.ContentType
import groovy.util.slurpersupport.GPathResult

abstract class XmlResponseValidator {
    static final String contentType = ContentType.XML.getContentTypeStrings()[1]

    def void validateCreateResponseData(def response)
    {
        assert response.success
        assert response.status == SC_CREATED
        assert response.data instanceof GPathResult

        assert response.data.@type == "Link"
        assert response.data.uri != null
    }

    String getResourceLinkURI(def response)
    {
        response.data.uri
    }

    def void validateResponseData(def response, def expectedData)
    {
        assert response.success
        assert response.status == SC_OK

        def responseData = response.data
        assert responseData instanceof GPathResult

        checkResponseContent(responseData, expectedData)
    }

    abstract void checkResponseContent(GPathResult responseData, def expectedData)
}
