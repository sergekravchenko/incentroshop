package geb.com.intershop.b2x.pages.storefront.responsive.account.punchout

import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage

class PunchoutProfileEmailEditPage extends AccountPage
{
    String pageId() {"account-punchout-profile-settings-email-page"}

    static content=
    {
		emailInput { contentSlot. $("input[data-testing-id='input-punchout-email']") }

		saveButton { $('[data-testing-id="button-oci-punchout-email-submit"]') }	
    }

}
