package geb.com.intershop.b2x.pages.storefront.responsive.modules

import geb.Module;
import geb.navigator.Navigator;

class ProductTile extends Module
{
    def productTerm;
    
    static content = {
        expressShopContainerDiv { 
            $("div",class:"product-tile",text:iContains(productTerm)).
            find("div" ,class: "product-image-container") }
        expressShopTriggerDiv { expressShopContainerDiv.find("div" ,class: "express-shop-trigger") }
        title { $("a",class:"product-title",text:iContains(productTerm)) }
    }
    
    def clickExpressShop()
    {
        interact
        {
            moveToElement( expressShopContainerDiv)
            expressShopTriggerDiv.jquery.show()
            expressShopTriggerDiv.click()
        }
    }
    
    def clickExpressShop(String productName)
    {
        productTerm = productName
        expressShopTriggerDiv.jquery.show().click()
        waitFor(60) { !$('div[data-testing-id="section-expressshop-dialog"]').isEmpty() }
    }
    
    def checkVariationsCount(String countStr)
    {
        !$("div", class:"product-number", text:iContains(countStr)).isEmpty()
    }
    
    Navigator click(){
        title.click()
    }
}
