package geb.com.intershop.b2x.pages.storefront.responsive.shopping

import geb.com.intershop.b2x.pages.storefront.responsive.StorefrontPage
import geb.com.intershop.b2x.pages.storefront.responsive.modules.CategoryTile

class CategoryPage extends StorefrontPage
{

    static at=
    {
        return familyTiles.size()>0
    }

    static content=
    {
        contentSlot   { $("div",class:"category-page") }
        navigationBar { contentSlot.find("div",class:"category-panel") }
        categoryName  { contentSlot.@"data-testing-id" }
        categoryList  { contentSlot.$("ul",class:contains("category-list")) }
        familyTiles   { term ->  module(new CategoryTile(categoryID: term)) }
        richContent   { contentSlot.$('[data-testing-id="category-rich-content"]') }
    }

    //------------------------------------------------------------
    // Page checks
    //------------------------------------------------------------
    def withCategory(name)
    {
        categoryName==name
    }

    def clickCategoryLink(categoryId) {
        $('[data-testing-id="'+categoryId+'-link"]').click()
    }
    
    def subCategoryLink(name)
    {
        return $('[data-testing-id="category-'+name+'"] a');
    }
   
}
