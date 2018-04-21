package twtst;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.mongodb.bolt.MongoInsertBolt;
import org.apache.storm.mongodb.bolt.MongoUpdateBolt;
import org.apache.storm.mongodb.common.QueryFilterCreator;
import org.apache.storm.mongodb.common.SimpleQueryFilterCreator;
import org.apache.storm.mongodb.common.mapper.MongoMapper;
import org.apache.storm.mongodb.common.mapper.SimpleMongoMapper;
import org.apache.storm.mongodb.common.mapper.SimpleMongoUpdateMapper;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import twtst.TwitterSpout;

public class TwitterTopology {

	/* Driver Method to get started */
	public static void main(String args[])
	{
		//Topology Builder object to start building a Topology
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("TweetSpout", new TwitterSpout());
		
		
		//Create a Mongo Insert Bolt
		String url = "mongodb://mongo-flash:27017/Twitter";
		
		String collectionName = "Senti";
		MongoMapper mapper = new SimpleMongoMapper()
		        .withFields("score","text","time","country");
		MongoInsertBolt sentiInsert = new MongoInsertBolt(url, collectionName, mapper);
		
		//create Mongo upsert Bolt for word count
		collectionName = "WordCount";
		mapper = new SimpleMongoUpdateMapper()
		        .withFields("word" , "count");
		QueryFilterCreator updateQueryCreator = new SimpleQueryFilterCreator()
                .withField("word");
		MongoUpdateBolt updateBolt = new MongoUpdateBolt(url, collectionName, updateQueryCreator, mapper);
		updateBolt.withUpsert(true);
		
		//create Mongo upsert Bolt for country count
		collectionName = "CountryCount";
		mapper = new SimpleMongoUpdateMapper()
		        .withFields("country" , "count");
		updateQueryCreator = new SimpleQueryFilterCreator()
                .withField("country");
		MongoUpdateBolt upsertBolt = new MongoUpdateBolt(url, collectionName, updateQueryCreator, mapper);
		upsertBolt.withUpsert(true);
		
		//Create a sentiment analysis bolt
		builder.setBolt("senti", new SentiBolt())
		.shuffleGrouping("TweetSpout","Tweets");
		
		//create a Word count DAG
		builder.setBolt("Splitter", new SplitterBolt())
		.shuffleGrouping("TweetSpout","Tweets");
		builder.setBolt("counter", new CounterBolt())
		.fieldsGrouping("Splitter", new Fields("Words"))
		.fieldsGrouping("TweetSpout", "Tweets",new Fields("Country"));
		
		
		//add Mongo insert bolt for sentiment to topology
		builder.setBolt("Mongo-Write", sentiInsert)
		.shuffleGrouping("senti");
		Config conf = new Config();
		
		//add Mongo upsert for word count to topology
		builder.setBolt("Word-write", updateBolt)
		.shuffleGrouping("counter","wordstream");
	
		//add Mongo upsert for word count to topology
		builder.setBolt("country-write", upsertBolt)
		.shuffleGrouping("counter","countrystream");
		
		//Submit the topology to the cluster	
		try 
		{
			StormSubmitter.submitTopologyWithProgressBar("Twitter", conf, builder.createTopology());
		
		} catch (AlreadyAliveException e) {
			
			e.printStackTrace();
		} catch (InvalidTopologyException e) {
			
			e.printStackTrace();
		} catch (AuthorizationException e) {
			
			e.printStackTrace();
		}
			
	}
	
	
}
