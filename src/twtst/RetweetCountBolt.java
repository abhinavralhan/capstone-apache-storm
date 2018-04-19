package twtst;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;

import java.util.*;
import java.util.concurrent.TimeUnit;


public final class RetweetCountBolt extends BaseRichBolt {
	
	public static void main(String[] args) {
		
		
            final Config config = new Config();
            config.setMessageTimeoutSecs(120);
            config.setDebug(false);
            
            final TopologyBuilder topologyBuilder = new TopologyBuilder();
            topologyBuilder.setSpout("twitterspout", new TwitterSpout());
            //Create Bolt with the frequency of logging [in seconds] and # of sorted [descending order] tweets to log.
            topologyBuilder.setBolt("retweetcountbolt", new RetweetCountBolt(30, 10))
                    .shuffleGrouping("twitterspout");
            final LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology(TOPOLOGY_NAME, config, topologyBuilder.createTopology());

/*            //Submit it to the cluster, or submit it locally
            if (null != args && 0 < args.length) {
                config.setNumWorkers(3);
                StormSubmitter.submitTopology(args[0], config, topologyBuilder.createTopology());
            } else {
                config.setMaxTaskParallelism(10);*/
                
/*                //Run this topology for 120 seconds so that we can complete processing of decent # of tweets.
                Utils.sleep(120 * 1000);

                LOGGER.info("Shutting down the cluster...");
                localCluster.killTopology(TOPOLOGY_NAME);
                localCluster.shutdown();
            }*/
        
	}
	public static final String TOPOLOGY_NAME = "TopRetweets";
	
    public static final Logger LOGGER = LoggerFactory.getLogger(RetweetCountBolt.class);
    public static final long serialVersionUID = -3110662788756713318L;

    /**
     * Interval between logging the output.
     */
    public final long logIntervalInSeconds;
    /**
     * Log only the retweets which crosses this threshold value.
     */
    public final int rankMaxThreshold;

    public SortedMap<String, Status> retweetCountTracker;
    private long runCounter;
    private Stopwatch stopwatch = null;

    public RetweetCountBolt(final long logIntervalInSeconds, final int rankMaxThreshold) {
        this.logIntervalInSeconds = logIntervalInSeconds;
        this.rankMaxThreshold = rankMaxThreshold;
    }

    public final void prepare1(final Map map, final TopologyContext topologyContext,
                              final OutputCollector collector) {
        
    }

    public final void declareOutputFields1(final OutputFieldsDeclarer outputFieldsDeclarer) {
    }

    public final void execute1(final Tuple input) {
        
    }

    private void logRetweetCount() {
        //Doing this circus so that we can sort the Map on the basis of # of retweets a tweet got.
        List<Map.Entry<String, Status>> list = new ArrayList<>(retweetCountTracker.entrySet());
        Collections.sort(list, new RetweetsOrdering());

        if (rankMaxThreshold < list.size()) {
            list = list.subList(0, rankMaxThreshold);
        }

        final List<String> newList = Lists.transform(list, getTweetGist());
        final String retweetInfo = Joiner.on("").join(newList);

        runCounter++;
        /*LOGGER.info("At {}, total # of retweeted tweets received in run#{}: {}", new Date(),
                runCounter, retweetCountTracker.size());
        LOGGER.info("\n{}", retweetInfo);

        // Empty retweetCountTracker Map for further iterations.*/
    	System.out.println("At {}, total # of retweeted tweets received in run#{}: {}" +  new Date() +
                runCounter + retweetCountTracker.size());
        retweetCountTracker.clear();

    }

    private static Function<Map.Entry<String, Status>, String> getTweetGist() {
        return input -> {
            final Status status = input.getValue();
            final StringBuilder retweetAnalysis = new StringBuilder();
            retweetAnalysis
                    .append("\t")
                    .append(status.getRetweetCount())
                    .append(" ==> @")
                    .append(status.getUser().getScreenName())
                    .append(" | ")
                    .append(status.getId())
                    .append(" | ")
                    .append(status.getText().replaceAll("\n", " "))
                    .append("\n");
            return retweetAnalysis.toString();
        };
    }

	@Override
	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		final Status retweet = (Status) input.getValueByField("Retweets");
        final String screenName = retweet.getUser().getScreenName();
        retweetCountTracker.put(screenName, retweet);

        if (logIntervalInSeconds <= stopwatch.elapsed(TimeUnit.SECONDS)) {
            logRetweetCount();
            stopwatch.reset();
            stopwatch.start();
        }
		
	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		// TODO Auto-generated method stub
		retweetCountTracker = Maps.newTreeMap();
        runCounter = 0;
        stopwatch = Stopwatch.createStarted();
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub
		
	}
}