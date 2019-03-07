package tests.embedded.com.intershop.application.responsive.category;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.intershop.beehive.core.capi.pipeline.PipelineResult;
import com.intershop.component.catalog.capi.CatalogCategoryBO;
import com.intershop.component.product.capi.ProductBO;
import com.intershop.tools.etest.server.Cartridges;
import com.intershop.tools.etest.server.DomainCreatorRule;
import com.intershop.tools.etest.server.DomainCreatorRule.ForceApplicationContext;
import com.intershop.tools.etest.server.EmbeddedServerRule;
import com.intershop.tools.etest.server.PipelineRule;

@Cartridges({"app_sf_responsive", "orm_oracle"})
@ForceApplicationContext
public class CategoryAccessibilityTest
{
    public EmbeddedServerRule server = new EmbeddedServerRule(this);
    
    public PipelineRule pipelineRule = PipelineRule.builder().build();
    private DomainCreatorRule domainCreatorRule = DomainCreatorRule.site().build(this);
    
    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(server).around(domainCreatorRule).around(pipelineRule);
    
    @Mock
    private ProductBO productBO;
    
    @Mock
    private CatalogCategoryBO category1;
    
    @Mock
    private CatalogCategoryBO category2;
    
    @Mock
    private CatalogCategoryBO category3;
    
    private Map<String, Object> dictionary;
    
    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        dictionary = new HashMap<>();
    }
    
    @Test
    @ForceApplicationContext
    public void testGetFirstAccessibleCatalogCategoryBO()
    {
        when(category1.isAccessible()).thenReturn(true);
        when(category2.isAccessible()).thenReturn(false);
        when(category3.isAccessible()).thenReturn(true);
        
        List<CatalogCategoryBO> categories = Arrays.asList(new CatalogCategoryBO[] { category1, category2, category3 });
        when(productBO.getCatalogCategoryBOs()).thenReturn(categories);
        
        dictionary.put("ProductBO", productBO);
        PipelineResult pipelineResult = pipelineRule.getPipelineResult("ViewProduct", "GetFirstAccessibleCatalogCategoryBO", dictionary);
        
        Object resultCategoryBO = pipelineResult.getPipelineDictionary().get("CategoryBO");
        assertNotNull(resultCategoryBO);
        assertSame(category1, resultCategoryBO);
    }
}
