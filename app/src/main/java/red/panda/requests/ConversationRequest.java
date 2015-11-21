package red.panda.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response.Listener;

import java.util.HashMap;
import java.util.Map;

import red.panda.utils.misc.Constants;
import red.panda.utils.misc.SharedPrefUtils;

public class ConversationRequest extends StringRequest
{
    public static final String URL = Constants.SERVER_URL + "/pm/";
    // TODO : add join as parameter to constructor

    /**
     * Get all available PM IDs
     * @param resListener success listener
     * @param errorListener error listener
     */
    public ConversationRequest(Listener<String> resListener, ErrorListener errorListener)
    {
        super(URL + "?join=author,to&sort=+lastReplyOn", resListener, errorListener);
    }

    /**
     * Get PMs by id
     * @param id PM id
     * @param resListener success listener
     * @param errorListener error listener
     */
    public ConversationRequest(String id, Listener<String> resListener, ErrorListener errorListener)
    {
        super(URL+id, resListener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        Map<String, String> headers = new HashMap<>();
        String authToken = Constants.User.AUTH_TOKEN;
        headers.put("Authorization", authToken);
        return headers;
    }

}
