package geb.com.intershop.b2x.model.storefront.responsive

import java.time.LocalDate

/**
 * This class represents an Order.
 */
class Order
{
    private final String orderNumber

    private final BigDecimal orderTotal

    private final String currency

    private final LocalDate orderDate

	private final RecurrenceInformation recurrenceInformation

	private Order(OrderBuilder builder) {
        this.orderNumber = builder.orderNumber
        this.orderTotal = builder.orderTotal
        this.currency = builder.currency
        this.orderDate = builder.orderDate
		this.recurrenceInformation = builder.recurrenceInformation
	}

    /**
     * Check whether anotherOrder object represent the same order
     */
    boolean hasSameInfo(Order anotherOrder) {
        boolean isSame
        isSame = this.orderNumber.equals(anotherOrder.orderNumber)
        if (isSame && orderTotal != null && anotherOrder.orderTotal != null)
        {
            isSame = orderTotal.equals(anotherOrder.orderTotal)
        }

        if (isSame && currency != null && anotherOrder.currency != null)
        {
            isSame = currency.equals(anotherOrder.currency)
        }

        if (isSame && orderDate != null && anotherOrder.orderDate != null)
        {
            isSame = orderDate.equals(anotherOrder.orderDate)
        }

        isSame
    }

	boolean isRecurring() {
	    recurrenceInformation != null
	}

	static class OrderBuilder {
	    private static String DEFAULT_CURRENCY = "USD"
	    private final String orderNumber
        private BigDecimal orderTotal
        private String currency
        private LocalDate orderDate
	    private RecurrenceInformation recurrenceInformation

        OrderBuilder(String orderNumber) {
            this.orderNumber = orderNumber
			this.currency = DEFAULT_CURRENCY
        }

		OrderBuilder total(BigDecimal orderTotal) {
            this.orderTotal = orderTotal
            this
        }

		OrderBuilder currency(String currency) {
            this.currency = currency
            this
        }

		OrderBuilder orderDate(LocalDate orderDate) {
            this.orderDate = orderDate
            this;
        }

		OrderBuilder defaultOrderDate() {
            orderDate(LocalDate.now())
        }

		OrderBuilder defaultCurrency() {
            currency(DEFAULT_CURRENCY)
        }

		OrderBuilder recurrence(RecurrenceInformation recurrenceInformation) {
            this.recurrenceInformation = recurrenceInformation
            this
        }

		Order build() {
            new Order(this)
        }
	}
}