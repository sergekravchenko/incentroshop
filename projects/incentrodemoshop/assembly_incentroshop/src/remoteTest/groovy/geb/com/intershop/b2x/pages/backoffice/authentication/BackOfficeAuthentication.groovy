package geb.com.intershop.b2x.pages.backoffice.authentication

import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.backoffice.BackOfficeLoginPage
import geb.com.intershop.b2x.pages.backoffice.BackOfficePage

/**
 * Trait used to login in the BackOffice.
 * Will be used by the Geb specs.
 */
trait  BackOfficeAuthentication
{

    BackOfficePage logInUser(User user, String organization) {
        when: "I open the Back Office Login Page"
            to BackOfficeLoginPage
        and: "I submit the login form with valid credentials"
            login(user, organization)
        then: "The BackOffice Organization page is displayed and provided user is logged"
            at BackOfficePage
            //verifyLoggedUser(user)
            browser.page
    }

    void logOutUser() {
        when: "I enter the home page URL"
            to BackOfficePage
        and: "Click the logout link"
            logoutUser()
        then: "The home page is displayed and signin link is available"
            at BackOfficeLoginPage
            isSignedIn false
    }
}