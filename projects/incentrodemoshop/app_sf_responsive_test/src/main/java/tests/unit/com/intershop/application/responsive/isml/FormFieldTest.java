package tests.unit.com.intershop.application.responsive.isml;

import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.jasper.compiler.JspUtil;
import org.junit.Test;

import com.intershop.beehive.core.capi.app.AppContextUtil;
import com.intershop.beehive.core.capi.component.ComponentMgr;
import com.intershop.beehive.core.capi.domain.Application;
import com.intershop.beehive.core.capi.domain.ApplicationContext;
import com.intershop.beehive.core.capi.domain.DomainMgr;
import com.intershop.beehive.core.capi.request.Request;
import com.intershop.beehive.core.capi.request.ServletRequest;
import com.intershop.beehive.core.capi.request.ServletResponse;
import com.intershop.beehive.core.capi.webform.Form;
import com.intershop.beehive.core.capi.webform.FormParameter;
import com.intershop.beehive.core.internal.configuration.ConfigurationMgrImpl;
import com.intershop.beehive.core.internal.template.ISMLTemplateContextFactoryImpl;
import com.intershop.beehive.emf.webform.WebFormFactoryDefinition;
import com.intershop.beehive.isml.capi.ISMLTemplateContext;
import com.intershop.beehive.isml.capi.ISMLTemplateEngine;
import com.intershop.beehive.isml.capi.ISMLTemplateIdentifier;

import tests.server.com.intershop.beehive.core.internal.template.isml.ISMLTestCase;

/**
 * 
 * @author hborch
 * @deprectaed Has to be reworked. See <a href="https://jira.intershop.de/browse/IS-15960">IS-15960</a> for further details.
 *
 */
@Deprecated
public class FormFieldTest extends ISMLTestCase
{
    private static String TEST_CLASS = "test_class";

    private static String TEST_ID = "test_id";

    private static String TEST_JSP_SUBDIR = "/ish/cartridges";

    private static String TEST_LOCALE = "default";

    private static String TEST_TEMPLATE_NAME = "modules/address/forms/inc/FormField";

    private Application application;

    public FormFieldTest(String testName)
    {
        super(testName);

    }

    @Test
    public void testCheckTemplate()
    {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("id", TEST_ID);
        parameters.put("groupclass", TEST_CLASS);

        // System.out.println("rendering template " + TEST_TEMPLATE_NAME);
        String responseStr = executeTemplate("app_sf_responsive", TEST_TEMPLATE_NAME, parameters);
        // System.out.println(responseStr);
        assertNotEquals(null, responseStr);
        if (responseStr != null)
            assertEquals(true, responseStr.contains(TEST_CLASS));

    }

    @Inject
    private ComponentMgr componentMgr;

    private String executeTemplate(String cartridgeName, String templateName, Map<String, String> parameters)
    {
        ISMLTemplateIdentifier ismlTID = null;
        ApplicationContext applicationContext = getApplication().forceApplicationContext();

        try
        {

            ISMLTemplateEngine engine = (ISMLTemplateEngine)componentMgr
                            .getGlobalComponentInstance("ISMLTemplateEngine");

            ismlTID = new ISMLTemplateIdentifier(templateName, TEST_LOCALE,
                            engine.getTemplateSetTemplateDirectories(cartridgeName).get(0),
                            engine.getTemplateSetPageCompileDirectory(cartridgeName),
                            TEST_JSP_SUBDIR + "/" + cartridgeName + "/" + TEST_LOCALE + "/" + templateName,
                            TEST_JSP_SUBDIR + "/" + JspUtil.makeJavaPackage(cartridgeName), cartridgeName);

            assertNotNull(ismlTID.getISMLFile());

            Request request = Request.getCurrent();
            request.setExecutionApplication(application);
            request.setAppUrlIdentifier(application.getUrlIdentifier());
            request.getPipelineDictionary().putAll(parameters);

            // Map<String, String> formparameter = new HashMap<String, String>();

            request.getPipelineDictionary().put("formparameter",
                            new FormParameter(new Form("TestForm", request.getLocale()),
                                            WebFormFactoryDefinition.eINSTANCE.createFormParameter()));

            ServletRequest fwRequest = request.getServletRequest();
            ServletResponse fwResponse = new ServletResponse();
            fwResponse.setDefaultContentType("text/plain");

            ISMLTemplateContextFactoryImpl templateContextFactory = (ISMLTemplateContextFactoryImpl)AppContextUtil
                            .getCurrentAppContext().getVariable("ISMLTemplateContextFactory");
            templateContextFactory.setDevelopmentMode(true);
            templateContextFactory.setCheckSourceModified(true);
            ISMLTemplateContext templateCtx = templateContextFactory.create(AppContextUtil.getCurrentAppContext(),
                            fwRequest, fwResponse, fwRequest.getServletContext());

            ConfigurationMgrImpl.setString("intershop.template.PrintTemplateMarker", "false");

            engine.renderTemplate(templateName, templateCtx);

            return fwResponse.getContent();

        }
        catch(Throwable localThrowable1)
        {
            // Do nothing
        }

        return null;

    }

    @Inject
    private DomainMgr domainMgr;

    @Override
    protected Application getApplication()
    {

        application = domainMgr.getDomainByName("inSPIRED-inTRONICS-Site").getDefaultApplication();
        return application;
    }

}
