package geb.com.intershop.b2x.pages.backoffice.channel

import geb.Page
import geb.com.intershop.b2x.pages.backoffice.modules.ChannelSiteNavigationBarModule;

class ApplicationsListPage extends Page
{
    static at =
    {
        waitFor {contentSlot.size() > 0}
    }
    
    static content = 
    {
        siteNavBar {module ChannelSiteNavigationBarModule}
        
        contentSlot             {$('table[data-testing-id="bo-applicationslist-page"]')}
        linkShowAll (to: this) {$("input", type: "submit", name: "PageSize_-1")}
    }
    
    /**
     * Clicks the link of the application with the URL identifier {@code urlIdentifier}.
     * Browser will be sent to {@code ApplicationGeneralTabPage}.
     * 
     * @param urlIdentifier The URL identifier of the application.
     */
    def clickApplication(urlIdentifier)
    {
        $("table[data-testing-id='bo-applicationlist-table']")
            .$("td[data-testing-id='bo-applicationlist-urlidentifier-column']", text: urlIdentifier).previous().$("a")
            .click(ApplicationGeneralTabPage)
    }
}
