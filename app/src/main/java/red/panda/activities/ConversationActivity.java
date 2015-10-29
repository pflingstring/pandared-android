package red.panda.activities;

import red.panda.activities.fragments.ConversationFragment;
import red.panda.activities.fragments.FragmentDrawer;
import red.panda.adapters.ConversationAdapter;
import red.panda.models.Conversation;
import red.panda.requests.ConversationRequest;
import red.panda.utils.ConversationUtils;
import red.panda.utils.FragmentUtils;
import red.panda.utils.JsonUtils;
import red.panda.R;
import red.panda.utils.SocketUtils;
import red.panda.utils.misc.RequestQueueSingleton;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.MenuItem;
import android.view.Menu;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.Response;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

public class ConversationActivity extends AppCompatActivity
    implements FragmentDrawer.FragmentDrawerListener
{
    FragmentDrawer drawerFragment;
    ConversationAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Socket socket = SocketUtils.init();
    Toolbar toolbar;

    public String dataSet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Conversations");
        }

        drawerFragment = (FragmentDrawer) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        final ConversationFragment fragment = new ConversationFragment();
        Response.ErrorListener conversationError = ConversationUtils.createErrorListener(this, "ERROR");
        Response.Listener<String> conversationListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                fragment.bindDataToAdapter(response);
            }
        };
        ConversationRequest request = ConversationUtils.requestConversations(
                conversationListener
                , conversationError
                , this);
        RequestQueueSingleton.addToQueue(request, this);

        FragmentUtils.replaceFragmentWith(fragment, this, false);
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
            getSupportActionBar().setTitle(
                "Conversations"
            );
            getSupportActionBar().setIcon(android.R.
                color.transparent);
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
