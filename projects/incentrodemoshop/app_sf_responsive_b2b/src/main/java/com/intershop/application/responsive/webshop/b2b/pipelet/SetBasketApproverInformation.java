package com.intershop.application.responsive.webshop.b2b.pipelet;

import java.util.Date;
import java.util.Optional;

import javax.inject.Inject;

import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.component.approval.capi.basket.BasketBOOrderApprovalExtension;
import com.intershop.component.approval.capi.step.ApprovalStepBO;
import com.intershop.component.approval.capi.step.BusinessObjectApprovalStepExtension;
import com.intershop.component.basket.capi.BasketBO;
import com.intershop.component.repository.capi.BusinessObjectRepositoryContext;
import com.intershop.component.repository.capi.BusinessObjectRepositoryContextProvider;
import com.intershop.component.user.capi.RepositoryBOUserExtension;
import com.intershop.component.user.capi.UserBO;
import com.intershop.component.user.capi.UserBORepository;

/**
 * Sets the approver information to the given basket. the information isn't explicitly provided it will be taken from
 * the latest approval step that has passed through the approval process (is either approved or rejected).
 */
public class SetBasketApproverInformation extends Pipelet
{
    public static final String DN_USER_BO = "UserBO";
    public static final String DN_BASKET_BO = "BasketBO";
    public static final String DN_APPROVAL_COMMENT = "ApprovalComment";
    
    @Inject
    private BusinessObjectRepositoryContextProvider repositoryContextProvider;

    @Override
    public int execute(PipelineDictionary dict) throws PipeletExecutionException
    {
        // lookup 'BasketBO' in pipeline dictionary
        BasketBO basket = dict.getRequired(DN_BASKET_BO);
        
        // lookup 'UserBO' in pipeline dictionary
        UserBO user = dict.getOptional(DN_USER_BO);
        
        // lookup 'ApprovalComment' in pipeline dictionary
        String approvalComment = dict.getOptional(DN_APPROVAL_COMMENT);
        
        if(user == null || approvalComment == null)
        {
            Optional<ApprovalStepBO> lastStep = getLastApprovalStep(basket);
            
            if(lastStep.isPresent())
            {
                ApprovalStepBO step = lastStep.get();
                
                if(user == null)
                {
                    user = getApprover(step);
                }
                
                if(approvalComment == null)
                {
                    approvalComment = step.getComment().orElse(null);
                }
            }
            
            if(user == null)
            {
                return PIPELET_NEXT;
            }
        }
        
        BasketBOOrderApprovalExtension extension = basket.getExtension(BasketBOOrderApprovalExtension.EXTENSION_ID);
        extension.setApproverID(user.getID());
        extension.setApproverFirstName(user.getFirstName());
        extension.setApproverLastName(user.getLastName());
        extension.setApprovalDate(new Date());
        
        if (approvalComment != null && approvalComment.trim().length() > 0)
        {
            extension.setApprovalComment(approvalComment);
        }
        
        return PIPELET_NEXT;
    }
    
    private Optional<ApprovalStepBO> getLastApprovalStep(BasketBO basket)
    {
        BusinessObjectApprovalStepExtension<BasketBO> approvalStepExtension =
                        basket.getExtension(BusinessObjectApprovalStepExtension.EXTENSION_ID);
        
        return approvalStepExtension.getSteps().stream()
            .filter((step) -> step.getApprover().isPresent() && step.getApprovalDate().isPresent())
            .max((step1, step2) -> step1.getApprovalDate().get().compareTo(step2.getApprovalDate().get()));
    }
    
    private UserBO getApprover(ApprovalStepBO step)
    {
        BusinessObjectRepositoryContext boRepositoryContext = repositoryContextProvider.getBusinessObjectRepositoryContext();
        UserBORepository userRepository = boRepositoryContext.getRepository(RepositoryBOUserExtension.EXTENSION_ID);
        
        Optional<String> approverLogin = step.getApprover();
        Optional<UserBO> user = approverLogin.map((login) -> userRepository.getUserBOByLogin(login));
        
        return user.orElse(null);
    }
}