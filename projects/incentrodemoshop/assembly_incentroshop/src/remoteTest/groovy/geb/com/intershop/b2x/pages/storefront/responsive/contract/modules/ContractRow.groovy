package geb.com.intershop.b2x.pages.storefront.responsive.contract.modules

import geb.Module
import geb.navigator.Navigator
import geb.com.intershop.b2x.pages.storefront.responsive.contract.AccountContractDetailsPage

class ContractRow extends Module
{
    static content =
    {
        cell {$("td", it)}
        detailsLink(to:AccountContractDetailsPage) { cell(0).$('a') } 
        name                                       { detailsLink.text() }
        id                                         { cell(1).text() }
        type                                       { cell(2).text() }
        period                                     { cell(3).text() }
        progress                                   { cell(4).$('.progress-display').text() }
    }
}