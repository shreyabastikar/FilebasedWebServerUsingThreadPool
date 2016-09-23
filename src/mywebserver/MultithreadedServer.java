package mywebserver;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The Class MultithreadedServer handles multiple client request in a Multithreaded way.
 * It manages the requests with the help of Thread-pooling in Java
 */
public class MultithreadedServer implements Runnable{

	//Set maximum number of Client HTTP requests allowed by the Server
	private static final int MAX_REQUESTS_ALLOWED = 5;

	//Set maximum Idle waiting time for requests/responses
	private static final int MAX_WAIT_TIME = 100000;

	//Create the Thread pool to handle Client HTTP requests
	private static ExecutorService pool = Executors.newFixedThreadPool(MAX_REQUESTS_ALLOWED);

	//Current start time of the Thread
	private static long startTime = System.currentTimeMillis();

	//Directory where html files exist
	private String serverDirectory;

	//Server port which is accepting client requests
	private int serverPort;

	//Server socket which is accepting client requests
	private ServerSocket serverSocket = null;

	/**
	 * Instantiates a new Multithreaded server.
	 *
	 * @param serverPort  is the port which is accepting client requests
	 * @param serverDirectory is the folder where the requested files are present 
	 * @throws IOException Signals that an I/O exception has occurred, in case serverSocket fails.
	 */
	public MultithreadedServer(final int serverPort, final String serverDirectory) throws IOException{
		this.serverPort = serverPort;
		this.serverDirectory = serverDirectory;
		this.serverSocket = new ServerSocket(this.serverPort);
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {


		try{
			if(args.length<2)
				System.out.println("Run Server: using java MultithreadedServer <portnumber> <serverDirectory>");
			else if(args[0]==null || args[0].length()==0 ){
				System.out.println("Invalid server port number as the argument.");
			}
			else if( args[1]==null || args[1].length()==0){
				System.out.println("Invalid server directory as the argument.");
			}
			else{

				//Create a Server instance thread and start it to be ready to accept client requests
				int portNumber = Integer.parseInt(args[0]);
				String serverDirectory = args[1];

				//Check if correct server directory is passed
				if(Files.notExists(Paths.get(serverDirectory)))
					System.out.println("The directory "+serverDirectory+" does not exist on the server ");
				else{
					System.out.println("Waiting for client connections at port "+portNumber+"....");
					Thread serverInstanceThread = new Thread(new MultithreadedServer(portNumber, serverDirectory));
					serverInstanceThread.start();
				}
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){

		//Process the client requests
		processClientRequests();
	}

	/**
	 * Process client requests.
	 */
	public void processClientRequests() {
		try{
			while(true)
			{
				//Wait for connection from Client 
				Socket clientSocket = serverSocket.accept();
				Runnable r = new Runnable()
				{
					@Override
					public void run()
					{	
						System.out.println("Connected to " + clientSocket.getRemoteSocketAddress() );

						boolean keepAlive = false;
						do
						{	
							//Input stream from accepting Client requests
							DataInputStream in = null;

							//Buffered writer for sending response to Clients
							BufferedWriter writer = null;
							try{
								//Initialize input stream object
								in = new DataInputStream(clientSocket.getInputStream());

								//Read the Client's request
								String requestFromClient = in.readUTF();
								System.out.println("Received request:"+requestFromClient);

								//Check if the request is of type Keep-Alive
								String fieldToCheck = "Connection: Keep-Alive";
								if(isFieldPresent(fieldToCheck, requestFromClient))
									keepAlive = true;

								//Process the Client's request and return HTTP response object
								HttpResponse response = processRequest(requestFromClient);

								//DataOutputStream object to send response to Client
								DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

								//Convert the HTTP response object to String format
								String responseToClient = response.toString();

								//Initialize the BufferedWriter object to send response to Client via OutputStream
								writer = new BufferedWriter(new OutputStreamWriter(out), responseToClient.length());

								//Send the response to the Client
								writer.write(response.toString(),0,response.toString().length());
								writer.flush();

							}
							catch(EOFException e){
								if(e.getMessage()!=null)
									System.out.println(e.getMessage());
								//Stop accepting requests when Client is terminated
								break;

							}
							catch(IOException ioException){
								System.out.println(ioException.getMessage());
								//Stop accepting request when there is an I/O Exception
								break;
							}
							System.out.println("Request successfully processed!");
						}while((System.currentTimeMillis()-startTime)<MAX_WAIT_TIME && keepAlive);

						//Print that the connection with the Client has ended
						System.out.println("Connection with client "+" "+
								clientSocket.getRemoteSocketAddress()+" has now ended");

					}
				};
				pool.execute(r);
			}

		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		finally{
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			pool.shutdown();
		}
	}

	/**
	 * Validate HTTP request.
	 *
	 * @param requestFromClient the requestLine received from Client
	 * @return true, if it is a valid request
	 */
	public boolean validateRequest(final String requestFromClient){

		String request = null;

		//Check if the request has body
		if(requestFromClient.indexOf("\r\n")==-1){
			request = requestFromClient;
		}
		else{
			String requestAndBody[] = requestFromClient.split("\r\n");
			request = requestAndBody[0];
		}

		//Get all header info into a list
		String requestHeaderList[] = request.split("\n");

		//Extract method, file path, protocol
		String requestLine[] = requestHeaderList[0].split(" ");

		//Check if it is an HTTP Request
		if(requestLine[2].indexOf("HTTP")==-1){
			System.out.println("Server processes only HTTP request");
			return false;
		}
		return true;
	}

	/**
	 * Process request. 
	 *
	 * @param requestFromClient the request from client
	 * @return the HTTPResponse
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public HttpResponse processRequest(String requestFromClient) throws IOException{
		String request = null;
		String body = null;

		//Check if the request has body
		if(requestFromClient.indexOf("\r\n")==-1){
			request = requestFromClient;
		}
		else{
			String requestAndBody[] = requestFromClient.split("\r\n");
			request = requestAndBody[0];
			body = requestAndBody[1];
		}
		//Get all header info into a list
		String tokenizedRequestHeader[] = request.split("\n");

		//Extract method, file path, protocol
		String requestLine[] = tokenizedRequestHeader[0].split(" ");

		//Process only GET requests, as currently only GET is supported
		if(requestLine[0].equals("GET")){

			//Create HTTP response object
			HttpResponse response = processGETRequest(request, body);
			return response;
		}
		else{
			//If it is not a GET request, send a 501 NOT_IMPLEMENTED response to the user
			String httpProtocol = requestLine[2];

			//Create ResponseStatusCode object for representing the error
			ResponseStatusCode statusCodeObj = new ResponseStatusCode(501);

			//Create response body as HTML page to display Unimplemented method
			String responseBody = "<html><title>Page Error</title><body>Unimplemented method</body></html>";

			//Create the Response line to include HTTP protocol, and status code of the response
			ResponseLine responseLine = new ResponseLine(httpProtocol, statusCodeObj);

			//Generate response headers in the HTTP response
			Map<String, String> httpResponseHeader = createResponseHeaderFields(responseBody);

			//Generate HTTP response object with all the required fields
			HttpResponse httpResponseObject = new HttpResponse(responseLine, httpResponseHeader, 
					responseBody);

			return httpResponseObject;

		}


	}

	/**
	 * Process GET request. Based on the current implementation, the Request Body of the request
	 * send by the Client will be ignored, because this GET request just retrieves the contents
	 * of the file specified by the file-path/URL.
	 *
	 * @param request the request
	 * @param body the body
	 * @return the my http response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public HttpResponse processGETRequest(String request, String body) throws IOException{
		/*
		 * get details of the file in the request and save in response body
		 * create the response object
		 * create string based response object
		 * return string response back to server
		 * NOTES: Not considering body this time
		 */
		String requestHeaderList[] = request.split("\n");

		/*
		 * Process request line
		 */
		ResponseStatusCode statusCodeObj = null;
		byte[] responseBytes = null;

		String requestLine[] = requestHeaderList[0].split(" ");
		String filePath = requestLine[1];
		String responseBody = null;
		String httpProtocol = requestLine[2];
		responseBytes = getFileContent(filePath);
		if(responseBytes == null){
			statusCodeObj = new ResponseStatusCode(404);
			responseBody = "<html><title>Page Error</title><body>Page not found</body></html>";
		}
		else{
			statusCodeObj = new ResponseStatusCode(200);
			responseBody = new String(responseBytes);
		}

		//Create Response line eg.: HTTP/1.1 XXX STATUS
		ResponseLine responseLine = new ResponseLine(httpProtocol, statusCodeObj);

		Map<String, String> httpResponseHeader = createResponseHeaderFields(responseBody);

		HttpResponse httpResponseObject = new HttpResponse(responseLine, httpResponseHeader, 
				responseBody);
		return httpResponseObject;
	}

	/**
	 * Creates the response header fields.
	 *
	 * @param responseBody the response body
	 * @return the map
	 */
	public Map<String, String> createResponseHeaderFields(String responseBody){
		Map<String, String> httpResponseHeader = new LinkedHashMap<String, String>();
		httpResponseHeader.put("Date", new Date().toString());
		httpResponseHeader.put("Content-Type", "text/html");
		httpResponseHeader.put("Content-Length", Integer.toString(responseBody.length()));
		return httpResponseHeader;
	}


	/**
	 * Gets the file content.
	 *
	 * @param filePath the file path
	 * @return the file content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte[] getFileContent(String filePath) throws IOException{

		//Get current working directory
		String workingDirectory = System.getProperty("user.dir");

		//Get complete file path
		String completeFilePath = workingDirectory+"/"+serverDirectory + filePath;

		//Try to access the file
		File file = new File(completeFilePath);

		//Check if the file exists
		if(file.isFile()){

			//Initialize a file reader
			FileInputStream reader = new FileInputStream(file);

			//Initialize a buffer of file length
			long length = file.length();
			byte[] responseBody = new byte[(int) length];

			//Read the content of the file into the buffer
			reader.read(responseBody);

			//Close the reader after reading is done
			reader.close();

			//Return the contents of the file
			return responseBody;
		}
		else{
			return null;
		}

	}

	/**
	 * Checks if a particular header field is present in the request.
	 *
	 * @param fieldToCheck the header field to check
	 * @param request the request 
	 * @return true, if the header field present in the request
	 */
	public boolean isFieldPresent(String fieldToCheck, String request){
		return request.indexOf(fieldToCheck)!=-1;

	}
}
