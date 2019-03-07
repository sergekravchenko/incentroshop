package rest.com.intershop.b2x.specs.storefront.common

import geb.com.intershop.b2x.testdata.TestDataUsage
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import spock.lang.*

class RestSpec extends Specification implements TestDataUsage {

    @Shared
    RESTClient client = new LoggingRESTClient()

    public static final String XML = ContentType.XML.getContentTypeStrings()[1]
    public static final String JSON = ContentType.JSON.toString()

    public static final String DEBUG = "DEBUG"
    public static final String ERROR = "ERROR"

	static boolean debug = false

	static void debugModeOn()
	{
		debug = true
	}

	static void debugModeOff()
	{
		debug = false
	}

	static void log(def loglevel, def message)
	{
		if (loglevel == ERROR || ((loglevel == DEBUG) && debug)) 
		{
			println message
		}
	}

	static void assertType(def responseData, def expectedType, def contentType)
	{
		if (contentType == XML)
		{
			log(DEBUG, "Expect that type is '" + expectedType + "' and result is: " + (responseData.@type == expectedType))
			assert responseData.@type == expectedType
			if (expectedType == "Link")
			{
				log(DEBUG, "  For type 'Link' additionally check the 'link' with result: " + (responseData.link != null))
				assert responseData.link != null
			}
		}
		if (contentType == JSON) 
		{
			log(DEBUG, "Expect that type is '" + expectedType + "' and result is: " + (responseData.type == expectedType))
			assert responseData.type == expectedType
		}
	}

	static void assertName(def responseData, def expectedName, def contentType)
	{
		if (contentType == XML)
		{
			log(DEBUG, "Expect that name is '" + expectedName + "' and result is: " + (responseData.@name == expectedName))
			assert responseData.@name == expectedName
		}
		if (contentType == JSON) 
		{
			log(DEBUG, "Expect that name is '" + expectedName + "' and result is: " + (responseData.name == expectedName))
			assert responseData.name == expectedName
		}
	}

    static String generateNumber()
    {
        new Random().nextInt(10 ** 5) + ""
    }
}
