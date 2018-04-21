package twtst;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class CounterBolt implements IBasicBolt {

	private Map<String,Integer> countMap;
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
		declarer.declareStream("wordstream",new Fields("word","count"));
		declarer.declareStream("countrystream",new Fields("country","count"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		// TODO Auto-generated method stub
		
		if(tuple.getSourceStreamId().equals("Tweets"))
		{	
			String word = tuple.getString(3);
			if(countMap.get(word) == null)
			{
				countMap.put(word, 1);
			
			}
			else
			{
				Integer wcount = countMap.get(word);
				countMap.put(word, ++wcount);		
			}
			
			collector.emit("countrystream",new Values(word,countMap.get(word)));
		}
		else
		{
			
			String country = tuple.getString(0);
			if(countMap.get(country) == null)
			{
				countMap.put(country, 1);
			
			}
			else
			{
				Integer wcount = countMap.get(country);
				countMap.put(country, ++wcount);		
			}
			
			collector.emit("wordstream",new Values(country,countMap.get(country)));
		}
	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1) {
		
		countMap = new HashMap<String , Integer>();
		
	}

}
