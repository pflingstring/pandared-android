package my.fancyapp;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView token = (TextView) findViewById(R.id.authToken);

        SharedPreferences authToken = PreferenceManager.getDefaultSharedPreferences(this);
        String AUTH_TOKEN = authToken.getString("AUTH_TOKEN", null);

        token.setText(AUTH_TOKEN);
    }
}

