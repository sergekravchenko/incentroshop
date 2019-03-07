package geb.com.intershop.b2x.pages.storefront.responsive.approval

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage
import geb.com.intershop.b2x.pages.storefront.responsive.order.modules.OrderInfo
import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.com.intershop.b2x.model.storefront.responsive.Order

class ApprovalDetailsPage extends AccountPage
{
    String pageId() {"approval-details-page"}

    static content =
    {
      orderInfo {contentSlot.module(OrderInfo)}
	  orderApprovalActionsForm(required: false) {contentSlot.$("form", name: "approveOrderForm").first()}
      approveButton (required: false, toWait: true, to: RequisitionApprovedPage){orderApprovalActionsForm.$("button", name:"approve")}
      rejectButton (required: false, toWait: true, to: RejectOrderDialogPage){orderApprovalActionsForm.$("button", name:"reject")}
	  alertMessage(required: false) {contentSlot.$("div", class: "alert")}
    }

    RejectOrderDialogPage clickReject()
    {
		rejectButton.click()
        browser.page
    }

    RequisitionApprovedPage clickApprove()
    {
		approveButton.click()
        browser.page
    }

    boolean isForApproval()
    {
        contentSlot.$("p", class: "label-info").size() > 0 && approveButton.size() > 0
    }

    boolean isApproved()
    {
        contentSlot.$("p", class: "label-success").size() > 0
    }

    boolean isRejected()
    {
        contentSlot.$("p", class: "label-danger").size() > 0
    }

	Order getOrder() {
        orderInfo.getOrder()
    }

    ApprovalDetailsPage verifyCostCenterPresent(CostCenter costCenter)
    {
        orderInfo.verifyCostCenterPresent(costCenter)
        browser.page
    }
}