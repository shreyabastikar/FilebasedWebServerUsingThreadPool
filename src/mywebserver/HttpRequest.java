package mywebserver;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mywebserver.RequestLine;


/**
 * The Class MyHttpRequest represents all the methods related to formatting the HTTP 
 * request payload format to be sent to the Server.
 */
public class HttpRequest {
	
	//HTTP Request line
	RequestLine requestLine;
	
	//HTTPRequest header
	Map<String, String> httpRequestHeader = new LinkedHashMap<String, String>();
	
	//HTTP Request body
	String requestBody;
	
	/**
	 * Constructor to create a custom HTTP Request payload
	 *
	 * @param requestLine the first line in the request containing 
	 * 					   the request method, file path, protocol					
	 * @param httpRequestHeader the HTTP Request header fields
	 * @param requestBody the body of the Client's request
	 */
	public HttpRequest(RequestLine requestLine, Map<String, String> httpRequestHeader,
						String requestBody){
		this.requestLine = requestLine;
		this.httpRequestHeader = httpRequestHeader;
		this.requestBody = requestBody;
		
	}
	
	/**
	 * Instantiates a new HttpRequest.
	 *
	 * @param requestLine the request line
	 * @param hostName the host name
	 * @param requestBody the request body
	 * @param keepAlive to keep the connection alive
	 */
	public HttpRequest(String requestLine, String hostName, String requestBody, boolean keepAlive){
		
		//First line of the HTTP request payload. eg. GET /abc.html HTTP/1.1
		this.requestLine = new RequestLine(requestLine);
		
		//Header fields of HTTP request headers 
		this.httpRequestHeader = new LinkedHashMap<String, String>();
		
		//Mandatory header field since HTTP/1.1
		this.httpRequestHeader.put("Host", hostName);
		
		//Initializing optional fields, currently supports only HTML file requests
		this.httpRequestHeader.put("Accept", "text/html");
		
		//Initializing optional fields, Currently supports only english
		this.httpRequestHeader.put("Accept-Language", "en");
		
		//Add Connection header field to represent Keep-Alive connections
		if(keepAlive)
			this.httpRequestHeader.put("Connection", "Keep-Alive");
		//Currently supports application/json content type
		if(requestBody!=null && requestBody.length()!=0){
			this.requestBody = requestBody;
			int contentLength = requestBody.length();
			this.httpRequestHeader.put("Content-Length", Integer.toString(contentLength));
			this.httpRequestHeader.put("Content-Type", "application/json");
		}
	}

	/**
	 * Gets the request line.
	 *
	 * @return the request line
	 */
	public RequestLine getRequestLine() {
		return requestLine;
	}


	/**
	 * Sets the request line.
	 *
	 * @param requestLine the new request line
	 */
	public void setRequestLine(RequestLine requestLine) {
		this.requestLine = requestLine;
	}


	/**
	 * Gets the request body.
	 *
	 * @return the request body
	 */
	public String getRequestBody() {
		return requestBody;
	}


	/**
	 * Sets the request body.
	 *
	 * @param requestBody the new request body
	 */
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}
	
	public Map<String, String> getHttpRequestHeader() {
		return httpRequestHeader;
	}


	public void setHttpRequestHeader(Map<String, String> httpRequestHeader) {
		this.httpRequestHeader = httpRequestHeader;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * Returns String format of MyHttpRequest object
	 */
	public String toString() {
		
		//Verify the clientRequest is valid
		if(!validateClientRequest())
			return null;
		else{
			
			//Buffer to store the Client request payload
			StringBuffer requestToServer = new StringBuffer();
			
			//Append the request line from the Client's request
			requestToServer.append(this.getRequestLine().toString()+"\n");
			
			//Append the request headers
			for(Entry<String, String> headerInfo: this.httpRequestHeader.entrySet()){
				if(headerInfo.getValue()!=null && headerInfo.getValue().length()!=0)
					requestToServer.append(headerInfo.getKey()+": "+headerInfo.getValue()+"\n");
			}
			
			//Append the request body
			if(this.getRequestBody()!=null && this.getRequestBody().length()!=0 && 
					!this.getRequestBody().equalsIgnoreCase("None")){
				requestToServer.append("\r\n");
				requestToServer.append(this.getRequestBody());
			}
			
			//Return the String form of the request
			return requestToServer.toString();
		}
	}
	
	/**
	 * Validate client request.
	 *
	 * @return true, if successful
	 */
	public boolean validateClientRequest(){
		//Validate the required fields in HTTP request
		
		//Verify URL is valid
		Pattern urlPattern = Pattern.compile("(/([a-zA-Z0-9]+))+(.[hH][tT][mM][lL])");
	    Matcher urlMatcher = urlPattern.matcher(requestLine.getURL());
	    if(!urlMatcher.matches()){
	    	System.out.println("Please enter URL of format /../../<filename>.html");
	    	return false;
	    }
	    
	    //Verify HTTP protocol is used
	    Pattern httpProtocol = Pattern.compile("HTTP/[0|1].[0-9]");
	    Matcher httpProtocolMatcher = httpProtocol.matcher(requestLine.getHttpProtocol());
	    if(!httpProtocolMatcher.matches()){
	    	System.out.println("Please enter HTTP Protocol as HTTP/<x.y>");
	    	return false;
	    }
	    
	    //Verify Host field is valid
	    String host = httpRequestHeader.get("Host");
	    if(host==null||host.length()==0)
	    	return false;

		return true;
	}
	
	

}
