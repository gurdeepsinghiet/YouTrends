package system;

import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;

import lombok.extern.log4j.Log4j2;
import system.shared.Feed;
import system.shared.Video;

@Log4j2
public class ImageCollector
{
    void collectImages(Feed feed)
    {
        for (Video video : feed.getVideos())
        {
            try
            {
                URL url = new URL(video.getImgUrl());

                video.setImage(ImageIO.read(url));
            }
            catch (MalformedURLException e)
            {
                log.warn("collect image error on video {}", video.getTitle());
            }
            catch (Exception e)
            {
                log.error("Error", e);
            }
        }
    }
}
