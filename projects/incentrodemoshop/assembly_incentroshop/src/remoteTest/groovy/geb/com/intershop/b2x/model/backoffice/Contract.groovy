package geb.com.intershop.b2x.model.backoffice

/**
 * This class represents a Contract in Back office.
 */
class Contract
{
    private static final String CONTRACT_TYPE = "CONTRACT_TYPE_REVENUE_BASED"
    
    private String name

    private String customerName

    private String customerId

    private String startDate

    private String endDate
    
    private float targetRevenue

    private int progress

    private String exeedance

    private String type
    
    private String comment
    
    /**
     * Constructor for Contract.
     */
    Contract(String name, String startDate, String endDate, String type, String comment)
    {
        this.name = name
        this.customerName = customerName
        this.customerId = customerId
        this.startDate = startDate
        this.endDate = endDate
        this.targetRevenue = 0.0
        this.progress = 0
        this.exeedance = ""
        this.type = type
        this.comment = comment
    }
}