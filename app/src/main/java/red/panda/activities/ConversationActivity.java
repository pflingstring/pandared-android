package red.panda.activities;

import red.panda.activities.fragments.ConversationFragment;
import red.panda.activities.fragments.CreateNewMessageDialogFragment;
import red.panda.activities.fragments.FragmentDrawer;
import red.panda.utils.FragmentUtils;
import red.panda.R;
import red.panda.utils.SocketUtils;

import com.github.nkzawa.socketio.client.Socket;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.MenuItem;
import android.view.Menu;
import android.app.Activity;
import android.os.Bundle;

public class ConversationActivity extends AppCompatActivity
    implements FragmentDrawer.FragmentDrawerListener
{
    private static final String NEW_MESSAGE_DIALOG = "red.panda.newMessageDialog";
    Socket socket = SocketUtils.init();
    FragmentDrawer drawerFragment;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // stop listening to all events
        socket.off();

        // setup toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Conversations");
        }

        // setup drawer fragment
        drawerFragment = (FragmentDrawer) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,
            (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        // start conversation fragment
        final ConversationFragment fragment = new ConversationFragment();
        FragmentUtils.replaceFragmentWith(fragment, this, false);
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

    public void onBackPressed()
    {
        super.onBackPressed();

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Conversations");
            getSupportActionBar().setIcon(android.R.color.transparent);
        }

        // hide soft keyboard
        InputMethodManager inputMethodManager = (InputMethodManager)this
            .getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        drawerFragment.setDrawerToggle(true);
    }

    public void sendNewPM(View view)
    {
        new CreateNewMessageDialogFragment()
            .show(getSupportFragmentManager(), NEW_MESSAGE_DIALOG);
    }

}
