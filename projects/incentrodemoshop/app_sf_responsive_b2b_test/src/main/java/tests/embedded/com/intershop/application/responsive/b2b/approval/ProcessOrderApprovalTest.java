package tests.embedded.com.intershop.application.responsive.b2b.approval;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.intershop.beehive.core.capi.pipeline.PipelineResult;
import com.intershop.component.basket.capi.BasketBO;
import com.intershop.component.user.capi.UserBO;
import com.intershop.tools.etest.server.Cartridges;
import com.intershop.tools.etest.server.DomainCreatorRule;
import com.intershop.tools.etest.server.DomainCreatorRule.ForceApplicationContext;
import com.intershop.tools.etest.server.EmbeddedServerRule;
import com.intershop.tools.etest.server.PipelineRule;

@Cartridges({"app_sf_responsive_b2b"})
@ForceApplicationContext
public class ProcessOrderApprovalTest
{
    private final static String PIPELINE_NAME = "ProcessOrderApproval";
    
    @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule();
    
    private EmbeddedServerRule server = new EmbeddedServerRule(this);
    private PipelineRule pipelineRule = PipelineRule.builder().build();
    private DomainCreatorRule domainCreatorRule = DomainCreatorRule.site().build(this);
    
    @Rule public final RuleChain ruleChain = RuleChain.outerRule(server).around(domainCreatorRule).around(pipelineRule);
    
    @Mock private UserBO userBO;
    
    @Mock private BasketBO basketBO;
    
    @Test
    public void testIsOrderApprovalRequiredWithoutCustomer() throws Exception
    {
        Map<String, Object> dictValues = new HashMap<>();
        dictValues.put("UserBO", userBO);
        dictValues.put("BasketBO", basketBO);
        
        PipelineResult result = pipelineRule.getPipelineResult(PIPELINE_NAME, "IsOrderApprovalRequired", dictValues);
        
        assertEquals("Unexpected end node name.", "false", result.getEndName());
    }
}
