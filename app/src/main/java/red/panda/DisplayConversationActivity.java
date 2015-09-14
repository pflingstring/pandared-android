package red.panda;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import red.panda.utils.ConversationAdapter;
import red.panda.utils.ConversationUtils;

public class DisplayConversationActivity extends Activity
{
    RecyclerView messages;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_conversation);

        Intent intent = getIntent();
        String pm = intent.getStringExtra("PM");
        String[] input = ConversationUtils.extractFieldsFromJSONArray(pm, "msg");

        messages = (RecyclerView) findViewById(R.id.messages);

        layoutManager = new LinearLayoutManager(this);
        messages.setLayoutManager(layoutManager);

        adapter = new ConversationAdapter(input);
        messages.setAdapter(adapter);
    }

}
