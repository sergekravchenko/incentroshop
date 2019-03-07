package rest.com.intershop.b2x.specs.storefront.store

import geb.com.intershop.b2x.model.storefront.responsive.Store
import geb.com.intershop.b2x.model.storefront.responsive.demo.*
import geb.com.intershop.b2x.testdata.TestDataLoader
import rest.com.intershop.b2x.specs.storefront.common.RestSpec
import spock.lang.*

@Narrative("""
As a customer
I want to use the Intershop REST API
to get stores
""")
@Title("i want to get stores")
class StoreLocatorRestTest extends RestSpec
{
    @Shared String postalCodeParameter, countryCodeParameter, cityParameter, storeNameParameter, storeRequestURI

    private static  Collection<Store> allDemoStores;

    def setupSpec()
    {
        allDemoStores           = DemoStore.findAll();
        countryCodeParameter    = "Germany"
        postalCodeParameter     = "28039"
        cityParameter           = "Jena"
        storeNameParameter      = "inSPIRED Village"

        storeRequestURI         =  testData.get("rest.base.uri")[0] + "/stores";
    }

    @Unroll
    def "Happy path test Store Locator via #contentType"()
    {
        given:

            client.headers['Content-Type']  = contentType
            client.headers['Accept']        = contentType

        when: "Get Stores without search parameters."
            
            def response = client.get(uri: storeRequestURI, requestContentType: contentType)
        
        then: "Test if all existing stores are available in the response."
            
            with(response)
            {
                def elementMap =  response.data.elements
                
                // compare number of received store elements with number of existing stores
                assert elementMap.size() == allDemoStores.size()
                
                // each received store should contain in existing store list
                def elementIterator = elementMap.iterator();
                while(elementIterator.hasNext())
                {
                    def foundedName = elementIterator.next().name
                    assert allDemoStores.find { it.getStore().getStoreID() == foundedName } 
                }
                
                // response status
                assert success
                assert status == 200
            }


        when: "Get Stores with search parameter country."
            
            response = client.get(uri: storeRequestURI + "?countryCode=DE", requestContentType: contentType)
        
        then: "Test if all stores with country germany are available in the response."
            
            with(response)
            {
                def elementMap =  response.data.elements
                
                // compare number of received store elements with number of existing stores with country "Germany"
                assert elementMap.size() == allDemoStores.findAll {it.getAddress().country == countryCodeParameter }.size()
                
                // response status
                assert success
                assert status == 200
            }

        when: "Get Stores with search parameter postalCode."
            
               response = client.get(uri: storeRequestURI + "?postalCode=28039", requestContentType: contentType)
        
        then: "Test if all stores with specific postalCode are available in the response."
            
            with(response)
            {
                def elementMap =  response.data.elements
                
                // compare number of received store elements with number of existing stores with countryCode "28039"
                def actuallyExistingStores = allDemoStores.findAll {it.getAddress().postCode == postalCodeParameter }
                assert elementMap.size() == actuallyExistingStores.size()
                assert elementMap.get(0).get("name") == actuallyExistingStores.iterator().next().getStore().getStoreID()
                 
                // response status
                assert success
                assert status == 200
            }


        when: "Get Stores with search parameter city."
            
            response = client.get(uri: storeRequestURI + "?city=" + cityParameter, requestContentType: contentType)
        
        then: "Test if all stores with specific city are available in the response."
            
            with(response)
            {
                def elementMap =  response.data.elements
                
                // compare number of received store elements with number of existing stores with city "Jena"
                def actuallyExistingStores = allDemoStores.findAll {it.getAddress().city == cityParameter }
                assert elementMap.size() == actuallyExistingStores.size()
                assert elementMap.get(0).get("name") == actuallyExistingStores.iterator().next().getStore().getStoreID()
                 
                // response status
                assert success
                assert status == 200
            }
            
        when: "Get Stores with search parameter store name."
            
            response = client.get(uri: storeRequestURI + "?storeName=inspired%20Village", requestContentType: contentType)
        
        then: "Test if all stores with specific store name are available in the response."
            
            with(response)
            {
                def elementMap =  response.data.elements
                
                // compare number of received store elements with number of existing stores with store name "Oceanside Paradies Store"
                def actuallyExistingStores = allDemoStores.findAll {it.getStore().getStoreID() == storeNameParameter }
                assert elementMap.size() == actuallyExistingStores.size()
                assert elementMap.get(0).get("name") == actuallyExistingStores.iterator().next().getStore().getStoreID()
                 
                // response status
                assert success
                assert status == 200
            }
        where:
            contentType << [JSON]

    }

	@Unroll
    def "Happy path test Store Locator via #contentType"() {
        
        given:

            client.headers['Content-Type'] = contentType
            client.headers['Accept'] = contentType


        when: "Get Stores without search parameters."
            
            def response = client.get(uri: storeRequestURI)
        
        then: "Test if all existing stores are available in the response."
            
            with(response)
            {
                def elementMap =  response.data.elements.StoreLocation
                
                // compare number of received store elements with number of existing stores
                assert elementMap.size() == allDemoStores.size()
                
                // each received store should contain in existing store list
                def elementIterator = elementMap.iterator();
                while(elementIterator.hasNext())
                {
                    def foundedStreet = elementIterator.next().city.toString()
                    assert allDemoStores.find { it.getAddress().city == foundedStreet }
                }
                
                // response status
                assert success
                assert status == 200
            }
            
        when: "Get Stores with search parameter country."
            
            response = client.get(uri: storeRequestURI + "?countryCode=DE", requestContentType: contentType)
        
        then: "Test if all stores with country germany are available in the response."
            
            with(response)
            {
                def elementMap =  response.data.elements.StoreLocation
                
                // compare number of received store elements with number of existing stores with country "Germany"
                assert elementMap.size() == allDemoStores.findAll {it.getAddress().country == countryCodeParameter }.size()
                
                // response status
                assert success
                assert status == 200
            }
            
        when: "Get Stores with search parameter postalCode."
            
            response = client.get(uri: storeRequestURI + "?postalCode=28039", requestContentType: contentType)
        
        then: "Test if all stores with specific postalCode are available in the response."
            
            with(response)
            {
                def elementMap =  response.data.elements.StoreLocation
                
                // compare number of received store elements with number of existing stores with countryCode "28039"
                def actuallyExistingStores = allDemoStores.findAll {it.getAddress().postCode == postalCodeParameter }
                assert elementMap.size() == actuallyExistingStores.size()
                assert elementMap.iterator().next().postalCode.toString() == actuallyExistingStores.iterator().next().getAddress().postCode
                 
                // response status
                assert success
                assert status == 200
            }
            
        when: "Get Stores with search parameter city."
            
            response = client.get(uri: storeRequestURI + "?city=" + cityParameter, requestContentType: contentType)
        
        then: "Test if all stores with specific city are available in the response."
            
            with(response)
            {
                def elementMap =  response.data.elements.StoreLocation
                
                // compare number of received store elements with number of existing stores with city "Jena"
                def actuallyExistingStores = allDemoStores.findAll {it.getAddress().city == cityParameter }
                assert elementMap.size() == actuallyExistingStores.size()
                assert elementMap.iterator().next().city.toString() == actuallyExistingStores.iterator().next().getAddress().city
                 
                // response status
                assert success
                assert status == 200
            }
            
        when: "Get Stores with search parameter store name."
            
            response = client.get(uri: storeRequestURI + "?storeName=inspired%20Village", requestContentType: contentType)
        
        then: "Test if all stores with specific store name are available in the response."
            
            with(response)
            {
                def elementMap =  response.data.elements.StoreLocation
                
                // compare number of received store elements with number of existing stores with store name "Oceanside Paradies Store"
                def actuallyExistingStores = allDemoStores.findAll {it.getStore().getStoreID() == storeNameParameter }
                assert elementMap.size() == actuallyExistingStores.size()
                                 
                // response status
                assert success
                assert status == 200
            }
        where:
            contentType << [XML]

    }
}