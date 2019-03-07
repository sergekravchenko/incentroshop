package geb.com.intershop.b2x.pages.storefront.responsive.account.punchout

import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage

class PunchoutProfilePasswordEditPage extends AccountPage
{
    String pageId() {"account-punchout-profile-settings-password-page"}

    static content=
    {
        passwordInput { contentSlot.$("input", id:"OCIUpdatePasswordForm_NewPassword") }
        passwordConfirmationInput { contentSlot.$("input", id:"OCIUpdatePasswordForm_NewPasswordConfirmation") }
        yourPasswordInput { contentSlot. $("input[data-testing-id='input-punchout-your-password']") }

        saveButton { $('[data-testing-id="button-oci-punchout-password-submit"]') }
    }
}
