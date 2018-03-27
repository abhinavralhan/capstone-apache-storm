package twtst;


import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;


import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public final class TwitterSpout extends BaseRichSpout{

	private static final Logger log = LoggerFactory.getLogger(TwitterSpout.class);
	private static final long serialVersionUID = -6815379407002113362L;
	
	
	private LinkedBlockingQueue<Status> queue;
	private TwitterStream twitterStream;
	private SpoutOutputCollector op;
	
	@Override
	public void nextTuple() {
		// TODO Auto-generated method stub
		
		Status status = queue.poll();
		if(status == null)
		{
			Utils.sleep(500);
		}
		else
		{
			this.op.emit(new Values(status.getText() , status.getId()));
			log.error(status.toString());
		}
		
	}
	
	@Override
	public void close()
	{
		this.twitterStream.cleanUp();
		this.twitterStream.shutdown();
	}

	@Override
	public void open(Map arg0, TopologyContext arg1, SpoutOutputCollector arg2){
		// TODO Auto-generated method stub
		this.queue = new LinkedBlockingQueue<>(1000); 
		this.op = arg2;
		
		final StatusListener statusListener = new StatusListener() {

			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatus(final Status arg0) {
				// TODO Auto-generated method stub
				
				queue.offer(arg0);
				
			}

			@Override
			public void onTrackLimitationNotice(int arg0) {
				// TODO Auto-generated method stub
				
			}};
			
			final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
			configurationBuilder.setIncludeEntitiesEnabled(true);
			configurationBuilder.setOAuthAccessToken("98341825-qv8wF5hwRCGRrTpzxP7fes4nJ1g9rjHFyBuHOzFbu");
			configurationBuilder.setOAuthAccessTokenSecret("WW5SQKeJxuqdNr3vJ8ybNCPAYqOiCHDz2XLhMtnszVzNj");
			configurationBuilder.setOAuthConsumerKey("PdAKVXyxZAlzHQkH62RhgkuBY");
			configurationBuilder.setOAuthConsumerSecret("P0qJdJmzclI6otwiApVee7UcjWl4GcZa8GkGWHoV4IdOyjsfM1");
			this.twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance(); 
			this.twitterStream.addListener(statusListener);
			
			final FilterQuery filterQuery = new FilterQuery(); 
			final double[][] boundingBoxOfUS = {{-124.848974, 24.396308},
                    {-66.885444, 49.384358}};
			filterQuery.locations(boundingBoxOfUS);
			this.twitterStream.filter(filterQuery);
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub
		arg0.declare(new Fields("Text","id"));
	}

}
