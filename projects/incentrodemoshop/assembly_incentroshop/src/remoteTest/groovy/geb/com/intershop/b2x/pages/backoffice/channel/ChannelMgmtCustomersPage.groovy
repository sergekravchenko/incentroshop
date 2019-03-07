package geb.com.intershop.b2x.pages.backoffice.channel

import geb.Module
import geb.com.intershop.b2x.pages.backoffice.BackOfficePage

class ChannelMgmtCustomersPage extends BackOfficePage {
	static content = {
        customerListForm { $('form[data-testing-id^="page-bo-customer-list-channel"]') }
		
		customerList { moduleList CustomerListItem, customerListForm.$('table.stickyHeader').$('tbody').$('tr') }

		btnNew		{ siteContent.$('input', name:'create') }
		btnDelete	{ siteContent.$('input', name:'confirmDelete') }	
	}
	
	static at = {
         waitFor(30, 3){ customerListForm.size()>0 }
	}

	def selectByName(name) {

		def item = customerList.find({it.name == name })
		assert item != null
		
		item.select()
	}
	
	def selectById(customerId) {
		
				def item = customerList.find({it.id == customerId })
				assert item != null
				
				item.select()
			}

	def selectByLineNo(lineNo) {

		def item = customerList[lineNo]
		assert item != null
		
		item.select()
	}
	
}

class CustomerListItem extends Module {
	static content = {
		name { $('td')[2].text().trim() }
		id   { $('td')[3].text().trim() }
	}
	
	def check() {
		$('td')[0].$('input').value  true
	}
	
	def select() {
		$('td')[2].$('a').click()
	}
}

