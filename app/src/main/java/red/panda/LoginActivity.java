package red.panda;

import red.panda.NavDrawer.FragmentDrawer;
import red.panda.utils.RequestQueueSingleton;
import red.panda.requests.AuthRequest;
import red.panda.utils.Constants;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.android.volley.VolleyError;
import com.android.volley.Response;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.view.MenuItem;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;

import android.content.Intent;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener
{
    public final static String AUTH_TOKEN   = "red.panda.AUTH_TOKEN";
    public final static String USER_DETAILS = "red.panda.USER_DETAILS";
    private FragmentDrawer drawerFragment;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String userDetails = sharedPreferences.getString(USER_DETAILS, null);
        String authToken   = sharedPreferences.getString(AUTH_TOKEN, null);
        new Constants();
        Constants.User.AUTH_TOKEN = authToken;
        Constants.User.USER_DETAILS = userDetails;

        if (authToken != null)
        {
            setContentView(R.layout.activity_main);
            TextView userDetailsView = (TextView) findViewById(R.id.userDetails);
            userDetailsView.setText(userDetails);
            return;
        }
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loginButton(View view)
    {
        EditText usernameEditText = (EditText) findViewById(R.id.username);
        EditText passwordEditText = (EditText) findViewById(R.id.password);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        makeAuthRequest(username, password);
    }

    public void conversationButton(View view)
    {
        Intent intent = new Intent(this, ConversationActivity.class);
        startActivity(intent);
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

                String authToken = AuthRequest.getFieldFromJSON(response, "AUTH_TOKEN");
                String userDetails = AuthRequest.getUserDetails(response);
                Constants.User.AUTH_TOKEN = authToken;
                Constants.User.USER_DETAILS = userDetails;

                editor.putString(AUTH_TOKEN, authToken);
                editor.putString(USER_DETAILS, userDetails);
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
                int length = Toast.LENGTH_SHORT;
                Context context = getApplicationContext();
                String errorStr = "Login or password incorrect. Try again";
                Toast loginErrorToast = Toast.makeText(context, errorStr, length);
                loginErrorToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                loginErrorToast.show();
            }
        };

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
}
