package red.panda.utils;

import red.panda.Config;
import red.panda.activities.fragments.DisplayConversationFragment;
import red.panda.models.Conversation;
import red.panda.models.User;
import red.panda.requests.ConversationRequest;
import red.panda.utils.misc.Constants;
import red.panda.utils.misc.RequestQueueSingleton;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class ConversationUtils
{
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

    public static void getUnreadMessages(Context context, final Set<String> result)
    {
        ErrorListener onError = createErrorListener(context, "Unable to get the unread messages");
        Listener<String> onResponse = new Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONArray unreadJson = new JSONObject(response).getJSONArray("data");
                    for (int i = 0; i < unreadJson.length(); i++)
                        result.add(unreadJson.getJSONObject(i).getString("id"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };
        ConversationRequest request = new ConversationRequest("unread", onResponse, onError);
        RequestQueueSingleton.addToQueue(request, context);
    }


    public static ConversationRequest getConversationMessages(String id, final FragmentActivity context, final String user)
    {
        ErrorListener errListener = createErrorListener(context, "ConversationUtils error");
        Listener<String> listener = new Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    User author = new User(new JSONObject(user));
                    Fragment fragment = DisplayConversationFragment.newInstance(response,
                            author.getUsername(), author.getId(), author.getAvatar());
                    FragmentUtils.replaceFragmentWith(fragment, context, true);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };
        return new ConversationRequest(id, listener, errListener);
    }

    public static String makeAvatarURL(String id)
    {
        String url;
        if (Constants.SERVER_URL.equals(Config.ENV.LOCAL))
            url = "http://localhost";
        else
            url = "https://i.imgur.com/";

        return url + id + "s.png";
    }
}