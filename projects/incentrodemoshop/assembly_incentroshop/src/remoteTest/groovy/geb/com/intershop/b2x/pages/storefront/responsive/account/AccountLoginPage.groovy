package geb.com.intershop.b2x.pages.storefront.responsive.account

import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage

class AccountLoginPage extends StorefrontPage
{
    static at =
    {
        waitFor{contentSlot.size()>0}
    }

    static content =
    {
        contentSlot   { $("div[data-testing-id='account-login-page']") }
        loginInput    { $('input', id:'ShopLoginForm_Login') }
        passwordInput { $('input', id:'ShopLoginForm_Password') }
        loginButton(to: [AccountHomePage, AccountLoginPage]) { $('button', name:'login') }
    }

    //------------------------------------------------------------
    // link functions
    //------------------------------------------------------------
    def login(user,password)
    {
        loginInput.value   user
        passwordInput.value   password

        loginButton.click()

    }

    StorefrontPage login(User user)
    {
        login(user.email, user.password)
        browser.page
    }

}
