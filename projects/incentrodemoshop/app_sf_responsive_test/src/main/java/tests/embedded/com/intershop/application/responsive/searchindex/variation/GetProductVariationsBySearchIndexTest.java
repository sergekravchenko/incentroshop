package tests.embedded.com.intershop.application.responsive.searchindex.variation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.google.inject.Inject;
import com.intershop.beehive.core.capi.common.SystemException;
import com.intershop.beehive.core.capi.domain.Domain;
import com.intershop.beehive.core.capi.pipeline.PipelineResult;
import com.intershop.beehive.xcs.capi.product.ProductConstants;
import com.intershop.beehive.xcs.capi.productvariation.ConflictingAttributeNamesException;
import com.intershop.beehive.xcs.capi.productvariation.ProductVariationException;
import com.intershop.beehive.xcs.capi.productvariation.ProductVariationMgr;
import com.intershop.beehive.xcs.internal.product.ProductPO;
import com.intershop.beehive.xcs.internal.product.ProductPOFactory;
import com.intershop.component.search.capi.Attribute;
import com.intershop.component.search.capi.FilterAttribute;
import com.intershop.tools.etest.server.Cartridges;
import com.intershop.tools.etest.server.DomainCreatorRule;
import com.intershop.tools.etest.server.DomainCreatorRule.Create;
import com.intershop.tools.etest.server.DomainCreatorRule.ForceApplicationContext;
import com.intershop.tools.etest.server.EmbeddedServerRule;
import com.intershop.tools.etest.server.PipelineRule;

/**
 * Tests GetProductVariationsBySearchIndex pipeline
 */
@Ignore
@Cartridges("app_sf_responsive")
@ForceApplicationContext
public class GetProductVariationsBySearchIndexTest
{
    private final static String PIPELINE_NAME = "GetProductVariationsBySearchIndex";
    private final static String HARD_DISC_DRIVE_CAPACITY = "Hard_disk_drive_capacity";
    private final static String MASTER_UUID = "MasterUUID";
    private final static String FILTER_ENTRIES = "FilterEntries";
    
    @Rule 
    public final MockitoRule mockitoRule = MockitoJUnit.rule();
    
    public EmbeddedServerRule server = new EmbeddedServerRule(this);
    
    private DomainCreatorRule dc = DomainCreatorRule.site().build(this);
    
    private PipelineRule pipelineRule = PipelineRule.builder().build();
    
    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(server).around(dc).around(pipelineRule);
    
    @Create
    private Domain domain;
    
    @Inject
    public ProductPOFactory productPOFactory;
    
    @Inject
    public ProductVariationMgr variationMgr;
    
    @Mock
    private FilterAttribute filterAttribute1;
    
    @Mock
    private FilterAttribute filterAttribute2;
    
    @Mock
    private Attribute attribute1;
    
    @Mock
    private Attribute attribute2;
    
    private ProductPO master, variation1, variation2;
    
    private List<FilterAttribute> filterEntries = new ArrayList<>();
    
    private Map<String, Object> dictValues = new HashMap<>();
    
    @Before
    public void setup() throws SystemException, ConflictingAttributeNamesException, ProductVariationException
    {
        server.beginTransaction();
        master = productPOFactory.create(domain);
        master.setTypeCode(ProductConstants.MASTER);
        
        variationMgr.createVariableVariationAttribute(
                        master.getDomain(),
                        HARD_DISC_DRIVE_CAPACITY,
                        false,
                        0,
                        master);
        
        variation1 = productPOFactory.create(domain);
        variation2 = productPOFactory.create(domain);
        variation1.putString(HARD_DISC_DRIVE_CAPACITY, "500GB");
        variation2.putString(HARD_DISC_DRIVE_CAPACITY, "750GB");
        variationMgr.addProduct(master, variation1);
        variationMgr.addProduct(master, variation2);
        server.storeTransaction();
        
        filterEntries.add(filterAttribute1);
        filterEntries.add(filterAttribute2);
        when(filterAttribute1.getConfigurationAttribute()).thenReturn(attribute1);
        when(filterAttribute2.getConfigurationAttribute()).thenReturn(attribute2);
        when(attribute1.getName()).thenReturn("Test1");

        dictValues.put(MASTER_UUID, master.getUUID());
        dictValues.put(FILTER_ENTRIES, filterEntries.iterator());
    }
    
    @Test
    public void testFilterFilterEntriesByVariationAttributesWithNoMatchingAttributeNames()
    {
        System.out.println();
        when(attribute2.getName()).thenReturn("Test2");
        
        PipelineResult result = pipelineRule.getPipelineResult(PIPELINE_NAME, "FilterFilterEntriesByVariationAttributes", dictValues);
        Iterator<FilterAttribute> resultFilterEntries = result.getPipelineDictionary().get(FILTER_ENTRIES);
        assertFalse(resultFilterEntries.hasNext());
    }
    
    @Test
    public void testFilterFilterEntriesByVariationAttributesWithMatchingAttributeNames()
    {
        System.out.println();
        when(attribute2.getName()).thenReturn(HARD_DISC_DRIVE_CAPACITY);
        
        PipelineResult result = pipelineRule.getPipelineResult(PIPELINE_NAME, "FilterFilterEntriesByVariationAttributes", dictValues);
        Iterator<FilterAttribute> resultFilterEntries = result.getPipelineDictionary().get(FILTER_ENTRIES);
        assertTrue(resultFilterEntries.hasNext());
        assertTrue(HARD_DISC_DRIVE_CAPACITY.equals(resultFilterEntries.next().getConfigurationAttribute().getName()));
    }
    
    @After
    public void tearDown()
    {
        server.rollbackTransaction();
    }
}
