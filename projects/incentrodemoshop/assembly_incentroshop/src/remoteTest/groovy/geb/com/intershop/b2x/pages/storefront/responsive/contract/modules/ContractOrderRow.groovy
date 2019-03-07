package geb.com.intershop.b2x.pages.storefront.responsive.contract.modules

import geb.Module
import geb.navigator.Navigator

class ContractOrderRow extends Module
{
    static content =
    {
        cell {$("td", it)}
        orderDate       { cell(0).text() } 
        orderDetails    { cell(1) }
        orderState      { cell(2).text() }
        contractRevenue { cell(3).text() }
        orderTotal      { cell(4).text() }
    }
}