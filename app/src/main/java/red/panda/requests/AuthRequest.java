package red.panda.requests;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Request;

import java.io.UnsupportedEncodingException;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class AuthRequest extends JsonObjectRequest
{
    static String url = "https://api.panda.red/auth/default";

    public AuthRequest(String userData, Listener<JSONObject> listener, ErrorListener error)
    {
        super(Request.Method.POST, url, userData, listener, error);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response)
    {
        try
        {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            String token = response.headers.get("authorization");

            JSONObject result = new JSONObject(jsonString).put("AUTH_TOKEN", token);
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        }
        catch (UnsupportedEncodingException e)
        {
            return Response.error(new ParseError(e));
        }
        catch (JSONException je)
        {
            return Response.error(new ParseError(je));
        }
    }

    public static String getFieldFromJSON(JSONObject response, String field)
    {
        try
        {
            return response.getString(field);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String getUserDetails(JSONObject response)
    {
        try
        {
            return response.put("AUTH_TOKEN", null).toString();
        }
        catch (JSONException exception)
        {
            Log.w("JSON_USER_DETAILS_ERROR", exception.toString());
            return null;
        }
    }

}
