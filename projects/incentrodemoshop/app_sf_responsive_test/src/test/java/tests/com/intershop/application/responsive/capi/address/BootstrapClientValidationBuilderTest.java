package tests.com.intershop.application.responsive.capi.address;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mock;

import com.google.inject.Inject;
import com.intershop.beehive.core.capi.domain.Domain;
import com.intershop.beehive.core.capi.domain.DomainMgr;
import com.intershop.beehive.core.capi.webform.Form;
import com.intershop.beehive.core.capi.webform.FormField;
import com.intershop.beehive.core.capi.webform.FormMgr;
import com.intershop.tools.etest.server.Cartridges;
import com.intershop.tools.etest.server.DomainCreatorRule;
import com.intershop.tools.etest.server.DomainCreatorRule.ForceApplicationContext;
import com.intershop.tools.etest.server.EmbeddedServerRule;
import com.intershop.tools.etest.server.PipelineRule;

@Ignore
@Cartridges({ "app_sf_responsive"})
@ForceApplicationContext
public class BootstrapClientValidationBuilderTest
{
    public EmbeddedServerRule server = new EmbeddedServerRule(this);
    
    private PipelineRule pipelineRule = PipelineRule.builder().build();
    private DomainCreatorRule domainCreatorRule = DomainCreatorRule.site().build(this);
    
    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(server).around(domainCreatorRule).around(pipelineRule);

    @Mock
    private FormField postalCodeField;
    
    private static String formResource="AddressUS";
    private static String formName="address";
    
    @Inject
    private FormMgr formMgr;
    
    @Inject
    private DomainMgr domainMgr;
    
    @Before
    public void init()
    {   
        postalCodeField = mock(FormField.class);
        when(postalCodeField.getID()).thenReturn("postal_code");
        
    }
    
    @Test
    public void testGetClientValidationConfig(){
        
        Domain inspiredSite = domainMgr.getDomainByName("inSPIRED-inTRONICS-Site");
        Form form = formMgr.createForm(inspiredSite, formResource, formName);
        
        //BootstrapClientValidationBuilder validationBuilder = new BootstrapClientValidationBuilder();
        //String clientString = validationBuilder.getClientString(form);
        
        assertNull(form.getParameter("CountryCode").getClientValidationConfig("html5"));
        assertNotNull(form.getParameter("PostalCode").getClientValidationConfig("html5"));
        
        
    }
    
}
