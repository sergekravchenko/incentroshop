package rest.com.intershop.b2x.specs.storefront.common

import static javax.servlet.http.HttpServletResponse.SC_CREATED
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND
import static javax.servlet.http.HttpServletResponse.SC_OK
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED

import groovyx.net.http.ContentType

class JsonResponseValidator {

    static final String contentType = ContentType.JSON.toString()

    void assertStatusCreated(def response, def URI, def expectedData = null)
    {
        hasStatusCreated(response)
        hasLocationHeader(response, URI)
        hasNoErrors(response.data)
        hasNoInfos(response.data)

        checkResponseData(response.data, expectedData)
    }

    void assertStatusUnauthorized(def response, def expectedData = null)
    {
        hasStatusUnauthorized(response)
    }

    void assertStatusOk(def response, def expectedData = null)
    {
        hasStatusOk(response)
        hasAuthenticationToken(response)
        hasNoErrors(response.data)

        checkResponseData(response.data, expectedData)
    }

    void assertStatusNotFound(def response, def expectedData = null)
    {
        hasStatusNotFound(response)
        hasErrors(response.data)
        hasError404(response.data)

        hasErrorCode(response.data, expectedData)
    }

    void assertStatusUnprocessable(def response, def expectedData = null)
    {
        hasStatusUnprocessable(response)
        checkResponseData(response.data, expectedData)
    }

    static hasStatusCreated(def response)
    {
        assert response.success == true
        assert response.status == SC_CREATED
    }

    static hasStatusUnauthorized(def response)
    {
        assert response.status == SC_UNAUTHORIZED
    }

    static hasStatusOk(def response)
    {
        assert response.status == SC_OK
    }

    static hasStatusNotFound(def response)
    {
        assert response.status == SC_NOT_FOUND
    }

    static hasStatusUnprocessable(def response)
    {
        assert response.status == 422
    }

    static hasNoErrors(def responseData)
    {
        assert responseData.errors == null
    }

    static hasErrors(def responseData)
    {
        List errors = responseData.errors
        assert errors.size() == 1
    }

    static hasError404(def responseData)
    {
        def error = responseData.errors.get(0)
        assert error.status == "404"
    }

    static hasNoData(def responseData)
    {
        assert responseData.data == null
    }

    static hasNoInfos(def responseData)
    {
        assert responseData.infos == null
    }

    static hasInfos(def responseData)
    {
        List infos = responseData.infos
        assert infos.size() == 1
    }

    static hasIncludes(def responseData)
    {
        assert responseData.included != null
    }
    
    static hasData(def responseData)
    {
        assert responseData.data != null
    }

    static hasInfoStatus200(def responseData)
    {
        def info = responseData.infos.get(0)
        assert info.status == "200"
    }

    static hasAuthenticationToken(def response)
    {
        assert response.headers."authentication-token" != null
    }

    static hasErrorCode(def responseData, String errorCode)
    {
        assert responseData.errors.find{it.code.contains(errorCode)} != null
    }

    static hasInfoCode(def responseData, String infoCode)
    {
        assert responseData.infos.find{it.code.contains(infoCode)} != null
    }

    static hasDataId(def responseData, String id)
    {
        assert responseData.data.id == id
    }

    static hasLocationHeader(def response, def URI)
    {
        Map responseData = response.data
        String ID = responseData.data.id
        assert ID != null
        String location = response.headers."Location"
        assert location.endsWith("${URI}/${ID}")
    }

    static hasErrorCause(def responseData, String cause)
    {
        assert responseData.errors.causes.find{it.code.contains(cause)} != null
    }

    def void validateCreateResponseData(def response)
    {
        assert response.success
        assert response.status == SC_CREATED
        assert response.data instanceof Map

        assert response.data.type == "Link"
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
        assert responseData instanceof Map

        checkResponseContent(responseData, expectedData)
    }

    void checkResponseContent(Map responseData, def expectedData) {}

    void checkResponseData(def responseData, def expectedData = null) {}
}
