package geb.com.intershop.b2x.pages.storefront.responsive.account.subscriptions

import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage

class SubscriptionDetailsPage extends AccountPage
{
    String pageId() {"account-subscription-details-page"}  // Needed from 'AccountPage'

    static at =
    {
        waitFor {
            contentSlot.displayed
            subscriptionDetails.displayed
        }
    }

    static content = {
        subscriptionDetails (required: true) { $('[data-testing-id="subscription-details"]') }
        subscriptionNumber (required: true) { $('[data-testing-id="subscription-number"]').text().trim() }
    }

}
