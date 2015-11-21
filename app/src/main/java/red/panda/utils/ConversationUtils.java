package red.panda.utils;

import red.panda.Config;
import red.panda.activities.fragments.DisplayConversationFragment;
import red.panda.adapters.ConversationAdapter;
import red.panda.requests.ConversationRequest;
import red.panda.utils.misc.Constants;
import red.panda.utils.misc.RequestQueueSingleton;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

    public static void getUnreadMessages(Context context, final Set<String> result,
        final ConversationAdapter adapter, final RecyclerView recyclerView)
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

                    if (result != null && adapter != null)
                    {
                        for (String id : result)
                        {
                            int position = adapter.getItemPosition(id);
                            adapter.setHasUnread(position, true);
                            adapter.notifyItemChanged(position);
                        }
                        recyclerView.setAdapter(adapter);
                    }
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


    public static ConversationRequest getConversationMessages(final String id, final FragmentActivity context, final String user)
    {
        ErrorListener errListener = createErrorListener(context, "ConversationUtils error");
        Listener<String> listener = new Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Fragment fragment = DisplayConversationFragment.newInstance(response, user, id);
                FragmentUtils.replaceFragmentWith(fragment, context, true);
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