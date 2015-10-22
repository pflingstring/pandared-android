package red.panda.utils;

import red.panda.activities.fragments.DisplayConversationFragment;
import red.panda.requests.ConversationRequest;
import red.panda.utils.misc.SharedPrefUtils;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;

import android.support.v7.widget.RecyclerView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import android.support.annotation.Nullable;
import android.content.Context;
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

    static Listener<String> createResponse(final Context context, @Nullable final String id, final RecyclerView view, @Nullable final String user)
    {
        return new Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                FragmentActivity activity = (FragmentActivity) context;
                Fragment fragment = DisplayConversationFragment.newInstance(response, user);
                FragmentUtils.replaceFragmentWith(fragment, activity, true);
            }
        };
    }

    public static ConversationRequest createRequest(@Nullable String id, Context context,
            @Nullable RecyclerView view, @Nullable String user)
    {
        ErrorListener errListener = createErrorListener(context, "ConversationUtils error");
        Listener<String> resListener;

        if (id == null)
        {
            resListener = createResponse(context, null, view, null);
            return requestConversations(resListener, errListener, context);
        }
        else
        {
            resListener = createResponse(context, id, null, user);
            return requestConversationByID(id, resListener, errListener, context);
        }
    }

    public static String makeAvatarURL(String id)
    {
        String url = "https://i.imgur.com/";
        return url + id + "s.png";
    }
}