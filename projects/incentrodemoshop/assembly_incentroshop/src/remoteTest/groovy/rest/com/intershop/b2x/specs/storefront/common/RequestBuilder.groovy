package rest.com.intershop.b2x.specs.storefront.common

import groovyx.net.http.ContentType
import groovy.util.XmlSlurper
import groovy.xml.StreamingMarkupBuilder
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class RequestBuilder {
    static final String XML_FORMAT = ContentType.XML.name()
    static final String JSON_FORMAT = ContentType.JSON.name()

	static RequestBody toXML(Closure content)
    {
        def builder = new StreamingMarkupBuilder()
        builder.useDoubleQuotes = true

        String bodyString = builder.bind(content).toString()
        def body = new XmlSlurper().parseText(bodyString)
		new RequestBody(ContentType.XML.getContentTypeStrings()[1], bodyString, body)
    }

    static RequestBody toJSON(Closure content)
    {
		String bodyString = new JsonBuilder(content).toString()
        def body = new JsonSlurper().parseText(bodyString)
		new RequestBody(ContentType.JSON.toString(), bodyString, body)
    }
}