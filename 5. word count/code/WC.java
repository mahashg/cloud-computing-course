import java.io.IOException;
import org.apache.hadoop.io.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;

public class WC {

	public static void main(String args[]) throws Exception{		
		Job job = new Job();
		job.setJarByClass(WC.class);
		job.setJobName("Word Count Program");

		FileInputFormat.addInputPath(job, new Path("/user/hduser/wc_files/wc/"));
		FileOutputFormat.setOutputPath(job, new Path("/user/hduser/wc_files/op/"));

		job.setMapperClass(WCMapper.class);
		job.setCombinerClass(WCReducer.class);
		job.setReducerClass(WCReducer.class);
	
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		System.exit((job.waitForCompletion(true) ? 0 : 1));
	}
}

class WCMapper extends Mapper<LongWritable, Text, Text, IntWritable>{

		private final IntWritable one = new IntWritable(1);

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException
        {
			String txt = value.toString().trim();
			txt = txt.replaceAll("\t", " ");
			txt = txt.replaceAll("\n", " ");
			txt = txt.replaceAll("  ", " ");
			txt = txt.replaceAll("[^a-zA-Z0-9]", " ");
			for(String s : txt.split(" ")){
				context.write(new Text(s), one);
			}
        }

}

class WCReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
	
	@Override
	public void reduce(Text value, Iterable<IntWritable> values, Context context) 
		throws IOException, InterruptedException
	{
		int total=0;
		for(IntWritable val : values){
			total += val.get();
		}

		context.write(value, new IntWritable(total));
	}

}
