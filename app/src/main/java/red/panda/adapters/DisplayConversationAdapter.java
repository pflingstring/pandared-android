package red.panda.adapters;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import red.panda.R;
import red.panda.models.ConversationMessage;
import red.panda.utils.misc.Constants;

public class DisplayConversationAdapter extends RecyclerView.Adapter<DisplayConversationAdapter.ViewHolder>
{
    private List<ConversationMessage> dataSet;

    public void setDataSet(List<ConversationMessage> dataSet)
    {
        this.dataSet = dataSet;
    }

    public void addItemToDataSet(JSONObject json)
    {
        dataSet.add(new ConversationMessage(json));
    }

    public DisplayConversationAdapter() {}

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageView;
        public ViewHolder(View view)
        {
            super(view);
            messageView = (TextView) view.findViewById(R.id.message_text);
        }

        public void setMessage(String message)
        {
            messageView.setText(message);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        int layout = -1;
        switch (viewType)
        {
            case Constants.Conversation.AUTHOR_IS_ME:
                layout = R.layout.message_mine_recycler;
                break;
            case Constants.Conversation.AUTHOR_IS_NOT_ME:
                layout = R.layout.message_yours_recycler;
                break;
        }

        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        viewHolder.setMessage(dataSet.get(position).getMessage());
    }

    @Override
    public int getItemCount()
    {
        return dataSet.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (dataSet.get(position).isAuthorIsMe())
            return Constants.Conversation.AUTHOR_IS_ME;
        else
            return Constants.Conversation.AUTHOR_IS_NOT_ME;
    }
}
