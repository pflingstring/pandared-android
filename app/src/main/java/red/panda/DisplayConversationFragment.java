package red.panda;

import red.panda.utils.ConversationAdapter;
import red.panda.utils.JsonUtils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.View;

public class DisplayConversationFragment extends Fragment
{
    public DisplayConversationFragment() {}

    private static final String MESSAGES = "conversation.id.messages";
    private String messages;

    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    RecyclerView messagesView;

    public static DisplayConversationFragment newInstance(String response)
    {
        DisplayConversationFragment fragment = new DisplayConversationFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGES, response);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_display_conversation, container, false);

        messagesView = (RecyclerView) rootView.findViewById(R.id.messages);
        layoutManager = new LinearLayoutManager(getActivity());
        messagesView.setLayoutManager(layoutManager);

        JSONObject[] input = JsonUtils.toArrayOfJSON(messages);
        if (input != null)
        {
            adapter = new ConversationAdapter(input);
            messagesView.setAdapter(adapter);
        }

        return rootView;
    }

}
