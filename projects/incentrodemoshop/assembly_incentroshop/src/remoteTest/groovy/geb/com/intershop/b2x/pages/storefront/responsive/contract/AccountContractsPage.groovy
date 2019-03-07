package geb.com.intershop.b2x.pages.storefront.responsive.contract

import geb.com.intershop.b2x.model.backoffice.Contract
import geb.com.intershop.b2x.pages.backoffice.modules.ContractRow
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage

class AccountContractsPage extends AccountPage {
	String pageId() {"b2b-account-contracts"}
	
    static at =
    {
        waitFor{contentSlot.size()>0}
    }
    
    static content =
	{
		contentSlot { $('div[data-testing-id="b2b-account-contracts"]') }
        contracts   { moduleList ContractRow, $('table[data-testing-id="contractList"] tbody tr') }
	}

    def gotoContractDetailsOf(Contract actualContract) {
        println "searching for a contract with name ${actualContract.name} ..."
        ContractRow foundContract = contracts.find {contract -> contract.name == actualContract.name }
        assert foundContract != null
        println "Contract: ${foundContract.contractDetails}"
        foundContract.contractDetails.click()
    }
    
    def printContracts() {
        println "contracts: " + contracts.size()
        contracts.each {contract -> println "contractname: ${contract.name}" }
    }
}