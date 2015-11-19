package red.panda.activities.fragments;

import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import red.panda.R;
import red.panda.adapters.ConversationAdapter;
import red.panda.models.Conversation;
import red.panda.requests.ConversationRequest;
import red.panda.utils.ConversationUtils;
import red.panda.utils.JsonUtils;
import red.panda.utils.SocketUtils;
import red.panda.utils.UserUtils;
import red.panda.utils.misc.ItemClickSupport;
import red.panda.utils.misc.RequestQueueSingleton;

public class ConversationFragment extends Fragment
{
    public static final String UNREAD_MESSAGES = "red.panda.unreadMessages";
    public static final String USERNAME = "red.panda.username";
    public ConversationAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;

    List<Conversation> dataSet = new ArrayList<>();
    Set<String> unreadMessages;
    Socket socket = SocketUtils.init();

    public ConversationFragment() {}

    public static ConversationFragment newInstance(String unreadMsgId, String username)
    {
        ConversationFragment fragment = new ConversationFragment();
        Bundle arguments = new Bundle();
        arguments.putString(UNREAD_MESSAGES, unreadMsgId);
        arguments.putString(USERNAME, username);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        socket.on("conversation:post:response", emitter);
    }

    public void onResume()
    {
        super.onResume();

        Response.ErrorListener conversationError = ConversationUtils.createErrorListener(getActivity(), "ERROR");
        Response.Listener<String> conversationListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                bindDataToAdapter(response);
            }
        };
        ConversationRequest request = ConversationUtils.requestConversations(
                conversationListener
                , conversationError
                , getActivity());
        RequestQueueSingleton.addToQueue(request, getActivity());
    }

    public void bindDataToAdapter(String data)
    {
        dataSet = JsonUtils.toConversationList(data);
        adapter = new ConversationAdapter(dataSet);
        recyclerView.setAdapter(adapter);

        Response.ErrorListener errListener = ConversationUtils.createErrorListener(getActivity(), "UNREAD ERROR");
        Response.Listener<String> listener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    if (adapter != null)
                        recyclerView.setAdapter(adapter);

                    JSONArray unreadJson = new JSONObject(response).getJSONArray("data");
                    unreadMessages = new HashSet<>();
                    for (int i = 0; i < unreadJson.length(); i++)
                    {
                        Conversation conversation = new Conversation(unreadJson.getJSONObject(i));
                        conversation.setHasUnreadMessages(true);
                        unreadMessages.add(conversation.getId());
                    }

                    if (unreadMessages != null && adapter != null)
                        for (String id : unreadMessages)
                        {
                            int position = adapter.getItemPosition(id);
                            adapter.setUnread(position);
                            adapter.notifyItemChanged(position);
                        }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        };
        ConversationRequest unreadReq = ConversationUtils.requestConversationByID("unread", listener, errListener, getActivity());
        RequestQueueSingleton.addToQueue(unreadReq, getActivity());
    }

    Emitter.Listener emitter = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            JSONObject json = JsonUtils.getJson(
                (JSONObject) args[0]
                , "message"
            );

            if (!UserUtils.userIsMe(JsonUtils.getFieldFromJSON(
                json
                , "authorId"
            )))
            {
                int position = adapter.getItemPosition(JsonUtils.getFieldFromJSON(
                        (JSONObject) args[0]
                        , "id"));
                adapter.setUnread(position);
                adapter.notifyItemChanged(position);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        if (adapter != null)
        {
            recyclerView.setAdapter(adapter);
        }

        View rootView = inflater.inflate(R.layout.fragment_conversation, container, false);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.peopleList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener()
        {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v)
            {
                Conversation conversation = dataSet.get(position);
                conversation.setHasUnreadMessages(false);
                adapter.notifyItemChanged(position);

                Socket socket = SocketUtils.init();
                try
                {
                    JSONArray jsonArray = JsonUtils.createJsonArray("captures", conversation.getId());
                    JSONObject jsonObject = (new JSONObject()).put("captures", jsonArray);
                    socket.emit("seen-on:post", jsonObject);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                ConversationRequest request = ConversationUtils.createRequest(conversation.getId(),
                        getActivity(), null, conversation.getUser().toString(), position);
                RequestQueueSingleton.addToQueue(request, getActivity());
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override

    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }
}
