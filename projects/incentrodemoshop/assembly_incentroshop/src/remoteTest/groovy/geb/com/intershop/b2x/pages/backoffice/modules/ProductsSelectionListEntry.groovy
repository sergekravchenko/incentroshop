package geb.com.intershop.b2x.pages.backoffice.modules

import geb.Module;
import geb.navigator.Navigator;

class ProductsSelectionListEntry extends Module
{
    static content = {
        entry       { i -> $('td', i) }
        
        selected    { entry(0).find("input", name:"SelectedProductID") }
        sku         { entry(2).text().trim() }
    }
}