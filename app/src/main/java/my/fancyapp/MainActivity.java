package my.fancyapp;

import android.widget.TextView;
import android.content.Intent;
import android.app.Activity;
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
}

