package geb.com.intershop.b2x.pages.backoffice

import geb.Page
import geb.module.RadioButtons
import geb.navigator.Navigator

class BackOfficePage extends Page
{
    static at=
    {
        $("body", "data-testing-id": "page-bo-base")
    }

    static content = {
        siteHeader(required: true)      { $('#main_header') }
        //siteNavigation { $('#main_navigation') }
        //siteContent(required: true)     { $('#main_content') }
        //siteFooter     { $('#main_footer') }
        channelSelector(required: true) { $("button", class: "channel-select-application") }
        channel { $('input', 'name': 'dropdown_channel-select-application').module(RadioButtons) }
        logoutButton(required: true, to: BackOfficeLoginPage) { $("li",class:"logout").find("a") }
        homeButton(required: true, to: BackOfficePage) { $('a', 'id': 'brand_title') }
//        preferences(required: true, to: BackOfficePreferencesPage ) { $('a', 'data-testing-id': 'bo-sitenavbar-preferences') }
    }
    
    BackOfficeLoginPage logoutUser() {
        logoutButton.click()
        browser.page
    }
    
    void selectChannel(String channelName) {
        // open the Channel Select Menu
        channelSelector.click()
        // goto the channel name you wish to change to
        interact {
            moveToElement( $('input', 'title': channelName).closest("li") )
            click()
        }
    }
    
	/**
     * Helper method to select a menu item from the top main navigation bar in the backoffice
	 * ASSUMPTION: hyperlinks in menu and menu item has "data-testing-id" HTML attribute set
     */
	void navigateToMainMenuItem(String menuId, String menuItemId) {
        interact
        {
            moveToElement( $("a", "data-testing-id": menuId))
            moveToElement( $("a", "data-testing-id": menuItemId))
            click()
        }
	}
}