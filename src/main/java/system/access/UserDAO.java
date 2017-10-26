package system.access;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import system.MyLogger;
import system.User;
import system.UserSettingsData;

public class UserDAO
{
    private JdbcTemplate jdbcTemplate;

    private static UserDAO instance;

    public UserDAO()
    {
        instance = this;
    }

    public static UserDAO getInstance()
    {
        return instance;
    }

    public void setDataSource(DataSource dataSource)
    {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void registerUserIfNotExist(User user)
    {
        String insertQuery = "INSERT INTO User (id, firstName, lastName, userName, language, isBot) " +
                             "VALUES (?, ?, ?, ?, ?, ?)" +
                             "ON DUPLICATE KEY UPDATE " +
                             "firstName = VALUES(firstName)," +
                             "lastName = VALUES(lastName)," +
                             "userName = VALUES(userName)," +
                             "language = VALUES(language)," +
                             "isBot = VALUES(isBot)";

        try
        {
            jdbcTemplate.update(insertQuery,
                                user.getId(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getUserName(),
                                user.getLanguageCode(),
                                user.getBot());
        }
        catch (Exception e)
        {
            MyLogger.logErr("registerUserIfNotExist error");
            e.printStackTrace();
        }

        MyLogger.logWarn("New user: " + user.getId() + " " + user.getFirstName());
    }

    public void subscribeUser(User user)
    {
        String updateQuery = "UPDATE User SET isSubscribe = 1 WHERE id = ?";

        try
        {
            jdbcTemplate.update(updateQuery, user.getId().toString());
        }
        catch (Exception e)
        {
            MyLogger.logErr("subscribeUser error");
            e.printStackTrace();
        }

        MyLogger.logWarn("Subscribe. User: " + user.getId().toString());
    }

    public void unsubscribeUser(User user)
    {
        String updateQuery = "UPDATE User SET isSubscribe = 0 WHERE id = ?";

        try
        {
            jdbcTemplate.update(updateQuery, user.getId().toString());
        }
        catch (Exception e)
        {
            MyLogger.logErr("unsubscribeUser error");
            e.printStackTrace();
        }

        MyLogger.logWarn("Unubscribe. User: " + user.getId().toString());
    }

    public List<User> getSubscribeUsers()
    {
        List<User> users = new ArrayList<>();

        String query = "SELECT * FROM User WHERE isSubscribe = 1";

        try
        {
            jdbcTemplate.query(query, result ->
            {
                User user = new User();

                user.setId(result.getInt("id"));
                user.setFirstName(result.getString("firstName"));
                user.setLastName(result.getString("lastName"));
                user.setUserName(result.getString("userName"));
                user.setLanguageCode(result.getString("language"));
                user.setBot(result.getBoolean("isBot"));
                user.setBanned(result.getBoolean("isBanned"));
                user.setSubscribe(result.getBoolean("isSubscribe"));

                users.add(user);
            });

            return users;
        }
        catch (Exception e)
        {
            MyLogger.logErr("getSubscribeUsers error");
            e.printStackTrace();
        }

        return users;
    }

    public boolean checkCredential(String chatId, String password)
    {
        String query = "SELECT password FROM User WHERE id = ?";

        StringBuilder passwordFromDB = new StringBuilder();

        try
        {
            jdbcTemplate.query(query, result ->
            {
                passwordFromDB.append(result.getString("password"));
            }, chatId);

            if (!passwordFromDB.toString().isEmpty())
            {
                return passwordFromDB.toString().equals(password);
            }
        }
        catch (Exception e)
        {
            MyLogger.logErr("checkCredential error");
            e.printStackTrace();
        }

        return false;
    }

    public UserSettingsData getUserSettingsData(String chatId)
    {
        List<String> bannedChannels = getBannedChannelsByUser(chatId);
        List<String> bannedTags = getBannedTagsByUser(chatId);

        UserSettingsData userSettingsData = new UserSettingsData();
        userSettingsData.setBannedChannels(bannedChannels);
        userSettingsData.setBannedTags(bannedTags);

        return userSettingsData;
    }


    public UserSettingsData setUserSettingData(UserSettingsData userSettingData)
    {
        String login = userSettingData.getCredentials().getLogin();

        insertBannedChannels(userSettingData.getBannedChannels());
        insertBannedTags(userSettingData.getBannedTags());

        String deleteChannelsQuery = "DELETE FROM UserBannedChannel WHERE userId = ?";
        String deleteTagsQuery = "DELETE FROM UserBannedTag WHERE userId = ?";


        String insertChannelQuery = "INSERT INTO UserBannedChannel (userId, channelId) " +
                                    "VALUES (?,?)";

        String insertTagsQuery = "INSERT INTO UserBannedTag (userId, tagId) " +
                                 "VALUES (?,?)";

        try
        {
            userSettingData.setBannedChannels(userSettingData.getBannedChannels().stream().distinct().collect(Collectors.toList()));
            userSettingData.setBannedTags(userSettingData.getBannedTags().stream().distinct().collect(Collectors.toList()));

            jdbcTemplate.update(deleteChannelsQuery, login);
            jdbcTemplate.update(deleteTagsQuery, login);

            for (String bannedChannel : userSettingData.getBannedChannels())
            {
                if (bannedChannel != null && !bannedChannel.isEmpty() && bannedChannel.length() < 64 && !bannedChannel.equals(" "))
                {
                    jdbcTemplate.update(insertChannelQuery, login, bannedChannel);
                }
            }

            for (String bannedTag : userSettingData.getBannedTags())
            {
                if (bannedTag != null && !bannedTag.isEmpty() && bannedTag.length() < 64 && !bannedTag.equals(" "))
                {
                    jdbcTemplate.update(insertTagsQuery, login, bannedTag);
                }
            }

            return getUserSettingsData(login);
        }
        catch (Exception e)
        {
            MyLogger.logErr(" error");
            e.printStackTrace();
        }

        return null;
    }

    private List<String> getBannedChannelsByUser(String chatId)
    {
        String query = "SELECT channelId FROM UserBannedChannel WHERE userId = ?";

        List<String> bannedChannels = new ArrayList<>();

        try
        {
            jdbcTemplate.query(query, result ->
            {
                bannedChannels.add(result.getString("channelId"));
            }, chatId);
        }
        catch (Exception e)
        {
            MyLogger.logErr("checkCredential error");
            e.printStackTrace();
        }

        return bannedChannels;
    }

    private List<String> getBannedTagsByUser(String chatId)
    {
        String query = "SELECT tagId FROM UserBannedTag WHERE userId = ?";

        List<String> bannedTags = new ArrayList<>();

        try
        {
            jdbcTemplate.query(query, result ->
            {
                bannedTags.add(result.getString("tagId"));
            }, chatId);
        }
        catch (Exception e)
        {
            MyLogger.logErr("checkCredential error");
            e.printStackTrace();
        }

        return bannedTags;
    }

    private void insertBannedChannels(List<String> bannedChannels)
    {
        String insertQuery = "INSERT INTO BannedChannel (name) " +
                             "VALUES (?)" +
                             "ON DUPLICATE KEY UPDATE " +
                             "name = VALUES(name)";

        try
        {
            for (String bannedChannel : bannedChannels)
            {
                if (bannedChannel != null && !bannedChannel.isEmpty() && bannedChannel.length() < 64 && !bannedChannel.equals(" "))
                {
                    jdbcTemplate.update(insertQuery, bannedChannel);
                }
            }
        }
        catch (Exception e)
        {
            MyLogger.logErr("registerUserIfNotExist error");
            e.printStackTrace();
        }
    }

    private void insertBannedTags(List<String> bannedTags)
    {
        String insertQuery = "INSERT INTO BannedTag (name) " +
                             "VALUES (?)" +
                             "ON DUPLICATE KEY UPDATE " +
                             "name = VALUES(name)";

        try
        {
            for (String bannedTag : bannedTags)
            {
                if (bannedTag != null && !bannedTag.isEmpty() && bannedTag.length() < 64 && !bannedTag.equals(" "))
                {
                    jdbcTemplate.update(insertQuery, bannedTag);
                }
            }
        }
        catch (Exception e)
        {
            MyLogger.logErr(" error");
            e.printStackTrace();
        }
    }
}
