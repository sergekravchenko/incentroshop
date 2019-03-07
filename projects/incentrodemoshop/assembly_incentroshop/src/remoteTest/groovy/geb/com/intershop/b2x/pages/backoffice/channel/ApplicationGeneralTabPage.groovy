package geb.com.intershop.b2x.pages.backoffice.channel

import geb.Page
import geb.com.intershop.b2x.pages.backoffice.modules.ChannelSiteNavigationBarModule;

class ApplicationGeneralTabPage extends Page
{
    static at =
    {
        waitFor {contentSlot.size() > 0}
    }
    
    static content = 
    {
        siteNavBar {module ChannelSiteNavigationBarModule}
        
        contentSlot                 {$('form[data-testing-id="bo-application-general-page"]')}
        btnManageLocales            {contentSlot.$("input", name: "ManageLocales")}
        linkAddAllLanguages         {$("div", id: "LocalesMgmtDialog").$("a", text: contains('Add all'))}
        btnSelectLanguagesPopupOK   {$("div", id: "LocalesMgmtDialog").$("input", id: "LocalesMgmtDialog-ok")}
        btnApply                    {contentSlot.$("input", name: "Update")}
    }
    
    /**
     * Adds all available languages to the application.
     */
    def addAllAvailableLanguages()
    {
        btnManageLocales.click()
        linkAddAllLanguages.click()
        btnSelectLanguagesPopupOK.click()
        btnApply.click()
    }
}
