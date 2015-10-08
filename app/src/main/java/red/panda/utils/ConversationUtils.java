package red.panda.utils;

import red.panda.activities.fragments.DisplayConversationFragment;
import red.panda.adapters.ConversationAdapter;
import red.panda.requests.ConversationRequest;

import red.panda.utils.misc.RequestQueueSingleton;
import red.panda.utils.misc.ItemClickSupport;
import red.panda.utils.misc.SharedPrefUtils;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;

import android.support.v7.widget.RecyclerView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import android.view.View;

import android.support.annotation.Nullable;
import android.content.Context;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ConversationUtils
{
    public static ConversationRequest requestConversations(Listener<String> resListener,
            ErrorListener errListener, final Context context)
    {
        return new ConversationRequest(resListener, errListener)
        {
            // TODO: add headers to all requests automatically
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = new HashMap<>();
                String authToken = SharedPrefUtils.getAuthToken(context);
                headers.put("Authorization", authToken);
                return headers;
            }
        };
    }

    public static ConversationRequest requestConversationByID(String id,
            Listener<String> resListener, ErrorListener errListener, final Context context)
    {
        return new ConversationRequest(id+"/?sort=-date", resListener, errListener)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = new HashMap<>();
                String authToken = SharedPrefUtils.getAuthToken(context);
                headers.put("Authorization", authToken);
                return headers;
            }
        };
    }

    static void populateViews(String input, final Context context, RecyclerView view)
    {
        RecyclerView.Adapter adapter;
        final JSONObject[] dataSet = JsonUtils.toArrayOfJSON(input);
        adapter = new ConversationAdapter(dataSet);
        view.setAdapter(adapter);

        ItemClickSupport.addTo(view).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                String username, avatar, conversationID, authorID;
                JSONObject json   = dataSet[position];
                JSONObject author = JsonUtils.getAuthor(json);

                username = JsonUtils.getFieldFromJSON(author, "username");
                avatar   = JsonUtils.getFieldFromJSON(author, "avatar");
                authorID = JsonUtils.getFieldFromJSON(author, "id");
                conversationID = JsonUtils.getFieldFromJSON(json, "id");

                Map<String, String> map = new HashMap<>();
                map.put("name", username);
                map.put("icon", avatar);
                map.put("id", authorID);

                String clickedUser = new JSONObject(map).toString();
                createRequest(conversationID, context, null, clickedUser);
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

    static Listener<String> createResponse(final Context context, @Nullable final String id,
            final RecyclerView view, @Nullable final String user)
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
                    FragmentActivity activity = (FragmentActivity) context;
                    Fragment fragment = DisplayConversationFragment.newInstance(response, user);
                    FragmentUtils.replaceFragmentWith(fragment, activity, true);
                }};
    }

    public static void createRequest(@Nullable String id, Context context,
            @Nullable RecyclerView view, @Nullable String user)
    {
        ErrorListener errListener = createErrorListener(context, "ConversationUtils error");
        Listener<String> resListener;
        ConversationRequest request;

        if (id == null)
        {
            resListener = createResponse(context, null, view, null);
            request = requestConversations(resListener, errListener, context);
        }
        else
        {
            resListener = createResponse(context, id, null, user);
            request = requestConversationByID(id, resListener, errListener, context);
        }
        RequestQueueSingleton.addToQueue(request, context);
    }

    public static String makeAvatarURL(String id)
    {
        String url = "https://i.imgur.com/";
        return url + id + "s.png";
    }

}