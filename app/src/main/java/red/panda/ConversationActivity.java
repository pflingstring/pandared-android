package red.panda;

import red.panda.requests.ConversationRequest;
import red.panda.utils.RequestQueueSingleton;
import red.panda.utils.ConversationUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

import android.support.annotation.Nullable;
import android.widget.AdapterView;
import android.widget.ListView;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ConversationActivity extends Activity
{
    RequestQueue queue;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        queue = RequestQueueSingleton.getQueue(this);
        listView = (ListView) findViewById(R.id.pmIDList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String myID = (String) parent.getItemAtPosition(position);
                Response.ErrorListener errorListener = ConversationUtils.
                        createErrorListener(getApplicationContext(), "Error while retrieving this conversation");
                ConversationRequest request = ConversationUtils.
                        requestConversationByID(myID, createResponse(myID), errorListener, getApplicationContext());

                RequestQueueSingleton.addToQueue(request, getApplicationContext());
            }
        });

        Response.ErrorListener errListener = ConversationUtils.
                createErrorListener(this, "Error while loading conversation list");
        Response.Listener<String> resListener = createResponse(null);
        ConversationRequest conversationRequest = ConversationUtils
                .requestConversationIDs(resListener, errListener, this);

        RequestQueueSingleton.addToQueue(conversationRequest, this);
    }

    Response.Listener<String> createResponse(@Nullable String id)
    {
        if (id == null)
            // get all conversation's IDs
            return new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    ConversationUtils.populateViewsWithPM(
                            response, listView, getApplicationContext());
                }};
        else
            // load messages by ID in new Activity
            return new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    Intent intent = new Intent(getApplicationContext(), DisplayConversationActivity.class);
                    intent.putExtra("PM", response);
                    startActivity(intent);
                }};
    }

}
