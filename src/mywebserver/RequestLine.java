package mywebserver;

import java.util.*;
/**
 * The Class MyRequestLine represents the first line in the Request payload sent by the client
 * to the Server in the format. 
 * Eg. GET /xxx.html HTTP/1.1 
 */
public class RequestLine {
	/**
	 * Instantiates a new MyRequestLine which is the first line of Client's request.
	 *
	 * @param requestLine the request line
	 */
	RequestLine(String requestLine){

		/*Process Client's request which is of the format:
		 * <HTTP Method> <html file path> <protocol>
		 */
		String requestLineTokens[] = requestLine.split(" ");

		//Tokenize request to HTTP Method, requested file's path, Request protocol
		this.httpMethod = requestLineTokens[0];
		this.URL = requestLineTokens[1];
		this.httpProtocol = requestLineTokens[2];
	}

	//Following are the HTTP Methods supported by HTTP/1.1
	public static final Set<String> SUPPORTED_METHODS_SET = new HashSet<String>(Arrays.asList(
			"GET", //Only this type of request can be handled at this time.
			"POST", 
			"DELETE",
			"PUT",
			"HEAD",
			"CONNECT",
			"OPTIONS",
			"TRACE"));

	//HTTPRequest method
	String httpMethod;

	//Request URL
	String URL;

	//HTTP protocol to be used
	String httpProtocol;

	/**
	 * Gets the http method.
	 *
	 * @return the http method
	 */
	public String getHttpMethod() {
		return httpMethod;
	}

	/**
	 * Sets the HTTP Method.
	 *
	 * @param httpMethod the new HTTP Method
	 */
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getURL() {
		return URL;
	}

	/**
	 * Sets the url.
	 *
	 * @param uRL the new url
	 */
	public void setURL(String uRL) {
		URL = uRL;
	}

	/**
	 * Gets the HTTP protocol.
	 *
	 * @return the HTTP protocol
	 */
	public String getHttpProtocol() {
		return httpProtocol;
	}

	/**
	 * Sets the HTTP protocol.
	 *
	 * @param httpProtocol the new HTTP protocol
	 */
	public void setHttpProtocol(String httpProtocol) {
		this.httpProtocol = httpProtocol;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		//
		return this.httpMethod+" "+this.URL+" "+this.httpProtocol;
	}
}
