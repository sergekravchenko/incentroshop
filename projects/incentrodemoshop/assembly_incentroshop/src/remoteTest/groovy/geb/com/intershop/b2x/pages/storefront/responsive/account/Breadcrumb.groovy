package geb.com.intershop.b2x.pages.storefront.responsive.account

import geb.Module

/**
 * Module for the breadcrumb GUI control used to keep track of user location within the web site.
 *
 */
class Breadcrumb extends Module
{
    static content = 
    {
      links{$("a", class:"breadcrumbs-list-link")}
      currentElement{$("li", class:"breadcrumbs-list-active").first()}
    }

    def asText() {
        return links.collect{it.text().trim()}.join("/") + currentElement.text().trim()
    }
}
