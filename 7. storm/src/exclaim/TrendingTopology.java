package exclaim;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

/**
 * This is a basic example of a Storm topology.
 */
public class TrendingTopology {

	public static void main(String[] args) throws Exception {
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout", new TopicSpout(), 5).setDebug(false);
		builder.setBolt("counter", new CounterBolt(), 10).fieldsGrouping("spout", new Fields("word")).setDebug(false);
		builder.setBolt("global", new GlobalCounterBolt(10), 1).globalGrouping("counter").setDebug(false);

		Config conf = new Config();
		
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("trending", conf, builder.createTopology());
		
		Utils.sleep(100000);
		cluster.killTopology("trending");
		cluster.shutdown();
	}
}
