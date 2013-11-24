

import jvishwa.client.*;
import jvishwa.query.*;
import jvishwa.metric.*;
import jvishwa.task.SchedulerType;
import jvishwa.Result;
import jvishwa.Context;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import jvishwa.file.*;
import java.io.InputStreamReader;
public class PrimeClientArg {
        public static void main(String args[])  {
                if(args.length < 1)
		{	
		System.out.println("ERROR: Give give grid node IP as commandline argument");
		System.exit(0);
		}
                //Getting the instance of client manager
                ClientManager manager=ClientManager.getInstance();              
                Metric metric=new Metric();
                Query query=new Query(metric);
                //Setting the configuration parameters
                query.setLowerBound(5);
                query.setMemoryFractionValue(0.3);
                query.setCPUFractionValue(0.7);
                manager.setConditionType(ConditionType.DEFAULT_TYPE);
                manager.setMetric(metric);
                manager.setQuery(query);
                manager.setSurplusValue(0);
                manager.setMinDonors(1);
                manager.setMaxDonors(10);        
                manager.setReplicaSize(1);
                manager.setSchedulerType(SchedulerType.DYNAMIC);        
                manager.setGridNodeIP(args[0]);
 /*               String OS = ""+System.getProperty("os.name");
               if(!OS.contains("Windows"))
               {
                  // manager.setCustomClassPath(System.getProperty("user.dir"));
                   System.out.println("PATH:  "+System.getProperty("user.dir"));
               }  */
                
                try {
                        manager.initilize();                                   
			System.out.println("Press any key to start the execution...");
			try
			{
				BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
				input.readLine();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

                }               
                catch(Exception e) {
                        e.printStackTrace();
                        System.exit(0);
                }               
                
               SubTaskHandle handleSet[]=new SubTaskHandle[10];
               Context context=null;           
               
               //Creating subtasks
               for(int i=1;i<=10;i++) {
                        context=new Context();
                        context.put("fromval",i+"000000");
                        context.put("toval",i+1+"000000");
                        handleSet[i-1]=manager.execute(new Prime(),context);
               }       
                
               //Waiting for all subtasks to finish.
                manager.barrier(); 
                //handleSet[i].waitSubTask()...Waiting for particular subtask to finish.
                try {                   
                        FileOutputStream fout=new FileOutputStream("Primeresult.txt");
                        PrintStream ps=new PrintStream(fout);
                               //placing the results into the  text file....
                        for(int i=1;i<=10;i++) {
                               Result r=handleSet[i-1].getResult();
                               VishwaFile vfile=r.getFile();
                               BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(vfile)));
                               String content=null;
                               System.out.println("Compilling results from Sub-Task: "+(i-1));
                               ps.println("Compilling results from Sub-Task:"+String.valueOf(i-1));
                               while((content=input.readLine())!=null)
                                        ps.println(content);
                                input.close();                          
                               
                       }               
                        ps.close();
                        fout.close();
                        
               }
               catch(Exception e) {
                       e.printStackTrace();
                }       
                        //Closing the client manager    
                manager.close();
        }
}
