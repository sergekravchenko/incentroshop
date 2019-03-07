package geb.com.intershop.b2x.pages.storefront.responsive.modules

import geb.Module;
import geb.navigator.Navigator;


class OCICatalogRow extends Module
{
    def rowNumber;
    
    static content = {
        productId   { $('input[name="NEW_ITEM-EXT_PRODUCT_ID['  +rowNumber+']"]').value() }
        description { $('input[name="NEW_ITEM-DESCRIPTION['     +rowNumber+']"]').value() }
        matNr       { $('input[name="NEW_ITEM-MATNR['           +rowNumber+']"]').value() }
        quantity    { $('input[name="NEW_ITEM-QUANTITY['        +rowNumber+']"]').value() }
        unit        { $('input[name="NEW_ITEM-UNIT['            +rowNumber+']"]').value() }
        price       { $('input[name="NEW_ITEM-PRICE['           +rowNumber+']"]').value() }
        currency    { $('input[name="NEW_ITEM-CURRENCY['        +rowNumber+']"]').value() }
        priceUnit   { $('input[name="NEW_ITEM-PRICEUNIT['       +rowNumber+']"]').value() }
        
        //but the complete name is like: NEW_ITEM-LONGTEXT_1:123[]
        //works with chrome driver: longText    { $('input[name="NEW_ITEM-LONGTEXT_'+rowNumber+':132[]"').value() }
        longText    { $('input[name^="NEW_ITEM-LONGTEXT_'       +rowNumber+'"]').value() }
        vendor      { $('input[name="NEW_ITEM-VENDOR['          +rowNumber+']"]').value() }
        vendorMat   { $('input[name="NEW_ITEM-VENDORMAT['       +rowNumber+']"]').value() }
        manufactCode{ $('input[name="NEW_ITEM-MANUFACTCODE['    +rowNumber+']"]').value() }
        manufactMat { $('input[name="NEW_ITEM-MANUFACTMAT['     +rowNumber+']"]').value() }
    }
}
