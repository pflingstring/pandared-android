package red.panda;

import red.panda.requests.ConversationRequest;
import red.panda.utils.RequestQueueSingleton;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
                Toast toast = Toast.makeText(getApplicationContext(), "you clicked me HAHA lol", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        makePMRequest("");
    }

    void makePMRequest(String id)
    {
        Response.Listener<String> resListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    String[] ids = getConversationIDs(response);
                    ArrayAdapter<String> idAdapter = new ArrayAdapter<String>(
                            getApplicationContext(), android.R.layout.simple_list_item_1, ids);
                    listView.setAdapter(idAdapter);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };

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

        ConversationRequest request = new ConversationRequest(resListener, errListener)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext());
                String authToken = preferences.getString(LoginActivity.AUTH_TOKEN, null);
                headers.put("Authorization", authToken);
                return headers;
            }
        };
        RequestQueueSingleton.addToQueue(request, this);
    }

    static String[] getConversationIDs(String jsonResponse) throws JSONException
    {
        JSONArray jsonArray = new JSONObject(jsonResponse).getJSONArray("data");
        int length = jsonArray.length();
        String[] result = new String[length];

        for (int i=0; i<length; i++)
        {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            String id = jsonObj.getString("id");
            result[i] = id;
        }
        return result;
    }

}
