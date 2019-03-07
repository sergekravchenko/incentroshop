package rest.com.intershop.b2x.specs.storefront.customer

import static rest.com.intershop.b2x.specs.storefront.common.RequestBuilder.toJSON
import static rest.com.intershop.b2x.specs.storefront.common.RequestBuilder.toXML
import static rest.com.intershop.b2x.specs.storefront.common.RequestBuilder.XML_FORMAT
import static rest.com.intershop.b2x.specs.storefront.common.RequestBuilder.JSON_FORMAT

import groovy.util.slurpersupport.GPathResult
import rest.com.intershop.b2x.specs.storefront.common.JsonResponseValidator
import rest.com.intershop.b2x.specs.storefront.common.XmlResponseValidator
import rest.com.intershop.b2x.specs.storefront.common.RestSpec
import rest.com.intershop.b2x.specs.storefront.common.RequestBuilder
import groovyx.net.http.ContentType
import spock.lang.Ignore
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Title
import spock.lang.Unroll

@Narrative("""
As a sales organization
I want the Intershop REST api that allows customers self registration in the channel of my organization
accessible also for non authenticated users
""")
@Title("Test the SMB customer registration via ISH REST api")
@Ignore("Ignore until Captcha Registration preference could be switched on/off quickly.")
class RegisterSMBCustomerSpec extends RestSpec
{
    private static final String SMB_CUSTOMER_TYPE = "SMBCustomer"
    private static final String SMB_USER_TYPE = "SMBCustomerUser"

	@Shared String customersRestServiceUri
    @Shared String newCustomerNo, passwordValue, preferredLanguageValue, firstNameValue, lastNameValue, securityQuestionValue, securityQuestionAnswerValue, companyNameValue
    @Shared String cityValue, countryCodeValue

    def setupSpec()
    {
        setup:

        String sitePath = getTestValue("rest.customer.register.site.app.path")
        customersRestServiceUri = "${sitePath}/customers"

        passwordValue = getTestValue("rest.customer.register.credentials.password")
        preferredLanguageValue = getTestValue("rest.customer.register.preferredLanguage")
        firstNameValue = getTestValue("rest.customer.register.fName")
        lastNameValue = getTestValue("rest.customer.register.lName")
        securityQuestionValue = getTestValue("rest.customer.register.credentials.securityQuestion")
        securityQuestionAnswerValue = getTestValue("rest.customer.register.credentials.securityQuestionAnswer")
        cityValue = getTestValue("rest.customer.register.address.city")
        countryCodeValue = getTestValue("rest.customer.register.address.countryCode")
        companyNameValue = getTestValue("rest.customer.register.smb.company")
        newCustomerNo = "newCustomer_" + generateNumber()
    }

    @Unroll
    def "register SMB customer using content type '#createRequest.contentType'"()
    {
        given: "Anonymous user"
            client.clearAuthorizationHeader()
            client.headers['Accept'] = customerResponseHandler.contentType
            client.headers['Content-Type'] = createRequest.contentType
        when: "Send POST request to register new private customer"
            def createResponse = client.post(uri: customersRestServiceUri, requestContentType: createRequest.contentType, body: createRequest.bodyString)
        then: "customer is created and response with link to the customer resource is returned"
            customerResponseHandler.validateCreateResponseData(createResponse)
        when: "Send GET request to see data of the new SMB customer"
            client.setAuthorizationHeader(createRequest.body.credentials.login.toString(), createRequest.body.credentials.password.toString())
            String currentCustomerResourceURI = customerResponseHandler.getResourceLinkURI(createResponse)
            def getCustomerResponse = client.get(uri: currentCustomerResourceURI)
        then: "Get response contains the data provided during registration"
            customerResponseHandler.validateResponseData(getCustomerResponse, createRequest.body)
        when: "Send GET request to see data of the admin user of new SMB customer"
            def getUserResponse = client.get(uri: currentCustomerResourceURI + "/users/-")
        then: "Get response contains the user data provided during registration"
            userResponseHandler.validateResponseData(getUserResponse, createRequest.body)
        when: "Send PUT request to update some of the data of the new SMB customer"
            def updateResponse = client.put(uri: currentCustomerResourceURI, requestContentType: updateRequest.contentType, body: updateRequest.bodyString)
        then: "Update response contains the changed data"
            updateCustomerResponseHandler.validateResponseData(updateResponse, updateRequest.body)
        where:
            createRequest << [getCreateRequestJson(), getCreateRequestXml()]
            customerResponseHandler << [new CustomerJSONResponseHandler(), new CustomerXMLResponseHandler()]
            userResponseHandler << [new UserJSONResponseHandler(), new UserXMLResponseHandler()]
            updateRequest << [getUpdateRequestJson(), getUpdateRequestXml()]
            updateCustomerResponseHandler << [new UpdateSMBCustomerJSONResponseHandler(), new UpdateSMBCustomerXMLResponseHandler()]
    }

	def getCreateRequestJson() {
	    String customerNoValue = newCustomerNo + JSON_FORMAT
        String loginValue = customerNoValue + "@test.net"
		toJSON {
            customerNo(customerNoValue)
            companyName(companyNameValue)
            credentials {
                login(loginValue)
                password(passwordValue)
                securityQuestion(securityQuestionValue)
                securityQuestionAnswer(securityQuestionAnswerValue)
            }
            address {
                city(cityValue)
                firstName(firstNameValue)
                lastName(lastNameValue)
                countryCode(countryCodeValue)
            }
            user() {
                firstName(firstNameValue)
                lastName(lastNameValue)
                businessPartnerNo(loginValue)
                email(loginValue)
                preferredLanguage(preferredLanguageValue)
            }
	    }
	}

	def getCreateRequestXml() {
		String customerNoValue = newCustomerNo + XML_FORMAT
        String loginValue = customerNoValue + "@test.net"
		toXML {
            NewCustomer()
            {
                customerNo(customerNoValue)
                companyName(companyNameValue)
                credentials() {
                    login(loginValue)
                    password(passwordValue)
                    securityQuestion(securityQuestionValue)
                    securityQuestionAnswer(securityQuestionAnswerValue)
                }
                address() {
                    city(cityValue)
                    firstName(firstNameValue)
                    lastName(lastNameValue)
                    countryCode(countryCodeValue)
                }
                user() {
                    firstName(firstNameValue)
                    lastName(lastNameValue)
                    businessPartnerNo(loginValue)
                    email(loginValue)
                    preferredLanguage(preferredLanguageValue)
                }
            }
		}
	}

	def getUpdateRequestJson() {
	    String customerNoValue = newCustomerNo + JSON_FORMAT
		String updatedCompanyValue = "Other-" + companyNameValue
	    toJSON {
            customerNo(customerNoValue)
            companyName(updatedCompanyValue)
		}
	}

	def getUpdateRequestXml() {
	    String customerNoValue = newCustomerNo + XML_FORMAT
		String updatedCompanyValue = "Other-" + companyNameValue
	    toXML {
            SMBCustomer()
            {
                customerNo(customerNoValue)
                companyName(updatedCompanyValue)
            }
		}
	}

    static class CustomerXMLResponseHandler extends XmlResponseValidator
    {
        void checkResponseContent(GPathResult responseData, def expectedData)
        {
            assert responseData.@type == SMB_CUSTOMER_TYPE
            assert responseData.customerNo == expectedData.customerNo
            assert responseData.companyName == expectedData.companyName
        }

        void checkGetUserResponseContent(GPathResult responseData, def expectedData)
        {
            assert responseData.@type == SMB_USER_TYPE
            assert responseData.businessPartnerNo == expectedData.user.businessPartnerNo
            assert responseData.firstName == expectedData.user.firstName
        }
    }

    static class CustomerJSONResponseHandler extends JsonResponseValidator
    {
        void checkResponseContent(Map responseData, def expectedData)
        {
            assert responseData.type == SMB_CUSTOMER_TYPE
            assert responseData.customerNo == expectedData.customerNo
            assert responseData.companyName == expectedData.companyName
        }

        void checkGetUserResponseContent(Map responseData, def expectedData)
        {
            assert responseData.type == SMB_USER_TYPE
            assert responseData.businessPartnerNo == expectedData.user.businessPartnerNo
            assert responseData.firstName == expectedData.user.firstName
        }
    }

    static class UserXMLResponseHandler extends XmlResponseValidator
    {
        void checkResponseContent(GPathResult responseData, def expectedData)
        {
            assert responseData.@type == SMB_USER_TYPE
            assert responseData.businessPartnerNo == expectedData.user.businessPartnerNo
            assert responseData.firstName == expectedData.user.firstName
        }
    }

    static class UserJSONResponseHandler extends JsonResponseValidator
    {
        void checkResponseContent(Map responseData, def expectedData)
        {
            assert responseData.type == SMB_USER_TYPE
            assert responseData.businessPartnerNo == expectedData.user.businessPartnerNo
            assert responseData.firstName == expectedData.user.firstName
        }
    }

    static class UpdateSMBCustomerXMLResponseHandler extends XmlResponseValidator
    {
        void checkResponseContent(GPathResult responseData, def expectedData)
        {
            assert responseData.@type == SMB_CUSTOMER_TYPE
            assert responseData.customerNo == expectedData.customerNo
            assert responseData.companyName == expectedData.companyName
        }
    }

    static class UpdateSMBCustomerJSONResponseHandler extends JsonResponseValidator
    {
        void checkResponseContent(Map responseData, def expectedData)
        {
            assert responseData.type == SMB_CUSTOMER_TYPE
            assert responseData.customerNo == expectedData.customerNo
            assert responseData.companyName == expectedData.companyName
        }
    }
}