package geb.com.intershop.b2x.pages.storefront.responsive.order.modules

import geb.Module
import geb.Page
import geb.com.intershop.b2x.model.storefront.responsive.Order
import geb.com.intershop.b2x.pages.storefront.responsive.order.OrderDetailsPage
import geb.navigator.Navigator
import java.util.List

import java.time.LocalDate
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoDateFormatter

class OrderRow extends Module {
    static final ORDER_ID_TAG_NAME = "order-id"
    static final ORDER_DATE_TAG_NAME = "order-date"
    static final ORDER_TOTAL_TAG_NAME = "order-total"
    
    int orderNumberCellIndex
    int orderDateCellIndex
    int totalPriceCellIndex

	Class<? extends Page> detailsPage = OrderDetailsPage

    static content = {
        cell {$("td", it)}
        orderDetailsLink(to: detailsPage) {cell(orderNumberCellIndex).$("a").first()}
        orderNumber {orderDetailsLink.text().trim()}
        orderDateText {cell(orderDateCellIndex).text().trim()}
        orderDate {LocalDate.parse(orderDateText, DemoDateFormatter.SHORT_DATE.format)}
        totalPrice {cell(totalPriceCellIndex).text().trim().replaceAll(',','').find(/\d+(\.\d+)?/).toBigDecimal()}
    }

    def calculateIndexes(header)
    {
        this.orderNumberCellIndex = header.find("th").findIndexOf({headerColumn -> headerColumn.attr("data-column-id").equals(OrderRow.ORDER_ID_TAG_NAME)})
        this.orderDateCellIndex = header.find("th").findIndexOf({headerColumn -> headerColumn.attr("data-column-id").equals(OrderRow.ORDER_DATE_TAG_NAME)})
        this.totalPriceCellIndex = header.find("th").findIndexOf({headerColumn -> headerColumn.attr("data-column-id").equals(OrderRow.ORDER_TOTAL_TAG_NAME)})
        
       return this
    }

    Order getOrder() {
        new Order.OrderBuilder(orderNumber)
                 .total(totalPrice)
                 .orderDate(orderDate)
                 .build()
    }
}