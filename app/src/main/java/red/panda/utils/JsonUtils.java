package red.panda.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    // TODO: rename the method
    public static JSONObject getAuthor(JSONObject json)
    {
        try
        {
            String authorUsername, myUsername;
            JSONObject myUserJSON = new JSONObject(Constants.User.USER_DETAILS);
            JSONObject authorJSON = json.getJSONObject("author");
            JSONObject toJSON = json.getJSONObject("to");

            authorUsername = JsonUtils.getFieldFromJSON(authorJSON, "username");
            myUsername = JsonUtils.getFieldFromJSON(myUserJSON, "username");
            boolean authorIsMe;

            authorIsMe = (myUsername != null) && (myUsername.equals(authorUsername));
            return authorIsMe ? toJSON : authorJSON;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }


}
