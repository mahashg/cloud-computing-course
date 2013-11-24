#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> 
#include <time.h>

int no_request;

int main()
{
	int sockfd, portno, n, i, j;
	struct sockaddr_in serv_addr;
	struct hostent *server;
	
	char read_buffer[256];
	char write_buffer[256];
	int no_request = 2000;

	int status_code;
	
	// locate host
	server = gethostbyname("127.0.0.1");
	if (server == NULL) {
		printf("Host Not Found Exception.\n");
		exit(0);
	}
	portno = 1780;
	
	// init server_addr socket
	bzero((char *) &serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
	bcopy((char *)server->h_addr, (char *)&serv_addr.sin_addr.s_addr, server->h_length);
	serv_addr.sin_port = htons(portno);

//	printf("Processing %d connections\n", no_request);
	for(i=0 ; i<no_request ; i++){

		//open socket at any free port
		sockfd = socket(AF_INET, SOCK_STREAM, 0);
		if (sockfd < 0) {
			printf("Could not open socket\n");
			exit(0);
		}

		// blocking call to connect to server
		status_code = connect(sockfd,(struct sockaddr *) &serv_addr,sizeof(serv_addr));
		if(status_code < 0){
			printf("Could not connect to server, status code %d\n", status_code);
			exit(0);
		}

		// write the msg to socket and send to server
		for(j=0 ; j<1000 ; j++){
			
			n = write(sockfd,"Hello Mr Jacob", 16);
			if (n < 0) {
				printf("Could not write to server\n");
				exit(0);
			}
			
			// read the msg from socket
			bzero(read_buffer,256);
			n = read(sockfd,read_buffer,255);
			if (n < 0) {
				printf("Could not read from Server\n");
				exit(0);
			}
		}
		// close the connection
		close(sockfd);
	}// end of loop

	return 0;
}
