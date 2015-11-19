package red.panda.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import red.panda.models.Conversation;

public class JsonUtils
{
    public static List<Conversation> toConversationList(String json)
    {
        List<Conversation> result = new ArrayList<>();
        try
        {
            JSONArray jsonArray = new JSONObject(json).getJSONArray("data");

            for (int i=0; i<jsonArray.length(); i++)
                result.add(new Conversation(jsonArray.getJSONObject(i)));

            return result;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return result;
        }
    }

    public static List<JSONObject> toListOfJSON(String json)
    {
        List<JSONObject> result = new LinkedList<>();
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i=0; i<jsonArray.length(); i++)
                result.add(jsonArray.getJSONObject(i));

            return result;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return result = null;
        }
    }

    public static String getFieldFromJSON(JSONObject from, String field)
    {
        try
        {
            return from.getString(field);
        }
        catch (JSONException e)
        {
            return null;
        }
    }

    public static JSONObject getJson(JSONObject json, String name)
    {
        try
        {
            return json.getJSONObject(name);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    // TODO: 10/21/15 make generic
    public static JSONArray createJsonArray(String key, String value)
    {
        String jsonString = "{\"" + key + "\" : [\"" + value + "\"]}";
        try
        {
            return new JSONObject(jsonString).getJSONArray(key);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }


}
