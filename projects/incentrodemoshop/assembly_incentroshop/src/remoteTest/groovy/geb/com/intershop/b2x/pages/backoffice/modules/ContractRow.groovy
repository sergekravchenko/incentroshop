package geb.com.intershop.b2x.pages.backoffice.modules

import geb.Module
import geb.navigator.Navigator

class ContractRow extends Module
{
    static content = {
        entry       { i -> $('td', i) }
        
        contractDetails { entry(0).$('a') }
        name            { contractDetails.text() }
        id              { entry(1).$('a').text() }
        customer        { entry(2).text() }
        startDate       { entry(4).text() }
        endDate         { entry(5).text() }
        targetRevenue   { entry(6).text() }
        progress        { entry(7).$('.text').text() }
        exeedance       { entry(8).text() }
        
        
    }
}