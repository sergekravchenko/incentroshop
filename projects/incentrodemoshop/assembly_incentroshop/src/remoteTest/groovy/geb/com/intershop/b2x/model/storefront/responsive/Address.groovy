package geb.com.intershop.b2x.model.storefront.responsive

/**
 * This class represents an Address in My Account section.
 */
class Address
{
    private static final String LINE_SEPARATOR = "\n"

    private String company

    private String firstName

    private String lastName

    private String addressLine

    private String city

    private String state
    
    private String stateCode

    private String postCode

    private String country

    /**
     * Constructor for Address.
     */
    Address(String country, String company, String firstName, String lastName, String addressLine, String city, String state, String postCode, String stateCode)
    {
        this.country = country
        this.company = company
        this.firstName = firstName
        this.lastName = lastName
        this.addressLine = addressLine
        this.city = city
        this.state = state
        this.stateCode = stateCode
        this.postCode = postCode
    }

    /**
     * Constructor for Address without state.
     */
    Address(String country, String company, String firstName, String lastName, String addressLine, String city, String postCode)
    {
        this(country, company, firstName, lastName, addressLine, city, null, postCode)
    }

    String asText()
    {
        StringBuilder buf = new StringBuilder()
        buf.append(company)
        buf.append(LINE_SEPARATOR)
        buf.append(firstName)
        buf.append(" ")
        buf.append(lastName)
        buf.append(LINE_SEPARATOR)
        buf.append(addressLine)
        buf.append(LINE_SEPARATOR)
        buf.append(city)
        if(country == "United States")
        {
            buf.append(",  ")
            buf.append(stateCode)
        }
        buf.append("  ")
        buf.append(postCode)
        buf.append(LINE_SEPARATOR)
        if(country != "United States")
        {
            buf.append(state)
            buf.append(LINE_SEPARATOR)
        }
        buf.append(country)
        buf.toString()
    }
    
    String toString()
    {
        asText()
    }
}