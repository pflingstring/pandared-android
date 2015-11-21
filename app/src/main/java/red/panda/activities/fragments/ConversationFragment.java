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
    private List<Conversation> dataSet = new ArrayList<>();
    private Set<String> unreadMessages = new HashSet<>();
    private RecyclerView.LayoutManager layoutManager;
    private ConversationAdapter adapter;
    private RecyclerView recyclerView;
    Socket socket = SocketUtils.init();

    public ConversationFragment() {}

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
        socket.on("conversation:post:response", emitter);

        ConversationUtils.getUnreadMessages(getActivity(), unreadMessages);
        if (unreadMessages != null)
            for (String id : unreadMessages)
            {
                int position = adapter.getItemPosition(id);
                adapter.setHasUnread(position, true);
                adapter.notifyItemChanged(position);
            }
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        if (adapter != null)
            recyclerView.setAdapter(adapter);
        else
        {
            // load the conversations
            final Response.ErrorListener onError = ConversationUtils.createErrorListener(getActivity(), "ERROR");
            Response.Listener<String> onResponse = new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    dataSet = JsonUtils.toConversationList(response);
                    adapter = new ConversationAdapter(dataSet);
                    recyclerView.setAdapter(adapter);
                }
            };
            ConversationRequest request = new ConversationRequest(onResponse, onError);
            RequestQueueSingleton.addToQueue(request, getActivity());
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

                ConversationRequest request = ConversationUtils.getConversationMessages(conversation.getId(), getActivity(), conversation.getUser().toString());
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
