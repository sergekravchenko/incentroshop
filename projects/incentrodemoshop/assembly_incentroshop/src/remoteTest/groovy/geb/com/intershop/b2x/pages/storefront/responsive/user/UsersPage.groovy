package geb.com.intershop.b2x.pages.storefront.responsive.user

import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage
import geb.com.intershop.b2x.pages.storefront.responsive.user.modules.UserRow

class UsersPage extends AccountPage {
    String pageId() {"users-page"}

    static content = {
        addUserButton (toWait: true, to: CreateNewUserPage){contentSlot.$("a", href:endsWith("ViewUser-New")).first()}
        usersContainer {contentSlot.$("div", "data-testing-id":"users-list")}
        usersList { usersContainer.$("div", class:"list-item-row")*.module(UserRow).collectEntries{[it.userID, it]}}
    }

    CreateNewUserPage clickAddUser() {
        addUserButton.click()
        browser.page
    }

    int getUsersCount() {
        usersList.size()
    }

    UsersPage verifyPresent(User user) {
        List users = usersList.values().collect {it.personName}
        assert users.contains(user.getName())
        browser.page
    }

    UsersPage verifyNotPresent(User user) {
        List users = usersList.values().collect {it.personName}
        assert !users.contains(user.getName())
        browser.page
    }

    UserDetailsPage clickViewUser(User user) {
        UserRow userRow = findUser(user)
        userRow.clickViewUser()
        browser.page
    }

    RemoveUserDialogPage clickDeleteUser(User user) {
        UserRow userRow = findUser(user)
        userRow.clickDeleteUser()
        browser.page
    }

    private UserRow findUser(User user) {
        List filteredUsers = usersList.values().findAll {it.personName == user.getName()}
        assert !filteredUsers.empty
        filteredUsers.get(0)
    }
}
