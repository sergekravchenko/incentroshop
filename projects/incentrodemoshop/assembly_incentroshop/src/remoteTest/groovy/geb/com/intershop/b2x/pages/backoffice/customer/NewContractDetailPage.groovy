package geb.com.intershop.b2x.pages.backoffice.customer

import geb.com.intershop.b2x.model.backoffice.Contract

class NewContractDetailPage extends ContractDetailPage {
	static content= {
		newContractForm 			            { $('form', name:'CreateCustomerContract') }

		btnApply(to: EditContractDetailPage)	{ $('input', name: 'createContract') }
		btnCancel				             	{ $('input', name: 'cancelContract') }
		btnBack						            { $('input', name: 'back')}
	}

	
	static at = {
         waitFor(20) { newContractForm.size() > 0 }
	}

	def createContract(Contract contract)
	{
		newContractForm.$('input', id: 'CreateCustomerContract_Name').value           contract.name
		newContractForm.$('input', id: 'CreateCustomerContract_ContractID').value     contract.name
		newContractForm.$('input', id: 'CreateCustomerContract_ContractType').value   contract.type
		newContractForm.$('textarea', id: 'CreateCustomerContract_Comment').value 	  contract.comment
		newContractForm.$('input', id: 'CreateCustomerContract_StartDate').value      contract.startDate
		newContractForm.$('input', id: 'CreateCustomerContract_EndDate').value 		  contract.endDate
		
		btnApply.click();
	}
}