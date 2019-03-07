package geb.com.intershop.b2x.pages.backoffice.preferences

import geb.com.intershop.b2x.pages.backoffice.BackOfficePage

class BackOfficePreferencesPage extends BackOfficePage
{
    static at = {
        $('div', 'data-testing-id': 'bo-channel-preferences-overview')
    }
    
    static content= {        
        CaptchaPreferences(required: true, to: CaptchaPreferencesPage) { $("a",text:contains("CAPTCHAs")) }
//        ContentObjectLockingPreferences(required: true, to: ContentObjectLockingPreferencesPage) {  }
//        DesignViewPreferences(required: true, to: DesignViewPreferencesPage) {  }
//        EmailMarketingPreferences(required: true, to: EMailMarketingPreferencesPage) {  }
//        GiftCertificatePreferences(required: true, to: GiftCertificatePreferencesPage) {  }
//        OrderNotificationPreferences(required: true, to: OrderNotificationPreferencesPage) {  }
        PageCachingPreferences(required: true, to: PageCachingPreferencesPage) {  }
        PricingPreferences(required: true, to: PricingPreferencesPage) {  }
//        ProductEditingPreferences(required: true, to: ProductEditingPreferencesPage) {  }
//        ProductHistoryPreferences(required: true, to: ProductHistoryPreferencesPage) {  }
//        ProductLockingPreferences(required: true, to: ProductLockingPreferencesPage) {  }
//        ProductNotificationPreferences(required: true, to: ProductNotificationPreferencesPage) {  }
//        ProductRatingPreferences(required: true, to: ProductRatingPreferencesPage) {  }
//        ProfanityWordDefinitionPreferences(required: true, to: ProfanityWordDefinitionPreferencesPage) {  }
//        PromotionPreferences(required: true, to: PromotionPreferencesPage) {  }
//        RecommendationEnginesPreferences(required: true, to: RecommendationEnginesPreferencesPage) {  }
//        RegionalSettingsPreferences(required: true, to: RegionalSettingsPreferencesPage) {  }
//        RegistrationPreferences(required: true, to: RegistrationPreferencesPage) {  }
//        SEOPreferences(required: true, to: SEOPreferencesPage) {  }
//        ShippingPreferences(required: true, to: ShippingPreferencesPage) {  }
//        SortingPreferences(required: true, to: SortingPreferencesPage) {  }
//        WarrantyPreferences(required: true, to: WarrantyPreferencesPage) {  }
//        WishlistPreferences(required: true, to: WishlistPreferencesPage) {  }
    }
}