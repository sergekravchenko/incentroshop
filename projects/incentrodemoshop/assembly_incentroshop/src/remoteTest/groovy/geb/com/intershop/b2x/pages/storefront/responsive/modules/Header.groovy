package geb.com.intershop.b2x.pages.storefront.responsive.modules

import geb.Module
import geb.com.intershop.b2x.pages.storefront.responsive.HomePage
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountHomePage
import geb.com.intershop.b2x.pages.storefront.responsive.order.QuickOrderPage
import geb.com.intershop.b2x.pages.storefront.responsive.modules.ProductSearch

class Header extends Module
{

    static content =
    {
        productSearch { module ProductSearch }
        miniCartLink { $('a', href:'#miniCart') }
        miniCart { $('div', id:'miniCart') }
        linkLogout (required: false, to: HomePage) {$('a[data-testing-id="link-logout"]')}
        linkQuickOrder (required: false, to: QuickOrderPage) {$('a[data-testing-id="link-quickorder"]')}
        myAccountLink (required: false, to: AccountHomePage) {$('a[data-testing-id="link-myaccount"]')}
    }

    def search(searchTerm)
    {
        productSearch.search(searchTerm)
    }
    
    def showMiniCart()
    {
        waitFor { miniCartLink.displayed }
        miniCartLink.click();
    }
    
    def viewCartMiniCart()
    {
        waitFor { miniCart.$('a', class:'view-cart').displayed }
        miniCart.$('a', class:'view-cart').click()
    }
    
}
