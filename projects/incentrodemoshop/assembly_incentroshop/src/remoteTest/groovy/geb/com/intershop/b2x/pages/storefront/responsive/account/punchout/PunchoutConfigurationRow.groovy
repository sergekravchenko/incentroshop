package geb.com.intershop.b2x.pages.storefront.responsive.account.punchout

import geb.Module
import geb.Page
import geb.navigator.Navigator

class PunchoutConfigurationRow extends Module
{
    static content = {
        label { $('[data-testing-class="row-label"]').text() }
        transform { $('[data-testing-class="row-transform"]') }
        format { $('[data-testing-class="row-format"]') }
    }
}
