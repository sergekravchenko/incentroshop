package rest.com.intershop.b2x.specs.storefront.common

class RequestBody {
    final String contentType
    final def body
    final String bodyString

	RequestBody(){}

    RequestBody(String contentType, String bodyString, def body)
	{
	    this.contentType = contentType
		this.bodyString = bodyString
		this.body = body
	}
}