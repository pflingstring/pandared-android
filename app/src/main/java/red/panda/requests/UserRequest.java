package red.panda.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response.*;

import java.util.HashMap;
import java.util.Map;

import red.panda.utils.misc.Constants;

public class UserRequest extends StringRequest
{
    public static final String URL = Constants.SERVER_URL + "/users/";

    public UserRequest(String username, Listener<String> onResponse, ErrorListener onError)
    {
        super(URL + username, onResponse, onError);
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
