package red.panda;

import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import red.panda.utils.ConversationUtils;


public class ConversationFragment extends Fragment
{
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    RecyclerView peopleListView;

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

        peopleListView = (RecyclerView) rootView.findViewById(R.id.peopleList);
        peopleListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        peopleListView.setLayoutManager(layoutManager);
        peopleListView.setAdapter(adapter);
        ConversationUtils.createRequest(null, getActivity(), peopleListView);

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
