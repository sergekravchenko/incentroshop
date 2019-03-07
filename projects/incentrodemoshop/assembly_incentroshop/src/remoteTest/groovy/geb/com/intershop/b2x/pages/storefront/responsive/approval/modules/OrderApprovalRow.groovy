package geb.com.intershop.b2x.pages.storefront.responsive.approval.modules

import geb.Page
import geb.com.intershop.b2x.pages.storefront.responsive.approval.ApprovalDetailsPage
import geb.com.intershop.b2x.pages.storefront.responsive.order.modules.OrderRow

class OrderApprovalRow extends OrderRow {
    Class<? extends Page> detailsPage = ApprovalDetailsPage
}
