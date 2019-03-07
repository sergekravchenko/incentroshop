package tests.unit.query;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    
})
public class QueryTestSuite
{
    @BeforeClass
    public static void configureSuite()
    {
        System.setProperty("ActiveComponentSet", "inSPIRED");
    }
    
    @AfterClass
    public static void teardown()
    {
        System.clearProperty("ActiveComponentSet");
    }
    
}
