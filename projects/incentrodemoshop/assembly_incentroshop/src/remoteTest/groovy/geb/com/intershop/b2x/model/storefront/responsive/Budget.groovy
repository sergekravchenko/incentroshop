package geb.com.intershop.b2x.model.storefront.responsive

/**
 * This class represents a Budget.
 */
class Budget
{
    static String DEFAULT_CURRENCY = "USD"
    
    static BudgetPeriod DEFAULT_PERIOD = BudgetPeriod.MONTHLY
    
    private String currency

    private BigDecimal amount

    private BudgetPeriod period

    /**
     * Constructor for Budget.
     */
    Budget(BigDecimal amount, String currency, BudgetPeriod period)
    {
        this.amount = amount
        this.period = period
        this.currency = currency
    }

    /**
     * Constructor for Budget with default currency
     */
    Budget(BigDecimal amount,BudgetPeriod period)
    {
       this(amount, DEFAULT_CURRENCY, period)
    }

    /**
     * Constructor for Budget with int and default currency
     */
    Budget(double amount)
    {
       this(amount, DEFAULT_PERIOD)
    }
    
    /**
     * Constructor for Budget with int and default currency
     */
    Budget(double amount,BudgetPeriod period)
    {
       this(BigDecimal.valueOf(amount), period)
    }

    String toString()
    {
        StringBuilder buf = new StringBuilder(getClass().getSimpleName())
        buf.append("[")
        buf.append(amount)
        buf.append(" ")
        buf.append(currency)
        buf.append(", ")
        buf.append(period.value)
        buf.append("]")

        buf.toString()
    }

    static enum BudgetPeriod
    {
        FIXED(value: "fixed"),
        WEEKLY(value: "weekly"),
        MONTHLY(value: "monthly"),
        QUARTERLY(value: "quarterly")

        String value
        
        static BudgetPeriod getPeriod(String period) {
            for (BudgetPeriod p : values()) {
              if (p.value.equals(period)) {
                 return p 
              }
            }
            return null
        }
    }
}





