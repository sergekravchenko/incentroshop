package geb.com.intershop.b2x.pages.backoffice.modules

import geb.Module;
import geb.navigator.Navigator;

class ProductListEntry extends Module
{
    static content = {
        entry       { i -> $('td', i) }
        
        selected    { entry(0).find("input", name:"SelectedProductUUID") }
        sku         { entry(3).text().trim() }
    }
}