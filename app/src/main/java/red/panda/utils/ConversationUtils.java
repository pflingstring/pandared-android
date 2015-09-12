package red.panda.utils;

import red.panda.requests.ConversationRequest;
import red.panda.LoginActivity;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class ConversationUtils
{
    public static ConversationRequest requestConversationIDs(
            Listener<String> resListener, ErrorListener errListener, final Context context)
    {
        return new ConversationRequest(resListener, errListener)
        {
            // TODO: add headers to all requests automatically
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                String authToken = preferences.getString(LoginActivity.AUTH_TOKEN, null);
                headers.put("Authorization", authToken);
                return headers;
            }
        };
    }

    public static ConversationRequest requestConversationByID(
            String id, Listener<String> resListener, ErrorListener errListener, final Context context)
    {
        return new ConversationRequest(id, resListener, errListener)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                String authToken = preferences.getString(LoginActivity.AUTH_TOKEN, null);
                headers.put("Authorization", authToken);
                return headers;
            }
        };
    }

    public static void populateViewsWithPM(String response, ListView listView, Context context)
    {
        try
        {
            String[] ids = extractPmIds(response);
            ArrayAdapter<String> idAdapter = new ArrayAdapter<>(
                    context, android.R.layout.simple_list_item_1, ids);

            listView.setAdapter(idAdapter);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static String[] extractPmIds(String jsonResponse) throws JSONException
    {
        JSONArray jsonArray = new JSONObject(jsonResponse).getJSONArray("data");
        int length = jsonArray.length();
        String[] result = new String[length];

        for (int i=0; i<length; i++)
        {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            String id = jsonObj.getString("id");
            result[i] = id;
        }
        return result;
    }

}
