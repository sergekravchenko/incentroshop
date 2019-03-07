package com.intershop.application.responsive.webshop.b2b.pipelet;

import java.util.Map;

import javax.inject.Inject;

import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.beehive.core.capi.user.User;
import com.intershop.beehive.xcs.capi.price.ProductPriceEntry;
import com.intershop.beehive.xcs.capi.price.ProductPriceMapping;
import com.intershop.beehive.xcs.capi.price.ProductPriceMgr;
import com.intershop.component.basket.capi.BasketProductLineItemBO;
import com.intershop.component.pricing.capi.pricelist.PriceList;
import com.intershop.component.pricing.capi.pricelist.PriceListConstants;
import com.intershop.component.product.capi.ProductBO;

/**
 * Determines the price list from which the price of given product line items is
 * determined. If the price is not taken from a price list, the error connector
 * will be used.
 */
public class GetPriceListFromProductLineItem extends Pipelet
{
    @Inject
    ProductPriceMgr priceMgr;

    @Override
    public int execute(PipelineDictionary dict) throws PipeletExecutionException
    {
        User user = dict.getRequired("User");
        BasketProductLineItemBO pli = dict.getRequired("BasketProductLineItemBO");
        ProductBO product = pli.getProductBO();

        ProductPriceMapping mapping = priceMgr.getPrice(user, product.getProductRef(), pli.getQuantity(), pli.getPrice().getCurrencyMnemonic(), null);
        ProductPriceEntry entry = mapping.getEntry(product.getProductRef());
        Map<String, Object> priceInfos = (Map<String, Object>)entry.getPriceInfo(PriceListConstants.PRICELIST_PRICE_INFO);
        if (priceInfos != null)
        {
            PriceList priceList = (PriceList)priceInfos.get(PriceListConstants.PRICEINFO_PRICE_LIST);
            dict.put("PriceList", priceList);
            return PIPELET_NEXT;
        }
        return PIPELET_ERROR;
    }
    
}
