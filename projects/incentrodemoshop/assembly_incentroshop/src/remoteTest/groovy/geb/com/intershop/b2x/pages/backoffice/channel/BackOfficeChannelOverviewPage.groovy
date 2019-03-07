package geb.com.intershop.b2x.pages.backoffice.channel

import geb.com.intershop.b2x.pages.backoffice.BackOfficePage
import geb.com.intershop.b2x.pages.backoffice.modules.ChannelSiteNavigationBarModule

class BackOfficeChannelOverviewPage extends BackOfficePage
{
    static at =
    {
        waitFor(120) { $("div", "data-testing-id": "bo-channel-overview") }
    }
    
    static content = {
        //siteHeader { $('#main_header') }
        siteNavBar {module ChannelSiteNavigationBarModule}
    }

    def goToPreferences()
    {
        siteNavBar.linkPreferences.click()
    }
    
    def goToCatalogs()
    {
        siteNavBar.linkCatalogs.click()
    }
    
    def goToApplications()
    {
        siteNavBar.linkApplications.click()
    }
}