package geb.com.intershop.b2x.specs.storefront.responsive.user

import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoUser
import geb.com.intershop.b2x.pages.storefront.responsive.user.UserDetailsPage
import geb.com.intershop.b2x.pages.storefront.responsive.user.UsersPage
import geb.com.intershop.b2x.specs.storefront.responsive.features.Authentication
import geb.com.intershop.b2x.testdata.TestDataUsage
import geb.spock.GebReportingSpec
import spock.lang.Ignore
import spock.lang.Shared

@Ignore("This spec is not maintainable. Please rewrite")
class UserManagementSpec extends GebReportingSpec implements Authentication, TestDataUsage {


    /**
     * The new user to be created
     */
    @Shared User testUser;

    def setupSpec() {
        setup:
        testUser = new User("test@test.intershop.de", "test@test.intershop.de", "de_DE", "TestFirstName", "TestLastName")
        // Login as Account Admin user and go to Users page
        logInUser(DemoUser.ACCOUNT_ADMIN.user)
    }

    def "Create new B2B user"() {
        given: "I am on Users Page and new user is not present in the users list"
          clickUsers()
          verifyNotPresent(newUser)
        when: "I enter new user data and store the user"
          addUserButton.click()
          setUser(newUser)
          createUser()
          def currentURL = getBrowser().getDriver().currentUrl
        then: "New User is available on Users list"
          at UsersPage
          verifyPresent(newUser)
        and: "Logged User language is not changed to newly created user language"
          loggedUser.language != newUser.language
          currentURL.contains(loggedUser.language)
        where:
           newUser = testUser
           loggedUser = DemoUser.ACCOUNT_ADMIN.user
    }

    def "Edit B2B User Profile"() {
        given: "I am on Users Page and user is present in the users list"
          at UsersPage
          verifyPresent(editedUser)
        when: "I go to edit user profile"
          clickViewUser(editedUser)
          clickEditProfile()
        and: "Fill the user data and store the user"
          setUserData(editedUser)
          clickSave()
          def currentURL = getBrowser().getDriver().currentUrl
        then: "The user details page is shown"
          at UserDetailsPage
          // "Logged User language is not changed to edited user language"
          loggedUser.language != editedUser.language
          currentURL.contains(loggedUser.language)
        where:
          editedUser = testUser
          loggedUser = DemoUser.ACCOUNT_ADMIN.user
    }

	def "Delete B2B user"() {
        given: "I am on Users Page and user is present in the users list"
          clickUsers()
          verifyPresent(userToDelete)
        when: "I delete the user"
          clickDeleteUser(userToDelete)
          clickConfirm()
          clickOK()
        then: "It is not available on the Users page"
          at UsersPage
          verifyNotPresent(userToDelete)
        where:
          userToDelete = testUser
    }
}
