package red.panda.activities;

import red.panda.R;
import red.panda.utils.misc.Constants;
import red.panda.utils.JsonUtils;
import red.panda.utils.misc.RequestQueueSingleton;
import red.panda.requests.AuthRequest;
import red.panda.utils.misc.SharedPrefUtils;

import android.support.v7.app.AppCompatActivity;
import com.android.volley.VolleyError;
import com.android.volley.Response;

import android.content.SharedPreferences;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import android.view.Gravity;
import android.view.View;

import android.content.Intent;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    void makeAuthRequest(String username, String password)
    {
        JSONObject loginData = new JSONObject();
        try
        {
            loginData.put("username", username);
            loginData.put("password", password);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        AuthRequest authRequest = new AuthRequest(loginData.toString(), resListener, errListener);
        RequestQueueSingleton.addToQueue(authRequest, this);
    }

    Response.Listener<JSONObject> resListener = new Response.Listener<JSONObject>()
    {
        @Override
        public void onResponse(JSONObject response)
        {
            SharedPreferences sharedPrefs = SharedPrefUtils.getPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPrefs.edit();

            String authToken   = JsonUtils.getFieldFromJSON(response, "AUTH_TOKEN");
            String userDetails = AuthRequest.getUserDetails(response);

            editor.putString(SharedPrefUtils.AUTH_TOKEN, authToken);
            editor.putString(SharedPrefUtils.USER_DETAILS, userDetails);
            editor.apply();
            Constants.init(getApplicationContext());

            Intent mainPage = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainPage);
            finish();
        }
    };

    Response.ErrorListener errListener = new Response.ErrorListener()
    {
        @Override
        public void onErrorResponse(VolleyError error)
        {
            int length = Toast.LENGTH_SHORT;
            Context context = getApplicationContext();
            String errorStr = "Login or password incorrect";
            Toast loginErrorToast = Toast.makeText(context, errorStr, length);
            loginErrorToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            loginErrorToast.show();
        }
    };

    public void loginButton(View view)
    {
        EditText usernameEditText = (EditText) findViewById(R.id.username);
        EditText passwordEditText = (EditText) findViewById(R.id.password);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        makeAuthRequest(username, password);
    }
}
