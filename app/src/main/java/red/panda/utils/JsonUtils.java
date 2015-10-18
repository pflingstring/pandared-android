package red.panda.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import red.panda.models.Conversation;
import red.panda.utils.misc.Constants;

public class JsonUtils
{
    public static JSONObject[] toArrayOfJSON(String json)
    {
        JSONObject[] result;
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            int length = jsonArray.length();
            result = new JSONObject[length];

            for (int i=0; i<length; i++)
                result[i] = jsonArray.getJSONObject(i);

            return result;

        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return result = null;
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


}
