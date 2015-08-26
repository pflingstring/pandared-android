package my.fancyapp;

import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity
{
    String url = "https://api.panda.red/auth/default";
    RequestQueue queue;
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.content);
        queue = Volley.newRequestQueue(this);
    }

    public void myClicker(View view) throws JSONException
    {
        Response.Listener<JSONObject> resListener = new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                textView.setText(response.toString());
            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                textView.setText("ERROR");
            }
        };

        JSONObject jsonObj = new JSONObject("{username: neex1, password: nixnix}");
        JsonObjectRequest jsonReq = new JsonObjectRequest(1, url, jsonObj, resListener, errListener);

        queue.add(jsonReq);
    }


}
