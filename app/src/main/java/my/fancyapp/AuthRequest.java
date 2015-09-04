package my.fancyapp;

import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.Response.Listener;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;

public class AuthRequest extends JsonRequest<String>
{
    static String url = "https://api.panda.red/auth/default";

    public AuthRequest(String userData, Listener<String> listener, ErrorListener error)
    {
        super(Method.POST, url, userData, listener, error);
    }

    protected Response<String> parseNetworkResponse(NetworkResponse response)
    {
        String token = response.headers.get("authorization");
        return Response.success(token, HttpHeaderParser.parseCacheHeaders(response));
    }

}
