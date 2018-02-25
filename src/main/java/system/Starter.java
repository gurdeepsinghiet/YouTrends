package system;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import system.access.UserDAO;
import system.access.VideoDAO;

public class Starter
{
    private static final Logger logger = LogManager.getLogger(Starter.class);

    private static final int DISPATCHER_PERIOD = 1; // Days

    private static final int DISPATCHER_HOUR = 20;
    private static final int DISPATCHER_MINUTES = 0;
    private static final int DISPATCHER_SECONDS = 0;

    public Starter(Telegram telegram, LastFeedContainer lastFeedContainer, UserDAO userDAO, VideoDAO videoDAO)
    {
        try
        {
            startDailyDispatcher(telegram, lastFeedContainer, userDAO);
            startHourlyParser(lastFeedContainer, videoDAO);
        }
        catch (Exception e)
        {
            logger.error("Some error", e);
        }
    }

    private void startDailyDispatcher(Telegram telegram, LastFeedContainer lastFeedContainer, UserDAO userDAO)
    {
        // Берём сегодняшний день и задаем время рассылки
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, DISPATCHER_HOUR);
        calendar.set(Calendar.MINUTE, DISPATCHER_MINUTES);
        calendar.set(Calendar.SECOND, DISPATCHER_SECONDS);

        // Высчитываем через сколько миллисекунд нужно будет запустить рассылку
        long startDelay = calendar.getTimeInMillis() - System.currentTimeMillis();

        // Если уже больше времени рассылки
        if (startDelay < 0)
        {
            calendar.add(Calendar.DAY_OF_YEAR, DISPATCHER_PERIOD);

            startDelay = calendar.getTimeInMillis() - System.currentTimeMillis();
        }

        // Start every day feed dispatcher
        Executors.newSingleThreadScheduledExecutor()
                 .scheduleWithFixedDelay(new EveryDayFeedDispatcher(telegram, userDAO, lastFeedContainer),
                                         startDelay,
                                         TimeUnit.DAYS.toMillis(DISPATCHER_PERIOD),
                                         TimeUnit.MILLISECONDS);
    }

    private void startHourlyParser(LastFeedContainer lastFeedContainer, VideoDAO videoDAO)
    {
        FeedParserRunnable feedParserRunnable = new FeedParserRunnable(lastFeedContainer, videoDAO);

        Executors.newSingleThreadScheduledExecutor().schedule(feedParserRunnable, 0, TimeUnit.SECONDS);

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        // Set calendar to begin next hour
        calendar.set(Calendar.HOUR_OF_DAY, currentHour + 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long initialDelay = calendar.getTimeInMillis() - System.currentTimeMillis();

        // Start FeedParser every hour
        Executors.newSingleThreadScheduledExecutor()
                 .scheduleWithFixedDelay(feedParserRunnable,
                                         initialDelay,
                                         TimeUnit.HOURS.toMillis(1),
                                         TimeUnit.MILLISECONDS);
    }
}
