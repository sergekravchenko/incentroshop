package geb.com.intershop.b2x.pages.storefront.responsive.modules

import geb.Module;
import geb.navigator.Navigator;

class CartItemRow extends Module
{
    static content = {
        sku { $('[data-testing-class="product-id"]').text() }
        quantity { $('[data-testing-id^="product-count-"]').value() }
    }
    
}
