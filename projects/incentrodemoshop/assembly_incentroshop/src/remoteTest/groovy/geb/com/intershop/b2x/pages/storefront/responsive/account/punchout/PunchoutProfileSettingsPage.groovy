package geb.com.intershop.b2x.pages.storefront.responsive.account.punchout

import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage

class PunchoutProfileSettingsPage extends AccountPage
{
    String pageId() {"account-punchout-profile-settings-page"}

    static content=
    {
		editEmailLink(to: PunchoutProfileEmailEditPage) {$('[data-testing-id="link-oci-punchout-edit-email"]')}
		editPasswordLink(to: PunchoutProfilePasswordEditPage) {$('[data-testing-id="link-oci-punchout-edit-password"]')}
    }
}
