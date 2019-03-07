package geb.com.intershop.b2x.pages.backoffice.customer

class CustomersMgmtDetailPage extends CustomerPage {
	static at = {
            waitFor
            {
                 updateCustomerForm.size()>0 && updateCustomerForm.displayed
            }
	}

	static content = {
        updateCustomerForm   { $('form', id: 'kor-customer-updateForm', name: 'UpdateCustomer') }	
        
	}					
}


