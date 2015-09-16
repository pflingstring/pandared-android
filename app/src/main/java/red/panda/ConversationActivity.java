package red.panda;

import red.panda.utils.Constants;
import red.panda.utils.ConversationUtils;
import red.panda.utils.ItemClickSupport;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ConversationActivity extends AppCompatActivity
{
    public final static String MESSAGES = "red.panda.MESSAGES";
    RecyclerView.LayoutManager layoutManager;
    RecyclerView peopleListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        peopleListView = (RecyclerView) findViewById(R.id.conversationPeople);
        layoutManager = new LinearLayoutManager(this);
        peopleListView.setLayoutManager(layoutManager);

        // TODO: initialize Constants on launch
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String userDetails = sharedPreferences.getString(LoginActivity.USER_DETAILS, null);
        String authToken   = sharedPreferences.getString(LoginActivity.AUTH_TOKEN, null);
        new Constants();
        Constants.User.AUTH_TOKEN = authToken;
        Constants.User.USER_DETAILS = userDetails;

        ConversationUtils.createRequest(null, getApplicationContext(), peopleListView);

    }

}
