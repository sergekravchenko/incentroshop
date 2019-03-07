package geb.com.intershop.b2x.pages.backoffice.customer


class CustomersAccountManagersPage extends CustomerPage {
	static content = {
        accountManagerListForm { $('form', name: 'customerAccountManagerList') }
        
		btnNew		          { accountManagerListForm.$('input', name:'create') }
		btnDelete	          { accountManagerListForm.$('input', name:'confirmDelete') }	
	}
	
	static at = {
         waitFor(30, 5){ accountManagerListForm.size()>0 }
	}

    def assignAllUsers()
    {
        
        btnNew.click()

        waitFor(10){
            //what if more than one product/link found?
            $('input', name:'assignAccountManager')
        }
        
        $("input", name: "selectAllOnPage").click()
        //sleepForNSeconds(3)
        
        $('input', name:'assignAccountManager').click()
        
    }
			
}


