package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Properties;


public class Client {
	public static void main(String [] args) {
		// server details
		String serverName = "127.0.0.1";
		int serverPort, clientPort;

		try {
			Properties props = new Properties();
			props.load(new FileInputStream("client.properties"));
			
			serverPort = Integer.parseInt(props.getProperty("serverPort", "1800"));
			clientPort = Integer.parseInt(props.getProperty("clientPort", "1900"));
			
			// connect to server		
			Socket client = new Socket(Inet4Address.getByName(serverName), serverPort, 
					Inet4Address.getByName(serverName), clientPort);

			// prepare the stream to send some request
			OutputStream outToServer = client.getOutputStream();
			InputStream inFromServer = client.getInputStream();

			DataInputStream in = new DataInputStream(inFromServer);
			DataOutputStream out = new DataOutputStream(outToServer);

			// check if unprocessed request
			boolean unprocessedRequest = in.readBoolean();
			
			if(unprocessedRequest){
				String resp = in.readUTF();
				System.out.println("Earlier Reply "+ resp);
			}
			
			// send the request				
			out.writeUTF(System.currentTimeMillis()+"");
			System.out.println("Request sent");
			String resp = in.readUTF();
			System.out.println("Reply received");

			// close the connection
			client.close();

		}catch(IOException e) {
			System.err.println("error while connected to server"+e.getMessage());
			e.printStackTrace();
		}
	}
}