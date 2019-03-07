package geb.com.intershop.b2x.pages.backoffice.customer

import geb.com.intershop.b2x.pages.backoffice.BackOfficePage
import geb.com.intershop.b2x.pages.backoffice.channel.ChannelMgmtCustomersPage

class CustomerPage extends BackOfficePage {
	
    static content= {
        linkGeneral         { $("a",href:contains(~/ViewCustomer.*-Show/)) }
        linkAttributes      { $("a",href:contains(~/ViewCustomer.*-DispatchCA/)) }
        linkAddresses       { $("a",href:contains(~/ViewCustomerAddressList.*-List/)) }
        linkPriceLists      { $("a",href:contains(~/ViewCustomerPriceListsList.*-List/)) }
        linkCatalogViews    { $("a",href:contains(~/ViewCustomerCatalogViewsList.*-List/)) }
        linkOrders          { $("a",href:contains(~/ViewCustomerOrderList.*-OrderSearch/)) }
        linkSegments        { $("a",href:contains(~/ViewCustomerCustomerSegments.*-ListAll/)) }
        linkUsers           { $("a",href:contains(~/ViewCustomerUserList.*-List/)) }
        linkAccountManagers (to: CustomersAccountManagersPage)  { $("a",href:contains(~/ViewCustomerAccountManagerList.*-List/)) }
        linkContracts       (to: CustomersContractsListPage)    { $("a",href:contains(~/ViewCustomerContracts.*-List/)) }

        btnBackToList       (to: ChannelMgmtCustomersPage)      { $('input', name: 'back') }
    }

    def clickGeneral()
    {
        linkGeneral.click()

    }

    def clickAttributes()
    {
        linkAttributes.click()

    }

    def clickAddresses()
    {
        linkAddresses.click()

    }

    def clickPriceLists()
    {
        linkPriceLists.click()

    }

    def clickCatalogViews()
    {
        linkCatalogViews.click()

    }

    def clickOrders()
    {
        linkOrders.click()

    }

    def clickSegments()
    {
        linkSegments.click()
    }

    def clickUsers()
    {
        linkUsers.click()

    }

    def clickAccountManagers()
    {
        linkAccountManagers.click()

    }

    def clickContracts()
    {
        linkContracts.click()

    }

    def clickBackToList()
    {
        btnBackToList.click()
    }

    def gotoCustomerTab(tabtext) {
        def link = $('a', class: "table_tabs_dis", text: tabtext)
        assert link.size() > 0
        
        link.click();
    }
   
}


