package geb.com.intershop.b2x.pages.storefront.responsive.account.punchout

import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage

class PunchoutConfigurationPage extends AccountPage
{
    String pageId() {"page-oci-punchout-configuration"}

    static content=
    {
        configuration { $('[data-testing-class="row-oci-punchout-configuration"]') }
        configurationItems {
            moduleList PunchoutConfigurationRow, configuration
        }
        saveButton { $('[data-testing-class="button-oci-punchout-configuration-submit"]') }
        cancelButton(to: PunchoutPage) { $('[data-testing-class="button-oci-punchout-configuration-cancel"]') }
        saveSuccessMessage { $('[data-testing-id="message-punchout-save-success"]') }
    }
}
