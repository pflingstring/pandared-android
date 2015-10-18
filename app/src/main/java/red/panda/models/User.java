package red.panda.models;

import org.json.JSONException;
import org.json.JSONObject;
import red.panda.utils.JsonUtils;
import red.panda.utils.misc.Constants;

public class User
{
    private String id;
    private String avatar;
    private String username;

    public User(JSONObject json)
    {
        id = JsonUtils.getFieldFromJSON(json, "id");
        avatar = JsonUtils.getFieldFromJSON(json, "avatar");
        username = JsonUtils.getFieldFromJSON(json, "username");
    }

    public String getId()
    {
        return id;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public String getUsername()
    {
        return username;
    }

    public static User getAuthor(JSONObject authorJson, JSONObject toJson)
    {
        String myId = Constants.User.ID;
        String authorId = JsonUtils.getFieldFromJSON(authorJson, "id");

        if (authorJson != null && toJson != null)
        {
            if (myId.equals(authorId))
                return new User(toJson);
            else
                return new User(authorJson);
        }
        else
            return null;
    }

    @Override
    public String toString()
    {
        JSONObject json = new JSONObject();
        try
        {
            json.put("username", this.username);
            json.put("avatar", this.avatar);
            json.put("id", this.id);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return json.toString();
    }
}
