package geb.com.intershop.b2x.specs.storefront.responsive.user

import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoUser
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountHomePage
import geb.com.intershop.b2x.pages.storefront.responsive.user.UserDetailsPage
import geb.com.intershop.b2x.pages.storefront.responsive.user.UserRolesPage
import geb.com.intershop.b2x.pages.storefront.responsive.user.UsersPage
import geb.com.intershop.b2x.specs.storefront.responsive.features.Authentication
import geb.spock.GebReportingSpec
import geb.spock.GebReportingSpec
import spock.lang.Ignore;

@Ignore
class EditUserRolesSpec extends GebReportingSpec implements Authentication
{

    def "Try to save own roles and check if they exist"()
    {
        given: "I have logged in and am on My Account Home Page as Account Admin"
          logInUser adminUser
        when:
          at AccountHomePage
          clickUsers()
        then: "The users page is displayed"
          at UsersPage
        when:
          clickViewUser adminUser
          clickEditRolesAndBudget()
        then: "I am on the User Roles page"
          isAccountAdminCheckboxDisabled()
          isAccountAdminCheckboxSelected()
        when:
          clickSaveButton()
        then: "The links of Navigation bar which need Account Admin privileges are present"
          !accountNavBar.costCentersLink.isEmpty()
          !accountNavBar.usersLink.isEmpty()
        where:
          adminUser = DemoUser.ACCOUNT_ADMIN.user
    }

    def "Check buyer permissions"()
    {
        given: "I have logged in as buyer"
            logInUser buyerUser
        when: "I am on My Account Home Page"
            at AccountHomePage
        then: "The approval section which need approver or cost center manager privileges are not present"
            myApprovalsSection.isEmpty()
        where:
            buyerUser = DemoUser.BUYER.getUser()
    }

    def "Check approver permissions"()
    {
        given: "I have logged in as approver"
            logInUser approverUser
        when: "I am on My Account Home Page"
            at AccountHomePage
        then: "The approval section which need approver privileges are present"
            !myApprovalsSection.isEmpty()
        where:
            approverUser = DemoUser.APPROVER_OIL_CORP.getUser()
    }

    def "Check cost center manager permissions"()
    {
        given: "I have logged in as cost center manager"
            logInUser costCenterManagerUser
        when: "I am on My Account Home Page"
            at AccountHomePage
        then: "The approval section which need cost center privileges are present"
            !myApprovalsSection.isEmpty()
        when:
            logOutUser()
            logInUser adminUser
        then: "I have logged in and I am on My Account Home Page as Account Admin"
            at AccountHomePage
        when:
            clickUsers()
        then: "The users page is displayed"
            at UsersPage
        when:
            clickViewUser costCenterManagerUser
            clickEditRolesAndBudget()
        then: "I am on the User Roles page"
            at UserRolesPage
        when:
            uncheckCostCenterManagerCheckbox()
        then: "The Cost Center Manager Role is not selected"
            at UserRolesPage
        when:
            clickSaveButton()
        then: "The Cost Center Manager Role is not assigned and user has only Buyer role"
            at UserDetailsPage
        when:
            logOutUser()
            logInUser costCenterManagerUser
        then: "Go to My Account Home Page as Buyer"
            myApprovalsSection.isEmpty()
        when:
            logOutUser()
            logInUser adminUser
        then: "Logged in as Account Admin"
            at AccountHomePage
        when:
            clickUsers()
        then: "The users page is displayed"
            at UsersPage
        when:
            clickViewUser costCenterManagerUser
            clickEditRolesAndBudget()
        then: "I am on the User Roles page"
            at UserRolesPage
        when:
            checkCostCenterManagerCheckbox()
        then: "The Cost Center Manager Role is selected"
            at UserRolesPage
        when:
            clickSaveButton()
        then: "The Cost Center Manager Role is not assigned and user has only Buyer role"
            at UserDetailsPage
        where:
            adminUser = DemoUser.ACCOUNT_ADMIN_OIL_CORP.getUser()
            costCenterManagerUser = DemoUser.COST_CENTER_MANAGER_OIL_CORP.getUser()
    }
        
}
