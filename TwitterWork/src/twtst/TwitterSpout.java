package twtst;


import java.util.ArrayList;
import java.util.List;
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

import com.mongodb.BasicDBObject;

import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
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
	private SpoutOutputCollector collector;

	
	/* Fill This method with computation that needs to be performed */
	public void nextTuple() {
		
		int followers=0;
		String  quoted ="", username="" ,country="";
		List<String> hashtags = new ArrayList<String>();
		BasicDBObject doc = new BasicDBObject(); 
		
		Status status = queue.poll();
		if(status == null)
		{
			//sleep if no tweet found to minimize waste of resources
			Utils.sleep(500); 
		}
		else
		{
			if(status.getUser()!=null)
				{
					followers = status.getUser().getFollowersCount();
					username  = status.getUser().getScreenName();
				}
			
			if(status.getQuotedStatus()!=null)
				quoted = status.getQuotedStatus().getText();

			if(status.getHashtagEntities()!=null)
				{
					HashtagEntity hash[] = status.getHashtagEntities();
					for(int i=0;i<hash.length;i++)
						hashtags.add(hash[i].getText());
					doc.append("tags", hashtags);				
				}
			if(status.getPlace()!=null)
				country = status.getPlace().getCountry();
			
			this.collector.emit(new Values(status.getText() , status.getCreatedAt() 
					, username , country
					, status.getRetweetCount() , status.getFavoriteCount()
					, followers, quoted
					,  doc
					));
			log.error(status.toString());
			
		}
		
	}
	
	@Override
	public void close()
	{
		this.twitterStream.cleanUp();
		this.twitterStream.shutdown();
	}
	
	/* code that needs to be executed once before we start streaming */
	@Override
	public void open(Map map, TopologyContext context, SpoutOutputCollector collector){

		
		
		this.queue = new LinkedBlockingQueue<>(1000); 
		this.collector = collector;
		
		final StatusListener statusListener = new StatusListener() {

			@Override
			public void onException(Exception arg0) {
				
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				
			}

			@Override
			public void onStatus(final Status arg0) {
				
				//offer tweet on availability
				queue.offer(arg0);
				
			}

			@Override
			public void onTrackLimitationNotice(int arg0) {
								
			}};
			
			//create a configuration with O-Auth credentials
			final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
			configurationBuilder.setIncludeEntitiesEnabled(true);
			configurationBuilder.setOAuthAccessToken("98341825-CLmXalWa10Yvt9hoTDW4yyylW6JXcTZLOyeaVyV91");
			configurationBuilder.setOAuthAccessTokenSecret("kfu7TVVB7pZBFx4F6WoXpMQG2ZRsnErGUVm5FgpH1q1iT");
			configurationBuilder.setOAuthConsumerKey("aNQTYWdH9XujXDArVhl50M2Dm");
			configurationBuilder.setOAuthConsumerSecret("YfNLjaX0BW1yCmdsnKkCt87ftUL5rgjhOPlM0zxYP23qCzGK1h");
			
			//generate a stream instance with interface we created
			this.twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance(); 
			this.twitterStream.addListener(statusListener);
			
			//filter tweets from all over the world , will not work without at least one bounding box
			double[][] bb= {{-180, -90}, {180, 90}}; 
			final FilterQuery filterQuery = new FilterQuery(); 
			filterQuery.language(new String[]{"en"});
			filterQuery.locations(bb);
			this.twitterStream.filter(filterQuery);
		
	}

	/* declare fields that the spout will be producing */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
		// TODO Auto-generated method stub
		declarer.declare(new Fields("Text","Time","User","Country","Retweets","Favourites","Followers","Quoted","Hashtags"));
	}

}
