package red.panda;

import red.panda.utils.ConversationUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ConversationActivity extends AppCompatActivity
{
    public final static String MESSAGES = "red.panda.MESSAGES";
    RecyclerView.LayoutManager layoutManager;
    RecyclerView peopleListView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        peopleListView = (RecyclerView) findViewById(R.id.conversationPeople);
        layoutManager = new LinearLayoutManager(this);
        peopleListView.setLayoutManager(layoutManager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ConversationUtils.createRequest(null, getApplicationContext(), peopleListView);

    }

}
