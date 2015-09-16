package red.panda;

import red.panda.utils.ConversationAdapter;
import red.panda.utils.JsonUtils;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Intent;
import android.app.Activity;
import org.json.JSONObject;
import android.os.Bundle;

public class DisplayConversationActivity extends AppCompatActivity
{
    RecyclerView messagesView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_conversation);


        Intent intent = getIntent();
        String allMessages = intent.getStringExtra(ConversationActivity.MESSAGES);
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
