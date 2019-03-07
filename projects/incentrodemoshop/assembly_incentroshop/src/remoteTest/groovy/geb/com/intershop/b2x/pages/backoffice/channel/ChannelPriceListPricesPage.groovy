package geb.com.intershop.b2x.pages.backoffice.channel

import geb.com.intershop.b2x.pages.backoffice.BOLoginPage

class ChannelPriceListPricesPage extends BOLoginPage {
	    
    static content= {

        searchForm   { $("form", name:"priceListPricesForm") }

        btnFind      { $('input', name: 'findSimple') }
		
		generalTabLink { $('a', class: "table_tabs_dis", text:"General") }
    }
    
	static at = {
         waitFor(20) { 
			$("td", class: "table_title_description", 
			        text: contains("Click the \"Add Price\" link to create a new price or the \"Delete\" link to delete a price. Click on the price to edit it.")
			)
		}
	}
    
    def addProduct(productSKU, productPrice){
        
        searchForm.$('input', id: 'WFSimpleSearch_NameOrID').value  productSKU
        searchForm.SearchType = "all"
                
        btnFind.click();
        
        waitFor(10){
            //what if more than one product/link found?
            $("a", text: 'Add Price')
        }
        searchForm.$("a", text: 'Add Price').click()
        
        waitFor(10){
            $("form", name:"formMask")
        }   
        
        $("form", name:"formMask").$('input', id: 'New_Quantity').value  1
        $("form", name:"formMask").$('input', id: 'New_Value').value  productPrice                
        $('input', name: 'add').click();
        
        waitFor(10){
            $('input', name: 'finish')          
        }
        
        $('input', name: 'finish').click()
        
    }   
	
	def clickGeneralTab()
    {
        generalTabLink.click()        
    }  
}