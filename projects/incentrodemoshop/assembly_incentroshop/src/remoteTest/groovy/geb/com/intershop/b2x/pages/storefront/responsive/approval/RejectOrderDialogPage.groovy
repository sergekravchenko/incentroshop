package geb.com.intershop.b2x.pages.storefront.responsive.approval

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage

/**
 * This class represents the reject order modal dialog
 */
class RejectOrderDialogPage extends StorefrontPage
{

    static at =
    {
        waitFor{modalDialog.size()>0 && modalDialog.displayed}
    }

    static content =
    {
        modalDialog { $("div", class: "modal-dialog")}
        dialogTitle {$("h2", class: "modal-title").text()}
        closeButton(to: ApprovalDetailsPage) {modalDialog.$("button[class='close']")}
        webForm { modalDialog.$("form", name:"rejectOrderForm")}
        commentField {webForm.$("textarea", id:"ApprovalComment")}
        submitButton(toWait: true, to: RequisitionRejectedPage) {webForm.$("button", type: "submit", name: "reject")}
        cancelButton(to: ApprovalDetailsPage) {webForm.$("button", name: "cancel")}
    }

    RejectOrderDialogPage setComment(String comment)
    {
        commentField.value(comment)
        browser.page
    }

    RequisitionRejectedPage reject()
    {
        submitButton.click()
        browser.page
    }

    ApprovalDetailsPage cancel()
    {
        cancelButton.click()
        browser.page
    }
}
