package com.intershop.application.responsive.webshop.b2b.pipelet;

import static com.intershop.application.responsive.webshop.b2b.pipelet.SetBasketApproverInformation.DN_APPROVAL_COMMENT;
import static com.intershop.application.responsive.webshop.b2b.pipelet.SetBasketApproverInformation.DN_BASKET_BO;
import static com.intershop.application.responsive.webshop.b2b.pipelet.SetBasketApproverInformation.DN_USER_BO;
import static com.intershop.beehive.core.capi.pipeline.Pipelet.PIPELET_NEXT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.google.inject.Guice;
import com.google.inject.Injector;
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

public class SetBasketApproverInformationTest
{
    @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule();
    
    @Mock private PipelineDictionary dictionary;
    
    @Mock private BasketBO basket;
    
    @Mock private BasketBOOrderApprovalExtension approvalExtension;
    
    @Mock private BusinessObjectApprovalStepExtension<BasketBO> stepExtension;
    
    private static final String suppliedPrefix = "supplied";
    private UserBO suppliedUser;
    
    private static final String olderStepPrefix = "olderStep";
    private static final String latestStepPrefix = "latestStep";
    
    private static final String approverIDSuffix = "ID";
    private static final String approverFirstNameSuffix = "FirstName";
    private static final String approverLastNameSuffix = "LastName";
    private static final String approverLoginSuffix = "@test.intershop.com";
    private static final String commentSuffix = "Comment";
    
    private static final String suppliedComment = suppliedPrefix + commentSuffix;
    
    private Collection<ApprovalStepBO> approvalSteps;
    
    @Mock private BusinessObjectRepositoryContextProvider repositoryContextProvider;
    @Mock private BusinessObjectRepositoryContext repositoryContext;
    @Mock private UserBORepository userBORepository;
    
    private SetBasketApproverInformation pipelet;
    
    @Before
    public void setUp()
    {
        when(dictionary.getRequired(DN_BASKET_BO)).thenReturn(basket);
        when(basket.getExtension(BasketBOOrderApprovalExtension.EXTENSION_ID)).thenReturn(approvalExtension);
        
        suppliedUser = prepareUserMock(suppliedPrefix);
        prepareUserMock(olderStepPrefix);
        prepareUserMock(latestStepPrefix);

        when(basket.getExtension(BusinessObjectApprovalStepExtension.EXTENSION_ID)).thenReturn(stepExtension);
        
        ApprovalStepBO olderStep = prepareApprovalStepMock(new Date(1), olderStepPrefix);
        ApprovalStepBO latestStep = prepareApprovalStepMock(new Date(2), latestStepPrefix);
        
        approvalSteps = Arrays.asList(olderStep, latestStep);
        
        when(stepExtension.getSteps()).thenReturn(approvalSteps);
        
        Injector injector = Guice.createInjector(
            (binder) -> binder.bind(BusinessObjectRepositoryContextProvider.class).toInstance(repositoryContextProvider)
        );
        
        when(repositoryContextProvider.getBusinessObjectRepositoryContext()).thenReturn(repositoryContext);
        when(repositoryContext.getRepository(RepositoryBOUserExtension.EXTENSION_ID)).thenReturn(userBORepository);
        
        pipelet = injector.getInstance(SetBasketApproverInformation.class);
    }
    
    @Test
    public void allInfoSuppliedTest() throws PipeletExecutionException
    {
        when(dictionary.getOptional(DN_USER_BO)).thenReturn(suppliedUser);
        when(dictionary.getOptional(DN_APPROVAL_COMMENT)).thenReturn(suppliedComment);
        
        int result = pipelet.execute(dictionary);
        
        assertEquals("Unexpected pipelet result.", PIPELET_NEXT , result);
        
        verify(dictionary).getRequired(DN_BASKET_BO);
        verify(dictionary).getOptional(DN_USER_BO);
        verify(dictionary).getOptional(DN_APPROVAL_COMMENT);
        
        verifyApprovalInformation(suppliedPrefix, suppliedPrefix);
    }
    
    @Test
    public void userMissingTest() throws PipeletExecutionException
    {
        when(dictionary.getOptional(DN_APPROVAL_COMMENT)).thenReturn(suppliedComment);
        
        int result = pipelet.execute(dictionary);
        
        assertEquals("Unexpected pipelet result.", PIPELET_NEXT , result);
        
        verifyApprovalInformation(latestStepPrefix, suppliedPrefix);
    }
    
    @Test
    public void commentMissingTest() throws PipeletExecutionException
    {
        when(dictionary.getOptional(DN_USER_BO)).thenReturn(suppliedUser);
        
        int result = pipelet.execute(dictionary);
        
        assertEquals("Unexpected pipelet result.", PIPELET_NEXT , result);
        
        verifyApprovalInformation(suppliedPrefix, latestStepPrefix);
    }
    
    @Test
    public void noStepsTest() throws PipeletExecutionException
    {
        when(dictionary.getOptional(DN_APPROVAL_COMMENT)).thenReturn(suppliedComment);
        
        when(stepExtension.getSteps()).thenReturn(Collections.emptyList());
        
        int result = pipelet.execute(dictionary);
        
        assertEquals("Unexpected pipelet result.", PIPELET_NEXT , result);
        
        verify(approvalExtension, never()).setApproverID(anyString());
        verify(approvalExtension, never()).setApproverFirstName(anyString());
        verify(approvalExtension, never()).setApproverLastName(anyString());
        verify(approvalExtension, never()).setApprovalDate(any());
        verify(approvalExtension, never()).setApprovalComment(anyString());
    }
    
    private UserBO prepareUserMock(String prefix)
    {
        UserBO mock = mock(UserBO.class);
        when(mock.getID()).thenReturn(prefix + approverIDSuffix);
        when(mock.getFirstName()).thenReturn(prefix + approverFirstNameSuffix);
        when(mock.getLastName()).thenReturn(prefix + approverLastNameSuffix);
        when(userBORepository.getUserBOByLogin(prefix + approverLoginSuffix)).thenReturn(mock);
        return mock;
    }
    
    private ApprovalStepBO prepareApprovalStepMock(Date approvalDate, String prefix)
    {
        ApprovalStepBO mock = mock(ApprovalStepBO.class);
        when(mock.getApprovalDate()).thenReturn(Optional.ofNullable(approvalDate));
        when(mock.getApprover()).thenReturn(Optional.ofNullable(prefix + approverLoginSuffix));
        when(mock.getComment()).thenReturn(Optional.ofNullable(prefix + commentSuffix));
        return mock;
    }
    
    private void verifyApprovalInformation(String approverPrefix, String commentPrefix)
    {
        verify(approvalExtension).setApproverID(approverPrefix + approverIDSuffix);
        verify(approvalExtension).setApproverFirstName(approverPrefix + approverFirstNameSuffix);
        verify(approvalExtension).setApproverLastName(approverPrefix + approverLastNameSuffix);
        verify(approvalExtension).setApprovalDate(any());
        verify(approvalExtension).setApprovalComment(commentPrefix + commentSuffix);
    }
}
