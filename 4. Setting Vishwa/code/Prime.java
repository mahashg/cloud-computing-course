

import jvishwa.Context;
import jvishwa.Result;
import jvishwa.VishwaSubTask;
import java.util.Vector;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import jvishwa.file.*;
import java.io.PrintStream;
import java.io.FileOutputStream;
public class Prime extends VishwaSubTask {
	Vector v;
        VishwaFile vfile=null;

	//Constructor
         public Prime()  {
                v=new Vector();
        }
	
         //calculation of prime
         public void run(Context c) {
		 //Accessing the parameters from the context
                int startpoint=Integer.parseInt((String)c.get("fromval"));
		int endpoint=Integer.parseInt((String)c.get("toval"));
		//File Service for creating a file
                FileService fileService=new FileService(this);
		vfile=fileService.createFile("result");
		PrintStream ps=null;
		try {           
			ps=new PrintStream(new FileOutputStream(vfile));                
		}
		catch(Exception e) {
			e.printStackTrace();
		}
                 //Calculation of prime
		for(int i=startpoint;i<=endpoint;i++) {
			if(isPrime(i))  
				ps.println(i+"");
		}               
		ps.close();
	 }       
         
         // Algorithm for calculation of prime
         private boolean isPrime(int n)  {
                 int max=(int)Math.sqrt(n);
                 for(int div=2;div<=max;div++) {
                         if(n%div==0) 
                                 return false;
                 }
                 return true;
         }
                 
         //Aggregating the results...
         public Result callback() {
                 Result r=new Result();
                 r.putFile(vfile);
                 return r;
         }
         
         //Deserialization method for getting the object
         public Object getObject(String fileName) { 
                 try
                 {
                         FileInputStream fin=new FileInputStream(fileName);
                         ObjectInputStream ois=new ObjectInputStream(fin);
                         Object o=ois.readObject();
                         ois.close();
                         return o;
                 }
                 catch(Exception e)
                 {
                         e.printStackTrace();
                 }
                 return null;
         }
}
