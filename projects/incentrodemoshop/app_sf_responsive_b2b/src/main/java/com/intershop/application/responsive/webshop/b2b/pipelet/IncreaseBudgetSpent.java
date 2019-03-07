package com.intershop.application.responsive.webshop.b2b.pipelet;

import com.intershop.beehive.core.capi.log.Logger;
import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.component.approval.capi.budget.BudgetType;
import com.intershop.component.approval.capi.user.UserBOOrderApprovalExtension;
import com.intershop.component.order.capi.OrderBO;
import com.intershop.component.user.capi.UserBO;

/**
 * Increases budget spent for given {@link BudgetType} If {@link BudgetType}
 * configuration parameter is not provided,
 * UserBOOrderApprovalExtension.getBudgetType() value is used.
 *
 */
public class IncreaseBudgetSpent extends Pipelet
{

    public static final String DN_USER_BO = "UserBO";
    public static final String DN_ORDER_BO = "OrderBO";
    public static final String DN_BUDGET_TYPE = "BudgetType";

    public int execute(PipelineDictionary dict) throws PipeletExecutionException
    {
        // lookup 'User' in pipeline dictionary
        UserBO user = dict.getRequired(DN_USER_BO);

        // lookup 'Order' in pipeline dictionary
        OrderBO order = dict.getRequired(DN_ORDER_BO);

        String btype = dict.getOptional(DN_BUDGET_TYPE);
        BudgetType budgetType = BudgetType.NONE;
        UserBOOrderApprovalExtension extension = user.getExtension(UserBOOrderApprovalExtension.class);
        try
        {
            if (null == btype)
            {
                budgetType = extension.getBudgetType();
            }
            else
            {
                budgetType =  BudgetType.valueOf(btype.toUpperCase());
            }
            extension.increaseBudgetSpent(order.getGrandTotalGross(), budgetType);
        }
        catch(Exception e)
        {
            Logger.error(this,
                            "Adding total amount of order #" + order.getDocumentNo() + " ("
                                            + order.getGrandTotalGross() + ") to monthly budget spent of user "
                                            + user.getLastName() + ", " + user.getFirstName() + " ("
                                            + extension.getBudgetSpent(budgetType) + ") finished with an error: ", e);
            return PIPELET_ERROR;
        }

        return PIPELET_NEXT;
    }

}
