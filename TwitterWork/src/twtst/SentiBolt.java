package twtst;

import java.net.URISyntaxException;
import java.util.Map;

import test.openNLPCategorizer;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.google.common.io.Resources;

public class SentiBolt implements IBasicBolt{

	private openNLPCategorizer categorizer;
	
	@Override
	public void cleanup() {
		// clean up after shutdown
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// Declare the output Fields
		
		declarer.declare(new Fields("score","text","time","country"));
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		
		if(tuple!=null)
		{
			String text = tuple.getString(0);
			String country = tuple.getString(3);
			String time = tuple.getString(1);
			int sentiment = categorizer.classifyNewTweet(text);
			collector.emit(new Values(sentiment , text , time , country ));
		}
		
	}

	@Override
	public void prepare(Map map, TopologyContext context) {
		// TODO Auto-generated method stub
		this.categorizer = new openNLPCategorizer();
		categorizer.trainModel(Resources.getResource("tweets.txt"));
	}

}
