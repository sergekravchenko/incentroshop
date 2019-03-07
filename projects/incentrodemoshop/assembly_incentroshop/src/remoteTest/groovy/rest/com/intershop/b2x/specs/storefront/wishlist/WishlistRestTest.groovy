package rest.com.intershop.b2x.specs.storefront.wishlist

import groovy.json.*
import rest.com.intershop.b2x.specs.storefront.common.RestSpec
import spock.lang.*


@Narrative("""
As a customer
I want to use the Intershop Webshop REST API
to manage my wishlists
""")
@Title("I want to handle wishlists")
class WishlistRestTest extends RestSpec
{
    @Shared
    String wishlistsURI, wishlistURI, authenticationToken, wishListID, body

    @Shared
    private Object response,user,customer,password

    def setupSpec()
    {
        setup:

            customer             = testData.get("rest.address.customerNo")[0];
            user                 = testData.get("rest.address.eMail")[0];
            password            = testData.get("rest.address.password")[0];

            wishlistsURI        = testData.get("rest.base.uri")[0] + "/customers/-/users/-/wishlists";

            client.getClient().getParams().setParameter("http.connection.timeout", new Integer(60000))
            client.getClient().getParams().setParameter("http.socket.timeout", new Integer(60000));
    }

    def "Create Wishlist Test"()
    {
        def contentType = JSON

        given:
            
            client.headers['Content-Type'] = contentType
            client.headers['Accept'] = contentType

        when: "create wishlist anonymously"

            body = "{ \"title\": \"MyWishlist\"}"
            response = client.post(uri: wishlistsURI, requestContentType: JSON, body: body)
            
        then: "Request failed => status code should be 404"
            
            with(response)
            {
                assert !success
                assert status == 401
            }

        when: "Create wishlist with wrong content"
            
            client.setAuthorizationHeader(user, password)
            body = "{ \"title\": \"MyWishlist\", \"public\":\"blubb\", \"preferred\" :\"false\"}"
            response = client.post(uri: wishlistsURI, requestContentType: JSON, body: body)
        
        then: "Request failed => status code should be 400"
        
            with(response)
            {
                assert !success
                assert status == 400
            }
            
        when: "Create correct wishlist"
            
            client.setAuthorizationHeader(user, password)
            body = "{ \"title\": \"MyWishlist2\", \"public\":\"false\", \"preferred\" :\"false\"}"
            response = client.post(uri: wishlistsURI, requestContentType: JSON, body: body)
        
        then: "Request success => status code should be 201"
        
            with(response)
            {
                assert success
                assert status == 201
            }
        
        cleanup:
            
            client.headers.remove('Content-Type')
            client.headers.remove('Accept')
            client.clearAuthorizationHeader()
    }

    
    def "Get Wishlists Test"()
    {
        def contentType = JSON

        given:
            
            client.headers['Content-Type'] = contentType
            client.headers['Accept'] = contentType

        // create 3 wishlist
        createWishlist("MyWishlist")
        createWishlist("MyWishlist2")
        createWishlist("MyVerySpecialWishlist")
        
        when: "Get wishlists"
       
            response = client.get(uri: wishlistsURI, requestContentType: contentType)
            
        then: "Request success => status code should be 200"
            
            with(response)
            {
                assert success
                assert status == 200
                assert data.elements.find{
                    it.title.equals("MyWishlist")
                }
                assert data.elements.find{
                    it.title.equals("MyWishlist2")
                }
                assert data.elements.find{
                    it.title.equals("MyVerySpecialWishlist")
                }
                
            }

        when: "Get wishlists without authorization"
            
            client.clearAuthorizationHeader()
            response = client.get(uri: wishlistsURI, requestContentType: contentType)
        
        then: "Request failed => status code should be 401"
        
            with(response)
            {
                assert !success
                assert status == 401
            }
            
        cleanup:
            
            client.headers.remove('Content-Type')
            client.headers.remove('Accept')
            client.clearAuthorizationHeader()
    }
    
    
    def "Get Wishlist Test"()
    {
        def contentType = JSON

        given:

            client.headers['Content-Type'] = contentType
            client.headers['Accept'] = contentType

            // create a wishlist
            createWishlist("MyWishlist")
       
        when: "Get wishlist with unknown id"
       
            response = client.get(uri: wishlistsURI + "/wrongWishlistID" , requestContentType: contentType)
            
        then: "Request failed => status code should be 404"
            
            with(response)
            {
                assert !success
                assert status == 404
            }

        when: "Get wishlist without authorization"
            
            client.clearAuthorizationHeader()
            response = client.get(uri: wishlistURI, requestContentType: contentType)
        
        then: "Request failed => status code should be 401"
        
            with(response)
            {
                assert !success
                assert status == 401
            }
            
        when: "Get wishlist with unknown customer"
        
            client.setAuthorizationHeader("blubber", "blubber")
            response = client.get(uri: wishlistURI, requestContentType: contentType)
        
        then: "Request failed => status code should be 401"
        
            with(response)
            {
                assert !success
                assert status == 401
            }
       
        when: "Get wishlist with current user"
            
            client.setAuthorizationHeader(authenticationToken)
            response = client.get(uri: wishlistURI, requestContentType: contentType)
        
        then: "Request success => status code should be 200"
        
            with(response)
            {
                assert success
                assert status == 200
                assert data.preferred == false
                assert data.public == false
                assert data.type == "WishList"
                assert data.title == "MyWishlist"

            }
        cleanup:
            
            client.headers.remove('Content-Type')
            client.headers.remove('Accept')
            client.clearAuthorizationHeader()
    }
    

    def "Delete Wishlist Test"()
    {
        def contentType = JSON

        given:
            
            client.headers['Content-Type'] = contentType
            client.headers['Accept'] = contentType

            // create a wishlist
        createWishlist("MyWishlist")
       
        when: "Delete wishlist anonymously"
       
            client.clearAuthorizationHeader()
            response = client.delete(uri: wishlistURI, requestContentType: JSON)
            
        then: "Request failed => status code should be 404"
            
            with(response)
            {
                assert !success
                assert status == 401
            }

        when: "Delete wishlist with unknown id"
            
            client.setAuthorizationHeader(authenticationToken)
            response = client.delete(uri: wishlistsURI + "/wrongWishlistID" , requestContentType: JSON)
        
        then: "Request failed => status code should be 404"
        
            with(response)
            {
                assert !success
                assert status == 404
            }
            
//        when: "Delete existing wishlist"
//            
//            client.headers['authentication-token'] = authenticationToken
//            client.handler.failure = client.handler.success
//            response = client.delete(uri: wishlistURI, requestContentType: JSON)
//            
//        then: "Request success => status code should be 204"
//        
//             assert response.status == 204
        
        cleanup:
            
            client.headers.remove('Content-Type')
            client.headers.remove('Accept')
            client.clearAuthorizationHeader()
    }
    
    
    def "Update Wishlist Test"(){
        
        def contentType = JSON

        given:
            
            client.headers['Content-Type'] = contentType
            client.headers['Accept'] = contentType

            // create a wishlist
            createWishlist("AnotherWishlist")
       
        when: "Update wishlist anonymously"
       
            client.clearAuthorizationHeader()
            body = "{ \"title\": \"UpdateAnotherWishlist\", \"public\":\"false\", \"preferred\" :\"false\"}"
            response = client.put(uri: wishlistURI , requestContentType: contentType, body: body)
            
        then: "Request failed => status code should be 401"
            
            with(response)
            {
                assert !success
                assert status == 401
            }

        when: "Update wishlist with unknown ID"
        
             client.setAuthorizationHeader(authenticationToken)
             body = "{ \"title\": \"UpdateAnotherWishlist\", \"public\":\"false\", \"preferred\" :\"false\"}"
             response = client.put(uri: wishlistsURI + "/wrongWishlistID" , requestContentType: contentType, body: body)
             
        then: "Request failed => status code should be 404"
             
             with(response)
             {
                 assert !success
                 assert status == 404
             }
                 
        when: "Update wishlist with missing field"
            
            body = "{ \"public\":\"false\", \"preferred\" :\"false\"}"
            response = client.put(uri: wishlistURI, requestContentType: contentType, body: body)
        
        then: "Request failed => status code should be 400"
        
            with(response)
            {
                assert !success
                assert status == 400
            }
            
        when: "Update Wishlist successfully"
            
            body = "{ \"title\": \"Update AnotherWishlist\", \"public\":\"true\", \"preferred\" :\"true\"}"
            response = client.put(uri: wishlistURI, requestContentType: contentType, body: body)
        
        then: "Request success => status code should be 200"
        
            with(response)
            {
                assert success
                assert status == 200
                assert data.preferred == true
                assert data.public == true
                assert data.type == "WishList"
                assert data.title == "Update AnotherWishlist"

            }
        cleanup:
            
            client.headers.remove('Content-Type')
            client.headers.remove('Accept')
            client.clearAuthorizationHeader()
    }
    
    
    def "Share Wishlist Test"(){
        
        def contentType = JSON

        given:
            
            client.headers['Content-Type'] = contentType
            client.headers['Accept'] = contentType

            // create a wishlist
            createWishlist("ToBeShared Wishlist")
       
        when: "Share wishlist anonymously"
            
            client.clearAuthorizationHeader()

            body = "{ \"message\": \"Hey, this is my wishlist!\", \"sender\":\"John Doe\", \"recipients\" :\"patricia@test.intershop.de\"}"
            response = client.post(uri: wishlistURI + "/share" , requestContentType: contentType, body: body)
            
        then: "Request failed => status code should be 401"
            
            with(response)
            {
                assert !success
                assert status == 401
            }

        when: "Share wishlist with unknown ID"
        
             client.setAuthorizationHeader(authenticationToken)
             body = "{ \"message\": \"Hey, this is my wishlist!\", \"sender\":\"John Doe\", \"recipients\" :\"patricia@test.intershop.de\"}"
             response = client.post(uri: wishlistsURI + "/anotherWrongWishlistID/share" , requestContentType: contentType, body: body)
             
        then: "Request failed => status code should be 404"
             
             with(response)
             {
                 assert !success
                 assert status == 404
             }
                 
        when: "Share wishlist with missing field"
            
            body = "{ \"message\": \"Hey, this is my wishlist!\", \"sender\":\"John Doe\"}"
            response = client.post(uri: wishlistURI + "/share" , requestContentType: contentType, body: body)
        
        then: "Request failed => status code should be 400"
        
            with(response)
            {
                assert !success
                assert status == 400
            }
            
//        when: "Share Wishlist successfully"
//            
//            body = "{ \"message\": \"Hey, this is my wishlist!\", \"sender\":\"John Doe\", \"recipients\" :\"patricia@test.intershop.de\"}"
//            response = client.post(uri: wishlistURI + "/share" , requestContentType: contentType, body: body)
//        
//        then: "Request success => status code should be 201"
//        
//            with(response)
//            {
//                assert success
//                assert status == 201
//
//            }
        cleanup:
            
            client.headers.remove('Content-Type')
            client.headers.remove('Accept')
            client.clearAuthorizationHeader()
    }
    
    
    def "Get Wishlist settings Test"()
    {
        def contentType = JSON

        given:
            
            client.headers['Content-Type'] = contentType
            client.headers['Accept'] = contentType

        when: "get wishlist settings anonymously"
       
            client.clearAuthorizationHeader()

            response = client.get(uri: wishlistsURI + "/settings" , requestContentType: contentType)
            
        then: "Request failed => status code should be 401"
            
            with(response)
            {
                assert !success
                assert status == 401
            }

        when: "Get wishlist settings successfully"
        
             client.setAuthorizationHeader(authenticationToken)
             response = client.get(uri: wishlistsURI + "/settings" , requestContentType: contentType)
             
        then: "Request success => status code should be 200"
             
             with(response)
             {
                 assert success
                 assert status == 200
                 assert data.type == "WishlistSettings"
             }
                 
       
        cleanup:
            
            client.headers.remove('Content-Type')
            client.headers.remove('Accept')
            client.clearAuthorizationHeader()
    }

    def "Update Wishlist settings Test"()
    {
        def contentType = JSON

        given:

            client.headers['Content-Type'] = contentType
            client.headers['Accept'] = contentType

        when: "Udate wishlist settings anonymously"

            body = "{ \"usePreferredWishlist\": \"true\"}"
            response = client.put(uri: wishlistsURI + "/settings" , requestContentType: contentType, body: body)
            
        then: "Request failed => status code should be 401"
            
            with(response)
            {
                assert !success
                assert status == 401
            }
            
        when: "Update wishlist settings with wrong data"
        
             client.setAuthorizationHeader(authenticationToken)
             body = "{ \"usePreferredWishlist\": \"blubb\"}"
             response = client.put(uri: wishlistsURI + "/settings" , requestContentType: contentType, body: body)
             
        then: "Request failure => status code should be 400"
             
             with(response)
             {
                 assert !success
                 assert status == 400
             }

        when: "Update wishlist settings successfully"

             body = "{ \"usePreferredWishlist\": \"false\"}"
             response = client.put(uri: wishlistsURI + "/settings" , requestContentType: contentType, body: body)
             
        then: "Request success => status code should be 200"
             
             with(response)
             {
                 assert success
                 assert status == 200
                 assert data.type == "WishlistSettings"
                 assert data.usePreferredWishlist == false
             }
             
         when: "Update wishlist settings successfully"

              body = "{ \"usePreferredWishlist\": \"true\"}"
              response = client.put(uri: wishlistsURI + "/settings" , requestContentType: contentType, body: body)
              
         then: "Request success => status code should be 200"
              
              with(response)
              {
                  assert success
                  assert status == 200
                  assert data.type == "WishlistSettings"
                  assert data.usePreferredWishlist == true
              }
                 
       
        cleanup:
            
            client.headers.remove('Content-Type')
            client.headers.remove('Accept')
            client.clearAuthorizationHeader()
    }
    
    private void createWishlist(String name)
    {
        client.setAuthorizationHeader(user, password)

        def body = "{ \"title\": \""+name+"\"}"
        def response = client.post(uri: wishlistsURI, requestContentType: JSON, body: body)
        wishListID = response.data.title
        authenticationToken = response.headers.'authentication-token';
        client.setAuthorizationHeader(authenticationToken)
        wishlistURI = wishlistsURI + "/" + wishListID;        
    }
}