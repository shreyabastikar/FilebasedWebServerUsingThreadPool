package mywebserver;

/**
 * The Class MyResponseLine represents the first line of the HTTP Response which is of the format:
 * Eg. HTTP/1.1 200 OK
 * 
 */
public class ResponseLine {
	String httpProtocol;
	ResponseStatusCode status;
	
	/**
	 * Instantiates a new my response line.
	 *
	 * @param httpProtocol the HTTP protocol
	 * @param status the status
	 */
	public ResponseLine(String httpProtocol, ResponseStatusCode status) {
		super();
		this.httpProtocol = httpProtocol;
		this.status = status;
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
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public ResponseStatusCode getStatus() {
		return status;
	}
	
	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(ResponseStatusCode status) {
		this.status = status;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return httpProtocol+" "+status.toString();
	}
	
}
