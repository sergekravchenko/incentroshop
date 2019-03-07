package geb.com.intershop.b2x.pages.storefront.responsive.user

import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage

class UserRolesPage extends AccountPage
{
    String pageId() {"user-roles-page"}
    
    static content =
    {
        accountAdminCheckbox {$("input[id='APP_B2B_ACCOUNT_OWNER']", type:"checkbox")}
        saveButton (to:UserDetailsPage){contentSlot.$("button[name='Update']", type:"submit")}
        costCenterManagerCheckbox {$("input[id='APP_B2B_COSTCENTER_OWNER']", type:"checkbox")}
        approverCheckbox {$("input[id='APP_B2B_APPROVER']", type:"checkbox")}
    }
    
    UserDetailsPage clickSaveButton()
    {
        saveButton.click()
        browser.page
    }
    
    boolean isAccountAdminCheckboxDisabled()
    {
        accountAdminCheckbox.@disabled == 'true'
    }
    
    boolean isAccountAdminCheckboxSelected()
    {
        accountAdminCheckbox.@checked == 'true'
    }
    
    boolean isCostCenterManagerCheckboxSelected()
    {
        costCenterManagerCheckbox.@checked == 'true'
    }
    
    void checkCostCenterManagerCheckbox()
    {
        if (!isCostCenterManagerCheckboxSelected())
        {
            costCenterManagerCheckbox.click()
        }
    }
    
    void uncheckCostCenterManagerCheckbox()
    {
        if (isCostCenterManagerCheckboxSelected())
        {
            costCenterManagerCheckbox.click()
        }
    }
    
    boolean isApproverCheckboxSelected()
    {
        approverCheckbox.@checked == 'true'
    }
        
    void checkApproverCheckbox()
    {
        if (!isApproverCheckboxSelected())
        {
            approverCheckbox.click()
        }
        
    }
    
    void uncheckApproverCheckbox()
    {
        if (isApproverCheckboxSelected())
        {
            approverCheckbox.click()
        }
        
    }
}
