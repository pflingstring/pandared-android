package red.panda.activities.fragments;

import red.panda.R;
import red.panda.adapters.DisplayConversationAdapter;
import red.panda.utils.JsonUtils;
import red.panda.utils.misc.SharedPrefUtils;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.List;

public class DisplayConversationFragment extends Fragment
{
    public DisplayConversationFragment() {}

    private static final String MESSAGES = "conversation.id.messages";
    private static final String TO_AUTHOR = "conversation.author.id"; // TODO: refactor
    private String messages;
    private String toAuthorId;

    RecyclerView.LayoutManager layoutManager;
    DisplayConversationAdapter adapter = new DisplayConversationAdapter();
    RecyclerView messagesView;
    Toolbar toolbar;
    Socket socket;

    {try
        {
            socket = IO.socket("https://api.panda.red");
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
    }


    public static DisplayConversationFragment newInstance(String response, String id)
    {
        DisplayConversationFragment fragment = new DisplayConversationFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGES, response);
        args.putString(TO_AUTHOR, id);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            messages = getArguments().getString(MESSAGES);
            toAuthorId = getArguments().getString(TO_AUTHOR);
        }

        setHasOptionsMenu(true);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        FragmentDrawer drawer = (FragmentDrawer) activity.getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawer.setDrawerToggle(false);

        toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null)
        {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getActivity() != null)
            socket.emit("auth", SharedPrefUtils.getAuthToken(getActivity().getApplicationContext()));

        socket.on("conversation:post:response", emitter);
        socket.connect();
    }

    private Emitter.Listener emitter = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            if (getActivity() != null)
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        JSONObject json = null;
                        try
                        {
                            json = ((JSONObject) args[0]).getJSONObject("message");
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        adapter.addItemToDataSet(json);
                        adapter.notifyItemInserted(adapter.getItemCount() - 1);
                        layoutManager.scrollToPosition(adapter.getItemCount() - 1);
                    }
                });
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_display_conversation, container, false);

        messagesView = (RecyclerView) rootView.findViewById(R.id.messages);
        layoutManager = new LinearLayoutManager(getActivity());
        messagesView.setLayoutManager(layoutManager);

        List<JSONObject> input = JsonUtils.toListOfJSON(messages);
        if (input != null)
        {
            adapter.setDataSet(input);
            messagesView.scrollToPosition(input.size() - 1);
            messagesView.setAdapter(adapter);
        }

        return rootView;
    }


    public void showSoftKeyboard(View view)
    {
        if (view.requestFocus())
        {
            InputMethodManager imm = (InputMethodManager) getActivity().
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        EditText editText = (EditText) view.findViewById(R.id.message_input);
        editText.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                showSoftKeyboard(view);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    public void run()
                    {
                        layoutManager.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }, 300);
                return true;
            }
        });

        Button button = (Button) view.findViewById(R.id.send_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                EditText editText = (EditText) getActivity().findViewById(R.id.message_input);
                JSONObject result = new JSONObject();

                String input = editText.getText().toString();
                try
                {
                    result.put("msg", input);
                    result.put("userId", toAuthorId);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                socket.emit("conversation:post", result);
                editText.setText("");
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
