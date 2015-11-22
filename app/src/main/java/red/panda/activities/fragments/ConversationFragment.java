package red.panda.activities.fragments;

import android.content.Context;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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
    private List<Conversation> dataSet = new ArrayList<>();
    private Set<String> unreadMessages = new HashSet<>();
    private LinearLayoutManager layoutManager;
    private ConversationAdapter adapter;
    private RecyclerView recyclerView;
    Socket socket = SocketUtils.init();

    public ConversationFragment() {}

    private static final String NEW_MESSAGE_CREATED = "red.panda.newMessageCreated";
    public static ConversationFragment newInstance(boolean flag)
    {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putBoolean(NEW_MESSAGE_CREATED, flag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        socket.on("conversation:post:response", emitter);
    }

    Emitter.Listener emitter = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            JSONObject json = JsonUtils.getJson((JSONObject) args[0], "message");

            if (!UserUtils.userIsMe(JsonUtils.getFieldFromJSON(json, "authorId")))
            {
                int position = adapter.getItemPosition(JsonUtils.getFieldFromJSON(
                    (JSONObject) args[0]
                    , "id"));
                adapter.setHasUnread(position, true);
                adapter.notifyItemChanged(position);
            }
        }
    };

    public void onResume()
    {
        super.onResume();

        ImageButton button = (ImageButton) getActivity().findViewById(R.id.sendNewMessage);
        button.setVisibility(View.VISIBLE);

        if (!socket.hasListeners("conversation:post:response"))
            socket.on("conversation:post:response", emitter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_conversation, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.peopleList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        if (adapter == null)
        {
            // load the conversations
            final boolean newConversationCreated =
                getArguments() != null &&
                getArguments().getBoolean(NEW_MESSAGE_CREATED);

            final Response.ErrorListener onError = ConversationUtils.createErrorListener(getActivity(), "ERROR");
            Response.Listener<String> onResponse = new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    dataSet = JsonUtils.toConversationList(response);
                    adapter = new ConversationAdapter(dataSet);
                    recyclerView.setAdapter(adapter);

                    if (newConversationCreated)
                    {
                        adapter.setHasUnread(0, true);
                        adapter.notifyItemChanged(0);
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                adapter.setHasUnread(0, false);
                                adapter.notifyItemChanged(0);
                            }
                        }, 1250);
                    }

                    ConversationUtils.getUnreadMessages(getActivity(), unreadMessages, adapter, recyclerView);
                }
            };
            ConversationRequest request = new ConversationRequest(onResponse, onError);
            RequestQueueSingleton.addToQueue(request, getActivity());
        }
        else
        {
            recyclerView.setAdapter(adapter);
            ConversationUtils.getUnreadMessages(getActivity(), unreadMessages, adapter, recyclerView);
        }

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener()
        {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v)
            {
                Conversation conversation = dataSet.get(position);
                try
                {
                    JSONArray jsonArray = JsonUtils.createJsonArray("captures", conversation.getId());
                    JSONObject jsonObject = (new JSONObject()).put("captures", jsonArray);
                    socket.emit("seen-on:post", jsonObject);
                    unreadMessages.remove(conversation.getId());
                    adapter.setHasUnread(position, false);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                ConversationRequest request = ConversationUtils.getConversationMessages(
                    conversation.getId(), getActivity(), conversation.getUser().toString());
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
        socket.off("conversation:post:response", emitter);
    }
}
