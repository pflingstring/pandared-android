package red.panda;

import red.panda.utils.RequestQueueSingleton;
import red.panda.requests.AuthRequest;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;

import android.widget.TextView;
import org.json.JSONObject;

public class LoginActivity extends Activity
{
    RequestQueue queue;
    public final static String AUTH_TOKEN   = "red.panda.AUTH_TOKEN";
    public final static String USER_DETAILS = "red.panda.USER_DETAILS";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        queue = RequestQueueSingleton.getQueue(this);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        String userDetails = sharedPreferences.getString(USER_DETAILS, null);
        String authToken   = sharedPreferences.getString(AUTH_TOKEN, null);

        if (authToken != null)
        {
            setContentView(R.layout.activity_main);
            TextView userDetailsView = (TextView) findViewById(R.id.userDetails);
            userDetailsView.setText(userDetails);
            return;
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

    public void logoutButton(View view)
    {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        setContentView(R.layout.activity_login);
    }

    void makeAuthRequest(String username, String password)
    {
        Response.Listener<JSONObject> resListener = new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AUTH_TOKEN, AuthRequest.getAuthToken(response));
                editor.putString(USER_DETAILS, AuthRequest.getUserDetails(response));
                editor.apply();

                Intent mainPage = new Intent(getApplicationContext(), MainActivity.class);
                mainPage.putExtra(USER_DETAILS, AuthRequest.getUserDetails(response));
                startActivity(mainPage);
                finish();
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
        RequestQueueSingleton.addToQueue(authRequest, this);
    }
}
