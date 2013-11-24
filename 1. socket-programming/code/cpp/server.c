#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
	
int main()
{
	int sockfd, newsockfd, portno, n, status_code;
	socklen_t clilen;
	struct sockaddr_in serv_addr, cli_addr;
	char buffer[256];
	int i;
	
	// initialize socket
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (sockfd < 0) {
		printf("Could not open socket\n");
		exit(0);
	}

	portno = 1780;
	
	// initialize server addr
	bzero((char *) &serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = INADDR_ANY;
	serv_addr.sin_port = htons(portno);

	// bind the socket to particular server address
	status_code = bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr));
	if (status_code < 0) {
		printf("Could not b socket\n");
		exit(0);
	}

	// listen for socket connections
	listen(sockfd, 5);
	clilen = sizeof(cli_addr);
	printf("Server Started Waiting for client request\n");
	
	// wait indefinitely for any new connections
	while(1) {
		// accept new socket connection
		newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);
		if (newsockfd < 0) {
			printf("Error Accepting connection\n");
			exit(0);
		}
		
		//fork and create a new thread to process the request
		int pid = fork();
		if(!pid) {
			for(i=0 ; i<1000 ; ++i){
				
				// init the read buffer and read the message
				bzero(buffer,256);
				n = read(newsockfd,buffer,255);			 
				if (n < 0) {
					printf("ERROR reading from socket\n");
					exit(0);
				}
				 
				// send the message to client
				n = write(newsockfd,"Message Received.",18);
				if (n < 0){
					printf("ERROR writing to socket\n");
					exit(0);
				}
			}// loop

		} else {
			// if the parent thread do no processing close the socket
			close(newsockfd);		
		}
	}// while loop
	
	// close the connection at the end
	close(sockfd);
	
	return 0; 
}
