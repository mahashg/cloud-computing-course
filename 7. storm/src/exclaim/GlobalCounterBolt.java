package exclaim;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class GlobalCounterBolt extends BaseRichBolt {

	public int count;
	public String word;

	public int emit_time;
	public long last_time;
	
	private OutputCollector _collector;
	
	public GlobalCounterBolt(int time){
		this.emit_time = time;
		this.last_time = System.currentTimeMillis();
	}
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {

		this._collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		String word = input.getString(0);
		int word_count = input.getInteger(1);
		if(word_count > count){
			count = word_count;
			this.word = word;
			emitValue();
		}
		_collector.ack(input);
	}

	private void emitValue() {
		long current_time = System.currentTimeMillis();
		int no_seconds = (int) ((current_time-last_time)/1000);
		
		if(no_seconds >= emit_time){	
			_collector.emit(this.word, new Values(word));
			System.out.println("Emitting ("+this.word+", "+this.count+") !!!");
			last_time = current_time;
		}
		
	}
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));		
	}

}
