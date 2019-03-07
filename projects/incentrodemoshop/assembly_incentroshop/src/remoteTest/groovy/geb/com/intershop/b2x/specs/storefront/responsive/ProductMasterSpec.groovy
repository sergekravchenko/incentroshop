package geb.com.intershop.b2x.specs.storefront.responsive

import geb.com.intershop.b2x.pages.storefront.responsive.*
import geb.com.intershop.b2x.pages.storefront.responsive.account.*
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.*
import geb.com.intershop.b2x.pages.storefront.responsive.shopping.*
import geb.com.intershop.b2x.testdata.TestDataLoader
import geb.spock.GebReportingSpec
import spock.lang.*


/**
 * Storefront tests for Product Master at inSPIRED - B2X
 *
 */
@Ignore
@Timeout(600)
class ProductMasterSpec extends GebReportingSpec {
    
    private static Map<String,List> testData;

    def setupSpec() {
        setup:
        testData = TestDataLoader.getTestData();
    }

    /**
     * Check Master Product in the search result<p>
     */
    def "Check Master Product Search"() {
        when: "I go to the home page, search for master product..."
        to HomePage
        at HomePage
        header.search searchTerm
        
        then: "Now I can see the products from the search '" + searchTerm + "'"        
        at SearchResultPage
        productTiles(productName).size()>0
        and: "I see all variations of the product"
        productTiles(productName).checkVariationsCount("6")
        where:
        searchTerm  << testData.get("defaultProduct.master.name")
        productName << testData.get("defaultProduct.master.defaultVariation.name")
    }
    
    /**
     * Filters variations, check variation count of certain filter combinations 
     */
    def "Filter Variations at Family Page"() {
        when: "I go to the home page and click on a catalog..."
        to HomePage
        at HomePage
        selectCatalog(categoryTerm)

        then: "... now I'm at the category page...."
        at CategoryPage
        withCategory(categoryTerm)

        when: "... and choose one."
        familyTiles(familyCode).click()
        
        then: "I can see all products of this subcategory."
        at FamilyPage
        showsProduct(masterName)
        productTiles(masterName).checkVariationsCount(variationCount)
        and: "there are selection filters available"
        getFilterLinkByID(filterSelect1).size() > 0 && getFilterLinkByID(filterSelect2).size() > 0
        
        when: "I filter for a specific value..."
        getFilterLinkByID(filterSelect1).click()

        then: "the filter is selected"
        checkSelectedFilterValue(filterValue1)
        and: "the variation count reflects the applied filter"
        productTiles(masterName).checkVariationsCount(filter1VariationCount)
        and: "the non-existing variation combination is gone"
        getFilterLinkByID(filterSelect2).isEmpty()
        
        when: "I deselect the filter value 1"
        deselectFilterInNavigationBar(filterValue1)
        then: "The filter value is visible again"
        getFilterLinkByID(filterSelect2).size() > 0
        
        when: "I click the select filter 2"
        getFilterLinkByID(filterSelect2).click(); 
        then: "The 1 variation master product is shown" 
        showsProduct(masterName)
        and: "the variation count reflects the applied filter"
        productTiles(masterName).checkVariationsCount(filter2VariationCount)

        when: "I de-select the filter 2"
        getFilterLinkByID(filterSelect2).click();
        and: "I switch to listview..."
        listViewButton.click()

        then: "I can see again all products of this subcategory in a list view."
        at FamilyPage
        showsProduct(masterName)
        
        when: "And then I click on listed product"
        productTiles(masterName).click()

        then: "... to see details of the master product."
        at ProductDetailPage
        lookedForSKU productSKU

        where:
        categoryTerm  << testData.get("defaultProduct.category.title")
        familyCode    << testData.get("defaultProduct.family.title")
        productSKU    << testData.get("defaultProduct.master.sku")
        masterName    << testData.get("defaultProduct.master.name")
        filterSelect1 << testData.get("defaultProduct.master.filter1.id")
        filterValue1  << testData.get("defaultProduct.master.filter1.value")
        filterSelect2 << testData.get("defaultProduct.master.filter2.id")
        variationCount<< testData.get("defaultProduct.master.variationCount")
        filter1VariationCount << testData.get("defaultProduct.master.filter1.variationCount")
        filter2VariationCount << testData.get("defaultProduct.master.filter2.variationCount")
    }
    
    /**
     * Check Master Product page with the respective content and variation products<p>
     */
    def "Check Master Product Detail Page Content"() {
        when: "I go to the home page, search for master product..."
        to HomePage
        at HomePage
        header.search searchTerm

        then: "Now I can see all products from the search."        
        at SearchResultPage
        productTiles(masterName).size()>0
        
        when: "I switch to listview and click on listed product."
        listViewButton.click()
        productTiles(masterName).click()

        then: "... to see details."
        at ProductDetailPage
        lookedForSKU variationSKU
        
        when: "In master details page I switch to listview..."
        listViewButton.click()
        
        then: "... to see the variation list in a list view."
        at ProductDetailPage
        assert $("h3", text:iContains(productFilterHeading)).size() > 0
        assert $("ul", id:"Colour_of_product_id").size() > 0
        assert $("div",class:"product-list").text().contains(variationSKU)

        where:
        searchTerm  << testData.get("defaultProduct.master.name")
        productFilterHeading  << testData.get("defaultProduct.master.filter.heading")
        variationSKU  << testData.get("defaultProduct.master.defaultVariation.sku")
        masterSKU  << testData.get("defaultProduct.master.sku")
        masterName << testData.get("defaultProduct.master.name")
    }
}
