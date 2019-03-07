package geb.com.intershop.b2x.pages.storefront.responsive.checkout.modules

import geb.Module
import geb.module.*
import geb.com.intershop.b2x.model.storefront.responsive.RecurrenceInformation

/**
 * This Geb module represents the subscriptions form on Cart page, where recurrence information is selected
 */
class CartSubscriptionForm extends Module
{
    static content = {
        basketTypeSelector    { $('[name="CartToSubscriptionSwitch"]').module(RadioButtons) }
        subscriptionForm      { $('[data-testing-id="cartSubscriptionForm"]') }
        subscriptionCount     { $('[data-testing-id="subscriptionRecurrenceCount"]') }
        subscriptionInterval  { $('[data-testing-id="subscriptionInterval"]').module(Select) }
        subscriptionStartDate { $('[data-testing-id="subscriptionStartDate"]').module(TextInput) }
        subscriptionEndDate   { $('[data-testing-id="subscriptionEndDate"]').module(TextInput) }
    }

    void selectOnetimepurchace() {
        basketTypeSelector.checked = "default"
        println "Selected checkout type 'One-time purchase'."
    }

    void selectSubscription() {
        basketTypeSelector.checked = "subscription"
        waitFor { subscriptionForm.displayed }

        println "Selected checkout type 'Subscription'."
    }

    boolean isSubscriptionSelected() {
        basketTypeSelector.checked == "subscription"
    }

    void setRecurrenceData(count, interval, startDate, endDate) {
        selectSubscription()
        subscriptionCount.value(count)
        subscriptionInterval.selected = interval

        subscriptionStartDate.text = startDate
        subscriptionEndDate.text = endDate

        println "Subscription form values defined."
        println ">> " + subscriptionCount.value()
        println ">> " + subscriptionInterval.selected
        println ">> " + subscriptionStartDate.text
        println ">> " + subscriptionEndDate.text
    }

    void setRecurrenceData(RecurrenceInformation recurrence) {
        setRecurrenceData(recurrence.repetitions, recurrence.getPeriodTypeString(), recurrence.getStartDateString(), recurrence.getEndDateString())
    }
}
