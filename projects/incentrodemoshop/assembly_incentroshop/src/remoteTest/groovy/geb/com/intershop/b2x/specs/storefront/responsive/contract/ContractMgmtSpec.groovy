package geb.com.intershop.b2x.specs.storefront.responsive.contract

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import geb.com.intershop.b2x.model.backoffice.Contract
import geb.com.intershop.b2x.model.storefront.responsive.Product
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.backoffice.authentication.BackOfficeAuthentication
import geb.com.intershop.b2x.pages.backoffice.channel.BackOfficeChannelOverviewPage
import geb.com.intershop.b2x.pages.backoffice.channel.ChannelMgmtCustomersPage
import geb.com.intershop.b2x.pages.backoffice.customer.*
import geb.com.intershop.b2x.pages.storefront.responsive.*
import geb.com.intershop.b2x.pages.storefront.responsive.account.*
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.*
import geb.com.intershop.b2x.pages.storefront.responsive.contract.*
import geb.com.intershop.b2x.pages.storefront.responsive.shopping.*
import geb.com.intershop.b2x.specs.storefront.responsive.features.Authentication
import geb.com.intershop.b2x.specs.storefront.responsive.features.CheckoutProduct
import geb.com.intershop.b2x.testdata.TestDataLoader
import geb.spock.GebReportingSpec
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
class ContractMgmtSpec extends GebReportingSpec implements BackOfficeAuthentication, Authentication, CheckoutProduct
{
    private static final String BUSINESS_CHANNEL = "inTRONICS Business"
    // keep the contracts to handle invalidation during clean-up
    @Shared
    private Stack<Contract> contracts = new Stack()
    
    @Shared
    private Contract testContract

    @Shared
    private String restUrl
    
    @Shared
    private Map<String,List> testData
    
    @Shared
    private User adminUser = new User("admin", "admin", "admin")
    
    def setupSpec()
    {
        setup:
        testData = TestDataLoader.getTestData()

        restUrl = baseUrl + testData.get("contract.rest.uri")[0]
        
        String customerID = testData.get("contract.customer.id")[0]
        
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        def contractStartDate = currentDate.format(formatter);
        
        // contract should end in one week
        def contractEndDate = currentDate.plusWeeks(3).format(formatter);
        
        String contractId = "Contract_" + System.currentTimeMillis()
        String contract_comment = "Comment_${contractId}"
        testContract = new Contract(contractId, contractStartDate, contractEndDate, Contract.CONTRACT_TYPE, contract_comment)
        
        given: "Logged in as admin.."
            logInUser(adminUser, "inSPIRED")
        
        when: "I want to assign a Customer Administrator"
            selectChannel(BUSINESS_CHANNEL)
            at BackOfficeChannelOverviewPage
        and:
            navigateToMainMenuItem "bo-sitenavbar-customers", "link-customers-customers-channel"
            at ChannelMgmtCustomersPage
        and:"I'm at the ChannelMgmtCustomersPage."
            selectById(customerID)
        and: " the Customer Account Manager Overview is opened ..."
            at CustomersMgmtDetailPage
            
            clickAccountManagers()
            
            boolean adminAssigned = $("td", class: "column-text", text: contains("admin admin")).size() > 0
    
            // at CustomersAccountManagersPage
            // Assign all shown users as account managers
            if (adminAssigned == false) {
                assignAllUsers()
            }
        then: " the admin is assigned as Customer Administrator."
            assert $("td", class: "column-text", text: contains("admin admin")).size() > 0            
    }
    
    def "configure a new contract with an existing pricelist"() {
        
        given: "on ... page "
            at CustomersAccountManagersPage
        /*
         * Get one of the price lists from the demo_responsive_b2b test data
         * */
        when: "... create a new contract."
            clickContracts()
            clickNew()
            
            createContract(testContract)
            
            // add to contracts stack for later clean-up
            contracts.push testContract
            
            //at EditContractDetailPage
        and: "I switch to configuration tab, "
            gotoConfigurationTab()
            
        // at ContractConfigurationPage
        and: ".. configure the sales target of the contract."
            configureContract(priceList)
                
        and: "Then I go to the contracts list (Customers -> Customers)"
            navigateToMainMenuItem "bo-sitenavbar-customers", "link-customers-contracts-channel"
        then: "I'm on contract list of all customers"
            at ContractsListPage
            assert existsContract(testContract)
                
        where:
            priceList << testData.get("contract.pricelist.name")
    }
    
    def "Checkout a product which is in the contract pricelist"() {
        User buyer = new User(userLogin, "Bernhard", "Assange")    
        Product laptop = new Product("Laptop", "Laptop", productSKU)
        
        given: "I log into storefront as account admin"
            logInUser(buyer)
        when: "checkout a product"
            checkoutProduct laptop
        then:
            at CheckoutReceiptPage 
        where:
            userLogin           << testData.get("contract.user.login")
            productSKU          << testData.get("contract.pricelist.product.sku")
    }
    
    def "Verify that order was assigned to the correct contract"() {
        given:
            at CheckoutReceiptPage
        /*
         * CHECK CONTRACT INFORMATION
         */            
        when: "navigate to the MyAccount area and "
    		clickMyAccount()
        and: 
            clickContracts()
        and: "goto the contract details page and "
            gotoContractDetailsOf testContract
    
        // check if product price is current revenue
        then: "check if the order was assigned to the contract"
            at AccountContractDetailsPage
            assert checkContract(productPrice) == true
                        
        where:
            productPrice        << testData.get("contract.pricelist.product.price")
    }

    /**
     * Runs after all tests in this Spec run once.
     * Disables all price lists
     * Contracts with orders cannot be deleted
     * 
     */
    def cleanupSpec() {
        
        String pricelist4cleanup = testData.get("contract.pricelist4cleanup.name")[0]
                        
        given: "Logged in as admin.."
            logInUser(adminUser, "inSPIRED")
            
        when: "I want to switch to the B2B Channel"
            selectChannel("inTRONICS Business")
        then:
            at BackOfficeChannelOverviewPage
            
            // goto Customers -> Customers
            navigateToMainMenuItem "bo-sitenavbar-customers", "link-customers-contracts-channel"
            at ContractsListPage
            
            while ( !contracts.empty )
            {
                Contract contract = contracts.pop()
                                
                gotoContract contract
                
                at EditContractDetailPage
                gotoConfigurationTab()
                
                //at ContractConfigurationPage
                configureContract(pricelist4cleanup)
            }
            logOutUser()
    }
}