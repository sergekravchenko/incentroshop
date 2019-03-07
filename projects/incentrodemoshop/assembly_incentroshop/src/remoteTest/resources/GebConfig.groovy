import org.openqa.selenium.Dimension
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.phantomjs.*
import org.openqa.selenium.remote.*

def gebEnv = System.getProperty("geb.env");
def webDriverDir = System.getProperty("webDriverDir")
def webDriverExec = new File(webDriverDir, System.getProperty("webDriverExec")).absolutePath

waiting {
	// max request time in seconds
    timeout = 180
    
    // http://gebish.org/manual/current/#failure-causes
    includeCauseInMessage = true
}

environments {
    phantomJsPC {
        driver = {
            def driver = createPhantomJsDriverInstance(webDriverExec)
            driver.manage().window().setSize(new Dimension(1920, 1200))
            driver
        }
    }

    chromeTablet {
        driver = {
            def driver = createChromeDriverInstance(webDriverExec)
            driver.manage().window().setSize(new Dimension(1024, 768))
            driver
        }
    }

    chromePC {
        driver = {
            def driver = createChromeDriverInstance(webDriverExec)
            driver.manage().window().setSize(new Dimension(1920, 1200))
            driver
        }
    }
}


private def createChromeDriverInstance(String webDriverExec) {
    System.setProperty("webdriver.chrome.driver", webDriverExec)
    driverInstance = new ChromeDriver()
    driverInstance
}


private def createPhantomJsDriverInstance(String webDriverExec) {
    System.setProperty("phantomjs.binary.path", webDriverExec)

    ArrayList cliArgsCap = new ArrayList();
    cliArgsCap.add("--web-security=false");
    cliArgsCap.add("--ssl-protocol=any");
    cliArgsCap.add("--ignore-ssl-errors=true");

    DesiredCapabilities desiredCapabilities = new DesiredCapabilities()
    desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);

    new PhantomJSDriver(desiredCapabilities)
}