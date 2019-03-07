package geb.com.intershop.b2x.pages.backoffice.channel

import geb.com.intershop.b2x.pages.backoffice.BOLoginPage

class ChannelPriceListsPage extends BOLoginPage {
	
    static content =
    {
        newLink { $("input", name:"new")}
    }
    
	static at = {
         waitFor(20) { 
			$("td", class: "table_title_description", 
			        text: contains("The list shows all standard price lists of the channel.")
			)
		}
	}
    
    def clickNewPriceList()
    {
        newLink.click()
    }
	
	def clickPriceList(priceListName){	
		$("a", text:priceListName).first().click() 	
	}
	
}


