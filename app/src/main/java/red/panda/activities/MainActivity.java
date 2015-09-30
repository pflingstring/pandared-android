package red.panda.activities;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

import red.panda.activities.fragments.ConversationFragment;
import red.panda.activities.fragments.FragmentDrawer;
import red.panda.activities.fragments.HomeFragment;
import red.panda.R;
import red.panda.utils.misc.Constants;
import red.panda.utils.misc.SharedPrefUtils;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener
{
    Toolbar toolbar;
    FragmentDrawer drawerFragment;
    Socket socket;
    {
        try
        {
            socket = IO.socket("https://api.panda.red");
        }
        catch (URISyntaxException e) {throw new RuntimeException(e);}
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String authToken = SharedPrefUtils.getAuthToken(this);
        if (authToken == null)
        {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
            finish();
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

        // set HomeFragment as default view on start
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container_body, new HomeFragment());
        tx.commit();

        drawerFragment = (FragmentDrawer) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        socket.emit("auth", SharedPrefUtils.getAuthToken(this));
        socket.connect();
    }

    @Override
    public void onDrawerItemSelected(View view, int position)
    {
        displayView(position);
    }

    private void displayView(int position)
    {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position)
        {
            case 0:
                fragment = new ConversationFragment();
                title = getString(R.string.conversation);
                break;
            default:
                break;
        }

        if (fragment != null)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(title);
        }
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
        SharedPreferences preferences = SharedPrefUtils.getPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

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
            getSupportActionBar().setTitle("Conversation");
            getSupportActionBar().setIcon(android.R.color.transparent);
        }

        drawerFragment.setDrawerToggle(true);
    }
}
