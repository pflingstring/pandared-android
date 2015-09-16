package red.panda.utils;

import red.panda.requests.ConversationRequest;
import red.panda.DisplayConversationActivity;
import red.panda.ConversationActivity;
import red.panda.LoginActivity;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;

import android.support.v7.widget.RecyclerView;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import android.content.SharedPreferences;
import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import java.util.HashMap;
import java.util.Map;

public class ConversationUtils
{
    public static ConversationRequest requestConversations(
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

    static void populateViews(String input, final Context context, RecyclerView view)
    {
        RecyclerView.Adapter adapter;
        final JSONObject[] dataSet = JsonUtils.toArrayOfJSON(input);
        adapter = new ConversationPeopleAdapter(dataSet);
        view.setAdapter(adapter);

        ItemClickSupport.addTo(view).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                JSONObject json = dataSet[position];
                String id = JsonUtils.getFieldFromJSON(json, "id");

                createRequest(id, context, null);
            }
        });
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

    static Listener<String> createResponse(final Context context, @Nullable String id,
               final RecyclerView view)
    {
        if (id == null)
            // get all conversation's IDs
            return new Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    populateViews(response, context, view);
                }};
        else
            // load messages by ID in new Activity
            return new Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    Intent intent = new Intent(context, DisplayConversationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ConversationActivity.MESSAGES, response);
                    context.startActivity(intent);
                }};
    }

    public static void createRequest(@Nullable String id, Context context, @Nullable RecyclerView view)

    {
        ErrorListener errListener = createErrorListener(context, "ConversationUtils error");
        Listener<String> resListener;
        ConversationRequest request;

        if (id == null)
        {
            resListener = createResponse(context, null, view);
            request = requestConversations(resListener, errListener, context);
        }
        else
        {
            resListener = createResponse(context, id, view);
            request = requestConversationByID(id, resListener, errListener, context);
        }
        RequestQueueSingleton.addToQueue(request, context);
    }

    public static boolean authorIsMe(String id)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(Constants.User.USER_DETAILS);
            String myID = jsonObject.getString("id");
            return myID.equals(id);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}

