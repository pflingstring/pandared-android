package red.panda;

import red.panda.utils.ConversationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ConversationActivity extends Activity
{
    public final static String MESSAGES = "red.panda.MESSAGES";
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        listView = (ListView) findViewById(R.id.pmIDList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String myID = (String) parent.getItemAtPosition(position);
                ConversationUtils.createRequest(myID, getApplicationContext(),
                        listView, "Error loading this conversation");
            }
        });
        ConversationUtils.createRequest(null, this, listView, "Error loading available conversations");
    }

}
