package geb.com.intershop.b2x.pages.storefront.responsive.costcenter

import geb.Page
import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage

/**
 * This class encapsulates the common elements of modal dialogs used in Cost Center related pages
 */
abstract class CostCenterModalDialogPage extends StorefrontPage
{
   /**
    * The unique part of the form action(pipeline name and start node)
    */
    abstract String formAction()

   /**
    * the Page class that should be opened after the form is submitted or modal dialog is closed
    */
    Class<? extends Page> destination

    static at =
    {
        waitFor{modalDialog.size()>0 && modalDialog.displayed}
    }

    static content =
    {
        modalDialog { $("div", id: "general-costcenter-modal")}
        dialogTitle {$("h4", id: "modal-title-replacement").text()}
        webForm { modalDialog.$("form", action:endsWith(formAction()))}
        closeButton(to: destination) {modalDialog.$("button[class='close']")}
        submitButton(to: destination) {webForm.$("button", type: "submit").first()}
        cancelButton(to: destination) {webForm.$("button", name: "cancel")}
    }

    def submit()
    {
        submitButton.click()
        sleepForNSeconds(2) // If not added PhantomJSPCEnvironmentTest sometimes fails as the new content is still not available
    }

}
