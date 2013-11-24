import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client {
	public static void main(String [] args) {
		Scanner read = new Scanner(System.in);

		// server details
		String serverName = "127.0.0.1";//read.nextLine();
		int port = 1800; //read.nextInt();

		long initTime = System.nanoTime();
		for(int i=0 ; i<4000 ; ++i){
			try {
				// connect to server
				Socket client = new Socket(serverName, port);

				// prepare the stream to send some request
				OutputStream outToServer = client.getOutputStream();
				InputStream inFromServer = client.getInputStream();
				
				DataInputStream in = new DataInputStream(inFromServer);
				DataOutputStream out = new DataOutputStream(outToServer);

				for(int j=0 ; j<1000 ; ++j){
					// send the request	
					out.writeUTF("Hello Mr Jacob ");
					String resp = in.readUTF();
				}

				// close the connection
				client.close();
				
			}catch(IOException e) {
				System.err.println("error while connected to server"+e.getMessage());
				e.printStackTrace();
			}
		}
		long endTime = System.nanoTime();
		
		System.out.println("Total time : "+(endTime-initTime)/1000000+" ms.");

	}
}
