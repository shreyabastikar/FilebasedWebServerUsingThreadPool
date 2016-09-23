package mywebserver;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class MyStatusCode represents the Status of the response from the Server.
 */
public class ResponseStatusCode {
	
	//represents code of status of the response. Eg. 200/404/501
	private int code;
	
	//Map of possible codes, and status in an HTTP Response
	public static Map<Integer, String> statusCodeMap= new HashMap<Integer, String>();
	
	//Static block to initialize status codes
	static{
		initializeStatusCodeMap();
	}
	
	/**
	 * Instantiates a new MyStatusCode.
	 *
	 * @param code represents code of status of the response. Eg. 200/404/501
	 */
	public ResponseStatusCode(final int code) {
		
		this.code = code;
		
	}
	
	/**
	 * Gets the response code for HTTP Server Response.
	 *
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * Sets the response code for HTTP Server Response.
	 *
	 * @param code the new code
	 */
	public void setCode(final int code) {
		this.code = code;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		
		//Return the String format of the status of the response
		String statusFormat = statusCodeMap.get(code);
		return code+" "+statusFormat;
	}
	
	public static void initializeStatusCodeMap(){
		statusCodeMap.put(200, "OK");
		statusCodeMap.put(404, "Not Found");
		statusCodeMap.put(501, "Not Implemented");
		//More status codes can be added....
	}
}
