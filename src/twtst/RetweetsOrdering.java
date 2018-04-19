package twtst;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;
import twitter4j.Status;

import java.util.Map;


public final class RetweetsOrdering extends Ordering<Map.Entry<String, Status>> {
    @Override
    public final int compare(final Map.Entry<String, Status> status01,
                             final Map.Entry<String, Status> status02) {
        final long retweetCount01 = status01.getValue().getRetweetCount();
        final long retweetCount02 = status02.getValue().getRetweetCount();
        return Longs.compare(retweetCount02, retweetCount01);
    }
}