package geb.com.intershop.b2x.pages.backoffice.customer

import geb.com.intershop.b2x.pages.backoffice.BackOfficePage

class ContractDetailPage extends BackOfficePage {

    static content =
    {
        linkConfiguration(to: ContractConfigurationPage)      { $("a",href:contains(~/ViewContractConfiguration-Start/)) }
    }
    
	def gotoConfigurationTab ()
	{		
        linkConfiguration.click()        
	}
    
    def gotoMenuItem ()
    {        
        //siteNavigation.find('ul').jquery.show()
        
        $(".selected").find('ul').jquery.show()
        
        $("a", href:contains("ViewContracts-List")).click()
    }
    
    
}

