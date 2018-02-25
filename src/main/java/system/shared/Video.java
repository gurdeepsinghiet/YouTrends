package system.shared;

import java.awt.image.BufferedImage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Video
{
    // id use for creating video URL
    private String id;
    private String title;
    private String description;
    private String channel;
    private String imgUrl;
    private String old;
    private long viewCount;

    // FileId - need for incremental send photo without downloading photo again.
    @JsonIgnore
    private String fileId;

    @JsonIgnore
    private BufferedImage image;

    public Video(String id, String title, String imgUrl)
    {
        this.id = id;
        this.title = title;
        this.imgUrl = imgUrl;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Video && id.equals(((Video) obj).getId());
    }
}
