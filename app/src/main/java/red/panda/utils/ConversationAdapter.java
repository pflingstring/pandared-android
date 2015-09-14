package red.panda.utils;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import red.panda.R;


public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>
{
    private String[] dataSet;
    public ConversationAdapter(String[] myData)
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
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.setAvatar(R.drawable.avatar);
        viewHolder.setMessage(dataSet[position]);
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }

}
