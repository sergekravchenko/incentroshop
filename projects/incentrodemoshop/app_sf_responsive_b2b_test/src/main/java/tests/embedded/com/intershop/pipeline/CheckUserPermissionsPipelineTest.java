package tests.embedded.com.intershop.pipeline;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.intershop.beehive.core.capi.pipeline.PipelineResult;
import com.intershop.component.b2b.role.capi.user.UserBORoleExtension;
import com.intershop.component.user.capi.UserBO;
import com.intershop.tools.etest.server.AutoCleanup;
import com.intershop.tools.etest.server.Cartridges;
import com.intershop.tools.etest.server.DomainCreatorRule;
import com.intershop.tools.etest.server.DomainCreatorRule.ForceApplicationContext;
import com.intershop.tools.etest.server.EmbeddedServerRule;
import com.intershop.tools.etest.server.PipelineRule;

@Cartridges({ "app_sf_responsive_smb" , "app_sf_responsive_b2b"})
@AutoCleanup
@ForceApplicationContext
@RunWith(MockitoJUnitRunner.class)
public class CheckUserPermissionsPipelineTest
{
    private static final String PERMISSION_ID = "APP_B2B_MANAGE_USERS";

    private static final String ROLE_EXTENSION_NAME = "UserBORoleExtension";

    private static final String PIPELINE_NAME = "ProcessCompanyProfileSettings";

    private static final String START_NODE_NAME = "CheckUserPermissions";

    private static final String OK_END_NODE_NAME = "next";

    @Mock
    private UserBO userBOMock;

    @Mock
    private UserBORoleExtension userBORoleExtensionMock;

    private EmbeddedServerRule server = new EmbeddedServerRule(this);

    private PipelineRule pipelineRule = PipelineRule.builder().build();

    private DomainCreatorRule domainCreatorRule = DomainCreatorRule.site().build(this);

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(server).around(domainCreatorRule).around(pipelineRule);

    private  Map<String, Object> pipelineDict = new HashMap<>();

    @Before
    public void setUp()
    {
        when(userBOMock.getExtension(ROLE_EXTENSION_NAME)).thenReturn(userBORoleExtensionMock);
        pipelineDict.put("UserBO", userBOMock);
    }

    @Test
    public void testUserWithPermission()
    {
        when(userBORoleExtensionMock.hasPermission(PERMISSION_ID)).thenReturn(true);

        final PipelineResult pipelineResult = pipelineRule.getPipelineResult(PIPELINE_NAME, START_NODE_NAME ,pipelineDict);

        assertThat(pipelineResult.getEndName(), is(OK_END_NODE_NAME));
        assertTrue(pipelineResult.hasFinishedWithEnd());

        verify(userBOMock).getExtension(ROLE_EXTENSION_NAME);
        verify(userBORoleExtensionMock).hasPermission(PERMISSION_ID);
    }

    @Test
    public void testUserWithoutPermission()
    {
        when(userBORoleExtensionMock.hasPermission(PERMISSION_ID)).thenReturn(false);

        final PipelineResult pipelineResult = pipelineRule.getPipelineResult(PIPELINE_NAME, START_NODE_NAME ,pipelineDict);

        assertTrue(pipelineResult.getEndName() != (OK_END_NODE_NAME));
        assertFalse(pipelineResult.hasFinishedWithEnd());

        verify(userBOMock).getExtension(ROLE_EXTENSION_NAME);
        verify(userBORoleExtensionMock).hasPermission(PERMISSION_ID);
    }
}