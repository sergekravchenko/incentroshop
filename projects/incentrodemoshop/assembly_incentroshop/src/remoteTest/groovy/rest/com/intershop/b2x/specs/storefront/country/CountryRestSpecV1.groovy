package rest.com.intershop.b2x.specs.storefront.country

import java.time.*

import rest.com.intershop.b2x.specs.storefront.common.JsonResponseValidator
import rest.com.intershop.b2x.specs.storefront.common.RestSpec
import spock.lang.*

@Narrative("""As a customer
I want to use the ICM Web Shop REST API
to handle country operations.""")
@Title("Tests for country REST resources (v1)")
class CountryRestSpecV1 extends RestSpec
{
    static final String ACCEPT = "application/vnd.intershop.country.v1+json"
    static final String COUNTRY_PATH = "/countries"
    static final String MAINDIVISION_PATH = "main-divisions"

    static final String COUNTRY_NOT_FOUND_ERROR = "country.not_found.error"
    
    
    @Shared
    String countriesURI, country_name, country_ID, maindivision_name, site

    def setupSpec()
    {
        country_name = getTestValue("rest.country.countryName")
        country_ID = getTestValue("rest.country.countryID")
        maindivision_name = getTestValue("rest.country.mainDivision")
        site = getTestValue("rest.b2b.uri.ish")

        String host = System.properties["hostName"]
        String port = System.properties["webserverPort"]
        countriesURI = "http://${host}:${port}${site}${COUNTRY_PATH}"
    }

    def setup()
    {
        client.headers."Accept" = ACCEPT
        // handle failure like success (i. e. don't throw exception)
        client.handler.failure = client.handler.success
        // register parser for custom "Accept" content type
        def parser = client.parser
        parser.putAt(ACCEPT, parser.getAt(JSON))
    }

    @Ignore
    def "Get country list"()
    {
        when: "Send \"GET /countries\" request"
            def response = client.get(uri: countriesURI, requestContentType: JSON)

        then: "Request is successful"
            responseValidator.assertStatusOk(response, [country_ID, country_name])

        where:
            responseValidator << new GetCountryListResponseHandler()
    }

    @Ignore
    def "Get country item"()
    {
        when: "Send \"GET /countries/<country_ID>\" request"
            def response = client.get(uri: countriesURI + "/DDR", requestContentType: JSON)

        then: "Request failed(404)"
            responseValidator.assertStatusNotFound(response, COUNTRY_NOT_FOUND_ERROR)

        when: "Send \"GET /countries/<country_ID>\" request"
            response = client.get(uri: countriesURI + "/${country_ID}", requestContentType: JSON)

        then: "Request is successful"
            responseValidator.assertStatusOk(response, [country_ID, country_name])

        where:
            responseValidator << new GetCountryResponseHandler()
    }

    def "Get main-division list"()
    {
        when: "Send \"GET /countries/<country_ID>/main-divisions\" request"
            def response = client.get(uri: countriesURI + "/${country_ID}/${MAINDIVISION_PATH}", requestContentType: JSON)

        then: "Request is successful"
            responseValidator.assertStatusOk(response, maindivision_name)

        where:
            responseValidator << new GetMainDivisionListResponseHandler()
    }
}

/**
 *  VALIDATION CLASS IMPLEMENTATIONS
 */
class GetMainDivisionListResponseHandler extends JsonResponseValidator
{
    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        def data = responseData.data
        // maindivision are returned
        assert data.find{it.name.equals(expectedData)} != null
        assert data.size() >1
    }
}

class GetCountryListResponseHandler extends JsonResponseValidator
{
    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
        def data = responseData.data
        assert data.size() >1
        // countries are returned
        assert data.find{it.id.equals(expectedData[0])} != null
        assert data.find{it.name.equals(expectedData[1])} != null
    }
}

class GetCountryResponseHandler extends JsonResponseValidator
{
    
    @Override
    void checkResponseData(def responseData, def expectedData = null)
    {
            def data = responseData.data
            // country details are returned
            assert data.id.equals(expectedData[0])
            assert data.name.equals(expectedData[1])
    }
}