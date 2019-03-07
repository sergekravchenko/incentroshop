package geb.com.intershop.b2x.model.storefront.responsive

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

/**
 * Hold Recurrence information for Recurring Orders
 */
class RecurrenceInformation
{
    private static final String PATTERN = "MM/dd/YYYY"
    private final LocalDate startDate
    private final LocalDate endDate
    private int repetitions
    private PeriodType periodType

    RecurrenceInformation(String periodString, LocalDate startDate, LocalDate endDate) {
	    this.startDate = startDate
	    this.endDate = endDate
		processPeriod(Period.parse(periodString))
    }

    String getStartDateString() {
       startDate.format(DateTimeFormatter.ofPattern(PATTERN))
    }

    String getEndDateString() {
       endDate != null ? endDate.format(DateTimeFormatter.ofPattern(PATTERN)) : ""
    }

    String getPeriodTypeString() {
       periodType.toString()
    }

    private void processPeriod(Period period) {
	    if (period.getDays() > 0 ) {
	        if (period.getDays() % 7 == 0) {
	            this.periodType = PeriodType.WEEK
				this.repetitions = period.getDays() / 7
	        } else {
	            this.periodType = PeriodType.DAY
				this.repetitions = period.getDays()
	        }
	    } else if (period.getMonths() > 0) {
	        this.periodType = PeriodType.MONTH
			this.repetitions = period.getMonths()
	    } else if (period.getYears() > 0) {
	        this.periodType = PeriodType.YEAR
			this.repetitions = period.getYears()
	    } else {
	         throw IllegalArgumentException("Invalid period provided")
	    }
    }

    static RecurrenceInformation eachWeekOneYear() {
        LocalDate start = LocalDate.now()
	    new RecurrenceInformation(Period.ofDays(7).toString(), start, start.plusYears(1))
    }

    enum PeriodType {
        DAY,
	    WEEK,
	    MONTH,
        YEAR

        String toString() {
            this.name().substring(0,1)
        }
    }
}





