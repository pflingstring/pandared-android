package red.panda;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.widget.TextView;
import android.content.Intent;
import android.app.Activity;
import android.view.View;
import android.os.Bundle;


public class MainActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String userDetails = intent.getStringExtra(LoginActivity.USER_DETAILS);
        TextView userDetailsView = (TextView) findViewById(R.id.userDetails);
        userDetailsView.setText(userDetails);
    }

    public void logoutButton(View view)
    {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}

