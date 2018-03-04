package system.access;

import java.sql.Timestamp;
import java.util.List;
import javax.sql.DataSource;

import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import system.shared.Video;

@Log4j2
public class VideoDAO extends AbstractDAO
{
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
            log.error("Insert video error");
        }
    }

    public List<String> getUniqueTitlesByLastDay()
    {
        String query = "SELECT DISTINCT title FROM Video WHERE date > (now() - INTERVAL '1 day')";

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
            log.error("Insert video error");
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
