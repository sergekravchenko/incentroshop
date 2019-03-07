package geb.com.intershop.b2x.specs.storefront.responsive.features

import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.HomePage
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountHomePage
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountLoginPage

/**
 * Trait used to login in B2B storefront.
 * Will be used by the Spock specs.
 */
trait Authentication
{

    AccountHomePage logInUser(User user) {
        when: "I enter the home page URL and click on signin link"
            to HomePage
            if (isSignedIn(true)) {
                logOutUser()
            }
            pressLogIn()
        then: "I am on the Account Login Page"
            at AccountLoginPage
        when: "I Submit the login form with valid e-mail and password"
            login(user)
        then: "The account home page is displayed and provided user is logged"
            at AccountHomePage
            verifyLoggedUser(user)
            browser.page
    }

    void logOutUser() {
        when: "I enter the home page URL"
            to HomePage
        and: "Click the logout link"
            clickLogout()
        then: "The home page is displayed and signin link is available"
            at HomePage
            isSignedIn false
    }
}
