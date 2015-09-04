package my.fancyapp;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;

import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;


public class MainActivity extends Activity
{
    RequestQueue queue;
    TextView textView;
    TextView passkey;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.content);
        passkey  = (TextView) findViewById(R.id.passkey);
        queue = Volley.newRequestQueue(this);
    }

    public void myClicker(View view) throws JSONException
    {
        Response.Listener<String> resListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                textView.setText(response);
            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                    textView.setText("ERROR");
            }
        };

        String loginData = "{\"username\" : \"neex1\", \"password\" : \"nixnix\"}";
        AuthRequest authRequest = new AuthRequest(loginData, resListener, errListener);

        queue.add(authRequest);
    }
}
