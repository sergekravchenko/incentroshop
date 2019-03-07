package geb.com.intershop.b2x.pages.storefront.responsive.order

import geb.Module
import geb.Page
import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage;

class QuickOrderPage extends StorefrontPage
{
    static at=
    {
        waitFor{$("div[data-testing-id='page-quickorder']").size()>0}
    }
    
    static content=
    {
        addToQuoteButton    (required: false) { $('button[name="addToQuote"]') }
        addToQuoteCSVButton (required: false) { $('button[name="addToQuoteCSV"]') }
    }
}