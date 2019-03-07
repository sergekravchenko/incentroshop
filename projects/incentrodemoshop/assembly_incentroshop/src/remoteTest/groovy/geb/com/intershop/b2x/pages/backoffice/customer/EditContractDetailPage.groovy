package geb.com.intershop.b2x.pages.backoffice.customer

class EditContractDetailPage extends ContractDetailPage {
	static content= {
        editContractForm            { $('form', name:'EditCustomerContract') }

		btnApply					{ $('input', name: 'apply') }
		btnCancel					{ $('input', name: 'cancel') }
		btnBack						{ $('input', name: 'back') }
	}
	
	static at = {
         waitFor(20) { 
			editContractForm.size()>0
		}
	}
	
	def deleteContract()
	{
		btnDelete.click()
		$('input', name: 'delete').click()
	}
	
}



