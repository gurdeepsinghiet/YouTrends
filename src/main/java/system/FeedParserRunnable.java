package system;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import system.access.VideoDAO;
import system.parser.YouTubeParser;
import system.shared.Feed;

public class FeedParserRunnable implements Runnable
{
    private static final Logger logger = LogManager.getLogger(FeedParserRunnable.class);

    private final ImageCollector imageCollector = new ImageCollector();
    private final LastFeedContainer lastFeedContainer;
    private final VideoDAO videoDAO;

    FeedParserRunnable(LastFeedContainer lastFeedContainer, VideoDAO videoDAO)
    {
        this.lastFeedContainer = lastFeedContainer;
        this.videoDAO = videoDAO;
    }

    @Override
    public void run()
    {
        try
        {
            // Sometimes YouTube may be not reachable. Next loop do 5 iterations to try to get feed from YouTube.
            for (int i = 0; i < 6; i++)
            {
                Feed feed = parseFeed();

                if (!feed.getVideos().isEmpty())
                {
                    lastFeedContainer.setFeed(feed);

                    Timestamp lastInsertDate = videoDAO.getLastInsertDate();

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);

                    // this check need for don't write videos into database on start project when its not required
                    if (System.currentTimeMillis() - lastInsertDate.getTime() > TimeUnit.MINUTES.toMillis(59) ||
                        calendar.getTimeInMillis() - lastInsertDate.getTime() > 0)
                    {
                        videoDAO.insertVideos(feed.getVideos(), new Timestamp(calendar.getTimeInMillis()));
                    }

                    return;
                }

                logger.warn("Cant get feed");
                Thread.sleep(TimeUnit.MINUTES.toMillis(10));
            }
        }
        catch (Exception e)
        {
            logger.error("Error", e);
        }
    }

    private Feed parseFeed()
    {
        try
        {
            logger.info("Start feed collect");
            ScheduledFuture<Feed> future = Executors.newSingleThreadScheduledExecutor()
                                                    .schedule(new YouTubeParser(),
                                                              0,
                                                              TimeUnit.MICROSECONDS);

            Feed feed = future.get();
            logger.info("Feed was collect. Feed size: {}", feed.getVideos().size());
            logger.info("Start collect images");
            imageCollector.collectImages(feed);
            return feed;
        }
        catch (Exception e)
        {
            logger.error("Error", e);
            return new Feed();
        }
    }
}
