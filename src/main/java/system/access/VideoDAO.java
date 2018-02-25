package system.access;

import java.sql.Timestamp;
import java.util.List;
import javax.sql.DataSource;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import system.shared.Video;

public class VideoDAO extends AbstractDAO
{
    private static final Logger logger = LogManager.getLogger(VideoDAO.class);

    public VideoDAO(DataSource dataSource)
    {
        super(dataSource);
    }

    public void insertVideos(List<Video> videos, Timestamp dateOfInsert)
    {
        videos.forEach(video -> insertVideo(video, dateOfInsert));
    }

    public void insertVideo(Video video, Timestamp dateOfInsert)
    {
        String query = "INSERT INTO Video (videoId, title, description, channel, imgUrl, old, viewCount, date) " +
                       "VALUES (?,?,?,?,?,?,?,?)";

        try
        {
            jdbcTemplate.update(query,
                                video.getId(),
                                video.getTitle(),
                                video.getDescription(),
                                video.getChannel(),
                                video.getImgUrl(),
                                video.getOld(),
                                video.getViewCount(),
                                dateOfInsert);
        }
        catch (Exception e)
        {
            logger.error("Insert video error");
        }
    }

    public List<String> getUniqueTitlesByLastWeek()
    {
        String query = "SELECT DISTINCT title FROM Video WHERE date > (now() - INTERVAL '1 week')";

        List<String> titles = Lists.newArrayList();

        try
        {
            jdbcTemplate.query(query,
                               rs ->
                               {
                                   titles.add(rs.getString("title"));
                               }
                              );
        }
        catch (Exception e)
        {
            logger.error("Insert video error");
        }

        return titles;
    }

    public Timestamp getLastInsertDate()
    {
        String query = "SELECT date FROM Video ORDER BY date DESC LIMIT 1";

        Timestamp date = new Timestamp(0);

        // Incredible crutch, but jdbcTemplate.queryForObject not working. smth throwable exception.
        jdbcTemplate.query(query,
                           rs ->
                           {
                               date.setTime(rs.getTimestamp("date").getTime());
                           }
                          );

        return date;
    }
}
