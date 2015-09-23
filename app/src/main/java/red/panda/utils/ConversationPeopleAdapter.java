package red.panda.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

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
        public NetworkImageView avatar;
        ImageLoader imageLoader;

        public ViewHolder(View view)
        {
            super(view);
            lastMsg  = (TextView) view.findViewById(R.id.lastMessage);
            username = (TextView) view.findViewById(R.id.username);
            avatar = (NetworkImageView) view.findViewById(R.id.avatar);
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

        public void setAvatar(String id, Context context)
        {
            String url = ConversationUtils.makeAvatarURL(id);
            imageLoader = RequestQueueSingleton.getInstance(context).getImageLoader();
            avatar.setImageUrl(url, imageLoader);
        }

        public void setNoAvatar()
        {
            avatar.setImageResource(R.drawable.avatar_default);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        int layout = R.layout.conversation_people_recycler;

        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        JSONObject currentJson = dataSet[position];
        String author, authorTo, myUsername, msg, date, avatarURL;

        try
        {
            JSONObject authorJSON = currentJson.getJSONObject("author");
            JSONObject toJSON = currentJson.getJSONObject("to");

            author = JsonUtils.getFieldFromJSON(authorJSON, "username");
            authorTo = JsonUtils.getFieldFromJSON(toJSON, "username");
            myUsername = JsonUtils.getFieldFromJSON(new JSONObject(Constants.User.USER_DETAILS), "username");

            Context context = viewHolder.avatar.getContext();
            boolean authorIsMe, hasAvatar;

            authorIsMe = (myUsername != null) && (myUsername.equals(author));

            if (authorIsMe)
            {
                viewHolder.setUsername(authorTo);
                hasAvatar = toJSON.has("avatar");
            }
            else
            {
                viewHolder.setUsername(author);
                hasAvatar = authorJSON.has("avatar");
            }

            if (hasAvatar)
            {
                if (authorIsMe)
                    avatarURL = JsonUtils.getFieldFromJSON(toJSON, "avatar");
                else
                    avatarURL = JsonUtils.getFieldFromJSON(authorJSON, "avatar");
            }
            else
            {
                avatarURL = null;
                viewHolder.setNoAvatar();
            }

            date = currentJson.getString("lastReplyOn").substring(0, 10);
            msg = currentJson.getString("toAuthorId");
            viewHolder.setAvatar(avatarURL, context);
            viewHolder.setMessage(msg);
            viewHolder.setDate(date);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount()
    {
        return dataSet.length;
    }

}
