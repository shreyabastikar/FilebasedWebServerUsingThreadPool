package mywebserver;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The Class MyHttpResponse represents all the methods related to formatting the HTTP 
 * response payload format to be sent to the Client.
 */
public class HttpResponse {
	
	//HTTP Response line
	ResponseLine responseLine;
	
	//HTTPResponse header
	Map<String, String> httpResponseHeader = new LinkedHashMap<String, String>();
	
	//HTTP response body
	String responseBody;
	
	/**
	 * Instantiates a new HttpResponse.
	 *
	 * @param responseLine the first line in the response containing 
	 * 					   the protocol, status of the response
	 * @param httpResponseHeader the HTTP Response header fields 
	 * @param responseBody the body of the Server's response
	 */
	public HttpResponse(ResponseLine responseLine, Map<String, String> httpResponseHeader, String responseBody) {
		this.responseLine = responseLine;
		this.httpResponseHeader = httpResponseHeader;
		this.responseBody = responseBody;
	}

	/**
	 * Gets the response line.
	 *
	 * @return the response line
	 */
	public ResponseLine getResponseLine() {
		return responseLine;
	}

	/**
	 * Sets the response line.
	 *
	 * @param responseLine the new response line
	 */
	public void setResponseLine(ResponseLine responseLine) {
		this.responseLine = responseLine;
	}

	/**
	 * Gets the response body.
	 *
	 * @return the response body
	 */
	public String getResponseBody() {
		return responseBody;
	}

	/**
	 * Sets the response body.
	 *
	 * @param responseBody the new response body
	 */
	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		
		//Buffer to store the Server response payload
		StringBuffer responseForClient = new StringBuffer();
		
		//Append the response line from the Server's request
		responseForClient.append(this.getResponseLine().toString()+"\n");
		for(Entry<String, String> headerInfo: this.httpResponseHeader.entrySet()){
			responseForClient.append(headerInfo.getKey()+": "+headerInfo.getValue()+"\n");
		}
		//Append <CR><LF> as required by HTTP/1.1 Response conventions
		responseForClient.append("\r\n");
		
		//Append response body
		responseForClient.append(this.getResponseBody());
		
		//Return String format of the MyHttpResponse object
		return responseForClient.toString();
	}
	
}
