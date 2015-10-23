package red.panda.activities;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import red.panda.R;
import red.panda.activities.fragments.ConversationFragment;
import red.panda.activities.fragments.FragmentDrawer;
import red.panda.utils.FragmentUtils;
import red.panda.utils.JsonUtils;

public class ConversationActivity extends AppCompatActivity
{
    FragmentDrawer drawerFragment;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Conversations");
        }

        drawerFragment = (FragmentDrawer) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        Intent intent = getIntent();
        JSONObject json = null;
        try
        {
            json = new JSONObject(intent.getStringExtra("MSG_ID"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        String id = JsonUtils.getFieldFromJSON(json, "id");
        ConversationFragment fragment = ConversationFragment.newInstance(id);
        FragmentUtils.replaceFragmentWith(fragment, this, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
