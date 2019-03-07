package geb.com.intershop.b2x.pages.backoffice.channel

import geb.com.intershop.b2x.pages.backoffice.BOLoginPage

class ChannelMgmtCatalogsPage extends BOLoginPage {
	
    static content = {
         mainContainer { $('#main_wrapper') }
         contentSlot { mainContainer.$("div", "data-testing-id": "Catalog_Overview") }
	}
    
    static at = {
        waitFor { contentSlot.size() > 0 }
    }
    
    def gotoPriceLists()
    {
        $("a", text:contains("Price Lists")).click()
    }
}


