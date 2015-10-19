package red.panda.utils;

import red.panda.activities.fragments.DisplayConversationFragment;
import red.panda.adapters.ConversationAdapter;
import red.panda.models.Conversation;
import red.panda.models.User;
import red.panda.requests.ConversationRequest;

import red.panda.utils.misc.RequestQueueSingleton;
import red.panda.utils.misc.ItemClickSupport;
import red.panda.utils.misc.SharedPrefUtils;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.github.nkzawa.socketio.client.Socket;

import android.support.v7.widget.RecyclerView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import android.view.View;

import android.support.annotation.Nullable;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
        final Set<String> ids = new HashSet<>();
        final StringBuilder stringBuilder = new StringBuilder();

        Response.ErrorListener errListener = createErrorListener(context, "UNREAD ERROR");
        Response.Listener<String> listener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                stringBuilder.append(response);
                try
                {
                    JSONArray unreadJson = new JSONObject(stringBuilder.toString()).getJSONArray("data");
                    for (int i=0; i<unreadJson.length(); i++)
                    {
                        JSONObject json = unreadJson.getJSONObject(i);
                        Conversation conversation = new Conversation(json);
                        conversation.setHasUnreadMessages(true);
                        ids.add(conversation.getId());
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };
        ConversationRequest unreadReq = ConversationUtils.requestConversationByID("unread", listener, errListener, context);
        RequestQueueSingleton.addToQueue(unreadReq, context);

        RecyclerView.Adapter adapter;
        final JSONObject[] dataSet = JsonUtils.toArrayOfJSON(input);
        adapter = new ConversationAdapter(dataSet, ids);
        view.setAdapter(adapter);

        ItemClickSupport.addTo(view).setOnItemClickListener(new ItemClickSupport.OnItemClickListener()
        {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v)
            {
                JSONObject json = dataSet[position];
                User clickedUser = User.getAuthor(JsonUtils.getJson(json, "author"), JsonUtils.getJson(json, "to"));
                Conversation conversation = new Conversation(json);
                conversation.setHasUnreadMessages(false);

                Socket socket = SocketUtils.init();
                try
                {
                    //TODO: refactor
                    String[] unreadMsg = {conversation.getId()};
                    JSONArray jsonArray = new JSONArray(unreadMsg);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("captures", jsonArray);
                    socket.emit("seen-on:post", jsonObject);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                createRequest(conversation.getId(), context, null, clickedUser.toString());
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