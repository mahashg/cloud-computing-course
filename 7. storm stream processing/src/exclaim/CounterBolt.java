package exclaim;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class CounterBolt extends BaseRichBolt {
	
	public Map<String, Integer> map = new ConcurrentHashMap<String, Integer>();
	private OutputCollector _collector;

	@Override
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
		_collector = collector;
	}

	@Override
	public void execute(Tuple tuple) {
		String topic = tuple.getString(0);

		Integer count = map.get(topic);
		if(count == null){
			count = 0;
		}
		++count;
		map.put(topic, count);

		_collector.emit(tuple, new Values(topic, count));
		_collector.ack(tuple);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word", "count"));
	}
}