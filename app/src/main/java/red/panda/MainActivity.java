package red.panda;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import red.panda.NavDrawer.FragmentDrawer;
import red.panda.utils.Constants;
import red.panda.utils.SharedPrefUtils;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener
{
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
            finish();
        }
        Constants.init(this);

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FragmentDrawer drawerFragment = (FragmentDrawer) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);
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
                fragment = new HomeFragment();
                title = getString(R.string.app_name);
                break;
            default:
                break;
        }

        if (fragment != null)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        if (id == R.id.action_logout)
        {
            SharedPreferences preferences = SharedPrefUtils.getPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void conversationButton(View view)
    {
        Intent intent = new Intent(this, ConversationActivity.class);
        startActivity(intent);
    }

}


