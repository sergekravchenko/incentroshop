package geb.com.intershop.b2x.pages.backoffice

import geb.Page
import org.openqa.selenium.StaleElementReferenceException;


class BOLoginPage extends Page
{
    static url = "/INTERSHOP/web/WFS/SLDSystem"
    static at=
    {
        waitFor{$("div",id:"login").size()>0}
    }

	static content = {
		siteHeader     { $('#main_header') }
		siteNavigation { $('#main_navigation') }
		siteContent    { $('#main_content') }
		siteFooter     { $('#main_footer') }
	}
	
    def sleepForNSeconds(int n)
    {
        def originalMilliseconds = System.currentTimeMillis()
        waitFor(n + 1)
        {
            (System.currentTimeMillis() - originalMilliseconds) > (n * 1000)
        }
    }

    def login(name,password,org)
    {
        $("input",id:"LoginForm_Login").value name
        $("input",id:"LoginForm_Password").value password
        $("input",id:"LoginForm_RegistrationDomain").value org
        $("input",class:"loginbutton").click()

    }
    
    def logout()
    {
        $("li",class:"logout").find("a").click()
    }

	def selectChannel(channel) {
		def chSelectForm = siteHeader.$('form', name: 'ChannelSelectForm')
		
		js.exec 'jQuery("#channel-select-application").css("display", "block");'
		
		chSelectForm.ApplicationID = channel
		js.exec "jQuery('form[name=ChannelSelectForm]').submit();"
	}
	
    def uncheckB2CCAPTCHAs()
    {
        when:
        login "admin","!InterShop00!","inSPIRED"

        and:
        toB2CSalesChannel()

        then: "I wait for the correct side..."
        waitFor{$("a",href:contains("Preference")).size()>0}

        when: "I click the Preference link..."
        $("a",href:contains("Preference")).click()


        then: "...and wait for CAPTCHAs."
        waitFor{$("a",text:contains("CAPTCHAs")).size()>0}

        when: "I click at the CAPTCHAs-Link..."
        $("a",text:contains("CAPTCHAs")).click()

        then: "...wait for the CAPTCHAs input..."
        waitFor{$("input",name:"CaptchaSettingsForm_intershop.CaptchaPreferences.Register").size()>0}

        when: "...and uncheck it."
        if($("input",name:"CaptchaSettingsForm_intershop.CaptchaPreferences.Register",checked:"checked").size()>0){
            $("input",name:"CaptchaSettingsForm_intershop.CaptchaPreferences.Register",checked:"checked").click()
            $("input",name:"updateSettings",value:"Apply").click()

        }

        then: "Done... Back to Test."
        waitFor{$("input",name:"CaptchaSettingsForm_intershop.CaptchaPreferences.Register").size()>0}
        
        logout()

    }

    def uncheckSMBCAPTCHAs()
    {
        when:
        login "admin","!InterShop00!","inSPIRED"

        and:
        toSMBSalesChannel()

        then: "I wait for the correct side..."
        waitFor{$("a",href:contains("Preference")).size()>0}

        when: "I click the Preference link..."

        $("a",href:contains("Preference")).click()


        then: "...and wait for CAPTCHAs."
        waitFor{$("a",text:contains("CAPTCHAs")).size()>0}


        when: "I click at the CAPTCHAs-Link..."
        $("a",text:contains("CAPTCHAs")).click()

        then: "...wait for the CAPTCHAs input..."
        waitFor{$("input",name:"CaptchaSettingsForm_intershop.CaptchaPreferences.Register").size()>0}

        when: "...and uncheck it."
        if($("input",name:"CaptchaSettingsForm_intershop.CaptchaPreferences.Register",checked:"checked").size()>0){
            $("input",name:"CaptchaSettingsForm_intershop.CaptchaPreferences.Register",checked:"checked").click()
            $("input",name:"updateSettings",value:"Apply").click()

        }

        then: "Done... Back to Test."
        waitFor{$("input",name:"CaptchaSettingsForm_intershop.CaptchaPreferences.Register").size()>0}

    }

    def navigateToB2CCustomers(){
        when:
        login "admin","!InterShop00!","inSPIRED"

        and:
        toB2CSalesChannel()

        when: "I wait for the Customerlink..."
        waitFor{$("a",text:contains("Customers")).size()>0}

        then: "...click it..."
        $("a",text:contains("Customers")).click()

        when: "...search for the Subtitle..."
        waitFor{$("a",text:contains("Customers"),class:"overview_subtitle").size()>0}

        then: "...and click it."
        $("a",text:contains("Customers"),class:"overview_subtitle").click()

    }

    def navigateToSMBCustomers(){
        when:
        login "admin","!InterShop00!","inSPIRED"
        
        and:
        toSMBSalesChannel()

        when: "I wait for the Customerlink..."
        waitFor{$("a",text:contains("Customers")).size()>0}

        then: "...click it..."
        $("a",text:contains("Customers")).click()

        when: "...search for the Subtitle..."
        waitFor{$("a",text:contains("Customers")).size()>0}

        then: "...and click it."
        $("a",text:contains("Customers"),class:"overview_subtitle").click()

    }

    def deleteCustomer(name){
        when: "search customer with name " + name
        $("input",id:"CustomerSearch_NameOrID").value name
        $("input",value:"Find",class:"button").click()

        then: "result list contains customer name"
        waitFor{$("a",text:iContains(name)).size()>0}

        when: "click on customer "
        $("a",text:iContains(name)).click()

        then: "at customer detail page with delete button"
        sleepForNSeconds(2)
        waitFor{$("input",value:"Delete",class:"button").size()>0}

        when: "click on customer delete"
        $("input",value:"Delete",class:"button").click()

        then: "click confirmation OK to delete "
        waitFor{$("input",name:"delete",value:"OK",class:"button").size()>0}
        $("input",name:"delete",value:"OK",class:"button").click()
    }

    def selectNavigationItem(linkText) {

        def attempts = 0
        while (attempts < 2)
        {
            try
            {
                def link = siteNavigation.$('a', text:linkText)
                assert link.size() > 0
                link.click()
                return
            }
            catch(StaleElementReferenceException e)
            {
                // ignore
            }
            attempts++
        }
    }
    
    //select navigation item which may not be not visible 
    def selectSubNavigationItem(linkText, sublinktext) {
        
        def link = siteNavigation.$('a', text:linkText)
        assert link.size() > 0
        
        link.parent().find('ul').jquery.show()
        
        def sublink = link.parent().$('a', text:sublinktext)
        assert sublink.size() > 0
        
        sublink.click()
    }
    def hyperlinkByText(text)
    {
        $("a", text:contains(text)).click()
    }
}
