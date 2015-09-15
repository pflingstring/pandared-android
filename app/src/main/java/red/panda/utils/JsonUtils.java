package red.panda.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

}
