package red.panda.utils;

import red.panda.requests.ConversationRequest;
import red.panda.DisplayConversationFragment;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.Fragment;

import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;
import android.view.View;

import android.content.SharedPreferences;
import android.content.Context;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import red.panda.R;

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
                String authToken = preferences.getString(SharedPrefUtils.AUTH_TOKEN, null);
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
                String authToken = preferences.getString(SharedPrefUtils.AUTH_TOKEN, null);
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
            return new Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    populateViews(response, context, view);
                }};
        else
            return new Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    Fragment fragment = DisplayConversationFragment.newInstance(response);
                    FragmentActivity activity = (FragmentActivity) context;
                    FragmentManager manager = activity.getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.container_body, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
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

}

