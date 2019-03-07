package rest.com.intershop.b2x.specs.storefront.common.objects

import spock.lang.*

class AddressV1
{
    private String id
    private String firstName = "firstName_" + new Random().nextInt(10 ** 5)
    private String lastName = "lastName_" + new Random().nextInt(10 ** 5)
    private String addressLine1 = "addressLine1"
    private String postalCode = "12345"
    private String city ="TestCity"
    private String mainDivision = "Alabama"
    private String countryCode = "US"
    private String urn
    private Boolean eligibleInvoiceToAddress = true
    private Boolean eligibleShipToAddress = true
    private Boolean eligibleShipFromAddress = false
    private Boolean eligibleServiceToAddress = false
    private Boolean eligibleInstallToAddress = false

    public AddressV1(Boolean[] usage)
    {
        setUsage(usage)
    }


    public String getFirstName()
    {
        return firstName
    }

    public String getLastName()
    {
        return lastName
    }

    public String getAddressLine1()
    {
        return addressLine1
    }

    public String getPostalCode()
    {
        return postalCode
    }

    public String getCity()
    {
        return city
    }

    public String getMainDivision()
    {
        return mainDivision
    }

    public String getCountryCode()
    {
        return countryCode
    }

    public String getEligibleInvoiceToAddress()
    {
        return eligibleInvoiceToAddress
    }

    public String getEligibleShipToAddress()
    {
        return eligibleShipToAddress
    }

    public String getEligibleShipFromAddress()
    {
        return eligibleShipFromAddress
    }

    public String getEligibleServiceToAddress()
    {
        return eligibleServiceToAddress
    }

    public String getEligibleInstallToAddress()
    {
        return eligibleInstallToAddress
    }

    public String getID()
    {
        return id
    }

    public String getURN()
    {
        return urn
    }

    public setID(String id)
    {
        this.id = id
    }

    public setURN(String urn)
    {
        this.urn = urn
    }

    /**
     * The usage array can have different sizes up to a max size of 5, each index is associated to a specific usage
     * parameter.
     * index 0 => eligibleInvoiceToAddress
     * index 1 => eligibleShipToAddress
     * index 2 => eligibleShipFromAddress
     * index 3 => eligibleServiceToAddress
     * index 4 => eligibleInstallToAddress
     * @param usage
     */
    private void setUsage(Boolean[] usage)
    {
        for(int index = 0; index<usage.size(); index++)
        {
            switch(index)
            {
                case 0: this.eligibleInvoiceToAddress = usage[0]
                        break
                case 1: this.eligibleShipToAddress = usage[1]
                        break
                case 2: this.eligibleShipFromAddress = usage[2]
                        break
                case 3: this.eligibleServiceToAddress = usage[3]
                        break
                case 4: this.eligibleInstallToAddress = usage[4]
                        break
            }
        }
    }
}