/**
 * 
 */
package com.intershop.application.responsive.webshop.b2b.pipelet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.intershop.beehive.pipeline.capi.annotation.PipelineNode;
import com.intershop.beehive.pipeline.capi.annotation.PipelineNodeInput;
import com.intershop.beehive.pipeline.capi.annotation.PipelineNodeOutput;
import com.intershop.component.approval.capi.step.BusinessObjectApprovalStepExtension;
import com.intershop.component.basket.capi.BasketBO;
import com.intershop.component.customer.capi.CustomerBO;
import com.intershop.component.customer.capi.CustomerBOGroupCustomerExtension;
import com.intershop.component.user.capi.UserBO;

@PipelineNode(transactional = false)
public class GetApprovalUsersForBasket
{

    @PipelineNodeOutput
    private Next next;

    @PipelineNodeInput
    public Object execute(Input input)
    {
        BasketBO basketBO = input.getBasketBO();
        Collection<UserBO> approvalUsers = new ArrayList<>();

        CustomerBOGroupCustomerExtension<CustomerBO> customerBOGroupCustomerExtension = basketBO.getCustomerBO()
                        .getExtension(CustomerBOGroupCustomerExtension.EXTENSION_ID);
        
        if (customerBOGroupCustomerExtension != null)
        {
            BusinessObjectApprovalStepExtension<BasketBO> basketBOApprovalStepExtension = basketBO
                            .getExtension(BusinessObjectApprovalStepExtension.EXTENSION_ID);
            if (basketBOApprovalStepExtension != null)
            {
                approvalUsers.addAll(customerBOGroupCustomerExtension.getEnabledUserBOs().stream()
                                .filter(basketBOApprovalStepExtension::isEligibleApprover)
                                .collect(Collectors.toList()));
            }
        }
        next.setApprovalUsers(approvalUsers);
        
        return next;
    }

    public static interface Input
    {
        @Nonnull
        BasketBO getBasketBO();
    }

    public static interface Next
    {
        public void setApprovalUsers(Collection<UserBO> approvers);
    }

}
