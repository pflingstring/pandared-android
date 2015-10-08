package red.panda.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import red.panda.R;
import red.panda.utils.misc.Constants;
import red.panda.utils.ConversationUtils;
import red.panda.utils.JsonUtils;
import red.panda.utils.misc.RequestQueueSingleton;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>
{
    private JSONObject[] dataSet;

    public ConversationAdapter(JSONObject[] jsonObjects)
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
            avatar.setDefaultImageResId(R.drawable.place_holder);
            avatar.setImageUrl(url, imageLoader);
        }

        public void setNoAvatar()
        {
            avatar.setDefaultImageResId(R.drawable.avatar_default);
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
        try
        {
            Context context = viewHolder.avatar.getContext();
            boolean authorIsMe;

            String authorUsername, myUsername, msg, date, avatarURL;
            JSONObject myUserJSON = new JSONObject(Constants.User.USER_DETAILS);
            JSONObject authorJSON = currentJson.getJSONObject("author");
            JSONObject toJSON = currentJson.getJSONObject("to");

            authorUsername = JsonUtils.getFieldFromJSON(authorJSON, "username");
            myUsername = JsonUtils.getFieldFromJSON(myUserJSON, "username");
            date = currentJson.getString("lastReplyOn");

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            long milliseconds;
            try
            {
                Date mDate = dateFormatter.parse(date);
                milliseconds = mDate.getTime();
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                milliseconds = 0;
            }
            String formattedDate = DateUtils.getRelativeTimeSpanString(milliseconds,
                    System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();

            msg = currentJson.getString("toAuthorId");

            viewHolder.setMessage(msg);
            viewHolder.setDate(formattedDate);

            authorIsMe = (myUsername != null) && (myUsername.equals(authorUsername));
            JSONObject author = authorIsMe ? toJSON : authorJSON;
            viewHolder.setUsername(author.getString("username"));

            if (author.has("avatar"))
            {
                avatarURL = JsonUtils.getFieldFromJSON(author, "avatar");
                viewHolder.setAvatar(avatarURL, context);
            }
            else
            {
                viewHolder.setNoAvatar();
            }
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

