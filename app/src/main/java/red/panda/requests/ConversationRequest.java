package red.panda.requests;

import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response.Listener;

public class ConversationRequest extends StringRequest
{
    public static final String URL = "https://api.panda.red/pm/";

    /**
     * Get JSON array of available PM IDs
     * @param resListener success listener
     * @param errorListener error listener
     */
    public ConversationRequest(Listener<String> resListener, ErrorListener errorListener)
    {
        super(URL, resListener, errorListener);
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
}
