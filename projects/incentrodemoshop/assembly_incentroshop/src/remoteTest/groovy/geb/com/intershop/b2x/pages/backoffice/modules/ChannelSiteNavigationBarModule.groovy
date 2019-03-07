package geb.com.intershop.b2x.pages.backoffice.modules

import geb.Module
import geb.com.intershop.b2x.pages.backoffice.channel.ApplicationsListPage;
import geb.com.intershop.b2x.pages.backoffice.channel.ChannelPreferencesPage;

/**
 * Provides links of the channel navigation bar of the back office.
 *
 */
class ChannelSiteNavigationBarModule extends Module
{
    static content = 
    {
        linkPreferences (to: ChannelPreferencesPage) {$("a[data-testing-id='bo-sitenavbar-preferences']")}
        linkApplications (to: ApplicationsListPage) {$("a[data-testing-id='bo-sitenavbar-applications']")}
    }
}
