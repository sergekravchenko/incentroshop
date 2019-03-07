package geb.com.intershop.b2x.specs.storefront.responsive

import geb.com.intershop.b2x.pages.storefront.responsive.HomePage
import geb.com.intershop.b2x.pages.storefront.responsive.shopping.CategoryPage
import geb.com.intershop.b2x.testdata.TestDataLoader
import geb.com.intershop.inspired.pages.storefront.responsive.*
import geb.com.intershop.inspired.pages.storefront.responsive.account.*
import geb.com.intershop.inspired.pages.storefront.responsive.checkout.*
import geb.com.intershop.inspired.pages.storefront.responsive.shopping.*
import geb.spock.GebReportingSpec
import spock.lang.*


/**
 * Storefront tests for Category rich content
 *
 */
@Ignore
class CategoryRichContentSpec extends GebReportingSpec
{
    private static Map<String,List> testData;
    
    def setupSpec()
    {
        setup:
        testData = TestDataLoader.getTestData();
    }
       
    /**
     * Check rich content on category page computers-tablets
     *
     */
    def "Check rich content on tablets category page"()
    {
        when: "I go to the signIn page and log in..."
        to HomePage
        then: "I am at the homepage"
            at HomePage
        
        when: "Clicking a the 'Computers' catalog link"
        clickCategoryLink(categoryId1)

        then: "I am at the 'Computers' category page"
        at CategoryPage
        withCategory(categoryId1)

        and: "A subcategory 'Tablets' exists"
        assert subCategoryLink(categoryId2).size() > 0

        when: "Goto 'Tablets' page"
        subCategoryLink(categoryId2).click()

        then: "I am at the Tablets category page"
        at CategoryPage
        
        and: "The expected rich content is found"
        assert richContent.text().contains(categoryRichContent)
        
        where:
        categoryId1 = testData.get("categoryRichContent.computers.id")[0]
        categoryId2 = testData.get("categoryRichContent.tablets.id")[0]
        categoryRichContent = testData.get("categoryRichContent.tablets.text")[0]
        
    }

}

