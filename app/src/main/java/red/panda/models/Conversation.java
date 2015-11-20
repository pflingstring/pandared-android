package red.panda.models;

import red.panda.utils.JsonUtils;
import org.json.JSONObject;

public class Conversation
{
    private String id;
    private User user;
    private String lastReplyOn;
    private String lastMessage;
    private boolean hasUnreadMessages;

    private Conversation(JSONObject json, User user)
    {
        id = JsonUtils.getFieldFromJSON(json, "id");
        lastReplyOn = JsonUtils.getFieldFromJSON(json, "lastReplyOn");
        lastMessage = "not implemented yet";
        hasUnreadMessages = false;
        this.user = user;
    }

    public static Conversation createConversationWithUser(JSONObject json)
    {
        User user = User.getAuthor(
                JsonUtils.getJson(json, "author"),
                JsonUtils.getJson(json, "to"));
        return new Conversation(json, user);
    }

    private Conversation(JSONObject json)
    {
        id = JsonUtils.getFieldFromJSON(json, "id");
        lastReplyOn = JsonUtils.getFieldFromJSON(json, "lastReplyOn");
        lastMessage = "not implemented yet";
        hasUnreadMessages = true;
    }

    public static Conversation createUnreadConversation(JSONObject json)
    {
        return new Conversation(json);
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
