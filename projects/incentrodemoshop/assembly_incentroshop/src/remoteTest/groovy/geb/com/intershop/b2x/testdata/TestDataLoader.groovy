package geb.com.intershop.b2x.testdata;

class TestDataLoader
{
     private static Map<String,List> testData ;

    private static String fileName = "TestData";
    
    private void readTestDataFromFile()
    {
        testData = new HashMap<String,List>();
        ResourceBundle testDataBundle =
                ResourceBundle.getBundle(fileName,
                Locale.US);

        Enumeration bundleKeys = testDataBundle.getKeys();

        while (bundleKeys.hasMoreElements())
        {
            String key = (String)bundleKeys.nextElement();
            String value = testDataBundle.getString(key);
            if(!value.contains("["))
                value="["+value+"]"
            
            System.out.println(key + "\t=" + value);

            testData.put(key.toString(),Eval.me(value))
        }
    }

    public static Map<String,List> getTestData()
    {
        if(testData==null){
            new TestDataLoader().readTestDataFromFile();
            
        }
        return testData;
    }
   
}
