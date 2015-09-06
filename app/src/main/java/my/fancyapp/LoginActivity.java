package my.fancyapp;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;

public class LoginActivity extends Activity
{
    RequestQueue queue; // TODO: make the queue a singleton

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);

        // get the authToken or null if not present
        SharedPreferences authToken = PreferenceManager
                .getDefaultSharedPreferences(this);
        String AUTH_TOKEN = authToken.getString("AUTH_TOKEN", null);

        if (AUTH_TOKEN != null)
        {
            // BUG   : displays login layout on 'back' click
            // SHOULD: quit the app
            Intent mainPage = new Intent(this, MainActivity.class);
            startActivity(mainPage);
        }
        setContentView(R.layout.activity_login);
    }

    public void loginButton(View view)
    {
        EditText usernameEditText = (EditText) findViewById(R.id.username);
        EditText passwordEditText = (EditText) findViewById(R.id.password);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        makeAuthRequest(username, password);
    }

    void makeAuthRequest(String username, String password)
    {
        Response.Listener<String> resListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                SharedPreferences authToken = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = authToken.edit();
                editor.putString("AUTH_TOKEN", response);
                editor.apply();

                Intent mainPage = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainPage);
            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
            // TODO: handle error
            }
        };

        String loginData = "{\"username\" : \""   + username + // refactor
                           "\", \"password\": \"" + password + "\"}";
        AuthRequest authRequest = new AuthRequest(loginData, resListener, errListener);
        queue.add(authRequest);
    }
}
