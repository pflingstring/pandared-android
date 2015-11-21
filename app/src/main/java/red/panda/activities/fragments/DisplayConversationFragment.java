package red.panda.activities.fragments;

import red.panda.adapters.DisplayConversationAdapter;
import red.panda.models.ConversationMessage;
import red.panda.utils.misc.RequestQueueSingleton;
import red.panda.utils.ToolbarUtils;
import red.panda.utils.SocketUtils;
import red.panda.utils.JsonUtils;
import red.panda.R;

import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;

import android.view.inputmethod.InputMethodManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.toolbox.ImageLoader;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import android.widget.EditText;
import android.widget.Button;
import android.os.Handler;
import android.os.Bundle;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayConversationFragment extends Fragment
{
    public DisplayConversationFragment() {}

    private static final String MESSAGES = "red.panda.messages";
    private static final String USERNAME = "red.panda.author";
    private static final String USER_ID  = "red.panda.userId";
    private static final String AVATAR   = "red.panda.avatar";
    private String messages;
    private String username;
    private String userId;
    private String avatar;

    RecyclerView.LayoutManager layoutManager;
    DisplayConversationAdapter adapter = new DisplayConversationAdapter();
    RecyclerView recyclerView;
    Toolbar toolbar;
    Socket socket = SocketUtils.init();

    public static DisplayConversationFragment newInstance(String messages,
            String username, String userId, String avatar)
    {
        DisplayConversationFragment fragment = new DisplayConversationFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGES, messages);
        args.putString(USERNAME, username);
        args.putString(USER_ID, userId);
        args.putString(AVATAR, avatar);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        socket.off();

        if (getArguments() != null)
        {
            messages = getArguments().getString(MESSAGES);
            username = getArguments().getString(USERNAME);
            userId = getArguments().getString(USER_ID);
            avatar = getArguments().getString(AVATAR);
        }

        setHasOptionsMenu(true);

        // set back button on toolbar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        FragmentDrawer drawer = (FragmentDrawer) activity.
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawer.setDrawerToggle(false);

        // setup toolbar
        toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);

            ImageLoader loader = RequestQueueSingleton.getInstance(activity).getImageLoader();
            ToolbarUtils.setTitle(toolbar, username);
            if (avatar != null)
                ToolbarUtils.setAvatar(toolbar, avatar, loader, getActivity());
        }

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

        recyclerView  = (RecyclerView) rootView.findViewById(R.id.messages);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        List<ConversationMessage> input = JsonUtils.toListOfMessages(messages);
        if (input != null)
        {
            adapter.setDataSet(input);
            recyclerView.scrollToPosition(input.size() - 1);
            recyclerView.setAdapter(adapter);
        }

        return rootView;
    }

    public void showSoftKeyboard(View view)
    {
        if (view.requestFocus())
            ((InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // scroll to bottom when keyboard is shown
        EditText editText = (EditText) view.findViewById(R.id.message_input);
        editText.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                showSoftKeyboard(view);

                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        layoutManager.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }, 300);
                return true;
            }
        });

        // send a conversation message
        Button button = (Button) view.findViewById(R.id.send_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                EditText editText = (EditText) getActivity().findViewById(R.id.message_input);
                Map<String, String> message = new HashMap<>();
                message.put("msg", editText.getText().toString());
                message.put("userId", userId);
                socket.emit("conversation:post", new JSONObject(message));
                editText.setText("");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        // mark conversation as seen leaving it
        try
        {
            String conversationID = new JSONObject(
                    getArguments().getString(MESSAGES))
                    .getJSONArray("data")
                    .getJSONObject(0)
                    .getString("conversationId");


            JSONObject jsonObject = (new JSONObject()).put(
                    "captures"
                    , JsonUtils.createJsonArray("captures", conversationID));

            socket.emit("seen-on:post", jsonObject);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
