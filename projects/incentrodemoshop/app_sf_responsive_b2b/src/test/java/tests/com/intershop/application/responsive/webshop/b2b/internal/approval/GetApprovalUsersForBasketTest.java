package tests.com.intershop.application.responsive.webshop.b2b.internal.approval;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.google.inject.AbstractModule;
import com.intershop.application.responsive.webshop.b2b.pipelet.GetApprovalUsersForBasket;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.pipeline.capi.annotation.PipelineNodeOutput;
import com.intershop.component.approval.capi.step.BusinessObjectApprovalStepExtension;
import com.intershop.component.basket.capi.BasketBO;
import com.intershop.component.customer.capi.CustomerBO;
import com.intershop.component.customer.capi.CustomerBOGroupCustomerExtension;
import com.intershop.component.user.capi.UserBO;
import com.intershop.tools.etest.pipelinenode.PipelineNodeRule;

public class GetApprovalUsersForBasketTest
{
    @Rule
    public final MockitoRule mockingRule = MockitoJUnit.rule();
    
    @PipelineNodeOutput
    private TestNext  next = new TestNext() {};
    
    @Rule
    public final PipelineNodeRule pipelineNodeRule = new PipelineNodeRule(this, GetApprovalUsersForBasket.class,new AbstractModule()
    {
        @Override
        protected void configure()
        {
            bind(GetApprovalUsersForBasket.Next.class).toInstance(next);
        }
    });
    
    @Mock
    private UserBO approverBO1;
    
    @Mock
    private UserBO notApproverBO2;
    
    @Mock 
    private CustomerBO customerBO;
    
    @Mock
    private GetApprovalUsersForBasket.Input input;

    @Mock
    private BusinessObjectApprovalStepExtension<BasketBO> approvalStepExtension;

    @Mock
    private CustomerBOGroupCustomerExtension<CustomerBO> customerBOGroupCustomerExtension;
    
    @Mock
    private BusinessObjectApprovalStepExtension<BasketBO> businessObjectApprovalStepExtension;
    
    @Mock
    private BasketBO basketBO;
    
    private Collection<UserBO> customerUsers = new ArrayList<>();
    

    @Inject
    private GetApprovalUsersForBasket getApprovalUsersForBasket;

    @Before
    public void setUp()
    {
        when(approverBO1.getID()).thenReturn("approverBO1");
        when(notApproverBO2.getID()).thenReturn("notApproverBO2");
        
        customerUsers.add(approverBO1);
        customerUsers.add(notApproverBO2);
        
        when(input.getBasketBO()).thenReturn(basketBO);
        when(basketBO.getCustomerBO()).thenReturn(customerBO);
        when(customerBO.getExtension(CustomerBOGroupCustomerExtension.EXTENSION_ID)).thenReturn(customerBOGroupCustomerExtension);
        when(customerBOGroupCustomerExtension.getEnabledUserBOs()).thenReturn(customerUsers);
        when(basketBO.getExtension(BusinessObjectApprovalStepExtension.EXTENSION_ID)).thenReturn(businessObjectApprovalStepExtension);
        when(businessObjectApprovalStepExtension.isEligibleApprover(approverBO1)).thenReturn(Boolean.TRUE);
        when(businessObjectApprovalStepExtension.isEligibleApprover(notApproverBO2)).thenReturn(Boolean.FALSE);
        
    }
    
    @Test
    public void testGetApprovers() throws PipeletExecutionException
    {
        
        Collection<UserBO> result =  ((TestNext)getApprovalUsersForBasket.execute(input)).getResult();
        
       assertNotNull("Approvers collection can not be null, expected empty collection instance at least!",result);
       assertFalse("At least one approver expected!, but collection is empty!",result.isEmpty());
       assertTrue("Expected: " + approverBO1.getID() + " as approver for basket, but not present in returned approvers list!",result.stream().anyMatch(userBO -> userBO.getID().equals(approverBO1.getID())));
       assertTrue("Expected:  " + notApproverBO2.getID() + " not to be present in returned approvers list, but was found!",result.stream().noneMatch(userBO -> userBO.getID().equals(notApproverBO2.getID())));
    }
    
    private static interface  TestNext extends GetApprovalUsersForBasket.Next
    {
        Collection<UserBO> result = new ArrayList<>();
        
        @Override
        public default void setApprovalUsers(Collection<UserBO> approvers)  {result.addAll(approvers);}
        
        public default Collection<UserBO> getResult() {return result;}
    }
}
