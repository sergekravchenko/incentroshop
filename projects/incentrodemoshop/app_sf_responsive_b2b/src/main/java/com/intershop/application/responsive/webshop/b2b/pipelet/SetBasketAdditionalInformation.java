package com.intershop.application.responsive.webshop.b2b.pipelet;

import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.component.basket.capi.BasketBO;
import com.intershop.component.b2b.capi.basket.BasketBOAdditionalInformationExtension;

public class SetBasketAdditionalInformation extends Pipelet
{
    public static final String DN_BASKET_BO = "BasketBO";
    public static final String DN_COST_CENTER = "CostCenter";
    public static final String DN_MESSAGE_TO_MERCHANT = "MessageToMerchant";

    public int execute(PipelineDictionary dict) throws PipeletExecutionException
    {
        // lookup 'BasketBO' in pipeline dictionary
        BasketBO basket = dict.getRequired(DN_BASKET_BO);

        // lookup 'CostCenter' in pipeline dictionary
        String costCenter = dict.getOptional(DN_COST_CENTER);

        // lookup 'MessageToMerchant' in pipeline dictionary
        String messageToMerchant = dict.getOptional(DN_MESSAGE_TO_MERCHANT);

        BasketBOAdditionalInformationExtension extension = basket.getExtension(BasketBOAdditionalInformationExtension.class);
        if (costCenter != null && costCenter.trim().length() > 0)
        {
            extension.setCostCenter(costCenter);
        }
        if (messageToMerchant != null && messageToMerchant.trim().length() > 0)
        {
            extension.setMessageToMerchant(messageToMerchant);
        }

        return PIPELET_NEXT;
    }
}