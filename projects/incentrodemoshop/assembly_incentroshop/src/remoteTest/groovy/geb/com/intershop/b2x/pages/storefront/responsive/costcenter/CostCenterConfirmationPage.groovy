package geb.com.intershop.b2x.pages.storefront.responsive.costcenter

import geb.Page
import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage

abstract class CostCenterConfirmationPage extends StorefrontPage
{
    String action
    
    Class<? extends Page> destination
    
    static at = {waitFor {!contentSlot.isEmpty() && contentSlot.isDisplayed()}}
    
    static content =
    {
        contentSlot {$("div#general-costcenter-modal").has("div", "data-testing-id": "cost-center-confirm-${action}-dialog")}
        confirmationLink(to: destination) {contentSlot.$("a", "data-testing-id": "${action}-confirm-button")}
        cancelButton(to: destination) {contentSlot.$("button", "data-testing-id": "${action}-cancel-button")}
    }
    
    void confirm()
    {
        confirmationLink.click()
    }
    
    void cancel()
    {
        cancelButton.click()
    }
}
