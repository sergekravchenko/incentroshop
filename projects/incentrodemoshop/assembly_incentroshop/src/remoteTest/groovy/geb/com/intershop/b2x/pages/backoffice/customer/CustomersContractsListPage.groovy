package geb.com.intershop.b2x.pages.backoffice.customer

import geb.Module
import geb.com.intershop.b2x.pages.backoffice.BackOfficePage

class CustomersContractsListPage extends BackOfficePage {
	static content = {
        contractsListForm   { $('form[data-testing-id^="page-bo-customer-contracts-list-channel"]') }
        
		contractsList { moduleList ContractsListItem, contractsListForm.$('table.grid.stickyHeader').$('tbody').$('tr.row') }

        contractsForm          { $("form", name:"ContractsForm") }

        btnNew(to: NewContractDetailPage)		{ contractsForm.$('input', name:'new') }
		btnDelete	                            { contractsForm.$('input', name:'confirmDelete') }	
	}
	
	static at = {
         waitFor{ contractsListForm.displayed }
	}

    def clickNew()
    {
        btnNew.click()
    }

	def selectByName(name) {

		def item = contractsList.find({it.name == name })
		assert item != null
		
		item.select()
	}
	
	def selectById(customerId) {
		
		def item = contractsList.find({it.id == customerId })
		assert item != null
		
		item.select()
	}

	def selectByLineNo(lineNo) {

		def item = contractsList[lineNo]
		assert item != null
		
		item.select()
	}
    
    def deleteContract(contractName)
    {
        def link = contractsForm.$('a', text:contractName)
        
        link.parent().parent().$('input', class:"select").click()

        $('input', name:"confirmDelete").click();
        
        waitFor(20) {
            $("td", class: "confirm",
                    text: contains("Do you really want to delete the selected contract?")
            )
        }
         
        $('input', name:"delete").click();
        
    }			
}

class ContractsListItem extends Module {
	static content = {
		name { $('td')[1].text().trim() }
		id   { $('td')[2].text().trim() }
	}
	
	def check() {
		$('td')[0].$('input').value  true
	}
	
	def select() {
		$('td')[1].$('a').click()
	}
}

