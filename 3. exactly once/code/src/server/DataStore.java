package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
	private static Map<String, String> requestMap; // = new ConcurrentHashMap<String, String>();
	private static Map<String, String> responseMap; // = new ConcurrentHashMap<String, String>();
	static FileInputStream fis1;
	static FileInputStream fis2;
	static FileOutputStream fos1;
	static FileOutputStream fos2;

	static {
		
		try{
			File f = new File("request.out");
			if(!f.exists()){
				f.createNewFile();
			}
			fis1 = new FileInputStream(f);
			fos1 = new FileOutputStream(f);			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			File f = new File("response.out");
			if(!f.exists()){
				f.createNewFile();
			}
			fis2 = new FileInputStream(f);			
			fos2 = new FileOutputStream(f);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			ObjectInputStream ois = new ObjectInputStream(fis1);
			requestMap = (Map<String, String>)ois.readObject();
		}catch(Exception e){
			
		}
		if(requestMap == null){
			requestMap = new ConcurrentHashMap<String, String>();
		}
		try{
			ObjectInputStream ois = new ObjectInputStream(fis2);
			responseMap = (Map<String, String>)ois.readObject();
		}catch(Exception e){
			
		}
		if(responseMap == null){
			responseMap = new ConcurrentHashMap<String, String>();
		}

	}

	public static String readFromReqeustMap(String key){
		return requestMap.get(key);
	}

	public static String readFromResponseMap(String key){
		return responseMap.get(key);
	}

	public static boolean writeToRequestMap(String key, String val){
		System.out.println("writing "+key+", value = "+val);
		requestMap.put(key, val);
		try{
			synchronized (lock) {
				fos1 = new FileOutputStream("reqeuest.out");
				ObjectOutputStream oos = new ObjectOutputStream(fos1);
				oos.writeObject(requestMap);
				oos.flush();
				oos.close();
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean writeToResponseMap(String key, String val){
		responseMap.put(key, val);
		try{
			synchronized (lock) {
				fos2 = new FileOutputStream("response.out");
				ObjectOutputStream oos = new ObjectOutputStream(fos2);			
				oos.writeObject(responseMap);
				oos.flush();
				oos.close();
			}			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static Object lock = new Object();

	public static boolean removeFromResponseMap(String key) {
		responseMap.remove(key);
		try{
			synchronized (lock) {
				fos2 = new FileOutputStream("response.out");
				ObjectOutputStream oos = new ObjectOutputStream(fos2);			
				oos.writeObject(responseMap);
				oos.flush();
				oos.close();
			}			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
		
		
	}

	public static boolean removeFromRequestMap(String key) {
		requestMap.remove(key);
		try{
			synchronized (lock) {
				fos1 = new FileOutputStream("request.out");
				ObjectOutputStream oos = new ObjectOutputStream(fos1);			
				oos.writeObject(requestMap);
				oos.flush();
				oos.close();
			}			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
