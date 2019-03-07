package tests.embedded.com.intershop.application.responsive.punchout.outbound;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.Configuration;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.Inject;
import com.intershop.beehive.core.capi.domain.Domain;
import com.intershop.beehive.core.capi.localization.LocaleInformation;
import com.intershop.beehive.core.capi.localization.LocaleMgr;
import com.intershop.beehive.core.capi.pipeline.PipelineResult;
import com.intershop.beehive.core.capi.request.Request;
import com.intershop.beehive.core.capi.request.RequestInformation;
import com.intershop.beehive.core.capi.user.User;
import com.intershop.beehive.core.capi.util.UUIDGenerator;
import com.intershop.beehive.core.internal.request.SessionImpl;
import com.intershop.beehive.core.internal.request.TransactionSynchronizationHandler;
import com.intershop.beehive.core.internal.security.JAASConfiguration;
import com.intershop.beehive.core.request.test.RequestRule;
import com.intershop.component.application.capi.ApplicationBO;
import com.intershop.component.application.capi.CurrentApplicationBOProvider;
import com.intershop.component.basket.capi.BasketBO;
import com.intershop.component.organization.capi.Organization;
import com.intershop.component.organization.capi.OrganizationStructureMgr;
import com.intershop.component.punchout.capi.PunchoutRequestInfo;
import com.intershop.component.user.capi.UserBO;
import com.intershop.tools.etest.server.AutoCleanup;
import com.intershop.tools.etest.server.Cartridges;
import com.intershop.tools.etest.server.DomainCreatorRule;
import com.intershop.tools.etest.server.DomainCreatorRule.Create;
import com.intershop.tools.etest.server.DomainCreatorRule.ForceApplicationContext;
import com.intershop.tools.etest.server.EmbeddedServerRule;
import com.intershop.tools.etest.server.PipelineRule;

@Cartridges("app_sf_responsive_b2b")
@AutoCleanup
@ForceApplicationContext
public class PunchoutStorefrontAccessibilityTest
{
    public EmbeddedServerRule server = new EmbeddedServerRule(this);
    public PipelineRule pipelineRule = PipelineRule.builder().build();
    private DomainCreatorRule domainCreatorRule = DomainCreatorRule.site().build(this);
    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(server).around(domainCreatorRule).around(pipelineRule);
    
    private Map<String, Object> dictionary;
    
    @Mock
    private Organization organizationMock;
    
    @Mock
    private UserBO userBO;
    
    @Mock
    private SessionImpl sessionMock;

    @Rule 
    public RequestRule requestRule = new RequestRule();
    
    public Request requestMock;
    
    @Inject
    private CurrentApplicationBOProvider applicationBOProvider;
    
    @Inject
    private LocaleMgr localeMgr;
    
    private LocaleInformation userLocaleInformation;
    
    private ApplicationBO applicationBO;
    
    @Inject
    private OrganizationStructureMgr organizationMgr;
    
    private Organization currentOrganization;
    
    @Create
    private Domain site;

    @Inject
    private UUIDGenerator uuidGenerator;
    
    private PunchoutRequestInfo punchoutRequest;
    
    private static final String CURRENT_DOMAIN_NAME = "inSPIRED-inTRONICS_Business";
    private static final String CURRENT_ORGANIZATION_ID = CURRENT_DOMAIN_NAME + "-Anonymous";
    private static final String HTTP_PARAMETER_FUNCTION = "FUNCTION";
    private static final String OCI_FUNCTION_DETAIL = "DETAIL";
    private static final String HTTP_PARAMETER_PRODUCTID = "PRODUCTID";
    private static final String OCI_FUNCTION_PARAMETER_PRODUCTID = "0027242869158";
    private static final String HTTP_PARAMETER_LOGIN = "LOGIN";
    private static final String HTTP_PARAMETER_PASSWORD = "PASSWORD";
    private static final String HTTP_PARAMETER_HOOK_URL = "HOOK_URL";
    private static final String HTTP_OCI_VERSION = "OCI_VERSION";
    private static final String OCI_PARAMETER_VERSION = "4.0";
    private static final String OCI_TEST_LOGIN = "ociuser@test.intershop.de";
    private static final String OCI_TEST_PASSWORD = "!InterShop00!";
    private static final String OCI_TEST_HOOK_URL = "www.mydomain.com";
    private static final String USER_LOCALEID = "en_US";
    private static final String REQUEST_CURRENCY_CODE = "USD";
    private static final String TEST_PIPELINE = "ProcessOCICatalog";
    private static final String START_NODE_LOGIN_USER = "LoginUser";
    private static final String START_NODE_LOGIN = "Login";
    
    @SuppressWarnings("deprecation")
    @Before
    public void setUp()
    {
        Configuration.setConfiguration(new JAASConfiguration(Configuration.getConfiguration()));
        userLocaleInformation = localeMgr.getLocaleBy(USER_LOCALEID);
        currentOrganization = organizationMgr.getOrganizationByID(CURRENT_ORGANIZATION_ID);
        applicationBO = applicationBOProvider.get();
        // initializes the class variable 'punchoutRequest' with test data
        punchoutRequest = setupPunchoutRequest();
        // create anonymous CurrentUser for default pipeline dictionary values
        User user = new User(uuidGenerator.createUUIDString());
        MockitoAnnotations.initMocks(this);
        // request and session methods mock
        requestMock = requestRule.create();
        when(sessionMock.getUser()).thenReturn(user);
        when(sessionMock.getTransactionSynchronizationHandler())
                        .thenReturn(mock(TransactionSynchronizationHandler.class));
        // getLocale for session is 'deprecated' but used in a sub-pipeline call
        when(sessionMock.getLocale()).thenReturn(userLocaleInformation);
        // request methods mock
        when(requestMock.getObject(RequestInformation.SCHEME)).thenReturn("https");
        when(requestMock.getSession()).thenReturn(sessionMock);
        when(requestMock.getExecutionSite()).thenReturn(site.getDomain());
        when(requestMock.getMode()).thenReturn(1); // Session.TRANSIENT (=1)
        when(requestMock.getCurrencyCode()).thenReturn(REQUEST_CURRENCY_CODE);        
        // request LocaleInformation - 'en_US'
        when(requestMock.getLocale()).thenReturn(userLocaleInformation);
        // dictionary parameter required for all tests
        dictionary = new HashMap<>();
        dictionary.put("ApplicationBO", applicationBO);
        dictionary.put("CurrentRequest", requestMock);
        dictionary.put("CurrentOrganization", currentOrganization);
        dictionary.put("CurrentDomain", site.getDomain());
        dictionary.put("Locale", userLocaleInformation);
        dictionary.put("PunchoutRequest", punchoutRequest);
    }

    /**
     * a test to check the demo data oci-user exists in trying to log in as oci-user
     */
    @Test
    @ForceApplicationContext
    public void loginOCIUser() 
    {
        dictionary.put("Login", OCI_TEST_LOGIN);
        dictionary.put("Password", OCI_TEST_PASSWORD);
        PipelineResult pipelineResult = pipelineRule.getPipelineResult(TEST_PIPELINE, START_NODE_LOGIN_USER, dictionary);
        // basic pipeline result checks
        assertNotNull("Login failed for OCI User. pipelineResult is null", pipelineResult);
        assertTrue("Login failed for OCI User.", pipelineResult.hasFinishedWithEnd());
        // test specific pipeline result checks
        User currentUser = (User)pipelineResult.getPipelineDictionary().get("CurrentUser");
        assertNotNull("CurrentUser does not exist!", currentUser);
        assertNotNull("CurrentUser does not have a profile!", currentUser.getProfile());
        assertTrue("CurrentUser.Profile.Credentials does not have the right login!",
                        currentUser.getProfile().getCredentials().getLogin().equals(OCI_TEST_LOGIN));
    }

    /**
     * runs the full login user and create new user specific basket
     */
    @Test
    @ForceApplicationContext
    public void login() 
    {
        // set up required parameters to do the 'ProcessOCICatalog-Login' test
        setupLoginTest(); 
        // execute the 'ProcessOCICatalog-Login' test
        runLoginTest();
    }

    /**
     * logs-in the 'OCI User' twice <br>
     *  each time a new basket is created <br>
     *  this test verifies we got individual baskets per login
     */
    @Test
    @ForceApplicationContext
    public void checkIndividualBasketsPerOCIUser() 
    {
        // set up required parameters to do the 'ProcessOCICatalog-Login' test
        setupLoginTest(); 
        // execute the 'ProcessOCICatalog-Login' 1st test; check is basket exists internally
        BasketBO theFirstBasketBO = runLoginTest();
        // execute the 'ProcessOCICatalog-Login' 2nd test; check is basket exists internally
        BasketBO theSecondBasketBO = runLoginTest();

        // check Basket(ID) is new(different)!!!
        assertTrue("Baskets of two 'OCI User' logins are NOT different!", !theFirstBasketBO.getID().equals(theSecondBasketBO.getID()));
    }
    /**
     * used to initialize the class variable 'punchoutRequest'
     * @return PunchoutRequestInfo initialized with test data
     */
    private PunchoutRequestInfo setupPunchoutRequest()
    {
        HashMap<String, String> punchoutRequestDictFormParameter = new HashMap<>();
        punchoutRequestDictFormParameter.put(HTTP_PARAMETER_LOGIN, OCI_TEST_LOGIN);
        punchoutRequestDictFormParameter.put(HTTP_PARAMETER_PASSWORD, OCI_TEST_PASSWORD);
        punchoutRequestDictFormParameter.put(HTTP_PARAMETER_HOOK_URL, OCI_TEST_HOOK_URL);
        punchoutRequestDictFormParameter.put(HTTP_PARAMETER_FUNCTION, OCI_FUNCTION_DETAIL);
        punchoutRequestDictFormParameter.put(HTTP_PARAMETER_PRODUCTID, OCI_FUNCTION_PARAMETER_PRODUCTID);
        punchoutRequestDictFormParameter.put(HTTP_OCI_VERSION, OCI_PARAMETER_VERSION);
        
        PunchoutRequestInfo localPunchoutRequest = new PunchoutRequestInfo(punchoutRequestDictFormParameter);
        localPunchoutRequest.setLogin(OCI_TEST_LOGIN);
        localPunchoutRequest.setPassword(OCI_TEST_PASSWORD);
        localPunchoutRequest.setReturnURL(OCI_TEST_HOOK_URL);
        localPunchoutRequest.setFunction(OCI_FUNCTION_DETAIL);
        localPunchoutRequest.setProductId(OCI_FUNCTION_PARAMETER_PRODUCTID);
        
        return localPunchoutRequest;
    }
    /**
     * setup additional PD values to run the 'ProcessOCICatalog-Login' test
     */
    private void setupLoginTest()
    {
        dictionary.put("USERNAME", OCI_TEST_LOGIN);
        dictionary.put("PASSWORD", OCI_TEST_PASSWORD);
        dictionary.put("HOOK_URL", OCI_TEST_HOOK_URL);
        dictionary.put("CurrentSession", sessionMock);
    }
    
    /**
     * run the 'ProcessOCICatalog-Login' test
     * @return BasketBO - the basket created for the OCI User
     */
    private BasketBO runLoginTest()
    {
        PipelineResult pipelineResult = pipelineRule.getPipelineResult(TEST_PIPELINE, START_NODE_LOGIN, dictionary);
        // basic pipeline result checks
        assertNotNull("Login 'OCI User' and getting a new basket failed. pipelineResult is null", pipelineResult);
        assertTrue("Login 'OCI User' and getting a new basket failed.", pipelineResult.hasFinishedWithEnd());
        // test specific pipeline result checks
        BasketBO theBasketBO = pipelineResult.getPipelineDictionary().get("BasketBO");
        assertNotNull("BasketBO does not exist!", theBasketBO);
        PunchoutRequestInfo thePunchoutRequest = pipelineResult.getPipelineDictionary().get("PunchoutRequest");
        assertNotNull("PunchoutRequest does not exist!", thePunchoutRequest);
        assertNotNull("PunchoutRequest has NO HOOK_URL!", thePunchoutRequest.getReturnURL());
        assertTrue("PunchoutRequest has wrong HOOK_URL!", thePunchoutRequest.getReturnURL().equals(OCI_TEST_HOOK_URL));
        assertTrue("PunchoutRequest has wrong password!", thePunchoutRequest.getPassword().equals(OCI_TEST_PASSWORD));
        assertTrue("PunchoutRequest has wrong login!", thePunchoutRequest.getLogin().equals(OCI_TEST_LOGIN));
        assertTrue("PunchoutRequest has wrong function!", thePunchoutRequest.getFunction().equals(OCI_FUNCTION_DETAIL));
        // checks the map of all url parameters received on initial 'OCI catalog' request
        assertNotNull("PunchoutRequest is missing common URL parameters!",thePunchoutRequest.getParameters());
        assertNotNull("PunchoutRequest common URL parameter 'OCI_VERSION' is 'null'!",thePunchoutRequest.getParameter(HTTP_OCI_VERSION));
        assertTrue("PunchoutRequest common URL parameter 'OCI_VERSION' is wrong!",
                        thePunchoutRequest.getParameter(HTTP_OCI_VERSION).equals(OCI_PARAMETER_VERSION));
        
        return theBasketBO;
    }
}
