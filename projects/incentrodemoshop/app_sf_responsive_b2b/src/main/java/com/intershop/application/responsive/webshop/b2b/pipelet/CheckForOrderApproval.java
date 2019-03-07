package com.intershop.application.responsive.webshop.b2b.pipelet;

import java.util.Set;

import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.component.approval.capi.rule.ApprovalRequiredRule;
import com.intershop.component.approval.pipelet.IsApprovalNeeded;
import com.intershop.component.basket.capi.BasketBO;
import com.intershop.component.customer.capi.CustomerBO;
import com.intershop.component.user.capi.UserBO;

/**
 * Based on configured rules, the basket content and the user, checks if
 * approval is required before the order is submitted.
 * 
 * @deprecated use the {@link IsApprovalNeeded}
 */
@Deprecated
public class CheckForOrderApproval extends Pipelet
{
    private static final String DN_IS_APPROVAL_REQUIRED = "IsApprovalRequired";

    @Override
	public int execute(PipelineDictionary dict)	throws PipeletExecutionException
	{
        UserBO user = dict.getRequired("UserBO");
        CustomerBO customer = dict.getRequired("CustomerBO");
        BasketBO basket = dict.getRequired("BasketBO");
        Set<ApprovalRequiredRule> rules = dict.getRequired("OrderApprovaleRequiredRules");
        
        if (!customer.getCustomerType().isApplicableExtension("GroupCustomer"))
        {
            dict.put(DN_IS_APPROVAL_REQUIRED, Boolean.FALSE);
            return PIPELET_NEXT;
        }

        for(ApprovalRequiredRule rule : rules)
        {
            if(rule.isApprovalRequired(user, basket))
            {
                dict.put(DN_IS_APPROVAL_REQUIRED, Boolean.TRUE);
                return PIPELET_NEXT;
            }
        }
        return PIPELET_NEXT;
	}
}