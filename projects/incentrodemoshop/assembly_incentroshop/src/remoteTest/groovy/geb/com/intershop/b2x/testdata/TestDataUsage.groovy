package geb.com.intershop.b2x.testdata;

import spock.lang.Timeout

@Timeout(600)
trait TestDataUsage
{
    // A storage for some test data.
    def static final Map<String, List> testData = TestDataLoader.getTestData();

	String getTestValue(String key) {
	    testData.get(key)[0]
	}

	String getTestValue(String key, int index)
	{
		testData.get(key)[index]
	}
}
