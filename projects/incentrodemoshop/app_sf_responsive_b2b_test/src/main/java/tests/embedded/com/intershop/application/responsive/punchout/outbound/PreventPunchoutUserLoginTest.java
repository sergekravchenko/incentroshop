package tests.embedded.com.intershop.application.responsive.punchout.outbound;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

//import com.intershop.beehive.bts.capi.orderprocess.basket.Basket;
import com.intershop.beehive.configuration.capi.common.Configuration;
import com.intershop.beehive.core.capi.domain.Domain;
import com.intershop.beehive.core.capi.localization.LocaleInformation;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.beehive.core.capi.pipeline.PipelineResult;
import com.intershop.beehive.core.capi.request.Request;
import com.intershop.beehive.core.capi.request.ServletRequest;
import com.intershop.beehive.core.capi.request.Session;
import com.intershop.beehive.core.capi.user.User;
import com.intershop.beehive.core.internal.pipeline.PipelineDictionaryImpl;
import com.intershop.beehive.core.request.test.RequestRule;
import com.intershop.component.application.capi.ApplicationBO;
import com.intershop.component.b2b.role.capi.user.UserBORoleExtension;
import com.intershop.component.basket.capi.BasketBORepository;
import com.intershop.component.customer.capi.CustomerBO;
import com.intershop.component.customer.capi.CustomerBOCompanyCustomerExtension;
import com.intershop.component.customer.capi.CustomerBORepository;
import com.intershop.component.customer.capi.customertype.CustomerType;
import com.intershop.component.organization.capi.Organization;
import com.intershop.component.user.capi.UserBO;
import com.intershop.component.user.capi.UserBORepository;
import com.intershop.sellside.application.b2c.capi.basket.ApplicationBOBasketExtension;
import com.intershop.tools.etest.server.AutoCleanup;
import com.intershop.tools.etest.server.Cartridges;
import com.intershop.tools.etest.server.DomainCreatorRule;
import com.intershop.tools.etest.server.DomainCreatorRule.Create;
import com.intershop.tools.etest.server.DomainCreatorRule.ForceApplicationContext;
import com.intershop.tools.etest.server.EmbeddedServerRule;
import com.intershop.tools.etest.server.PipelineRule;

/**
 * Test of pipeline extension PreventPunchoutUserLogin.
 *
 */
@Cartridges("app_sf_responsive_b2b")
@ForceApplicationContext
@AutoCleanup
@RunWith(MockitoJUnitRunner.class)
public class PreventPunchoutUserLoginTest
{
    @Mock
    private Organization organizationMock;
    
    @Mock
    private ApplicationBO applicationBOMock;

    @Mock
    private UserBORepository userBORepositoryMock;
    
    @Mock
    private UserBO ociUserBOMock;
    
    @Mock
    private UserBO userBOMock;
    
    @Mock
    private User userMock;
    
    @Mock
    private BasketBORepository basketBORepositoryMock;
    
    @Mock
    private Domain domainMock;
    
    @Mock
    private CustomerBO customerBOMock;
    
    @Mock
    private CustomerBORepository customerBORepositoryMock;
    
    @Mock
    private Configuration configurationMock;
    
    @Mock
    private UserBORoleExtension ociUserBORoleExtensionMock;
    
    @Mock
    private UserBORoleExtension userBORoleExtensionMock;
    
    @Mock
    private ApplicationBOBasketExtension applicationBOBasketExtensionMock;
    
    @Mock
    private CustomerBOCompanyCustomerExtension<CustomerBO> customerBOCompanyCustomerExtensionMock;
    
    @Mock
    private CustomerType customerTypeMock;
    
    @Mock
    private Request requestMock;
    
    @Mock
    private LocaleInformation localeMock;
    
    private EmbeddedServerRule server = new EmbeddedServerRule(this);
    
    private DomainCreatorRule domainCreatorRule = DomainCreatorRule.site().build(this);
    private PipelineRule pipelineRule = PipelineRule.builder().build();
 
    @Rule
    public RequestRule mockRequest = new RequestRule();
    
    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(server).around(domainCreatorRule).around(pipelineRule).around(mockRequest); 
    
    @Create
    private Domain site;
    
    private static final String LOGIN_OCIUSER = "ociuser@test.com";
    
    private static final String LOGIN_NON_OCIUSER = "user@test.com";
    
    private static final String PASSWORD = "password";
    
    private static final String APP_B2B_OCI_USER_ROLE = "APP_B2B_OCI_USER";
    
    private static final String B2B_CUSTOMER_TYPE_ID = "B2B_CUSTOMER_ID";
    
    private PipelineDictionary pipelineDict = new PipelineDictionaryImpl(); 
    
    @Before
    public void setUp()
    {
        when(applicationBOMock.getRepository("UserBORepository")).thenReturn(userBORepositoryMock);
        when(applicationBOMock.getRepository("CustomerBORepository")).thenReturn(customerBORepositoryMock);
        when(applicationBOMock.getConfiguration()).thenReturn(configurationMock);
        when(customerBORepositoryMock.getCustomerBO(userBOMock)).thenReturn(customerBOMock);
        when(userBORepositoryMock.getUserBOByLogin(LOGIN_OCIUSER)).thenReturn(ociUserBOMock);
        when(ociUserBOMock.getExtension("UserBORoleExtension")).thenReturn(ociUserBORoleExtensionMock);
        when(ociUserBORoleExtensionMock.hasRole(APP_B2B_OCI_USER_ROLE)).thenReturn(Boolean.TRUE);
        when(userBORepositoryMock.getUserBOByLogin(LOGIN_NON_OCIUSER)).thenReturn(userBOMock);
        when(userBOMock.getExtension("UserBORoleExtension")).thenReturn(userBORoleExtensionMock);
        when(userBORoleExtensionMock.hasRole(APP_B2B_OCI_USER_ROLE)).thenReturn(Boolean.FALSE);
        when(configurationMock.getString("intershop.application.login")).thenReturn(B2B_CUSTOMER_TYPE_ID);
        when(customerBOMock.getCustomerType()).thenReturn(customerTypeMock);
        when(customerTypeMock.getCustomerTypeID()).thenReturn(B2B_CUSTOMER_TYPE_ID);
        when(customerBOMock.getExtension("CompanyCustomer")).thenReturn(customerBOCompanyCustomerExtensionMock);
        when(customerBOCompanyCustomerExtensionMock.isActive()).thenReturn(Boolean.TRUE);
        Request request = mockRequest.getRequest();
        when(request.getSession()).thenReturn(mock(Session.class));
        ServletRequest servletRequestMock = mock(ServletRequest.class);
        when(request.getServletRequest()).thenReturn(servletRequestMock);
        Cookie cookieMock = mock(Cookie.class);
        when(cookieMock.getName()).thenReturn("sid");
        when(servletRequestMock.getCookies()).thenReturn(new Cookie[]{cookieMock});
        when(request.getExecutionSite()).thenReturn(site);
        when(request.getLocale()).thenReturn(localeMock);
        when(request.getPipelineDictionary()).thenReturn(pipelineDict);
        
        pipelineDict.put("Password", PASSWORD);
        pipelineDict.put("CurrentOrganization", organizationMock);
        pipelineDict.put("CurrentDomain", site);
        pipelineDict.put("CurrentRequest", request);
        pipelineDict.put("ApplicationBO", applicationBOMock);
        pipelineDict.put("CurrentUser", userMock);
        pipelineDict.put("BasketBORepository", basketBORepositoryMock);
        pipelineDict.put("UserBORepository", userBORepositoryMock);
        pipelineDict.put("Locale", localeMock);
    }
    
    /**
     * Tests if the extension sets an error flag if the user is an OCI user.
     * Calls the extended pipeline where the extension is invoked.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPreventLoginOfOCIUser_CallOfExtendedPipeline()
    {
        pipelineDict.put("ShopLoginForm_Login", LOGIN_OCIUSER);
        pipelineDict.put("Login", LOGIN_OCIUSER);
        pipelineDict.put("ShopLoginForm_Password", PASSWORD);
        final PipelineResult pipelineResult = pipelineRule.getPipelineResult("ViewUserAccount", "ProcessLogin", pipelineDict);
        
        final PipelineDictionary resultDict = pipelineResult.getPipelineDictionary();
        assertThat(resultDict.get("ERROR_User"), is("UnableToFindMembershipData"));
        verify(ociUserBOMock).getExtension("UserBORoleExtension");
        verify(ociUserBORoleExtensionMock).hasRole(APP_B2B_OCI_USER_ROLE);
    }
    
    /**
     * Tests if the extension does not set an error flag if the user is an non OCI user.
     * Calls the extended pipeline where the extension is invoked.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testLoginOfNonOCIUser_CallOfExtendedPipeline()
    {
        pipelineDict.put("ShopLoginForm_Login", LOGIN_NON_OCIUSER);
        pipelineDict.put("Login", LOGIN_NON_OCIUSER);
        pipelineDict.put("ShopLoginForm_Password", PASSWORD);
        final PipelineResult pipelineResult = pipelineRule.getPipelineResult("ViewUserAccount", "ProcessLogin", pipelineDict);
        
        // After traversing all pipeline extensions, the pipeline ends with an exception because it's too 
        // costly to mock all data in order to reach the regular end of the pipeline.
        // This is OK, because we just want to test if PreventPunchoutUserLogin is invoked and does not
        // set an error flag.
        assertTrue(pipelineResult.hasFinishedWithException());
        final PipelineDictionary resultDict = pipelineResult.getPipelineDictionary();
        assertTrue(resultDict.get("ERROR_User") == null);
        verify(userBOMock).getExtension("UserBORoleExtension");
        verify(userBORoleExtensionMock).hasRole(APP_B2B_OCI_USER_ROLE);
    }
    
    /**
     * Calls the extension pipeline and tests if an error flag is set if the user is an OCI user.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPreventLoginOfOCIUser_CallOfExtensionPipeline()
    {
        pipelineDict.put("Login", LOGIN_OCIUSER);
        final PipelineResult pipelineResult = pipelineRule.getPipelineResult("PreventPunchoutUserLogin", "CheckUser", pipelineDict);
        
        assertTrue(pipelineResult.hasFinishedWithEnd());
        assertThat(pipelineResult.getEndName(), is("error"));
        final PipelineDictionary resultDict = pipelineResult.getPipelineDictionary();
        assertTrue(resultDict.get("ERROR_User") != null);
        verify(ociUserBOMock).getExtension("UserBORoleExtension");
        verify(ociUserBORoleExtensionMock).hasRole(APP_B2B_OCI_USER_ROLE);
    }
    
    /**
     * Calls the extension pipeline and tests if an error flag is not set if the user is an non OCI user.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testLoginOfNonOCIUser_CallOfExtensionPipeline()
    {
        pipelineDict.put("Login", LOGIN_NON_OCIUSER);
        final PipelineResult pipelineResult = pipelineRule.getPipelineResult("PreventPunchoutUserLogin", "CheckUser", pipelineDict);
        
        assertTrue(pipelineResult.hasFinishedWithEnd());
        assertThat(pipelineResult.getEndName(), is("next"));
        final PipelineDictionary resultDict = pipelineResult.getPipelineDictionary();
        assertTrue(resultDict.get("ERROR_User") == null);
        verify(userBOMock).getExtension("UserBORoleExtension");
        verify(userBORoleExtensionMock).hasRole(APP_B2B_OCI_USER_ROLE);
    }
}
