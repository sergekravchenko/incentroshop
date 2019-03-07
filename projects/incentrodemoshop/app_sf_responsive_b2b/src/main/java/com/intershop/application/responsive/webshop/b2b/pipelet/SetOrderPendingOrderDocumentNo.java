package com.intershop.application.responsive.webshop.b2b.pipelet;

import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.component.approval.capi.order.OrderBOOrderApprovalExtension;
import com.intershop.component.order.capi.OrderBO;

public class SetOrderPendingOrderDocumentNo extends Pipelet
{
    public static final String DN_ORDER_BO = "OrderBO";
    public static final String DN_PENDING_ORDER_DOCUMENT_NO = "PendingOrderDocumentNo";

    public int execute(PipelineDictionary dict) throws PipeletExecutionException
    {
        // lookup 'OrderBO' in pipeline dictionary
        OrderBO order = dict.getRequired(DN_ORDER_BO);

        // lookup 'OrderReferenceId' in pipeline dictionary
        String pendingOrderDocumentNo = dict.getRequired(DN_PENDING_ORDER_DOCUMENT_NO);

        OrderBOOrderApprovalExtension extension = order.getExtension(OrderBOOrderApprovalExtension.class);
        extension.setPendingOrderDocumentNo(pendingOrderDocumentNo);
        
        return PIPELET_NEXT;
    }
}