package twtst;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
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

	/* Driver Method to get started */
	public static void main(String args[])
	{
		//Topology Builder object to start building a Topology
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("TweetSpout", new TwitterSpout());
		
		
		//Create a Mongo Insert Bolt
		String url = "mongodb://127.0.0.1:27017/Twitter";
		String collectionName = "tweets";
		MongoMapper mapper = new SimpleMongoMapper()
		        .withFields("Text","Time","User","Country","Retweets","Favourites","Followers","Quoted","Hashtags");
		MongoInsertBolt insertBolt = new MongoInsertBolt(url, collectionName, mapper);
		
		
		//add mongo insert bolt to topology
		builder.setBolt("Mongo-Write", insertBolt).shuffleGrouping("TweetSpout");
		Config conf = new Config();

	
		//Submit the topology to the cluster	
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("Twitter", conf , builder.createTopology());
			
	}
	
	
}
