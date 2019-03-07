package tests.embedded.com.intershop.responsive.quickorder;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.intershop.beehive.core.capi.pipeline.PipelineResult;
import com.intershop.component.application.capi.ApplicationBO;
import com.intershop.component.product.capi.ProductBO;
import com.intershop.component.product.capi.ProductBORepository;
import com.intershop.tools.etest.server.AutoCleanup;
import com.intershop.tools.etest.server.Cartridges;
import com.intershop.tools.etest.server.DomainCreatorRule;
import com.intershop.tools.etest.server.DomainCreatorRule.ForceApplicationContext;
import com.intershop.tools.etest.server.EmbeddedServerRule;
import com.intershop.tools.etest.server.PipelineRule;

@Cartridges("app_sf_responsive_b2b")
@AutoCleanup
@ForceApplicationContext
public class ProcessQuickorderTest
{
    public EmbeddedServerRule server = new EmbeddedServerRule(this);
    public PipelineRule pipelineRule = PipelineRule.builder().build();
    private DomainCreatorRule domainCreatorRule = DomainCreatorRule.site().build(this);
    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(server).around(domainCreatorRule).around(pipelineRule);
    
    private Map<String, Object> dictionary = new HashMap<>();
    
    @Mock
    private ApplicationBO applicationBoMock;
    
    @Mock
    private ProductBO productBoMock;
    
    @Mock
    private ProductBORepository productBoRepoMock;
    
    private static final String PIPELINE = "ProcessQuickorder";
    
    private static final String START_NODE = "CheckProductAvailable";
    
    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        when(applicationBoMock.getRepository("ProductBORepository")).thenReturn(productBoRepoMock);
       
        when(productBoRepoMock.getProductBOBySKU("SKU")).thenReturn(productBoMock);
        
        dictionary.put("ApplicationBO", applicationBoMock);
        dictionary.put("SKU", "SKU");
    }
    
    @Test
    public void testCheckProductAvailable_ProductNotAvailable()
    {
        when(productBoMock.isAvailable()).thenReturn(true);
        PipelineResult pipelineResult = pipelineRule.getPipelineResult(PIPELINE, START_NODE, dictionary);
        
        assertThat(pipelineResult.getEndName(), is("AVAILABLE"));
    }

    @Test
    public void testCheckProductNotAvailable()
    {
        when(productBoRepoMock.getProductBOBySKU("SKU")).thenReturn(null);
        PipelineResult pipelineResult = pipelineRule.getPipelineResult(PIPELINE, START_NODE, dictionary);
        
        assertThat(pipelineResult.getEndName(), is("NOT_AVAILABLE"));
    }

    @Test
    public void testCheckProductAvailable_ProductAvailable()
    {
        when(productBoMock.isAvailable()).thenReturn(false);
        PipelineResult pipelineResult = pipelineRule.getPipelineResult(PIPELINE, START_NODE, dictionary);
        
        assertThat(pipelineResult.getEndName(), is("NOT_AVAILABLE"));
    }


}
