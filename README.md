# Multi-threaded file-based Web Server with Thread pools with Keep-Alive Behavior

## Description: ##
* The main aim of this project is to develop a File-based Multithreaded Web Server which uses Thread pooling. 
* The server can accept N number of client connections at a time with the help of Thread pooling.


## Note: ##
With the current implementation of the Server, following behavior is supported:
* The server can handle HTML file GET requests only. For other requests, Status Code 501 NOT_IMPLEMENTED is returned.
* The server supports HTTP requests only. 
* The client sends HTTP Request in the form of GET /<filepath> HTTP/1.1
* And HTTP Request following HTTP/1.1 standards is created and sent to the server.
* In case the file is found, its contents are returned are added as response body in the HTTP Response which follows HTTP/1.1 standards with 200 OK.
* In case the file is NOT found, an HTTP response body with appropriate HTML page is generated which is then appended to HTTP Response which follows HTTP/1.1 standards with 404 NOT_FOUND.
* Keep-alive behavior has also been implemented. The client can specify if it wants to continue sending requests on the same connection.


## Steps to run the program: ##
* Clone the project: 
https://github.com/shreyabastikar/FilebasedWebServerUsingThreadPool.git

#### For Server: ####
```
java MultiThreadedServer <portnumber> <serverDirectoryName>
```
#### For Client: ####
```
java Client <serverportnumber> <serverName OR hostname>
```

## Sample Output ##

#### Server side ####
```
Waiting for client connections at port 9008....
Connected to /127.0.0.1:57387
Received request:GET /subDir/subSubDir/SampleHTMLFile3.html HTTP/1.1
Host: localhost/127.0.0.1:9008
Accept: text/html
Accept-Language: en
Connection: Keep-Alive
Content-Length: 15
Content-Type: application/json

{"KEY":"VALUE"}
Request successfully processed!
Received request:GET /UnknownFile.html HTTP/1.1
Host: localhost/127.0.0.1:9008
Accept: text/html
Accept-Language: en

Request successfully processed!
Connection with client  /127.0.0.1:57387 has now ended
```

#### Client side ####
```
Trying to connect to localhost on port 9008
Connection to localhost/127.0.0.1:9008 has been established!

Enter HTTP request as follows:
<HTTPMethod> </filename.html> <HTTP protocol>
GET /subDir/subSubDir/SampleHTMLFile3.html HTTP/1.1
Current Server implementation does not process requestBody. 
This payload is used to display the format of a typical HTTP Request.
Please enter JSON format payload, if any. Else Press Enter.
{"KEY":"VALUE"}
Do you want the connection to be reused for further requests? (Y/N)
Y
Sending the following request to the server...

GET /subDir/subSubDir/SampleHTMLFile3.html HTTP/1.1
Host: localhost/127.0.0.1:9008
Accept: text/html
Accept-Language: en
Connection: Keep-Alive
Content-Length: 15
Content-Type: application/json

{"KEY":"VALUE"}
Waiting for server response....
Received server response!
Server response:
HTTP/1.1 200 OK
Date: Thu Sep 22 23:50:46 PDT 2016
Content-Type: text/html
Content-Length: 959

<html>
<title>Sample File HTML 3</title>
<body>
<h1>Lorem ipsum</h1>
Lorem ipsum dolor sit amet, commodo nunc sit arcu fusce, praesent feugiat at lacus magna pede neque, purus magna magna viverra convallis neque, malesuada nunc suscipit egestas maecenas quam, urna rhoncus aliquam purus. Imperdiet vitae eu dolor, dignissim pede eget nascetur, mauris purus vestibulum morbi arcu ut, rhoncus pede fermentum suspendisse. Mus elit sapien sit, proin lobortis, orci nibh. Eros vitae tortor felis orci libero potenti. Pellentesque vero dapibus, mauris dis tempus tellus id, ultrices orci sed facilisi fermentum diam, ac erat volutpat dignissim vehicula dignissim parturient. Magnis tempor duis aenean aliquam, eget sed imperdiet rutrum morbi quis. Sit tortor dictum lobortis, auctor in a a dis. Nunc nulla eget vivamus, laoreet est in tempor nec, etiam dignissim nam vel arcu diam sed. Curabitur nam dolor accumsan pulvinar, ut scelerisque in eget.
</body>
</html>

===========RESPONSE ENDED============

Enter HTTP request as follows:
<HTTPMethod> </filename.html> <HTTP protocol>
GET /UnknownFile.html HTTP/1.1
Current Server implementation does not process requestBody. 
This payload is used to display the format of a typical HTTP Request.
Please enter JSON format payload, if any. Else Press Enter.

Do you want the connection to be reused for further requests? (Y/N)
N
Sending the following request to the server...

GET /UnknownFile.html HTTP/1.1
Host: localhost/127.0.0.1:9008
Accept: text/html
Accept-Language: en

Waiting for server response....
Received server response!
Server response:
HTTP/1.1 404 Not Found
Date: Thu Sep 22 23:51:05 PDT 2016
Content-Type: text/html
Content-Length: 65

<html><title>Page Error</title><body>Page not found</body></html>
===========RESPONSE ENDED============
Client connection has been closed.
```

## References: ##
https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
https://www.jmarshall.com/easy/http/#http1.1clients
https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol#Client_request
http://www.javatpoint.com/multithreading-in-java
http://docs.oracle.com/javase/tutorial/networking/sockets/
