package geb.com.intershop.b2x.model.storefront.responsive;

/**
 * This class represents a product.
 */
class Product extends NamedObject
{
    String sku
    
    /**
     * Constructor for Product.
     */
    Product(String id, String name)
    {
        super(id, name);
    }
    
    /**
     * Constructor for Product.
     */
    Product(String id, String name, String sku)
    {
        super(id, name);
        this.sku = sku
    }
}