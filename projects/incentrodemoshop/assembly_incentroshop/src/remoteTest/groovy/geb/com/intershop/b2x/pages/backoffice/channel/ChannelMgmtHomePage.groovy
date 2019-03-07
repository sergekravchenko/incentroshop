package geb.com.intershop.b2x.pages.backoffice.channel

import geb.com.intershop.b2x.pages.backoffice.BOLoginPage


class ChannelMgmtHomePage extends BOLoginPage {
	
    static content =
    {
        
        catalogsLink { $("a", text:contains("Catalogs"), 1)}
    }
    
    static at = {
         waitFor(20) { 
			$("td", class: "table_title_description", 
			        text: contains("Use the modules below to manage all aspects of this channel. This includes the management of customers, products, catalogs, content, online marketing activities, received orders, and various channel preferences.")
			)
		}
	}
    
    def clickCatalogs() {
        
        catalogsLink.click()
    }
}

