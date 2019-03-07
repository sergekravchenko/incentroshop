package geb.com.intershop.b2x.pages.storefront.responsive.modules

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

import geb.Module
import geb.com.intershop.b2x.model.storefront.responsive.RecurrenceInformation

/**
 * Module that represents Subscription recurrence information box
 */
class RecurrenceInfo extends Module
{
    private static final String PATTERN = "M/d/yy"

    static content = {
        startDateText { $('[data-testing-id="subscription-start-date"]').text().trim() }
        endDateText { $('[data-testing-id="subscription-end-date"]').text().trim() }
        recurrenceElement { $('[data-testing-id^="subscription-recurrence-"]') }
        // Sample value in ISML template : data-testing-id="subscription-recurrence-P7D"
        recurrence {recurrenceElement.@"data-testing-id".split("-").last()}
    }

    RecurrenceInformation getRecurrenceInformation() {
        new RecurrenceInformation(recurrence, getStartDate(), getEndDate())
    }

    private LocalDate getStartDate() {
        LocalDate.parse(startDateText, DateTimeFormatter.ofPattern(PATTERN))
    }

    private LocalDate getEndDate() {
        LocalDate result;
        try {
            result = LocalDate.parse(endDateText, DateTimeFormatter.ofPattern(PATTERN))
        } catch (DateTimeParseException ex) {
            // No end date set currently displayed as "-" - return null in this case
            result = null
        }
        result
    }
}