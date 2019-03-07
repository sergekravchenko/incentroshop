package tests.unit.com.intershop.component.pipeline;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.intershop.beehive.component.service.test.ServiceConfigurationBORule;
import com.intershop.beehive.core.capi.pipeline.PipelineResult;
import com.intershop.beehive.core.capi.user.User;
import com.intershop.tools.etest.server.AutoCleanup;
import com.intershop.tools.etest.server.Cartridges;
import com.intershop.tools.etest.server.DomainCreatorRule;
import com.intershop.tools.etest.server.DomainCreatorRule.ForceApplicationContext;
import com.intershop.tools.etest.server.EmbeddedServerRule;
import com.intershop.tools.etest.server.PipelineRule;

@Cartridges({ "app_sf_responsive_b2b" })
@AutoCleanup
@RunWith(MockitoJUnitRunner.class)
@ForceApplicationContext
public class ProcessQuoteTest
{
    private static final String PARAM_CURRENT_USER = "CurrentUser";

    private EmbeddedServerRule server = new EmbeddedServerRule(this);
    private PipelineRule pipeline = PipelineRule.builder().build();
    private ServiceConfigurationBORule taxationService = ServiceConfigurationBORule
                    .builder("Product_VAT_Inclusive_Taxation_Services", "ac_taxation_std").build();
    private DomainCreatorRule domainCreatorRule = DomainCreatorRule.site().build(this);

    @Mock
    private User user;
    
    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(server).around(domainCreatorRule).around(pipeline).around(taxationService);
    
    @Test
    @ForceApplicationContext
    public void testCustomerWithoutProfile()
    {
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put(PARAM_CURRENT_USER, user);
        
        PipelineResult result = pipeline.getPipelineResult("ProcessQuote", "GetQuoteStatesCount", dictionary);
        assertThat(result.getEndName(), is("ERROR"));
    }
}
