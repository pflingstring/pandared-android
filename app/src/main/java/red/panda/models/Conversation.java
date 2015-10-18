package red.panda.models;

import org.json.JSONObject;
import red.panda.utils.JsonUtils;
import red.panda.utils.misc.Constants;

public class Conversation
{

    private String id;
    private String toUser;
    private String lastReplyOn;
    private String lastMessage;


    private boolean hasUnreadMessages;

    public Conversation(JSONObject jsonInput)
    {
        id = JsonUtils.getFieldFromJSON(jsonInput, "id");
        toUser = JsonUtils.getFieldFromJSON(jsonInput, "authorId").equals(Constants.User.ID)
                ? JsonUtils.getFieldFromJSON(jsonInput, "authorId")
                : JsonUtils.getFieldFromJSON(jsonInput, "toAuthorId");
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

    public String getToUser()
    {
        return toUser;
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
}
