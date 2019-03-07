package geb.com.intershop.b2x.pages.storefront.responsive.account.subscriptions

import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountPage
import geb.com.intershop.b2x.pages.storefront.responsive.modules.SubscriptionItem

class SubscriptionsPage extends AccountPage
{
    String pageId() {"account-subscriptions-page"}  // Needed from 'AccountPage'

    static at =
    {
        waitFor {
            contentSlot.displayed
            subscriptionsList.displayed
        }
    }

    static content = {
        subscriptionsList(required: true) {
            $('table[data-testing-id="subscriptionsList"] tbody tr')*.module(SubscriptionItem)
        }
    }

    SubscriptionDetailsPage openDetails(number)
    {
        def item = subscriptionsList.find{ it.subscriptionNumber == number }
        assert item != null
        item.openDetails()
		browser.page
    }

}
