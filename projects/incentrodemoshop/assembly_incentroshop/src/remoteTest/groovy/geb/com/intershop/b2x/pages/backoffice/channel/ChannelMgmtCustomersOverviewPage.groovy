package geb.com.intershop.b2x.pages.backoffice.channel

import geb.com.intershop.b2x.pages.backoffice.BOLoginPage


class ChannelMgmtCustomersOverviewPage extends BOLoginPage {
	static at = {
         waitFor(20) { 
			$("td", class: "table_title_description", 
			        text: contains("Use the modules below to manage the customers of this channel. Customers can be registered and customer profiles be assigned to customer segments.")
			)
		}
	}

    def navigateToCustomersMgmt() {
        def link = $('a', class: "overview_subtitle", text: "Customers")
		assert link.size() > 0
		
		link.click();
    }
	
}

