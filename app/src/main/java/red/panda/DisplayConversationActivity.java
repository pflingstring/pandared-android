package red.panda;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class DisplayConversationActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_conversation);

        TextView view = (TextView) findViewById(R.id.singlePM);

        Intent intent = getIntent();
        String pm = intent.getStringExtra("PM");
        view.setText(pm);


    }



}
