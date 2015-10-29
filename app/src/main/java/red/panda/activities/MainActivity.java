package red.panda.activities;

import red.panda.activities.fragments.DisplayConversationFragment;
import red.panda.activities.fragments.FragmentDrawer;
import red.panda.activities.fragments.HomeFragment;
import red.panda.utils.JsonUtils;
import red.panda.utils.SocketUtils;
import red.panda.utils.UserUtils;
import red.panda.utils.misc.SharedPrefUtils;
import red.panda.utils.misc.Constants;
import red.panda.utils.FragmentUtils;
import red.panda.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.DrawerLayout;

import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener
{
    Socket socket = SocketUtils.init();
    FragmentDrawer drawerFragment;
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String authToken = SharedPrefUtils.getAuthToken(this);
        if (authToken == null)
        {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
            return;
        }

        Constants.init(this);

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FragmentUtils.replaceFragmentWith(new HomeFragment(), this, false);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        socket.emit("auth", SharedPrefUtils.getAuthToken(this));
        socket.connect();

        socket.on("conversation:post:response", notificationEmitter);
    }

    private Emitter.Listener notificationEmitter = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            JSONObject json = JsonUtils.getJson(
                    (JSONObject) args[0]
                    , "message"
            );

            if (!UserUtils.userIsMe(JsonUtils.getFieldFromJSON(json, "authorId")))
            {
                NotificationManager notificationManager = (NotificationManager)
                        getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                intent.putExtra("MSG_ID", args[0].toString());
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        (int)System.currentTimeMillis(), intent, 0);

                Notification n  = new Notification.Builder(getApplicationContext())
                        .setContentTitle("You have a new personal message")
                        .setSmallIcon(R.drawable.launcher)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();

                notificationManager.notify(0, n);
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();

        socket.on("conversation:post:response", notificationEmitter);
    }

    @Override
    public void onDrawerItemSelected(View view, int position)
    {
        displayView(position);
    }

    private void displayView(int position)
    {
        Intent intent = null;
        String title = getString(R.string.app_name);
        switch (position)
        {
            case 0:
                intent = new Intent(this, ConversationActivity.class);
                title = getString(R.string.conversation);
                break;

            default:
                break;
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);

        if (intent != null)
            startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        ActionBarDrawerToggle drawToggle = drawerFragment.getDrawerToggle();
        if (drawToggle.isDrawerIndicatorEnabled() &&
            drawToggle.onOptionsItemSelected(item))
        { return true; }

        switch (item.getItemId())
        {
            case android.R.id.home :
                onBackPressed();
                return true;
            case R.id.action_logout :
                logout();
                return true;

            default :
                return super.onOptionsItemSelected(item);
        }
    }

    public void logout()
    {
        SharedPrefUtils.clearPrefs(this);
        Constants.User.AUTH_TOKEN = null;
        Constants.User.USER_DETAILS = null;

        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Conversations");
            getSupportActionBar().setIcon(android.R.color.transparent);
        }

        // hide soft keyboard
        InputMethodManager inputMethodManager = (InputMethodManager)
                this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
        {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        drawerFragment.setDrawerToggle(true);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }
}
