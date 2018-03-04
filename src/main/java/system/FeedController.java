package system;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import system.access.ChannelAnalytics;
import system.shared.Video;

@Log4j2
@RestController
public class FeedController
{
    @Autowired
    private LastFeedContainer lastFeedContainer;

    @Autowired
    private WordsFrequencyAnalyser wordsFrequencyAnalyser;

    @Autowired
    private ChannelAnalytics channelAnalytics;

    @RequestMapping("/trends")
    public List<Video> getTrends()
    {
        List<Video> feed = Lists.newArrayList();

        try
        {
            feed = lastFeedContainer.getAllFeed();
        }
        catch (Exception e)
        {
            log.error("Error", e);
        }

        return feed;
    }

    @RequestMapping("/popularWords")
    public  List<Entry<String, Integer>> getPopularWords(@RequestParam(value = "limit", defaultValue = "100") long limit)
    {
        List<Entry<String, Integer>> words = Lists.newArrayList();

        try
        {
            words = wordsFrequencyAnalyser.getPopularWords(limit);
        }
        catch (Exception e)
        {
            log.error("Error", e);
        }

        return words;
    }

    @RequestMapping("/topByHoursCount")
    public  List<Entry<String, Integer>> getTopByHoursCount()
    {
        List<Entry<String, Integer>> channels = Lists.newArrayList();

        try
        {
            channels = channelAnalytics.getTopChannelsByHoursInTrends();
        }
        catch (Exception e)
        {
            log.error("Error", e);
        }

        return channels;
    }

    @RequestMapping("/topByVideosCount")
    public  List<Entry<String, Integer>> getTopByVideosCount()
    {
        List<Entry<String, Integer>> channels = Lists.newArrayList();

        try
        {
            channels = channelAnalytics.getTopChannelsByCountUniqueVideoInTrends();
        }
        catch (Exception e)
        {
            log.error("Error", e);
        }

        return channels;
    }

}
