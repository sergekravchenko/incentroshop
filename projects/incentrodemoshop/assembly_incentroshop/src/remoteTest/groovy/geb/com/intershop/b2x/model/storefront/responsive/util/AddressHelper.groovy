package geb.com.intershop.b2x.model.storefront.responsive.util

import geb.com.intershop.b2x.model.storefront.responsive.Address

class AddressHelper
{
    def static rangeOfSuffixNumber = 100000000
    
    public static Address getRandomAddress(def prefix, def country, def stateName, def stateCode)
    {
        int suffix = Math.abs(new Random().nextInt() % rangeOfSuffixNumber);
        return new Address(country, prefix + suffix, prefix, prefix + suffix, prefix + suffix, prefix + suffix, stateName, "01234", stateCode);
    }
    
    public static Address getRandomUSAddress()
    {
        return getRandomUSAddress("User");
    }
    
    public static Address getRandomUSAddress(def prefix)
    {
        return getRandomAddress(prefix, "United States", "Alabama", "AL");
    }
    
    public static Address getRandomDEAddress(def prefix)
    {
        return getRandomAddress(prefix, "Germany", null, null);
    }
}
