package red.panda.models;

import org.json.JSONObject;
import red.panda.utils.JsonUtils;
import red.panda.utils.misc.Constants;

public class Conversation
{

    private String id;
    private String lastReplyOn;
    private String lastMessage;
    private User user;


    private boolean hasUnreadMessages;

    public Conversation(JSONObject jsonInput)
    {
        if (jsonInput.has("author") && jsonInput.has("to"))
            user = JsonUtils.getFieldFromJSON(jsonInput, "authorId").equals(Constants.User.ID)
                    ? new User(JsonUtils.getJson(jsonInput, "to"))
                    : new User(JsonUtils.getJson(jsonInput, "author"));

        id = JsonUtils.getFieldFromJSON(jsonInput, "id");
        lastReplyOn = JsonUtils.getFieldFromJSON(jsonInput, "lastReplyOn");
        lastMessage = "not implemented yet";
        hasUnreadMessages = false;
    }

    public void setHasUnreadMessages(boolean flag)
    {
        hasUnreadMessages = flag;
    }

    public void setLastReplyOn(String date)
    {
        lastReplyOn = date;
    }

    public String getId()
    {
        return id;
    }

    public String getLastReplyOn()
    {
        return lastReplyOn;
    }

    public String getLastMessage()
    {
        return lastMessage;
    }

    public boolean hasUnreadMessages()
    {
        return hasUnreadMessages;
    }

    public User getUser()
    {
        return user;
    }
}
