package geb.com.intershop.b2x.pages.storefront.responsive.approval

class RequisitionApprovedPage extends ApprovalDetailsPage
{
    static at =
    {
        waitFor{alertMessage.size()>0 && isApproved()}
    }
}