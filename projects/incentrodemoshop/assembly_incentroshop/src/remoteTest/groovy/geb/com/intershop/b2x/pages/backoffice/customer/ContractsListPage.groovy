package geb.com.intershop.b2x.pages.backoffice.customer

import geb.com.intershop.b2x.model.backoffice.Contract
import geb.com.intershop.b2x.pages.backoffice.BackOfficePage
import geb.com.intershop.b2x.pages.backoffice.modules.ContractRow

class ContractsListPage extends BackOfficePage {

    static at = {
         waitFor(30, 3){ contractsListForm.size()>0 }
    }
    
    static content = {
        contractsListForm              { $('form', name: 'ContractsForm') }
        contractItems                  { moduleList ContractRow, $("#ContractsGrid table tr").tail() } // tailing to skip the header row
        btnFind(to: ContractsListPage) { $('input', name: 'find') }
    }

    def searchContractByNameAndEndDate(contractName, contractSearchEndDate)
    {
        contractsListForm .$('input', id: 'WFContractSearch_EndDateTo').value  contractSearchEndDate
        
        btnFind.click();
        
        waitFor(10){
                contractsListForm.size()>0
        }
        
        //return contract found or not
        $("a", text: contains(contractName)).size()>0
    }
    
    boolean existsContract(Contract contract)
    {
        contractsListForm.WFContractSearch_ContractNameOrID = contract.name
        
        btnFind.click();
        
        waitFor(10) { contractsListForm.size() > 0 } // is it necessary ????
        
        //return contractItems.find { item -> item.name == contract.name }
        return $("a", text: contains(contract.name)).size() > 0
    }
    
    def gotoContract(Contract contract)
    {
//        $("a", text:contractName).first().click()
        def result = contractItems.find { 
            it.name == contract.name
        }
        
        result.contractDetails.click()
    }   
}

