package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private ServerSocket serverSocket;

	public Server(String ip, int port) throws IOException {	   
		serverSocket = new ServerSocket();		
		serverSocket.setSoTimeout(100000);		
		serverSocket.bind(new InetSocketAddress(ip, port), 5000);
	}

	public void start() {
		System.out.println("Server started at : "+serverSocket.getLocalSocketAddress());
		ExecutorService exec = Executors.newFixedThreadPool(8);
		List<Thread> list = new ArrayList<Thread>();
		while(true) {
			try {
				// wait for client to accept the connection request
				Socket server = serverSocket.accept();

				Thread t = new Thread(new ServerThread(server));
				exec.submit(t);				
				list.add(t);
			}catch(IOException e) {
				e.printStackTrace();
				break;
			}
		}// while loop

		try{
			// wait for each thread to finish the task
			for(Thread t : list) 
				t.join();
			
			serverSocket.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String [] args) {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream("server.properties"));
			int port = Integer.parseInt(props.getProperty("serverport", "1800"));
		
			Server server = new Server("127.0.0.1", port);
			server.start();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}

class ServerThread implements Runnable {
	Socket server;
	int no_of_tries=3;

	public ServerThread(Socket s) {
		this.server = s;
	}

	public String processInput(String ip){

		return Integer.toString(ip.hashCode());
	}

	public boolean send(DataOutputStream out, String msg){
//		System.out.println("Sedning "+msg);
		for(int i=0 ; i<no_of_tries ; ++i){
			try {
				out.writeUTF(msg);

				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public String read(DataInputStream in){
		try {
			String ip = in.readUTF();
	//		System.out.println("ip is: "+ip);
			return ip;
		} catch (IOException e) {
			return null;
		}
	}

	
	@Override
	public void run() {
		try {
			System.out.println("Processing request");
			String key = server.getRemoteSocketAddress().toString();

			// open streams
			DataInputStream in = new DataInputStream(server.getInputStream());			
			DataOutputStream out = new DataOutputStream(server.getOutputStream());

			// first send if server has some request to send
			boolean previous  = !(DataStore.readFromResponseMap(key) == null && DataStore.readFromReqeustMap(key) == null);
//			System.out.println("Previosu Request pending ? "+previous);
			out.writeBoolean(previous);
			
			// check if any unprocessed request
			String val;
			if((val = DataStore.readFromReqeustMap(key)) != null){

				val = processInput(val);

				synchronized (DataStore.lock) {
					DataStore.writeToResponseMap(key,  val);
					DataStore.removeFromRequestMap(key);
				}
			}

			if((val = DataStore.readFromResponseMap(key)) != null){

				if(send(out, val)){
					DataStore.removeFromResponseMap(key);					
				}else {
					closeConnection();
					return;
				}
			}


				String ip = "";
				// atomic
				synchronized (DataStore.lock) {
					ip = read(in);
					DataStore.writeToRequestMap(key, ip);
				}
				// -- atomic

				// atomic
				synchronized (DataStore.lock) {
					ip = processInput(ip);
					DataStore.writeToResponseMap(key, ip);
					DataStore.removeFromRequestMap(key);	
				}// -- atomic
				
				if(send(out, ip)){
					DataStore.removeFromResponseMap(key);
				}else {
//					System.out.println("cannot send");
				}
//				System.out.println("sent");

			// close the connection.
			closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void closeConnection() {
		try{
			server.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}