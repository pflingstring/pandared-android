package red.panda.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import red.panda.R;

public class ConversationPeopleAdapter extends
        RecyclerView.Adapter<ConversationPeopleAdapter.ViewHolder>
{
    private JSONObject[] dataSet;

    public ConversationPeopleAdapter(JSONObject[] jsonObjects)
    {
        dataSet = jsonObjects;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username;
        public TextView lastMsg;
        public TextView date;
        public ImageView avatar;

        public ViewHolder(View view)
        {
            super(view);
            lastMsg  = (TextView) view.findViewById(R.id.lastMessage);
            username = (TextView) view.findViewById(R.id.username);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            date   = (TextView) view.findViewById(R.id.date);
        }

        public void setUsername(String name)
        {
            username.setText(name);
        }

        public void setMessage(String message)
        {
            lastMsg.setText(message);
        }

        public void setDate(String myDate)
        {
            date.setText(myDate);
        }

        public void setAvatar(int id)
        {
            avatar.setImageResource(id);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        int layout = R.layout.conversation_people;

        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        JSONObject currentJson = dataSet[position];

        String author, authorTo, myUsername, msg, date;
        try
        {
            author = currentJson.getJSONObject("author").getString("username");
            authorTo = currentJson.getJSONObject("to").getString("username");
            myUsername = new JSONObject(Constants.User.USER_DETAILS).getString("username");

            if (myUsername.equals(author))
                viewHolder.setUsername(authorTo);
            else
                viewHolder.setUsername(author);

            date = currentJson.getString("lastReplyOn").substring(0, 10);
            msg = currentJson.getString("toAuthorId");

            viewHolder.setMessage(msg);
            viewHolder.setDate(date);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        viewHolder.setAvatar(R.drawable.avatar_my);
    }

    @Override
    public int getItemCount()
    {
        return dataSet.length;
    }

}
