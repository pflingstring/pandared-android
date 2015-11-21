package red.panda.models;

import org.json.JSONObject;

import red.panda.utils.JsonUtils;
import red.panda.utils.misc.Constants;

public final class ConversationMessage
{
    private final String date;
    private final String message;
    private final String conversationId;
    private final boolean authorIsMe;

    public ConversationMessage(JSONObject json)
    {
        date = JsonUtils.getFieldFromJSON(json, "date");
        message = JsonUtils.getFieldFromJSON(json, "msg");
        conversationId = JsonUtils.getFieldFromJSON(json, "conversationId");
        authorIsMe = Constants.User.ID.equals(
                JsonUtils.getFieldFromJSON(json, "authorId"));
    }

    public String getDate()
    {
        return date;
    }

    public String getMessage()
    {
        return message;
    }

    public boolean isAuthorIsMe()
    {
        return authorIsMe;
    }

    public String getConversationId()
    {
        return conversationId;
    }

}
