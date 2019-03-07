package geb.com.intershop.b2x.specs.storefront.responsive.features

import geb.com.intershop.b2x.model.storefront.responsive.User

import geb.Browser

/**
 * Implements OCI user login.
 *
 */
trait OCIUserLogin
{
    final OCI_LOGIN_URL = System.getProperty("baseURL_https").trim() +
                "/" + System.getProperty("url.prefix", "INTERSHOP/web/WFS/").trim() +
                System.getProperty("url.site", "inSPIRED-inTRONICS_Business-Site").trim() +
                "/" + System.getProperty("url.locale", "en_US").trim() +
                "/" + System.getProperty("url.application", "-").trim() +
                "/" + System.getProperty("url.currency" , "USD").trim() +
                "/ViewOCICatalog-Start"
               
    //basic OCI login              
    def loginOCIUser(User user)
    {
        Browser.drive {
            go OCI_LOGIN_URL, USERNAME: user.email, PASSWORD: user.password
        }
    }
    
    // basic login with hook url
    def loginOCIUser(String login, String password, String hookUrl)
    {
        Browser.drive {
            go OCI_LOGIN_URL, USERNAME: login, PASSWORD: password, HOOK_URL: hookUrl
        }
    }
    
    //OCI login and goes to a product details page by additional parameters directly
    def loginOCIUser_DETAIL(String login, String password, String sku, String hookUrl)
    {
        Browser.drive {
            go OCI_LOGIN_URL, USERNAME: login, PASSWORD: password, FUNCTION: "DETAIL", PRODUCTID: sku, HOOK_URL: hookUrl
        }
    }
    
    //OCI login and returns form with search results
    def loginOCIUser_BACKGROUND_SEARCH(String login, String password, String searchTerm, String hookUrl)
    {
        Browser.drive {
            go OCI_LOGIN_URL, USERNAME: login, PASSWORD: password, FUNCTION: "BACKGROUND_SEARCH", SEARCHSTRING: searchTerm, HOOK_URL: hookUrl
        }
    }
    
    //OCI login and returns form with given product
    def loginOCIUser_VALIDATE(String login, String password, String sku, String hookUrl)
    {
        Browser.drive {
            go OCI_LOGIN_URL, USERNAME: login, PASSWORD: password, FUNCTION: "VALIDATE", PRODUCTID: sku, QUANTITY : "1", AUTOSUBMIT : "false", HOOK_URL: hookUrl
        }
    }
}
