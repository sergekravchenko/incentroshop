package geb.com.intershop.b2x.pages.storefront.responsive.approval

import geb.com.intershop.b2x.pages.storefront.responsive.order.modules.OrderRow

class ApprovedOrdersPage extends OrdersListPage
{
    Class<? extends OrderRow> orderRowModule = OrderRow
	String pageId() {"approved-orders-page"}
}