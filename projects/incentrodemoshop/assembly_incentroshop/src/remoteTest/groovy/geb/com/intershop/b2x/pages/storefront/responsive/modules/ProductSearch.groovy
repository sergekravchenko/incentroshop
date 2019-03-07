package geb.com.intershop.b2x.pages.storefront.responsive.modules

import geb.Module
import geb.module.TextInput

class ProductSearch extends Module
{
    static base = {
        $('form', name:'SearchBox_Header')
    }
    
    static content =
    {
        searchField  { $(name: "SearchTerm").module(TextInput) }
        searchButton { $(name: "search") }
        
    }
    
    def search(String searchTerm) {
        searchField.text = searchTerm
        searchButton.click()
    }   
}