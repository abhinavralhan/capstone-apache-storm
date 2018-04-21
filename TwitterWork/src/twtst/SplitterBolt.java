package twtst;

import java.util.Arrays;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class SplitterBolt implements IBasicBolt
{

	private String[] skipWords = {"rt", "to", "me","on","that","we're","the",
			    "followers","watch","know","not","have","like","i'm","new","good","do",
			    "more","followers","Followers","you","and","my","is",
			    "in","for","this","go","en","all","no","don't","up","are",
			    "http","http:","https","https:","http://","https://","with","just","your",
			    "want","your","you're","really","video","it's","when","they","their","much",
			    "would","what","them","todo","FOLLOW","retweet","retweet","even","right",
			    "like","will","Will","can't","were","twitter",
			    "make","take","this","from","about","follows","followed","the","as","https://t","it’s"};

	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
		declarer.declare(new Fields("Words"));
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
		
		String text = tuple.getStringByField("Text");
		text=text.toLowerCase();
		String delims = "[ .,?!]+";
	    String words[] = text.split(delims);
	    for (String w : words) 
	    {
	    	if(w.length()>3 && !Arrays.asList(skipWords).contains(w))
	    		if(!w.matches("@.*"))
	    		collector.emit(new Values(w));
	    }	
		
	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1) {
		// TODO Auto-generated method stub
		
	}
	
}