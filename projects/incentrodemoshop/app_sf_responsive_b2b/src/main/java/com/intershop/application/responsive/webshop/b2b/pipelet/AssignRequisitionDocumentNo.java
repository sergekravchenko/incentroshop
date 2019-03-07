package com.intershop.application.responsive.webshop.b2b.pipelet;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import com.intershop.beehive.bts.capi.orderprocess.basket.Basket;
import com.intershop.beehive.core.capi.domain.PersistentObjectBOExtension;
import com.intershop.beehive.pipeline.capi.annotation.PipelineNode;
import com.intershop.beehive.pipeline.capi.annotation.PipelineNodeInput;
import com.intershop.beehive.pipeline.capi.annotation.PipelineNodeOutput;
import com.intershop.component.application.capi.ApplicationBO;
import com.intershop.component.application.capi.CurrentApplicationBOProvider;
import com.intershop.component.basket.capi.BasketBO;
import com.intershop.component.basket.capi.DocumentNumberProvider;

/**
 * Gets the next available requisition document number and assigns it to a basket.
 * 
 * @author Martin Bonev
 */
@PipelineNode(transactional = true)
public class AssignRequisitionDocumentNo
{
    @Inject @Named("Requisition") private DocumentNumberProvider numberProvider;
    
    @Inject private CurrentApplicationBOProvider applicationBOProvider;
    
    @PipelineNodeOutput private Next next;
    
    @PipelineNodeInput
    public Object setRequisitionDocumentNo(Input input)
    {
        BasketBO basket = input.getBasketBO();
        
        ApplicationBO applicationBO = applicationBOProvider.get();
        String domainID = applicationBO.getRepository().getRepositoryID();
        String requisitionDocumentNo = numberProvider.getDocumentNumber(domainID);
        
        Basket basketPO = basket.getExtension(PersistentObjectBOExtension.class).getPersistentObject();
        basketPO.setRequisitionDocumentNo(requisitionDocumentNo);
        
        return next;
    }
    
    public static interface Input
    {
        @Nonnull public BasketBO getBasketBO();
    }
    
    public static interface Next{}
}
