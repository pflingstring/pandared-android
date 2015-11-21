package red.panda.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import red.panda.R;
import red.panda.activities.ConversationActivity;

public class NotificationUtils
{
    public static void emitNotification(Context context, JSONObject json)
    {
        NotificationManager notificationManager = (NotificationManager)
            context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra("MSG_ID", json.toString());
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, (int)System.currentTimeMillis(), intent, 0);

        Notification n  = new Notification.Builder(context)
            .setContentTitle("You have a new personal message")
            .setSmallIcon(R.drawable.launcher)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build();

        notificationManager.notify(0, n);
    }
}
