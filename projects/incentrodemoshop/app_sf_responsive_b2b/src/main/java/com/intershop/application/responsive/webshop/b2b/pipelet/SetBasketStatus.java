package com.intershop.application.responsive.webshop.b2b.pipelet;

import com.intershop.beehive.bts.capi.orderprocess.basket.Basket;
import com.intershop.beehive.core.capi.domain.PersistentObjectBOExtension;
import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.component.basket.capi.BasketBO;

public class SetBasketStatus extends Pipelet
{
    public static final String DN_BASKET_BO = "BasketBO";
    public static final String DN_NEW_BASKET_STATUS = "NewBasketStatus";

	/**
     * The pipelet's execute method is called whenever the pipelets gets
     * executed in the context of a pipeline and a request. The pipeline
     * dictionary valid for the currently executing thread is provided as a
     * parameter.
     * 
     * @param dict
     *            The pipeline dictionary to be used.
     * @throws PipeletExecutionException
     *             Thrown in case of severe errors that make the pipelet execute
     *             impossible (e.g. missing required input data).
     */

	public int execute(PipelineDictionary dict)	throws PipeletExecutionException
	{
		// lookup 'Basket' in pipeline dictionary
		BasketBO basket = dict.getRequired(DN_BASKET_BO);

		// lookup 'NewBasketStatus' in pipeline dictionary
		Integer newBasketState = dict.getRequired(DN_NEW_BASKET_STATUS);

		Basket persistentBasket = basket.getExtension(PersistentObjectBOExtension.class).getPersistentObject();
		persistentBasket.setStatus(newBasketState);

		return PIPELET_NEXT;
	}
}