package red.panda;

import red.panda.utils.ConversationAdapter;
import red.panda.utils.JsonUtils;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Intent;
import org.json.JSONObject;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class DisplayConversationActivity extends AppCompatActivity
{
    Toolbar toolbar;
    RecyclerView messagesView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_conversation);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String allMessages = intent.getStringExtra(ConversationFragment.MESSAGES);
        JSONObject[] input = JsonUtils.toArrayOfJSON(allMessages);

        messagesView = (RecyclerView) findViewById(R.id.messages);
        layoutManager = new LinearLayoutManager(this);
        messagesView.setLayoutManager(layoutManager);

        if (input != null)
        {
            adapter = new ConversationAdapter(input);
            messagesView.setAdapter(adapter);
        }

    }

}
