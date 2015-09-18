package red.panda.utils;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import red.panda.R;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>
{
    private JSONObject[] dataSet;
    public ConversationAdapter(JSONObject[] myData)
    {
        dataSet = myData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView avatar;
        public TextView messageView;
        public ViewHolder(View view)
        {
            super(view);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            messageView = (TextView) view.findViewById(R.id.message_text);
        }

        public void setAvatar(int id)
        {
            avatar.setImageResource(id);
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
        JSONObject jsonObject = dataSet[position];
        String id, message, previousID;

        try
        {
            id = jsonObject.getString("authorId");
            message = jsonObject.getString("msg");
            boolean authorIsMe = ConversationUtils.authorIsMe(id);

            if (position != 0 && position != getItemCount())
            {
                previousID = dataSet[position - 1].getString("authorId");
                boolean previousAuthorIsTheSame = id.equals(previousID);

                if (!previousAuthorIsTheSame)
                    setAvatar(authorIsMe, viewHolder);
            }
            else
            {
                setAvatar(authorIsMe, viewHolder);
            }

            viewHolder.setMessage(message);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void setAvatar(boolean authorIsMe, ViewHolder viewHolder)
    {
        if (authorIsMe)
            viewHolder.setAvatar(R.drawable.avatar_my);
        else
            viewHolder.setAvatar(R.drawable.avatar_yours);
    }

    @Override
    public int getItemCount()
    {
        return dataSet.length;
    }

    @Override
    public int getItemViewType(int position)
    {
        JSONObject jsonObject = dataSet[position];
        String currentID, myID;
        try
        {
            currentID = jsonObject.getString("authorId");
            myID = new JSONObject(Constants.User.USER_DETAILS).getString("id");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return -1;
        }

        if (currentID.equals(myID))
            return Constants.Conversation.AUTHOR_IS_ME;
        else
            return Constants.Conversation.AUTHOR_IS_NOT_ME;
    }
}
