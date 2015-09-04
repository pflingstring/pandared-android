package my.fancyapp;

import android.widget.EditText;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity
{
    public final static String USERNAME = "my.fancyapp.USERNAME";
    public final static String PASSWORD = "my.fancyapp.PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view)
    {
        Intent loginIntent = new Intent(this, MainActivity.class);

        EditText usernameEditText = (EditText) findViewById(R.id.username);
        EditText passwordEditText = (EditText) findViewById(R.id.password);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        loginIntent.putExtra(USERNAME, username);
        loginIntent.putExtra(PASSWORD, password);

        startActivity(loginIntent);
    }
}
