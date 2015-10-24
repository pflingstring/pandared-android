package red.panda.activities;

import red.panda.activities.fragments.ConversationFragment;
import red.panda.activities.fragments.FragmentDrawer;
import red.panda.utils.FragmentUtils;
import red.panda.utils.JsonUtils;
import red.panda.R;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.DrawerLayout;
import android.view.inputmethod.InputMethodManager;
import android.view.MenuItem;
import android.view.Menu;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;

public class ConversationActivity extends AppCompatActivity
{
    FragmentDrawer drawerFragment;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R
                .id.toolbar
        );
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar()
                .setDisplayHomeAsUpEnabled(false)
            ;
            getSupportActionBar()
                .setTitle("Conversations")
            ;
        }

        drawerFragment = (FragmentDrawer) getSupportFragmentManager()
            .findFragmentById(R.id
                .fragment_navigation_drawer
        );

        drawerFragment.setUp(R.id
            .fragment_navigation_drawer
            , (DrawerLayout) findViewById(R.id
                .drawer_layout)
            , toolbar
        );

        Intent intent = getIntent();
        JSONObject json = null;

        try
        {
            json = new JSONObject(intent
                .getStringExtra("MSG_ID")
            );
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        String id = JsonUtils
            .getFieldFromJSON(
                json
                , "id"
        );

        String username = JsonUtils
            .getFieldFromJSON(
                json
                , "username"
        );

        ConversationFragment fragment = ConversationFragment
            .newInstance(
                id
                , username
        );

        FragmentUtils.replaceFragmentWith(
            fragment
            , this
            , false
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu
            .menu_conversation
            , menu
        );
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

            default :
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed()
    {
        super.onBackPressed();

        if (getSupportActionBar() != null)
        {
            getSupportActionBar()
                .setTitle("Conversations");
            getSupportActionBar()
                .setIcon(android.R.color.transparent);
        }

        // hide soft keyboard
        InputMethodManager inputMethodManager = (InputMethodManager)
            this
            .getSystemService(Activity.INPUT_METHOD_SERVICE)
        ;

        if (getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(
                getCurrentFocus()
                .getWindowToken()
                , 0
            );

        drawerFragment.setDrawerToggle(true);
    }
}
