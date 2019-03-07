package geb.com.intershop.b2x.specs.storefront.responsive.features

import geb.com.intershop.b2x.model.storefront.responsive.Order

/**
 * Trait used to approve/reject an orders/subscriptionds for in B2B front store.
 * Will be used by the Spock specs.
 */
trait OrderApproval
{

    /**
     * Approve the provided requisition/subscription.
     * ASSUMPTION: The currently logged user has permissions to approve orders for the buyer that created the provided requisition.
     * ASSUMPTION: The currently logged user is currently on a page within My Account section.
     * After executing the method the last visited poage will be ApprovalDetails page.
     * @param orderRequest - the requisition to be approved. In case it represents subscription recurrenceInformation field must be not null.
     */
    void approveOrder(Order orderRequest) {
        when: "Go to Orders to Approve page"
            clickOrdersToApprove()
        then: "Order is available in the list of orders for approval"
            verifyOrderPresent orderRequest
        when: "Go to Approval Details page"
            openOrderDetails orderRequest
        then: "Order require approval"
            isForApproval()
        when: "Approve the order request"
            clickApprove()
        then: "Requisition is approved and Approval Details page is opened"
            isApproved()
    }

    /**
     * Reject the provided requisition/subscription.
     * ASSUMPTION: The currently logged user has permissions to approve/reject orders for the buyer that created the provided requisition.
     * ASSUMPTION: The currently logged user is currently on a page within My Account section.
     * After executing the method the last visited poage will be ApprovalDetails page.
     * @param orderRequest - the requisition to be rejected. In case it represents subscription recurrenceInformation field must be not null.
     */
    void rejectOrder(Order orderRequest) {
        when: "Go to Orders to Approve page"
            clickOrdersToApprove()
        then: "Order is available in the list of orders for approval"
            verifyOrderPresent orderRequest
        when: "Go to Approval Details page"
            openOrderDetails orderRequest
        then: "Order require approval"
            isForApproval()
        when: "Reject the order request"
            clickReject()
            .setComment("Too expensive...")
            .reject()
        then: "Order request is rejected and Approval Details page is opened"
            isRejected()
    }
}
