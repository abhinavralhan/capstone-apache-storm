package twtst;

import org.apache.storm.Config;

import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.mongodb.bolt.MongoInsertBolt;
import org.apache.storm.mongodb.common.mapper.MongoMapper;
import org.apache.storm.mongodb.common.mapper.SimpleMongoMapper;
import org.apache.storm.topology.TopologyBuilder;
import twtst.TwitterSpout;

public class TwitterTopology {

	public static void main(String args[])
	{
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("TweetSpout", new TwitterSpout(),3);
		String url = "mongodb://127.0.0.1:27017/Twitter";
		String collectionName = "tweets";
		MongoMapper mapper = new SimpleMongoMapper()
		        .withFields("Text","id");
		MongoInsertBolt insertBolt = new MongoInsertBolt(url, collectionName, mapper);
		builder.setBolt("Mongo-Write", insertBolt,8).shuffleGrouping("TweetSpout");
		Config conf = new Config();
		conf.setDebug(false);
		conf.setNumWorkers(3);;
		try {
			StormSubmitter.submitTopologyWithProgressBar("Twitter", conf, builder.createTopology());
		} catch (AlreadyAliveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTopologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
