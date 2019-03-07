package geb.com.intershop.b2x.model.storefront.responsive.demo

import geb.com.intershop.b2x.model.storefront.responsive.Product

/**
 * ENUM of demo products used for testing in B2B inSpired Showcase
 */
enum DemoProduct
{
    MONITOR_HOLDER("4017128", "Newstar FPMA-D700DD4"),
    PLATE_FOR_DESK("5487860", "Newstar Grommet plate for desk mount"),
    SONY_VLP_SW225("0027242869158", "Sony VPL-SW225"),
    ACER_PREDATOR_G3_605("4713147997367","Acer Predator G3-605");

    final Product product;
 
    private DemoProduct(String id, String name) {
        this.product = new Product(id, name);
    }
 
    Product getProduct() {
        product
    }
}





