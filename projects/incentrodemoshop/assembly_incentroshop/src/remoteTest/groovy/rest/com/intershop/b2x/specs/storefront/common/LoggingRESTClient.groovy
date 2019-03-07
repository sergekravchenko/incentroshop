package rest.com.intershop.b2x.specs.storefront.common

import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT

import java.security.SecureRandom

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.AllowAllHostnameVerifier
import org.apache.http.conn.ssl.SSLSocketFactory

import groovy.json.JsonOutput
import groovy.xml.XmlUtil
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

/**
 * Decorator of the groovy RESTClient.
 * Could be called with relative URL-s so the test classes does not need to handle construction of absolute base URL from system properties.
 * Supports logging of REST request and response, which is active by default unless 'icm.remote.test.disableRestLogging' system property is set.
 * In case HTTPS protocol is used SSLContext is changed to trust to all SSL Certificates(developers use SSL Certificates not signed by trusted authority).
 * SSLContext replacement could be disabled by setting 'icm.remote.test.trustedSSLCerticateUsed' system property.
 * When calling RestClient methods(GET, POST, PUT, etc.) debugMessage could be passed as part of the arguments, which is used for logging.
 */
class LoggingRESTClient extends RESTClient 
{
    public String text

    enum HttpMethod {
        GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS
    }

    enum Protocol {
        HTTP, HTTPS
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private static String DEFAULT_REST_BASE_PATH = "INTERSHOP/rest/WFS"

    private String baseRestURI
    private String secureBaseRestURI
    boolean logDebugInfo
    private boolean useHTTPS
    private int sslPort
    private boolean sslContextInitialized

    public LoggingRESTClient(String restBasePath)
    {
        String hostName = System.properties['hostName']
        String httpPort = System.properties['webserverPort']
        String httpsPort = System.properties['webserverHttpsPort']
        String baseURI = System.properties['geb.build.baseUrl']

        if (hostName != null && httpPort != null && httpsPort != null)
        {
            baseRestURI = "${Protocol.HTTP}://${hostName}:${httpPort}/${restBasePath}/"
            secureBaseRestURI = "${Protocol.HTTPS}://${hostName}:${httpsPort}/${restBasePath}/"
            sslPort = getPort(httpsPort)
        }
        else if (baseURI != null)
        {
            if (baseURI.startsWith(Protocol.HTTPS.toString()))
            {
                secureBaseRestURI = "${baseURI}/${restBasePath}/"
                // We expect baseURI inf the form <protocol>://<host>:<port>
                sslPort = getPort(baseURI.substring(baseURI.lastIndexOf(":")))
                setUseHTTPS(true)
            } else {
                baseRestURI = "${baseURI}/${restBasePath}/"
            }
        }

        handler.failure = handler.success
        logDebugInfo = isLoggingEnabled()
    }

    public void setUseHTTPS(boolean useHTTPS)
    {
        this.useHTTPS = secureBaseRestURI != null && useHTTPS
        if (this.useHTTPS)
        {
            initSSLContext()
        }
    }

    public LoggingRESTClient()
    {
        this(DEFAULT_REST_BASE_PATH)
    }

    public Object get(Map args) 
    {
        executeRequest(HttpMethod.GET, args)
    }

    public Object post(Map args)
    {
        executeRequest(HttpMethod.POST, args)
    }

    public Object put(Map args) 
    {
        executeRequest(HttpMethod.PUT, args)
    }

    public Object patch(Map args)
    {
        executeRequest(HttpMethod.PATCH, args)
    }

    public Object head(Map args)
    {
        executeRequest(HttpMethod.HEAD, args)
    }

    public Object delete(Map args)
    {
        executeRequest(HttpMethod.DELETE, args)
    }

    public Object options(Map args)
    {
        executeRequest(HttpMethod.OPTIONS, args)
    }

    public void setAuthorizationHeader(String user, String password)
    {
        headers['Authorization'] = "Basic " + (user + ":" + password).bytes.encodeBase64().toString()
        headers.remove('authentication-token')
    }

    public void setAuthorizationHeader(String token)
    {
        headers['authentication-token'] = token
        headers.remove('Authorization')
    }
 
    public void clearAuthorizationHeader()
    {
        headers.remove('Authorization')
        headers.remove('authentication-token')
    }

    private Object executeRequest(HttpMethod method, Map args)
    {
        if (!args.uri.startsWith(Protocol.HTTP.toString()))
        {
            // Relative URI provided - construct the absolute URI
            String baseURI = useHTTPS ? secureBaseRestURI : baseRestURI

            if (args.uri.startsWith('/'))
            {
                args.uri = baseURI + args.uri.substring(1)
            }
            else
            {
                args.uri = baseURI + args.uri
            }
        }

        logRequest(args, method)

        def response
        if (args.debugMessage)
        {
            // Remove debugMessage from arguments that will be passed to the decorated groovy RestClient
            args = args.findAll { it.key != "debugMessage" }
        }

        switch(method) {
            case HttpMethod.GET: 
                response = super.get(args)
                break
            case HttpMethod.POST: 
                response = super.post(args)
                break 
            case HttpMethod.PUT:
                response = super.put(args)
                break
            case HttpMethod.PATCH:
                response = super.patch(args)
                break
            case HttpMethod.DELETE: 
                response = super.delete(args)
                break
            case HttpMethod.OPTIONS:
                response = super.options(args)
                break
            default:
                throw new IllegalArgumentException("Unsupported HTTP method:" + method)
        }

        logResponse(response)

        return response
    }

    private void logRequest(Map args, HttpMethod method)
    {
        if (logDebugInfo && isLoggingEnabled())
        {
            if (args.debugMessage)
            {
                println("$args.debugMessage")
            }
            println("Sending $method Request:")
            if (args.uri)
            {
                println("- Request URI:             $args.uri")
            }
            if (args.requestContentType)
            {
                println("- Request Content Type:    $args.requestContentType")
            }
            if (headers)
            {
                print "- Request Headers:         "
                println headers.collect{"'${it.key}': '${it.value}'"}.join(", ")
            }
            if (args.body)
            {
                if (args.requestContentType && ContentType.XML.getContentTypeStrings().contains(args.requestContentType.toString()))
                {
                    println("- Request Body:")
                    print(XmlUtil.serialize(args.body))
                }
                else
                {
                    println("- Request Body:            $args.body")
                }
            }
        }
    }

    private void logResponse(Object response)
    {
        if (logDebugInfo && isLoggingEnabled())
        {
            if (response.status)
            {
                println("- Response Status:         $response.status")
            }
            if ((response.status != SC_NO_CONTENT && response.status != SC_ACCEPTED) && response.contentType)
            {
                println("- Response Content Type:   $response.contentType")
            }
            if (response.headers)
            {
                print "- Response Headers:        "
                println response.headers.collect{"'${it.name}': '${it.value}'"}.join(", ")
            }
            if (response.data)
            {
                if (response.data instanceof StringReader)
                {
                    text = response.data.text
                    println("- Response Body:           $text")
                }
                else
                {
                    println("- Response Body:")
                    if (ContentType.XML.getContentTypeStrings().contains(response.contentType))
                    {
                        print(XmlUtil.serialize(response.data))
                    }
                    else if (ContentType.JSON.getContentTypeStrings().contains(response.contentType))
                    {
                        println(JsonOutput.prettyPrint(JsonOutput.toJson(response.data)))
                    }
                    else {
                        println(response.data)
                    }
                }
            }
            println("")
        }
    }

    private void initSSLContext()
    {
        if (!sslContextInitialized && (System.properties['icm.remote.test.trustedSSLCerticateUsed'] == null))
        {
            // A work-around so that it will not be necessary to import a trusted certificate in Bamboo server
            // and HTTPS connections will work with dummy SSL certificate:
            SSLContext sc = SSLContext.getInstance("SSL")
            def trustAll = [getAcceptedIssuers: {}, checkClientTrusted: { a, b -> }, checkServerTrusted: { a, b -> }]
            sc.init(null, [trustAll as X509TrustManager] as TrustManager[], new SecureRandom())
            SSLSocketFactory socketFactory = new SSLSocketFactory(sc.socketFactory, new AllowAllHostnameVerifier())
            client.connectionManager.schemeRegistry.register(new Scheme(Protocol.HTTPS.toString(), socketFactory, sslPort))
            sslContextInitialized = true
        }
    }

    private boolean isLoggingEnabled()
    {
        System.properties['icm.remote.test.disableRestLogging'] == null
    }

    private int getPort(String portValue)
    {
        Integer.valueOf(portValue.trim()).intValue()
    }
}
