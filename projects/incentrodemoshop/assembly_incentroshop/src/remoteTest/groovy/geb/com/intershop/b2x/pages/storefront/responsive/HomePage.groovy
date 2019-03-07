package geb.com.intershop.b2x.pages.storefront.responsive

import geb.Page
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountHomePage
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountLoginPage
import geb.com.intershop.b2x.pages.storefront.responsive.modules.ProductTile
import geb.com.intershop.b2x.pages.storefront.responsive.modules.ExpressShop

class HomePage extends StorefrontPage
{
    static url= StorefrontPage.url + "ViewHomepage-Start";
    
    static at =
    {
        //The homepage must get the class "homepage" in backoffice
        waitFor{$("body",class:"homepage").size()>0}
    }

    static content =
    {
        signInLink(required:false, to: AccountLoginPage){$("a",class:"my-account-links my-account-login")}
        catalogBar {$("ul",class:contains("navbar-nav"))}
        registerLink(required:false) {$("a",class:"ish-siteHeader-myAccountUtilitiesMenu-myAccount-register")}
        logoutLink(required:false, to: HomePage) {$("a",class:"my-account-link my-account-logout")}
        productTile {module ProductTile}
        expressShop {module ExpressShop}
    }

    //------------------------------------------------------------
    // Page checks
    //------------------------------------------------------------

    def isSignedIn(bool)
    {
        bool == (signInLink.size()==0)
    }

    //------------------------------------------------------------
    // link functions
    //------------------------------------------------------------
    AccountLoginPage pressLogIn()
    {
        signInLink.click()
        browser.page
    }

    AccountHomePage login(User user)
    {
        pressLogIn().login(user)
    }

    def clickLogout()
    {
       logoutLink.click();
    }

    def selectCatalog(channel)
    {
        $("a[data-testing-id='"+channel+"-link']").click()
    }
    
    def clickCategoryLink(categoryId) {
        $('[data-testing-id="'+categoryId+'-link"]').click()
    }

}


