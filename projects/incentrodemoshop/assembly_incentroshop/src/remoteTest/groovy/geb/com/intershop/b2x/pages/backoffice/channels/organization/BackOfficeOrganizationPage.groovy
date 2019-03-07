package geb.com.intershop.b2x.pages.backoffice.channels.organization

import geb.Page
import geb.com.intershop.b2x.pages.backoffice.BackOfficePage
import geb.navigator.Navigator

class BackOfficeOrganizationPage extends BackOfficePage
{
    static at=
    {
        $("body","data-testing-id":"page-bo-base")
    }

    static content = {
        siteHeader     { $('#main_header') }
    }
}