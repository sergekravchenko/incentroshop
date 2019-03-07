package geb.com.intershop.b2x.pages.storefront.responsive.order.modules

import geb.Module
import geb.Page
import geb.com.intershop.b2x.model.storefront.responsive.Order
import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.navigator.Navigator

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OrderInfo extends Module {

    OrderInfo()
    {
       
    }

    static content = {
		summarySection {$("div[data-testing-id='order-summary-info']")}
        orderNumber {summarySection.$("dd[data-testing-id='order-number']").text().trim()}
        orderDateText {summarySection.$("dd[data-testing-id='order-date']").text().trim()}
        orderDate {LocalDate.parse(orderDateText, DateTimeFormatter.ofPattern("M/d/yy"))}
        orderStatus {summarySection.$("dd[data-testing-id='order-status']").text().trim()}
        costSummarySection {$("div" ,class: "cost-summary")}
        totalPrice {costSummarySection.$("dd", class: "total-price").text().trim().replaceAll(',','').find(/\d+(\.\d+)?/).toBigDecimal()}
        costCenterInfo(required: false) {$("span[data-testing-id='order-cost-center']")}
    }

    Order getOrder() {
        new Order.OrderBuilder(orderNumber)
                 .total(totalPrice)
                 .orderDate(orderDate)
                 .build()
    }

    CostCenter getCostCenter()
    {
        String costCenterText = costCenterInfo.text().trim()
        int spaceIndex = costCenterText.indexOf(" ")
        String id = costCenterText.substring(0, spaceIndex)
        String name = costCenterText.substring(spaceIndex + 1)
        new CostCenter(id, name)
    }

    void verifyCostCenterPresent(CostCenter costCenter)
    {
        assert costCenterInfo.size() > 0 && costCenterInfo.text().trim().equals(costCenter.id + " " + costCenter.name)
    }
}
