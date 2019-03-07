package geb.com.intershop.b2x.pages.backoffice.channel

import geb.Page
import geb.com.intershop.b2x.pages.backoffice.modules.ChannelSiteNavigationBarModule;

class ChannelPreferencesPage extends Page
{
    static at =
    {
        waitFor {contentSlot.size() > 0}
    }
    
    static content = 
    {
        siteNavBar {module ChannelSiteNavigationBarModule}
        
        contentSlot         {$("table[data-testing-id='bo-channel-preferences-overview']")}
        linkSEOSettings (to: PreferencesSEOSettingsPage) {$("a[data-testing-class='link-preferences-seo-channel']")}
    }
}
