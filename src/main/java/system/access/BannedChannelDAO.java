package system.access;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import lombok.extern.log4j.Log4j2;
import system.shared.User;

@Log4j2
public class BannedChannelDAO extends AbstractDAO
{
    public BannedChannelDAO(DataSource dataSource)
    {
        super(dataSource);
    }

    public List<String> getBannedChannels(String userId)
    {
        List<String> bannedChannels = new ArrayList<>();

        String query = "SELECT name FROM BannedChannel, UserBannedChannel " +
                       "WHERE UserBannedChannel.channelId = BannedChannel.name AND " +
                       "UserBannedChannel.userId = ?";

        try
        {
            jdbcTemplate.query(query, result ->
            {
                bannedChannels.add(result.getString("name"));
            }, Long.valueOf(userId));

            return bannedChannels;
        }
        catch (Exception e)
        {
            log.error("Error", e);
        }

        return bannedChannels;
    }

    public boolean add(User user, String channel)
    {
        List<String> bannedChannels = getBannedChannels(user.getId().toString());

        if (!bannedChannels.contains(channel))
        {
            String insertBannedChannelQuery = "INSERT INTO BannedChannel (name) VALUES (?) ON CONFLICT DO NOTHING";
            String insertUserBannedChannelQuery = "INSERT INTO UserBannedChannel (userid, channelid) VALUES (?, ?) " +
                                                  "ON CONFLICT DO NOTHING";

            jdbcTemplate.update(insertBannedChannelQuery, channel);
            jdbcTemplate.update(insertUserBannedChannelQuery, user.getId(), channel);

            return true;
        }

        return false;
    }

    public boolean remove(User user, String channel)
    {
        String removeUserBannedChannelQuery = "DELETE FROM UserBannedChannel WHERE userid = ? AND channelid = ? ";

        int affectedRows = jdbcTemplate.update(removeUserBannedChannelQuery, user.getId(), channel);

        return affectedRows == 1;
    }

}
