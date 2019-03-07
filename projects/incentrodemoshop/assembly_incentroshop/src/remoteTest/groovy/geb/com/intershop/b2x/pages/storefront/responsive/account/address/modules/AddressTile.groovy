package geb.com.intershop.b2x.pages.storefront.responsive.account.address.modules

import geb.Module

/**
 * Module that encapsulates Address read-only representation
 */
class AddressTile extends Module
{
    static content =
    {
        addressText {$("address").text().trim()}
        editAddressLink {$("a", class: "update-address")}
        addressID {editAddressLink.attr("id")}
        deleteAddressLink(required: false) {$("a", class: "remove-address")}
    }    
}
