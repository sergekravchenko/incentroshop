package com.intershop.application.responsive.webshop.b2b.pipelet;

import java.util.Date;

import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.component.approval.capi.order.OrderBOOrderApprovalExtension;
import com.intershop.component.order.capi.OrderBO;

public class SetOrderApproverInformation extends Pipelet
{
    public static final String DN_ORDER_BO = "OrderBO";
    public static final String DN_APPROVER_FIRST_NAME = "ApproverFirstName";
    public static final String DN_APPROVER_LAST_NAME = "ApproverLastName";
    public static final String DN_APPROVAL_DATE = "ApprovalDate";
    public static final String DN_APPROVAL_COMMENT = "ApprovalComment";

    public int execute(PipelineDictionary dict) throws PipeletExecutionException
    {
        // lookup 'OrderBO' in pipeline dictionary
        OrderBO order = dict.getRequired(DN_ORDER_BO);

        // lookup 'ApproverFirstName' in pipeline dictionary
        String approverFirstName = dict.getOptional(DN_APPROVER_FIRST_NAME);

        // lookup 'ApproverLastName' in pipeline dictionary
        String approverLastName = dict.getOptional(DN_APPROVER_LAST_NAME);

        // lookup 'ApprovalDate' in pipeline dictionary
        Date approvalDate = dict.getOptional(DN_APPROVAL_DATE);

        // lookup 'ApprovalComment' in pipeline dictionary
        String approvalComment = dict.getOptional(DN_APPROVAL_COMMENT);

        OrderBOOrderApprovalExtension extension = order.getExtension(OrderBOOrderApprovalExtension.class);
        if (approverFirstName != null && approverFirstName.trim().length() > 0)
        {
            extension.setApproverFirstName(approverFirstName);
        }
        if (approverLastName != null && approverLastName.trim().length() > 0)
        {
            extension.setApproverLastName(approverLastName);
        }
        if (approvalDate != null)
        {
            extension.setApprovalDate(approvalDate);
        }
        if (approvalComment != null && approvalComment.trim().length() > 0)
        {
            extension.setApprovalComment(approvalComment);
        }
        
        return PIPELET_NEXT;
    }
}