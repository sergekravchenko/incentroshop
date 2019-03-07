package geb.com.intershop.b2x.pages.backoffice.customer

class ContractConfigurationPage extends ContractDetailPage {

    static content= {
		salesTargetForm	{ $("form", name:"AddSalesTargetToContract") }
        
        btnApply(to: ContractConfigurationPage)        { $('input', name: 'apply') }
	}
	
    static at = {
        waitFor(20) {
           salesTargetForm.size()>0
       }
   }	
    
   def configureContract(priceListName)
   {
       
       salesTargetForm.$('input', id: 'AddSalesTargetToContract_Revenue').value 10000
       salesTargetForm.AddSalesTargetToContract_PriceListID = priceListName
              
       btnApply.click()
       
       //check for error?
   } 
}



