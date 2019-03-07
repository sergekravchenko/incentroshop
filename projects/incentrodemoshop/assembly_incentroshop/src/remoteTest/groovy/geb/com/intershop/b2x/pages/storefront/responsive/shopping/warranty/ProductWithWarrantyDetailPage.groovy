package geb.com.intershop.b2x.pages.storefront.responsive.shopping.warranty

import geb.com.intershop.b2x.pages.storefront.responsive.shopping.ProductDetailPage;

class ProductWithWarrantyDetailPage extends ProductDetailPage
{
    static at=
    {
        waitFor{warrantyProducts.size()>0}
    }

    static content=
    {
        warrantyProducts { $('div[data-testing-id="product-details-warranty"]') }
        warrantyProductsList(required: false) { $('dl[data-testing-id="product-details-warranty-list"]') }
        // this attribute covers the currently unused way to display warranties (DisplayType EQ 'dropdown')  
        warrantyProductsDropDown(required: false) { $('select[data-testing-id="product-details-warranty-select"]') }
    }

    //------------------------------------------------------------
    // Page checks
    //------------------------------------------------------------
    def checkWarrantyProductExists(warrantySku)
    {
        warrantyProductsList.find("input" ,type: "radio", value: "Warranty_"+warrantySku).size()>0
    }
    //------------------------------------------------------------
    // link functions
    //------------------------------------------------------------
    def selectWarrantyProduct(warrantySku)
    {
        warrantyProductsList.$("input" ,type: "radio", value: "Warranty_"+warrantySku).click()
    }
}
