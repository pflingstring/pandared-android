package red.panda.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import red.panda.R;
import red.panda.models.Conversation;
import red.panda.models.User;
import red.panda.utils.ConversationUtils;
import red.panda.utils.JsonUtils;
import red.panda.utils.misc.RequestQueueSingleton;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>
{
    private Conversation[] dataSet;

    public int getItemPosition(String id)
    {
        for (int i=0; i<dataSet.length; i++)
        {
            Conversation conversation = dataSet[i];
            if (id.equals(conversation.getId()))
                return i;
        }
        return -1;
    }

    public void setUnread(int position)
    {
        dataSet[position].setHasUnreadMessages(true);
    }

    public ConversationAdapter(Conversation[] conversations)
    {
        dataSet = conversations;
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

        if (viewType == 1)
            v.setBackgroundColor(Color.parseColor("#A9DDA9"));

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        Context context = viewHolder.avatar.getContext();

        Conversation conversation = dataSet[position];
        User author = conversation.getUser();

        if (author != null)
        {
            viewHolder.setUsername(author.getUsername());
            if (author.getAvatar() != null)
                viewHolder.setAvatar(author.getAvatar(), context);
            else
                viewHolder.setNoAvatar();
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        long milliseconds;
        try
        {
            Date mDate = dateFormatter.parse(conversation.getLastReplyOn());
            milliseconds = mDate.getTime();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            milliseconds = 0;
        }
        String formattedDate = DateUtils.getRelativeTimeSpanString(milliseconds,
                System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
        viewHolder.setDate(formattedDate);
        viewHolder.setMessage(conversation.getLastMessage());
    }

    @Override
    public int getItemCount()
    {
        return dataSet.length;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (dataSet[position].hasUnreadMessages())
            return 1;

        return 0;
    }

}

