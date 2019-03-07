package geb.com.intershop.b2x.pages.backoffice.channel

import geb.com.intershop.b2x.pages.backoffice.BOLoginPage

class ChannelEditPriceListPage extends BOLoginPage {
	    
    static content= {
        updatePriceListForm  { $("form", name:"formMask") }

        pricesTabLink      { $('a', class: "table_tabs_dis", text:"Prices") }
		
		applyButton { $("input", name:"update")}
    }
    
	static at = {
         waitFor(20) { 
			$("td", class: "table_title_description", 
			        text: contains("Click \"Apply\" to save the details. Click \"Reset\" to discard changes. \"Delete\" deletes this price list.")
			)
		}
	}
    
    def gotoPricesTab()
    {        
        pricesTabLink.click();
        
    }   
	
	//disable and set valid from/to date to past
	def disablePriceList()
    {        		
        updatePriceListForm.$('input', name: 'UpdatePriceListForm_StartDate').value       '01/01/2001'
        updatePriceListForm.$('input', name: 'UpdatePriceListForm_EndDate').value         '01/01/2002'
        
		updatePriceListForm.$('input', name: 'UpdatePriceListForm_Enabled').click()
				
        applyButton.click()
    }
	
}
