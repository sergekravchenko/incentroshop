package tests.embedded.com.intershop.urlrewriting;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.intershop.beehive.core.capi.domain.Application;
import com.intershop.beehive.core.capi.domain.Domain;
import com.intershop.beehive.core.capi.file.FileTools;
import com.intershop.beehive.core.internal.file.StaticFileMgr;
import com.intershop.beehive.core.internal.urlrewrite.RuleParseException;
import com.intershop.beehive.core.internal.urlrewrite.RuleSet;
import com.intershop.beehive.core.internal.urlrewrite.RuleSetLoadingStrategy;
import com.intershop.beehive.core.internal.urlrewrite.URLContainer;
import com.intershop.tools.etest.server.AutoCleanup;
import com.intershop.tools.etest.server.Cartridges;
import com.intershop.tools.etest.server.DomainCreatorRule;
import com.intershop.tools.etest.server.EmbeddedServerRule;


/**
 * This is a test for the old URL rewriting handler which uses the regular expressions in urlrewrite.properties. <br>
 * It is a test for bug fix IS-19417.
 */
@Cartridges({"app_sf_responsive", "orm_oracle"})
@AutoCleanup
public class UrlRewritingTest
{
    public EmbeddedServerRule server = new EmbeddedServerRule(this);
    
    private DomainCreatorRule dc = DomainCreatorRule.site().build(this);
    
    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(server).around(dc);
    
    @Inject
    private StaticFileMgr staticFileMgr;
    
    @Mock
    private Application applicationMock;
    
    @Mock
    private Domain domainMock;
    
    @Mock
    private RuleSetLoadingStrategy ruleSetLoadingStrategyMock;
    
    private RuleSet ruleSet;
    
    @Before
    public void setUp() throws FileNotFoundException, IOException, RuleParseException
    {
        MockitoAnnotations.initMocks(this);
        
        when(applicationMock.getSite()).thenReturn(domainMock);
        when(domainMock.getDomainName()).thenReturn("domainname");
        
        File staticDir = new File(FileTools.adjustSeparator(staticFileMgr.getStaticFilesDirectory("app_sf_responsive"))); 
        Path staticPath = staticDir.toPath();
        // Directory 'urlrewrite' is a sibling of directory 'static'
        Path urlrewriteDir = staticPath.resolveSibling("urlrewrite");
        File rulesPropsFile = new File(urlrewriteDir.toFile(), "urlrewrite.properties");
        ruleSet = new RuleSet(applicationMock);
        // read rewrite rules properties file
        ruleSet.refresh(Arrays.asList(rulesPropsFile.getAbsolutePath()));
    }
    
    /**
     * Test expand rule for level 2: <br>
     * Level 1 is Cameras-Camcorders ($1 in rule -> CatalogID=$1) <br>
     * Level 2 is 853 ($2 in rule -> CategoryName=$2)
     */
    @Test
    public void testSitemapListRewriting_ExpandRuleForLevel2()
    {
        URLContainer currentUrl = new URLContainer("https", "www.myshop.de", "443", "/WFS/inTRONICS/en_US/-/USD/"
                        + "sitemap-products/Cameras-Camcorders/853", "");
        assertThat(ruleSet.expand(currentUrl, currentUrl).toString(), is("https://www.myshop.de/INTERSHOP/web/WFS/"
                        + "inSPIRED-inTRONICS-Site/en_US/-/USD/"
                        + "ViewSitemap-ProductList?CategoryName=853&CatalogID=Cameras-Camcorders"));
    }
    
    /**
     * Test expand rule for level 3: <br>
     * Level 1 is Cameras-Camcorders ($1 in rule -> CatalogID=$1) <br>
     * Level 2 is Cameras <br>
     * Level 3 is 853 ($3 in rule -> CategoryName=$3)
     */
    @Test
    public void testSitemapListRewriting_ExpandRuleForLevel3()
    {
        URLContainer currentUrl = new URLContainer("https", "www.myshop.de", "443", "/WFS/inTRONICS/en_US/-/USD/"
                        + "sitemap-products/Cameras-Camcorders/Cameras/853", "");
        assertThat(ruleSet.expand(currentUrl, currentUrl).toString(), is("https://www.myshop.de/INTERSHOP/web/WFS/"
                        + "inSPIRED-inTRONICS-Site/en_US/-/USD/"
                        + "ViewSitemap-ProductList?CategoryName=853&CatalogID=Cameras-Camcorders"));
    }
}
