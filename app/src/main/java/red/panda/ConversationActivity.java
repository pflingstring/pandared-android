package red.panda;

import red.panda.requests.ConversationRequest;
import red.panda.utils.RequestQueueSingleton;
import red.panda.utils.ConversationUtils;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
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

                final Response.ErrorListener errListener = new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        int length = Toast.LENGTH_SHORT;
                        Context context = getApplicationContext();
                        Toast loginErrorToast = Toast.makeText(context, error.toString(), length);
                        loginErrorToast.show();
                    }
                };

                ConversationRequest request = ConversationUtils.requestConversationByID(myID, createResponse(myID), errListener, getApplicationContext());
                RequestQueueSingleton.addToQueue(request, getApplicationContext());
            }
        });

        final Response.ErrorListener errListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                int length = Toast.LENGTH_SHORT;
                Context context = getApplicationContext();
                Toast loginErrorToast = Toast.makeText(context, error.toString(), length);
                loginErrorToast.show();
            }
        };

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
