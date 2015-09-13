package red.panda.utils;

import red.panda.requests.ConversationRequest;
import red.panda.DisplayConversationActivity;
import red.panda.LoginActivity;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;

import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import android.content.SharedPreferences;
import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.content.Intent;
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
        return new ConversationRequest(id+"/?sort=-date", resListener, errListener)
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

    public static void populateViews(String jsonData, String field, @Nullable ListView listView, Context context)
    {
            String[] fields = extractFieldsFromJSONArray(jsonData, field);
            ArrayAdapter<String> idAdapter = new ArrayAdapter<>(
                    context, android.R.layout.simple_list_item_1, fields);

            if (listView != null)
                listView.setAdapter(idAdapter);
    }

    public static String[] extractFieldsFromJSONArray(String jsonResponse, String field)
    {
        JSONArray jsonArray;
        String[] result;
        try
        {
            jsonArray = new JSONObject(jsonResponse).getJSONArray("data");
            int length = jsonArray.length();
            result = new String[length];

            for (int i=0; i<length; i++)
            {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                String id = jsonObj.getString(field);
                result[i] = id;
            }
            return result;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static ErrorListener createErrorListener(final Context context, final String message)
    {
        return new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                createToast(context, message);
            }};
    }

    public static Toast createToast(final Context context, String message)
    {
        int length = Toast.LENGTH_SHORT;
        return Toast.makeText(context, message, length);
    }

    static Listener<String> createResponse(final Context context, @Nullable String id, final ListView listView)
    {
        if (id == null)
            // get all conversation's IDs
            return new Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    populateViews(response, "id", listView, context);
                }};
        else
            // load messages by ID in new Activity
            return new Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    Intent intent = new Intent(context, DisplayConversationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("PM", response);
                    context.startActivity(intent);
                }};
    }

    public static void createRequest(@Nullable String id, Context context, @Nullable ListView listView, String error)
    {
        ErrorListener errListener = createErrorListener(context, error);
        Listener<String> resListener;
        ConversationRequest request;

        if (id == null)
            resListener = createResponse(context, null, listView);
        else
            resListener = createResponse(context, id, listView);

        if (id == null)
            request = requestConversationIDs(resListener, errListener, context);
        else
            request = requestConversationByID(id, resListener, errListener, context);

        RequestQueueSingleton.addToQueue(request, context);
    }

}
