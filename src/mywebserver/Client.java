package mywebserver;
import java.net.*;
import java.io.*;
import mywebserver.HttpRequest;

/**
 * This class represents the Client sends request to the Multithreaded Server
 */
public class Client {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */

	//Check if connection is of type Keep-Alive
	private static boolean keepAlive = false;

	//Wait for MAX_WAIT_TIME seconds till the client receives data from server
	private static final int MAX_WAIT_TIME = 100000;

	//Client socket
	private static Socket client = null;

	//Current start time of the Thread
	private static long startTime = System.currentTimeMillis();

	public static boolean isKeepAlive() {
		return keepAlive;
	}

	public static void setKeepAlive(boolean keepAlive) {
		Client.keepAlive = keepAlive;
	}

	public static void main(String[] args) {
		try {
			if(args.length<2)
				System.out.println("Run client using: java Client <portnumber> <hostname>");
			else if(args[0]==null || args[0].length()==0 ){
				System.out.println("Invalid server port number as the argument.");
			}
			else if( args[1]==null || args[1].length()==0){
				System.out.println("Invalid hostname as the argument.");
			}
			else{

				int port = Integer.parseInt(args[0]);
				String hostName = args[1];

				//Connect to the server via the specified port
				System.out.println("Trying to connect to " + hostName + " on port " + port);
				client = new Socket(hostName, port);
				String serverAddress = client.getRemoteSocketAddress().toString();
				System.out.println("Connection to " + serverAddress+" has been established!");

				//Send requests until client is not terminated
				do{

					//Set up a BufferedReader to accept request from the user
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					HttpRequest httpRequestObj = createClientHttpRequest(br, serverAddress);
					
					//If a valid HTTP Request object was returned
					if(httpRequestObj!=null && httpRequestObj.validateClientRequest()){
						//Send HTTP Request to server
						sendRequestToServer(httpRequestObj);

						//Receive response from Server
						String responseFromServer = getResponseFromServer();

						//Print Server response
						System.out.println("Server response:\n" + responseFromServer);
					}
					else{
						System.out.println("Invalid HTTP Request. Please try again later.");
					}
					System.out.println("===========RESPONSE ENDED============");
				}while(isKeepAlive() && (System.currentTimeMillis()-startTime)<MAX_WAIT_TIME);

				System.out.println("Client connection has been closed.");
				//Close the client socket when the current thread is interrupted
				client.close();
			}
		}
		catch(EOFException e){
			System.out.println(e.getMessage());
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Creates the Client HTTP request.
	 *
	 * @param br the BufferedReader object to accept request from user
	 * @param hostName the hostname of the server
	 * @return MyHttpRequest object created out of user input
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static HttpRequest createClientHttpRequest(BufferedReader br, 
			String hostName) throws IOException{
		
		//Accept User's HTTP request
		System.out.println("\nEnter HTTP request as follows:");
		System.out.println("<HTTPMethod> </filename.html> <HTTP protocol>");
		String requestLine = br.readLine();

		//GET <html file path> <protocol>
		String requestLineTokens[] = requestLine.split(" ");

		//Client side validation to prevent bad requests
		while(requestLineTokens.length!=3){
			System.out.println("Bad request line:"+requestLine);
			return null;
		}

		//Accept JSON payload
		System.out.println("Current Server implementation does not process requestBody. "
				+ "\nThis payload is used to display the format of a typical HTTP Request."
				+ "\nPlease enter JSON format payload, if any. Else Press Enter.");
		String requestBody = br.readLine();

		//Reuse connection choice
		System.out.println("Do you want the connection to be reused for further requests? (Y/N)");
		String reuseConnectionChoice = br.readLine();
		boolean keepAlive = false;
		if(reuseConnectionChoice!=null && reuseConnectionChoice.length()!=0 && reuseConnectionChoice.equalsIgnoreCase("Y"))
			keepAlive=true;

		//Create the MyHttpRequest object to send to server
		HttpRequest httpRequestObj = new HttpRequest(requestLine, hostName, requestBody, keepAlive);

		//Return the MyHttpRequest object
		return httpRequestObj; 

	}

	/**
	 * Send Request to Server.
	 *
	 * @param httpRequestObj HTTP Request object to be sent to the Server
	 */
	public static void sendRequestToServer(HttpRequest httpRequestObj){

		try{
			//Check if it is a Keep-Alive connection
			if(httpRequestObj.getHttpRequestHeader().containsKey("Connection") &&
					httpRequestObj.getHttpRequestHeader().get("Connection").equals("Keep-Alive"))
				setKeepAlive(true);
			else
				setKeepAlive(false);

			//Get the output stream to send request to server
			OutputStream outToServer = client.getOutputStream();

			//Get the DataOutputStream format to send request to Server
			DataOutputStream out = new DataOutputStream(outToServer);

			//Get String format of response from Server
			String requestToServer = httpRequestObj.toString();

			//Print the request being sent to the Server
			System.out.println("Sending the following request to the server...\n");
			System.out.println(requestToServer);

			//Send the request to the server
			out.writeUTF(requestToServer);
			

		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}


	}

	/**
	 * Gets the response from Server.
	 *
	 * @return the String format of response from Server
	 */
	public static String getResponseFromServer(){
		try{
			StringBuffer responseFromServerBuffer = new StringBuffer();

			//Create Input stream to accept response from server
			InputStream inFromServer = client.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inFromServer);

			//Buffer to save response from the server
			responseFromServerBuffer = new StringBuffer();

			//Wait for the data to be present in Input stream from server
			System.out.println("Waiting for server response....");
			while((System.currentTimeMillis()-startTime)<MAX_WAIT_TIME){

				//When data becomes available, save the response
				if(inFromServer.available()>0){
					int bufLength = inFromServer.available();
					System.out.println("Received server response!");

					//Create a reader to read data from the input stream
					BufferedReader reader = new BufferedReader(inputStreamReader,
							bufLength);

					//Create char buffer to save data received from server
					char[] cbuf = new char[bufLength];

					//Copy the contents from the reader to the buffer, when reader is ready
					if(reader.ready()){
						//Read the contents received into the char buffer
						reader.read(cbuf, 0, bufLength);

						//Save the contents
						responseFromServerBuffer.append(cbuf);
						break;
					}
				}
			}
			//Return Server response
			return responseFromServerBuffer.toString();

		}
		catch(IOException e){
			System.out.println(e.getMessage());
			return null;
		}
		
	}
}
