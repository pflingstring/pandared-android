package my.fancyapp;

import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;

import android.content.Intent;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity
{
    RequestQueue queue; // TODO: make the queue a singleton

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent loginIntent = getIntent();
        String username = loginIntent.getStringExtra(LoginActivity.USERNAME);
        String password = loginIntent.getStringExtra(LoginActivity.PASSWORD);
        TextView userDetails = (TextView) findViewById(R.id.userDetails);

        queue = Volley.newRequestQueue(this);
        makeRequest(userDetails, username, password);
    }

    void makeRequest(final TextView view, String username, String password)
    {
        Response.Listener<String> resListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                view.setText(response);
            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                view.setText("ERROR");
            }
        };

        String loginData = "{\"username\" : \"" + username + "\", \"password\": \"" + password + "\"}";
        AuthRequest authRequest = new AuthRequest(loginData, resListener, errListener);
        queue.add(authRequest);
    }

}

