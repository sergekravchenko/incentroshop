package com.intershop.application.responsive.webshop.b2b.pipelet;

import com.intershop.beehive.bts.capi.orderprocess.basket.Basket;
import com.intershop.beehive.core.capi.domain.PersistentObjectBOExtension;
import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.component.basket.capi.BasketBO;

public class SetBasketDocumentNo extends Pipelet
{
    public static final String DN_BASKET_BO = "BasketBO";
    public static final String DN_DOCUMENT_NO = "DocumentNo";

    @Override
    public int execute(PipelineDictionary dict) throws PipeletExecutionException
    {
        // lookup 'BasketBO' in pipeline dictionary
        BasketBO basket = dict.getRequired(DN_BASKET_BO);

        // lookup 'DocumentNo' in pipeline dictionary
        String documentNo = dict.getRequired(DN_DOCUMENT_NO);

        Basket persistentBasket = basket.getExtension(PersistentObjectBOExtension.class).getPersistentObject();
        persistentBasket.setDocumentNo(documentNo);
        
        return PIPELET_NEXT;
    }
}