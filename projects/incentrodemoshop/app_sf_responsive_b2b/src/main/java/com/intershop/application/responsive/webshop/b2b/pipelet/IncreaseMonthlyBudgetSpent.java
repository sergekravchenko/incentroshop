package com.intershop.application.responsive.webshop.b2b.pipelet;

import com.intershop.beehive.core.capi.log.Logger;
import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.component.approval.capi.user.UserBOOrderApprovalExtension;
import com.intershop.component.order.capi.OrderBO;
import com.intershop.component.user.capi.UserBO;

public class IncreaseMonthlyBudgetSpent extends Pipelet
{
    public static final String DN_USER_BO = "UserBO";
    public static final String DN_ORDER_BO = "OrderBO";

    public int execute(PipelineDictionary dict) throws PipeletExecutionException
    {
        // lookup 'User' in pipeline dictionary
        UserBO user = dict.getRequired(DN_USER_BO);

        // lookup 'Order' in pipeline dictionary
        OrderBO order = dict.getRequired(DN_ORDER_BO);

        UserBOOrderApprovalExtension extension = user.getExtension(UserBOOrderApprovalExtension.class);
        try
        {
            extension.increaseMonthlyBudgetSpent(order.getGrandTotalGross());
        }
        catch (Exception e)
        {
            Logger.error(this, "Adding total amount of order #" + order.getDocumentNo() 
                            + " (" + order.getGrandTotalGross() + ") to monthly budget spent of user "
                            + user.getLastName() + ", " + user.getFirstName() + " (" 
                            + extension.getMonthlyBudgetSpent() + ") finished with an error: ", e);
            return PIPELET_ERROR;
        }

        return PIPELET_NEXT;
    }
}