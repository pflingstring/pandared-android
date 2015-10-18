package red.panda.activities.fragments;

import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import red.panda.R;
import red.panda.utils.ConversationUtils;

public class ConversationFragment extends Fragment
{
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;


    public ConversationFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_conversation, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.peopleList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        ConversationUtils.createRequest(null, getActivity(), recyclerView, null);

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
}
