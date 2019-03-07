package geb.com.intershop.b2x.specs.storefront.responsive.punchout

import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoProduct
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoUser
import geb.com.intershop.b2x.pages.storefront.responsive.HomePage
import geb.com.intershop.b2x.pages.storefront.responsive.account.AccountHomePage
import geb.com.intershop.b2x.pages.storefront.responsive.account.punchout.PunchoutConfigurationPage
import geb.com.intershop.b2x.pages.storefront.responsive.account.punchout.PunchoutPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CartPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.OCICatalogPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.OCIErrorPage
import geb.com.intershop.b2x.pages.storefront.responsive.shopping.ProductDetailPage
import geb.com.intershop.b2x.pages.storefront.responsive.shopping.warranty.ProductWithWarrantyDetailPage
import geb.com.intershop.b2x.pages.storefront.responsive.user.UsersPage
import geb.com.intershop.b2x.specs.storefront.responsive.features.Authentication
import geb.com.intershop.b2x.specs.storefront.responsive.features.OCIUserLogin
import geb.com.intershop.b2x.testdata.TestDataUsage
import geb.spock.GebReportingSpec
import spock.lang.Ignore

@Ignore
public class PunchoutSpec extends GebReportingSpec implements Authentication, OCIUserLogin,TestDataUsage
{
    
    def "Enable punchout by OCI user creation"()
    {
         given: "I have logged in and am on My Account Home Page"
           logInUser(DemoUser.ACCOUNT_ADMIN.user);
         
         when:
           at AccountHomePage
         and:
             clickPunchout()
         then: "The punchout page is displayed"
             at PunchoutPage             
                  
         when: "I provide data for new user"    
             setUserData login,password
             createUserButton.click()
         then: "user is created and displayed"
             at PunchoutPage
             isUserListDisplayed(true)
             isUserDisplayed(login)
             
         where:
             login      << testData.get("punchout.user.newUser.login")
             password   << testData.get("punchout.user.password")
    }
    
    /**
     * Test to check whether each logged in oci user gets a new basket.
     * Therefore two sequential logins add a product each. Afterwards
     * only one product should be in the basket (NOT two).
     * @return
     */
    def "Check multiple logins will each get their own baskets"()
    {
        when: "Login as OCI user"
            loginOCIUser(login, password, hookUrl)
        
        then: "The OCI user is on the homepage"
            at HomePage
            
        when: "Navigating to a product's detail page via search"
            header.search(productId)
        then:
            at ProductDetailPage
            
        when: "Add to cart button is clicked"
            addToCart()
        then: "Transfered to the cart page"
            at CartPage
            
        when: "Login again as the same OCI user"
            loginOCIUser(login, password, hookUrl)
            
        then: "The OCI user is on the homepage"
            at HomePage
        
        when: "Navigating to a product's detail page via search"
            header.search(productId)
        then:
            at ProductDetailPage
            
        when: "Add to cart button is clicked"
            addToCart()
        then: "Transfered to the cart page"
            at CartPage
        and: "only one single product (not two) should be in the basket"
            productCartTable(productTitle).quantityInput.value 1
        
        where:
            productId << testData.get("punchout.product.productId")
            productTitle << testData.get("punchout.product.title")
            
            login      << testData.get("punchout.user.login")
            password   << testData.get("punchout.user.password")
            hookUrl    << testData.get("punchout.user.hookUrl")
    }
    
    def "Check the OCI Punchout catalog transfer"()
    {
        when: "Login as OCI user"
            loginOCIUser(login, password, hookUrl)
            
        then: "The OCI user is on the homepage"
            at HomePage
            
        when: "Navigating to a product's detail page via search"
            header.search(ociProductWithWarrantyId)
        then:
            at ProductWithWarrantyDetailPage
            
        when: "... check if warranty product(s) displayed on Detail Page."
            checkWarrantyProductExists(ociWarrantyProductId)
        
        then: "... select warranty product ..."
            selectWarrantyProduct(ociWarrantyProductId)
        
        when: "Add to cart button is clicked"
            addToCart()
            
        then: "Transfered to the cart page"
            at CartPage
            
        when: "Disable the autosubmit parameter"
            autoSubmit('false')
            
        then: "Autosubmit is disabled"
            assert checkAutoSubmit('false')
        
        when: "Click the oci transfer button"
            transferOCIButton.click()
        
        then:   "The OCI catalog page with transfer data is opened"
            at OCICatalogPage
            
        and: "Check if form is present"
            assert checkHookUrl(hookUrl)
            
            assert ociCatalog("1").productId    == ociProductWithWarrantyId
            assert ociCatalog("1").description  == ociProductWithWarrantyDescription
            assert ociCatalog("1").matNr        == ociProductWithWarrantyMatNr
            assert ociCatalog("1").quantity     == ociProductWithWarrantyQuantity
            assert ociCatalog("1").unit         == ociProductWithWarrantyUnit
            assert ociCatalog("1").price        == ociProductWithWarrantyPrice
            assert ociCatalog("1").currency     == ociProductWithWarrantyCurrency
            assert ociCatalog("1").priceUnit    == ociProductWithWarrantyPriceUnit
            assert ociCatalog("1").longText     == ociProductWithWarrantyLongText
            assert ociCatalog("1").vendor       == ociProductWithWarrantyVendor
            assert ociCatalog("1").vendorMat    == ociProductWithWarrantyVendorMat
            assert ociCatalog("1").manufactCode == ociProductWithWarrantyManufactCode
            assert ociCatalog("1").manufactMat  == ociProductWithWarrantyManufactMat
            
            assert ociCatalog("2").productId    == ociWarrantyProductId
            assert ociCatalog("2").description  == ociWarrantyProductDescription
            assert ociCatalog("2").matNr        == ociWarrantyProductMatNr
            assert ociCatalog("2").quantity     == ociWarrantyProductQuantity
            assert ociCatalog("2").unit         == ociWarrantyProductUnit
            assert ociCatalog("2").price        == ociWarrantyProductPrice
            assert ociCatalog("2").currency     == ociWarrantyProductCurrency
            assert ociCatalog("2").priceUnit    == ociWarrantyProductPriceUnit
            assert ociCatalog("2").longText     == ociWarrantyProductLongText
            assert ociCatalog("2").vendor       == ociWarrantyProductVendor
            assert ociCatalog("2").vendorMat    == ociWarrantyProductVendorMat
            assert ociCatalog("2").manufactCode == ociWarrantyProductManufactCode
            assert ociCatalog("2").manufactMat  == ociWarrantyProductManufactMat
            
        where:
            ociProductWithWarrantyId << testData.get("punchout.product.with.warranty.productId")
            ociProductWithWarrantyDescription << testData.get("punchout.product.with.warranty.description")
            ociProductWithWarrantyMatNr << testData.get("punchout.product.with.warranty.matNr")
            ociProductWithWarrantyQuantity << testData.get("punchout.product.with.warranty.quantity")
            ociProductWithWarrantyUnit << testData.get("punchout.product.with.warranty.unit")
            ociProductWithWarrantyPrice << testData.get("punchout.product.with.warranty.price")
            ociProductWithWarrantyCurrency << testData.get("punchout.product.with.warranty.currency")
            ociProductWithWarrantyPriceUnit << testData.get("punchout.product.with.warranty.priceUnit")
            ociProductWithWarrantyLongText << testData.get("punchout.product.with.warranty.longText")
            ociProductWithWarrantyVendor << testData.get("punchout.product.with.warranty.vendor")
            ociProductWithWarrantyVendorMat << testData.get("punchout.product.with.warranty.vendorMat")
            ociProductWithWarrantyManufactCode << testData.get("punchout.product.with.warranty.manufactCode")
            ociProductWithWarrantyManufactMat << testData.get("punchout.product.with.warranty.manufactMat")
            
            ociWarrantyProductId << testData.get("punchout.product.the.warranty.productId")
            ociWarrantyProductDescription << testData.get("punchout.product.the.warranty.description")
            ociWarrantyProductMatNr << testData.get("punchout.product.the.warranty.matNr")
            ociWarrantyProductQuantity << testData.get("punchout.product.the.warranty.quantity")
            ociWarrantyProductUnit << testData.get("punchout.product.the.warranty.unit")
            ociWarrantyProductPrice << testData.get("punchout.product.the.warranty.price")
            ociWarrantyProductCurrency << testData.get("punchout.product.the.warranty.currency")
            ociWarrantyProductPriceUnit << testData.get("punchout.product.the.warranty.priceUnit")
            ociWarrantyProductLongText << testData.get("punchout.product.the.warranty.longText")
            ociWarrantyProductVendor << testData.get("punchout.product.the.warranty.vendor")
            ociWarrantyProductVendorMat << testData.get("punchout.product.the.warranty.vendorMat")
            ociWarrantyProductManufactCode << testData.get("punchout.product.the.warranty.manufactCode")
            ociWarrantyProductManufactMat << testData.get("punchout.product.the.warranty.manufactMat")
            
            login      << testData.get("punchout.user.login")
            password   << testData.get("punchout.user.password")
            hookUrl    << testData.get("punchout.user.hookUrl")
    }
    
    def "Check limited storefront visibility of OCI user"()
    {
        given: "Login as OCI user"
            loginOCIUser(login, password, hookUrl)
            
        when: "The OCI user is on the homepage"
            at HomePage
        then: "There is no logout link and the footer is empty"
            assert header.linkLogout.isEmpty()
            assert footer.children().size() == 0
        
        when: "Navigating to a product's detail page via search"
            header.search(DemoProduct.SONY_VLP_SW225.product.id)
            at ProductDetailPage
        then: "There is no 'Add To Quote' button, no 'Price Notification' button, no Shipping info, "
              "no Return Policy info and no option to write a review"
            assert addToQuoteButton.isEmpty()
            assert addToQuoteLink.isEmpty()
            assert priceNotificationButton.isEmpty() 
            assert shippingShortInfoField.isEmpty()  
            assert shippingTabField.isEmpty()
            assert returnPolicyTabField.isEmpty()    
            assert ownReviewField.children().size() == 0

        when: "Navigating to homepage"
            to HomePage
        and: "Clicking Express Shop button of a product"
            productTile.clickExpressShop(DemoProduct.SONY_VLP_SW225.product.name)
        then: "The Express Shop page does not show 'Add To Quote' button,"
              "return policy, shipping information and a 'Price Notification' button"
            assert expressShop.addToQuoteButton.isEmpty()
            assert expressShop.addToQuoteLink.isEmpty()
            assert expressShop.returnPolicyTabField.isEmpty()
            assert expressShop.shippingTabField.isEmpty()
            assert expressShop.priceNotificationButton.isEmpty()
            assert expressShop.shippingShortInfoField.isEmpty()
            
        when: "Clicking 'Quick Order' on home page"
            to HomePage
            header.linkQuickOrder.click()
        then: "On Quick Order page there are no 'Add To Quote' buttons"
            assert addToQuoteButton.isEmpty()
            assert addToQuoteCSVButton.isEmpty()
            
        when: "Navigating to cart"
            header.search(DemoProduct.SONY_VLP_SW225.product.id)
            at ProductDetailPage
            addToCart()
        then: "There is no 'Create Quote Request' button, the cart cannot be sent as email, "
              "no Checkout button and a 'Transfer OCI Basket' button is shown"
            assert createQuoteLink.isEmpty()
            assert cartAsEmailLink.isEmpty()
            assert checkoutButton.isEmpty()
            assert transferOCIButton.isDisplayed()
            
        when: "Navigating to my Account"
            header.myAccountLink.click()
            
        then: "Only the link to Order Templates and Contracts are visible"
            at AccountHomePage
            accountNavBar.expandOrg()
            assert accountNavBar.logoutLink.isEmpty()
            assert accountNavBar.changeAddressLink.isEmpty()
            assert accountNavBar.profileSettingsLink.isEmpty()
            assert accountNavBar.costCentersLink.isEmpty()
            assert accountNavBar.punchoutLink.isEmpty()
            assert accountNavBar.usersLink.isEmpty()
            assert accountNavBar.approvedOrdersLink.isEmpty()
            assert accountNavBar.ordersToApproveLink.isEmpty()
            assert accountNavBar.rejectedOrdersLink.isEmpty()
            assert accountNavBar.pendingOrdersLink.isEmpty()
            assert accountNavBar.myRejectedOrdersLink.isEmpty()
            assert accountNavBar.ordersHistoryLink.isEmpty()
            assert accountNavBar.contractsLink.isDisplayed()
            assert accountNavBar.orderTemplatesLink.isDisplayed()
            
        where:
            login      << testData.get("punchout.user.login")
            password   << testData.get("punchout.user.password")
            hookUrl    << testData.get("punchout.user.hookUrl")
    }
    
    def "Call OCI function DETAIL"()
    {    
        given: "Login as OCI user with function DETAIL"
            loginOCIUser_DETAIL(login, password, DemoProduct.SONY_VLP_SW225.product.id, hookUrl)
        
        when: "The OCI user is on the respective details page"
            at ProductDetailPage
        then: "The product page is displayed."
            assert productSKU == DemoProduct.SONY_VLP_SW225.product.id
        where:
            login      << testData.get("punchout.user.login")
            password   << testData.get("punchout.user.password")
            hookUrl    << testData.get("punchout.user.hookUrl")
    }
    
    def "Check OCI Error handling when missing HOOK_URL"()
    {
        given: "Login as OCI user with missing HOOK_URL"
            loginOCIUser(login, password, "")
        
        when: "The OCI error page is opened"
            at OCIErrorPage

        then: "And an error is shown"
            assert missingParameterHookUrl.size() > 0
            
        where:
            login      << testData.get("punchout.user.login")
            password   << testData.get("punchout.user.password")
            hookUrl    << testData.get("punchout.user.hookUrl")
    }
    
    def "Check OCI Error handling when credentials are invalid"()
    {
        given: "Login with wrong name/password"
            loginOCIUser("invalid", "login", "")

        when: "The OCI error page is opened"
            at OCIErrorPage
            
        then: "A message is shown"
            assert unableToFindMembershipData.size() > 0
    }
    
    def "Call OCI function BACKGROUND_SEARCH"()
    {
        given: "Login as OCI user with function BACKGROUND_SEARCH"
            loginOCIUser_BACKGROUND_SEARCH(login, password, searchTerm, hookUrl)
        
        when: "The OCI user is on the OCI catalog form page"
            at OCICatalogPage
        then: "The page contains a form with product in OCI format."
            assert checkHookUrl(hookUrl)
            
            assert ociCatalog("1").productId    == ociProductId
            assert ociCatalog("1").description  == ociDescription
            assert ociCatalog("1").quantity     == ociQuantity
            assert ociCatalog("1").unit         == ociUnit
            assert ociCatalog("1").price        == ociPrice
            assert ociCatalog("1").currency     == ociCurrency
            assert ociCatalog("1").longText     == ociLongText
            assert ociCatalog("1").vendor       == ociVendor
            assert ociCatalog("1").vendorMat    == ociVendorMat
            assert ociCatalog("1").manufactCode == ociManufactCode
            assert ociCatalog("1").manufactMat  == ociManufactMat
            
            //actually five products are returned, but that might change over time
            assert ociCatalog("2").description  != null
            assert ociCatalog("3").description  != null
            assert ociCatalog("4").description  != null
        where:
            ociProductId    << testData.get("punchout.product.productId")
            ociDescription  << testData.get("punchout.product.description")
            ociQuantity     << testData.get("punchout.product.quantity")
            ociUnit         << testData.get("punchout.product.unit")
            ociPrice        << testData.get("punchout.product.price")
            ociCurrency     << testData.get("punchout.product.currency")
            ociLongText     << testData.get("punchout.product.longText")
            ociVendor       << testData.get("punchout.product.vendor")
            ociVendorMat    << testData.get("punchout.product.vendorMat")
            ociManufactCode << testData.get("punchout.product.manufactCode")
            ociManufactMat  << testData.get("punchout.product.manufactMat")
        
            login      << testData.get("punchout.user.login")
            password   << testData.get("punchout.user.password")
            hookUrl    << testData.get("punchout.user.hookUrl")
            searchTerm << testData.get("punchout.products.searchTerm")
    }
    
    def "Call OCI function VALIDATE"()
    {
        given: "Login as OCI user with function VALIDATE"
            loginOCIUser_VALIDATE(login, password, ociProductId, hookUrl)
        
        when: "The OCI user is on the OCI catalog form page"
            at OCICatalogPage
        then: "The page contains a form with one product in OCI format."
            assert checkHookUrl(hookUrl)
            
            assert ociCatalog("1").productId    == ociProductId
            assert ociCatalog("1").description  == ociDescription
            assert ociCatalog("1").quantity     == ociQuantity
            assert ociCatalog("1").unit         == ociUnit
            assert ociCatalog("1").price        == ociPrice
            assert ociCatalog("1").currency     == ociCurrency
            assert ociCatalog("1").longText     == ociLongText
            assert ociCatalog("1").vendor       == ociVendor
            assert ociCatalog("1").vendorMat    == ociVendorMat
            assert ociCatalog("1").manufactCode == ociManufactCode
            assert ociCatalog("1").manufactMat  == ociManufactMat
            
        where:
            ociProductId << testData.get("punchout.product.productId")
            ociDescription << testData.get("punchout.product.description")
            ociQuantity << testData.get("punchout.product.quantity")
            ociUnit << testData.get("punchout.product.unit")
            ociPrice << testData.get("punchout.product.price")
            ociCurrency << testData.get("punchout.product.currency")
            ociLongText << testData.get("punchout.product.longText")
            ociVendor << testData.get("punchout.product.vendor")
            ociVendorMat << testData.get("punchout.product.vendorMat")
            ociManufactCode << testData.get("punchout.product.manufactCode")
            ociManufactMat << testData.get("punchout.product.manufactMat")
            
            login      << testData.get("punchout.user.login")
            password   << testData.get("punchout.user.password")
            hookUrl    << testData.get("punchout.user.hookUrl")                      
    }

	def "Create a Punchout Configuration in My Account"()
    {
        given: "I have logged in and I am on the My Account homepage"
            logInUser(DemoUser.ACCOUNT_ADMIN_PUNCHOUT.user);
            
        when: "I navigate to the punchout page"
            clickPunchout()
            assert page instanceof PunchoutPage
            
        and: "Click the configuration button to open the punchout configuration page"
            configurationLink.click()
            assert page instanceof PunchoutConfigurationPage
            
        and: "set custom values for fields"
            configurationItems[2].transform.value "AA_{Sku}"
            configurationItems[2].format.value "UpperCase"
            
            configurationItems[0].transform.value "stk."
            configurationItems[0].format.value ""
            
        and: "I save the configuration"
            saveButton.click()
            
        then: "A success message appears"
            saveSuccessMessage.displayed
            
        when: "I leave the punchout page"
            cancelButton.click()
            
        and: "Click the configuration link again"
            configurationLink.click()
            
        then: "My modifications are displayed"
            assert configurationItems[2].label == "NEW_ITEM-MATGROUP"
            assert configurationItems[2].transform.value() == "AA_{Sku}"
            assert configurationItems[2].format.value() == "UpperCase"
            
            assert configurationItems[0].label == "NEW_ITEM-UNIT"
            assert configurationItems[0].transform.value() == "stk."
            assert configurationItems[0].format.value() == ""
            
//        when: "Click the configuration link again (reset)"
//            configurationLink.click()
//            assert page instanceof PunchoutConfigurationPage

        when: "I reset custom values for fields"
            configurationItems[2].transform.value ""
            configurationItems[2].format.value ""
            
            configurationItems[0].transform.value ""
            configurationItems[0].format.value ""
            
        and: "I save the configuration"
            saveButton.click()
            
        then: "My modifications are displayed"
            assert configurationItems[2].label == "NEW_ITEM-MATGROUP"
            assert configurationItems[2].transform.value() == ""
            assert configurationItems[2].format.value() == ""
            
            assert configurationItems[0].label == "NEW_ITEM-UNIT"
            assert configurationItems[0].transform.value() == ""
            assert configurationItems[0].format.value() == ""
            
    }
	
    /**
     * Runs after all tests in this Spec run once.*
     */
    def cleanupSpec()
    {
        //Delete the newly created test OCI user
        
        given: "I have logged in and am on My Account Home Page"
            logInUser(DemoUser.ACCOUNT_ADMIN.user);
        
         when:
           at AccountHomePage
         and:
             clickUsers()
         then: "The users page is displayed"
             at UsersPage
             
        //we could try to save the user id during creation    
        def ociUserRow = $('div[class="list-item-row"]', text:contains("OCI User"))
             
        ociUserRow.find('[data-testing-id^="user-delete-"]').click()
        
        //wait for modal dialog Remove button
        waitFor(){$("button", name: "DeleteUser").size()>0 && $("button", name: "DeleteUser").isDisplayed()}
                
        $("button", name: "DeleteUser").click()
    }
}