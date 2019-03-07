package geb.com.intershop.b2x.pages.storefront.responsive.account.punchout

import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage

class PunchoutPage extends AccountPage
{
    String pageId() {"account-punchout-page"}

    static content=
    {
        //might not be visible when a user already exists
        loginInput {$("input" ,id:"Login")}
        passwordInput {$("input",id:"Password")}
        passwordConfirmInput {$("input",id:"PasswordConfirmation")}
        createUserButton {contentSlot.$("button",name:"enable")}
        configurationLink (to: PunchoutConfigurationPage) {$('[data-testing-class="link-oci-punchout-configuration"]')}
		//use the first icon
		profileSettingsLink(to: PunchoutProfileSettingsPage) {$('[data-testing-class="link-oci-punchout-profile-settings-1"]')}
                       
    }
    
    //------------------------------------------------------------
    // link functions
    //------------------------------------------------------------
    def setUserData(login, password)
    {
        loginInput.value login
        passwordInput.value password
        passwordConfirmInput.value password
    }
    
    //------------------------------------------------------------
    // Page checks
    //------------------------------------------------------------

    def isUserListDisplayed(bool)
    {
        bool == ($("div[data-testing-id='account-oci-user-list']").size()==1)
    }
    
    def isUserDisplayed(login)
    {
        //only one user must exist        
        login == (($("div[data-testing-id='account-oci-user-1']"))[0].text());
    } 
}
