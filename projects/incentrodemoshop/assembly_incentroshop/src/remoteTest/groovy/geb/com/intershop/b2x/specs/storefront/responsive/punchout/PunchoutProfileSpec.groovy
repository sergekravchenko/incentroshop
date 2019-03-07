package geb.com.intershop.b2x.specs.storefront.responsive.punchout

import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoUser
import geb.com.intershop.b2x.pages.storefront.responsive.HomePage
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountHomePage
import geb.com.intershop.b2x.pages.storefront.responsive.account.punchout.PunchoutProfileSettingsPage
import geb.com.intershop.b2x.specs.storefront.responsive.features.Authentication
import geb.com.intershop.b2x.specs.storefront.responsive.features.OCIUserLogin
import geb.com.intershop.b2x.testdata.TestDataLoader
import geb.spock.GebReportingSpec
import spock.lang.Ignore

@Ignore
public class PunchoutProfileSpec extends GebReportingSpec implements Authentication, OCIUserLogin
{
    private static Map<String,List> testData;

    def setupSpec()
    {
        setup:
        testData = TestDataLoader.getTestData();
    }
    
    /** 
	*
	* Change the email of an OCI user
	*
	*/
	def "Change the email of the OCI punchout user"()
    {
        //a user already exists when using ACCOUNT_ADMIN_PUNCHOUT
        given: "I have logged in and am on My Account Home Page"
           logInUser(DemoUser.ACCOUNT_ADMIN_PUNCHOUT.user);
        
		def newEmail = System.currentTimeMillis() + "@test.intershop.de"
		
        when:
           at AccountHomePage
        and: "I click the punchout link in the menu"
             clickPunchout()
                  
		and:"I click on the profile settings icon"		  
			profileSettingsLink.click()				

		and:"I click on the icon to edit the email"		
			editEmailLink.click()		
		then:"I'm on the OCI Edit Email page"

		when:"I add an email address and submit"		  
			emailInput.value newEmail
			saveButton.click()
		then:"The new email address is displayed"		
			at PunchoutProfileSettingsPage
			assert $("[data-testing-id='account-punchout-email-field']").text() == newEmail

        when:"I reset the old email address and submit"          
            editEmailLink.click()       
            emailInput.value oldEmailAddress
            saveButton.click()
        then:"The old email address is displayed"       
            at PunchoutProfileSettingsPage
            assert $("[data-testing-id='account-punchout-email-field']").text() == oldEmailAddress
        where:            
            oldEmailAddress << testData.get("punchout.user.email")         
    }

    /** 
	*
	* Change the password of an OCI user
	* and check if it is working by connecting using the new password
	*
	*/
    def "Change the password of the OCI punchout user"()
    {
		def newPassword = System.currentTimeMillis() + "Ab"
		println(newPassword)
        
        //a user already exists when using ACCOUNT_ADMIN_PUNCHOUT
        given: "I have logged in and I am on My Account Home Page"
           logInUser(DemoUser.ACCOUNT_ADMIN_PUNCHOUT.user);
         
        when:
            at AccountHomePage
        and: "I click the punchout link in the menu"
            clickPunchout()
		and:"I click on the profile settings icon"		  
			profileSettingsLink.click()				
		and:"I click on the icon to edit the email"		
			editPasswordLink.click()		
		and:"I provide all data and submit"
			passwordInput.value newPassword
			passwordConfirmationInput.value newPassword
			yourPasswordInput.value "!InterShop00!"
			saveButton.click()
		then:"The new email password is saved"			
			at PunchoutProfileSettingsPage
			//there is nothing to test on that page
		when:"I connect as OCI user with new email"	
            loginOCIUser("ociuser@test.intershop.de", newPassword, hookUrl)        
        then: "The OCI user is on the homepage"
            at HomePage
		                 
        where:            
            hookUrl << testData.get("punchout.user.hookUrl")	
    }
        
    /**
     * password should be reverted to default
     * @return
     */
    def cleanupSpec() {
        
        //revert the password using same functions
        when: "I set the password back to default"
            logInUser(DemoUser.ACCOUNT_ADMIN_PUNCHOUT.user);
            
            clickPunchout()
            
            profileSettingsLink.click()
            editPasswordLink.click()
            passwordInput.value "!InterShop00!"
            passwordConfirmationInput.value "!InterShop00!"
            yourPasswordInput.value "!InterShop00!"
            saveButton.click()
        then:"The old password is restored"
            at PunchoutProfileSettingsPage
    }                    
}