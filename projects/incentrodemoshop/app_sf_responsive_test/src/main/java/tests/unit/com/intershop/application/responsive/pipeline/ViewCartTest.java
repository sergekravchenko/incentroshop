package tests.unit.com.intershop.application.responsive.pipeline;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.intershop.beehive.bts.internal.orderprocess.basket.BasketPO;
import com.intershop.beehive.businessobject.capi.BusinessObjectContext;
import com.intershop.beehive.configuration.capi.common.Configuration;
import com.intershop.beehive.core.capi.domain.PersistentObjectBOExtension;
import com.intershop.beehive.core.capi.pipeline.PipelineResult;
import com.intershop.beehive.core.capi.request.Request;
import com.intershop.beehive.core.capi.request.ServletRequest;
import com.intershop.beehive.core.internal.request.SessionImpl;
import com.intershop.beehive.core.internal.request.TransactionSynchronizationHandler;
import com.intershop.beehive.core.request.test.RequestRule;
import com.intershop.component.basket.capi.BasketBO;
import com.intershop.component.basket.capi.BasketBORepository;
import com.intershop.component.organization.capi.Organization;
import com.intershop.component.user.capi.UserBO;
import com.intershop.tools.etest.server.AutoCleanup;
import com.intershop.tools.etest.server.Cartridges;
import com.intershop.tools.etest.server.DomainCreatorRule;
import com.intershop.tools.etest.server.DomainCreatorRule.ForceApplicationContext;
import com.intershop.tools.etest.server.EmbeddedServerRule;
import com.intershop.tools.etest.server.PipelineRule;

@Cartridges({ "app_sf_responsive", "bc_basket", "orm_oracle"})
@AutoCleanup
@ForceApplicationContext
public class ViewCartTest
{    
    private static final String PIPELINE_VIEW_CART = "ViewCart"; 
    private static final String STARTNODE_GET_EXISTING_CART_BO = "GetExistingCartBO";
    
    private static final String PARAM_USER_BO = "UserBO";
    private static final String PARAM_BASKET_BO_REPOSITORY = "BasketBORepository";
    private static final String PARAM_BASKET_BO = "BasketBO";
    private static final String PARAM_ORGANIZATION = "Organization";
    
    private static final String SESSION_PARAM_BASKET_UUIDS = "BasketUUIDs";
    
    private static final String TEST_BASKET_ID = "ViewCartTestBasketID";
    private static final String TEST_BASKET_ID2 = "ViewCartTestBasketID2";
    private static final String TEST_USER_ID = "ViewCartTestUserID";
    private static final String TEST_DOMAIN_ID = "ViewCartTestDomainID";
    private static final String TEST_REQUISITIONTYPE_ID = "10";
    private static final String TEST_SESSION_KEY = TEST_USER_ID + '_' + TEST_DOMAIN_ID + '_' + TEST_REQUISITIONTYPE_ID;
    
    private static final int BASKET_EXPIRED = 4;
    
    @Mock
    private SessionImpl sessionMock;
    @Mock
    private Organization organization;
    @Mock
    private UserBO user;
    @Mock
    private BasketBORepository basketRepository;
    @Mock
    private BusinessObjectContext context;
    @Mock
    private Configuration configuration;
    @Mock
    private Request request;
    
    public RequestRule mockRequest = new RequestRule();
    
    public EmbeddedServerRule server = new EmbeddedServerRule(this);
    
    private PipelineRule pipelineRule = PipelineRule.builder().build();
    private DomainCreatorRule domainCreatorRule = DomainCreatorRule.site().build(this);
    
    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(server).around(domainCreatorRule).around(mockRequest).around(pipelineRule); 

    
    @Before
    public void init()
    {
        MockitoAnnotations.initMocks(this);
        request = mockRequest.create();
        when(request.getSession()).thenReturn(sessionMock);

        when(organization.getDomainID()).thenReturn(TEST_DOMAIN_ID);
        
        when(user.getID()).thenReturn(TEST_USER_ID);
        
        when(sessionMock.getTransactionSynchronizationHandler()).thenReturn(mock(TransactionSynchronizationHandler.class));

        when(basketRepository.getContext()).thenReturn(context);
        when(context.getConfiguration()).thenReturn(configuration);
        when(basketRepository.getRepositoryID()).thenReturn(TEST_DOMAIN_ID);

        when(configuration.getString(eq("intershop.user.cookie.cartCookieName"), any(String.class))).thenReturn("");
        when(configuration.getString(eq("intershop.basket.cookie.path"), any(String.class))).thenReturn("/");
    }
    
    @Test
    public void testGetCurrentCartBOFromSessionDict() throws Exception
    {
        fillSessionDictWithBasketUUID();
        
        when(sessionMock.isLoggedIn()).thenReturn(true);
        
        BasketBO basket = mock(BasketBO.class);
        when(basketRepository.getActiveBasketBO(TEST_BASKET_ID)).thenReturn(basket);
        
        Map<String, Object> dictValues = fillPipelineDictionary(basketRepository, user, organization);
        PipelineResult pipelineResult = pipelineRule.getPipelineResult(PIPELINE_VIEW_CART, STARTNODE_GET_EXISTING_CART_BO, dictValues);
       
        // assert that correct basket is returned
        assertNotNull(pipelineResult);
        BasketBO existingBasket = pipelineResult.getPipelineDictionary().get(PARAM_BASKET_BO);
        assertThat(existingBasket, is(basket));
        
    }

    @Test
    public void testGetCurrentCartBOFromCookie()
    {
        initBasketCookie();

        when(sessionMock.isLoggedIn()).thenReturn(false);
        when(user.isRegistered()).thenReturn(false);

        BasketBO basket = mock(BasketBO.class);
        when(basketRepository.getBasketBO(TEST_BASKET_ID)).thenReturn(basket);

        Map<String, Object> dictValues = fillPipelineDictionary(basketRepository, user, organization);
        PipelineResult pipelineResult = pipelineRule.getPipelineResult(PIPELINE_VIEW_CART, STARTNODE_GET_EXISTING_CART_BO, dictValues);
       
        // assert that correct basket is returned
        assertNotNull(pipelineResult);
        assertThat(pipelineResult.getEndName(), nullValue());
        BasketBO existingBasket = pipelineResult.getPipelineDictionary().get(PARAM_BASKET_BO);
        assertThat(existingBasket, is(basket));
    }

    @Test
    public void testGetCurrentCartBOFromCookieWithRegisteredUser()
    {
        initBasketCookie();

        when(sessionMock.isLoggedIn()).thenReturn(false);
        when(user.isRegistered()).thenReturn(true);

        BasketBO basket = mock(BasketBO.class);
        when(basketRepository.getBasketBO(TEST_BASKET_ID)).thenReturn(basket);

        Map<String, Object> dictValues = fillPipelineDictionary(basketRepository, user, organization);
        PipelineResult pipelineResult = pipelineRule.getPipelineResult(PIPELINE_VIEW_CART, STARTNODE_GET_EXISTING_CART_BO, dictValues);
       
        // assert that correct basket is returned
        assertNotNull(pipelineResult);
        assertThat(pipelineResult.getEndName(), is("NoBasket"));
        BasketBO existingBasket = pipelineResult.getPipelineDictionary().get(PARAM_BASKET_BO);
        assertThat(existingBasket, nullValue());
    }

    @Test
    public void testGetCurrentCartBOFromCookieWithRegisteredBasketUser()
    {
        initBasketCookie();

        when(sessionMock.isLoggedIn()).thenReturn(false);
        when(user.isRegistered()).thenReturn(false);

        BasketBO basket = mock(BasketBO.class);
        when(basketRepository.getBasketBO(TEST_BASKET_ID)).thenReturn(basket);
        UserBO user2 = mock(UserBO.class);
        when(basket.getUserBO()).thenReturn(user2);
        when(user2.isRegistered()).thenReturn(true);

        Map<String, Object> dictValues = fillPipelineDictionary(basketRepository, user, organization);
        PipelineResult pipelineResult = pipelineRule.getPipelineResult(PIPELINE_VIEW_CART, STARTNODE_GET_EXISTING_CART_BO, dictValues);
       
        // assert that correct basket is returned
        assertNotNull(pipelineResult);
        assertThat(pipelineResult.getEndName(), is("NoBasket"));
        BasketBO existingBasket = pipelineResult.getPipelineDictionary().get(PARAM_BASKET_BO);
        assertThat(existingBasket, nullValue());
    }
    
    @Test
    public void testGetCurrentCartBOByUserWithAnonymousUser() throws Exception
    {
        fillSessionDictWithBasketUUID();
        
        when(sessionMock.isLoggedIn()).thenReturn(false);
        
        BasketBO basket = mock(BasketBO.class);
        when(basket.getID()).thenReturn(TEST_BASKET_ID2);
        when(basket.isExpired()).thenReturn(false);
        PersistentObjectBOExtension ext = mock(PersistentObjectBOExtension.class);
        when(basket.getExtension("PersistentObjectBOExtension")).thenReturn(ext);
        when(ext.getPersistentObject()).thenReturn(mock(BasketPO.class));
        when(basketRepository.getActiveBasketBO(TEST_BASKET_ID)).thenReturn(null);       
        when(basketRepository.getActiveBasketBOs(user)).thenReturn((Collection)Arrays.asList(basket));
        
        Map<String, Object> dictValues = fillPipelineDictionary(basketRepository, user, organization);
        PipelineResult pipelineResult = pipelineRule.getPipelineResult(PIPELINE_VIEW_CART, STARTNODE_GET_EXISTING_CART_BO, dictValues);
       
        assertNotNull(pipelineResult);

        // assert that correct basket is returned
        BasketBO existingBasket = pipelineResult.getPipelineDictionary().get(PARAM_BASKET_BO);
        assertThat(existingBasket, is(basket));       
        
        // verify that sessiondict was filled
        Map<String, String> basketUUIDs = new Hashtable();
        basketUUIDs.put(TEST_SESSION_KEY, TEST_BASKET_ID2);        
        verify(sessionMock).putObject(SESSION_PARAM_BASKET_UUIDS, basketUUIDs);
        
        // verify that basket was anonymized
        verify(basket).anonymizeBasket();
    }
    
    @Test
    public void testGetCurrentCartBOByUser() throws Exception
    {
        fillSessionDictWithBasketUUID();
        
        when(sessionMock.isLoggedIn()).thenReturn(true);
        
        BasketBO basket = mock(BasketBO.class);
        when(basket.getID()).thenReturn(TEST_BASKET_ID2);
        when(basketRepository.getActiveBasketBO(TEST_BASKET_ID)).thenReturn(null);       
        when(basketRepository.getActiveBasketBOs(user)).thenReturn((Collection)Arrays.asList(basket));
        
        Map<String, Object> dictValues = fillPipelineDictionary(basketRepository, user, organization);
        PipelineResult pipelineResult = pipelineRule.getPipelineResult(PIPELINE_VIEW_CART, STARTNODE_GET_EXISTING_CART_BO, dictValues);
       
        // assert that correct basket is returned
        assertNotNull(pipelineResult);
        BasketBO existingBasket = pipelineResult.getPipelineDictionary().get(PARAM_BASKET_BO);
        assertThat(existingBasket, is(basket));
        
        // verify that sessiondict was filled
        Map<String, String> basketUUIDs = new Hashtable();        
        basketUUIDs.put(TEST_SESSION_KEY, TEST_BASKET_ID2);        
        verify(sessionMock).putObject(SESSION_PARAM_BASKET_UUIDS, basketUUIDs);
    }
    
    @Test
    public void testGetCurrentCartBONotAvailable() throws Exception
    {
        fillSessionDictWithBasketUUID();
        
        when(sessionMock.isLoggedIn()).thenReturn(true);
        
        when(basketRepository.getActiveBasketBO(TEST_BASKET_ID)).thenReturn(null);       
        when(basketRepository.getActiveBasketBOs(user)).thenReturn(Collections.emptyList());

        Map<String, Object> dictValues = fillPipelineDictionary(basketRepository, user, organization);
        PipelineResult pipelineResult = pipelineRule.getPipelineResult(PIPELINE_VIEW_CART, STARTNODE_GET_EXISTING_CART_BO, dictValues);
       
        // assert that pipeline ended in correct end node
        assertNotNull(pipelineResult);
        assertThat(pipelineResult.getEndName(), is("NoBasket"));
        
        // verify that sessiondict was filled
        // Map<String, String> basketUUIDs = new Hashtable();
        // basketUUIDs.put(TEST_SESSION_KEY, "");        
        // verify(sessionMock).putObject(SESSION_PARAM_BASKET_UUIDS, basketUUIDs);
    }
    
    @Test
    public void testGetCurrentCartBOIsExpired() throws Exception
    {
        fillSessionDictWithBasketUUID();
        
        when(sessionMock.isLoggedIn()).thenReturn(true);
        
        BasketBO basket = mock(BasketBO.class);
        when(basket.isExpired()).thenReturn(true);
        PersistentObjectBOExtension ext = mock(PersistentObjectBOExtension.class);
        when(basket.getExtension("PersistentObjectBOExtension")).thenReturn(ext);
        when(ext.getPersistentObject()).thenReturn(mock(BasketPO.class));
        when(basketRepository.getActiveBasketBO(TEST_BASKET_ID)).thenReturn(basket);
        when(basketRepository.getActiveBasketBOs(user)).thenReturn(Collections.emptyList());

        Map<String, Object> dictValues = fillPipelineDictionary(basketRepository, user, organization);
        PipelineResult pipelineResult = pipelineRule.getPipelineResult(PIPELINE_VIEW_CART, STARTNODE_GET_EXISTING_CART_BO, dictValues);
       
        // assert that pipeline ended in correct end node
        assertNotNull(pipelineResult);
        assertThat(pipelineResult.getEndName(), is("NoBasket"));
        
        // verify move to history was called
         verify(basketRepository).moveBasketToBasketHistory(basket, BASKET_EXPIRED);
        
        // verify that sessiondict was filled
        // Map<String, String> basketUUIDs = new Hashtable();
        // basketUUIDs.put(TEST_SESSION_KEY, "");        
        // verify(sessionMock).putObject(SESSION_PARAM_BASKET_UUIDS, basketUUIDs); 
    }

    
    private void fillSessionDictWithBasketUUID()
    {
        Map<String, String> basketUUIDs = new HashMap<>();
        basketUUIDs.put(TEST_SESSION_KEY, TEST_BASKET_ID);
        when(sessionMock.getObject(SESSION_PARAM_BASKET_UUIDS)).thenReturn(basketUUIDs);
    }
    
    
    private Map<String, Object> fillPipelineDictionary(BasketBORepository basketBORepository, UserBO user, Organization organization)
    {
        Map<String, Object> dictValues = new HashMap<>();
        dictValues.put(PARAM_BASKET_BO_REPOSITORY, basketBORepository);
        dictValues.put(PARAM_USER_BO, user);
        dictValues.put(PARAM_ORGANIZATION, organization);
        return dictValues;
    }

    private void initBasketCookie()
    {
        when(configuration.getString(eq("BasketExpirationType"), any(String.class))).thenReturn("Time");
        ServletRequest servletRequest = mock(ServletRequest.class);
        when(request.getServletRequest()).thenReturn(servletRequest);
        Cookie[] cookies = new Cookie[1];
        when(servletRequest.getCookies()).thenReturn(cookies);
        Cookie cookie = new Cookie(TEST_DOMAIN_ID, TEST_BASKET_ID);
        cookies[0] = cookie;
    }
}
