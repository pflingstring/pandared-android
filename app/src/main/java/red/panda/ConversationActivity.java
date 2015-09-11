package red.panda;

import red.panda.requests.ConversationRequest;
import red.panda.utils.RequestQueueSingleton;
import android.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;

import android.content.SharedPreferences;
import android.content.Context;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

public class ConversationActivity extends Activity
{
    RequestQueue queue;
    TextView conversationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        queue = RequestQueueSingleton.getQueue(this);
        conversationView = (TextView) findViewById(R.id.pm);
        makePMRequest("");
    }

    void makePMRequest(String id)
    {
        Response.Listener<String> resListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                conversationView.setText(response);
            }
        };

        final Response.ErrorListener errListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                int length = Toast.LENGTH_SHORT;
                Context context = getApplicationContext();
                Toast loginErrorToast = Toast.makeText(context, error.toString(), length);
                loginErrorToast.show();
            }
        };

        ConversationRequest request = new ConversationRequest(
                ConversationRequest.URL, resListener, errListener)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext());
                String authToken = preferences.getString(LoginActivity.AUTH_TOKEN, null);
                headers.put("Authorization", authToken);
                return headers;
            }
        };
        RequestQueueSingleton.addToQueue(request, this);
    }
}
