package red.panda;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import red.panda.utils.ConversationAdapter;
import red.panda.utils.ConversationUtils;

public class DisplayConversationActivity extends Activity
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

        JSONObject[] input;
        try
        {
            JSONArray jsonArray = new JSONObject(allMessages).getJSONArray("data");
            int length = jsonArray.length();
            input = new JSONObject[length];

            for (int i=0; i<length; i++)
                input[i] = jsonArray.getJSONObject(i);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            input = null;
        }

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
