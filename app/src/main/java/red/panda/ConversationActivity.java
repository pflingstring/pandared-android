package red.panda;

import red.panda.requests.ConversationRequest;
import red.panda.utils.Constants;
import red.panda.utils.ConversationUtils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ConversationActivity extends Activity
{
    public final static String MESSAGES = "red.panda.MESSAGES";
    RecyclerView.LayoutManager layoutManager;
    RecyclerView peopleView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        peopleView = (RecyclerView) findViewById(R.id.conversationPeople);
        layoutManager = new LinearLayoutManager(this);
        peopleView.setLayoutManager(layoutManager);

        // TODO: initialize Constants on launch
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String userDetails = sharedPreferences.getString(LoginActivity.USER_DETAILS, null);
        String authToken   = sharedPreferences.getString(LoginActivity.AUTH_TOKEN, null);
        new Constants();
        Constants.User.AUTH_TOKEN = authToken;
        Constants.User.USER_DETAILS = userDetails;

        ConversationUtils.createRequest(null, getApplicationContext(), peopleView);

    }

}
