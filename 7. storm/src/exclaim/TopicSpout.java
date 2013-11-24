package exclaim;

import backtype.storm.Config;
import backtype.storm.topology.OutputFieldsDeclarer;
import java.util.Map;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import java.util.HashMap;
import java.util.Random;
import org.apache.log4j.Logger;


public class TopicSpout extends BaseRichSpout {
    
	public static Logger LOG = Logger.getLogger(TopicSpout.class);
    boolean _isDistributed;
    SpoutOutputCollector _collector;
    public final static String[] topics = {"Progress", "Education", "Poverty", "Health", "Freedom", "Security", "Justice",
    				"Equality", "Corruption", "RTI"};
    
    public TopicSpout() {
        this(true);
    }

    public TopicSpout(boolean isDistributed) {
        _isDistributed = isDistributed;
    }
        
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
    }
    
    public void close() {
        
    }
        
    public void nextTuple() {
        Utils.sleep(50);
        
        final Random rand = new Random();
        final String word = topics[rand.nextInt(topics.length)];
        _collector.emit(new Values(word));
    }
    
    public void ack(Object msgId) {

    }

    public void fail(Object msgId) {
        
    }
    
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        if(!_isDistributed) {
            Map<String, Object> ret = new HashMap<String, Object>();
            ret.put(Config.TOPOLOGY_MAX_TASK_PARALLELISM, 1);
            return ret;
        } else {
            return null;
        }
    }    
}