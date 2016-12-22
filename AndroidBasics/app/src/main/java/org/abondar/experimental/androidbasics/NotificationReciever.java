package org.abondar.experimental.androidbasics;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by abondar on 12/14/16.
 */
public class NotificationReciever extends BroadcastReceiver {
    private static final String tag = "NotificationReciever";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(tag, "intent=" + intent);
        String message = intent.getStringExtra("msg");
        this.sendNotification(context, message);
    }

    private void sendNotification(Context ctx, String msg) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(ns);
        //prep notification object
        int icon = R.drawable.icon;
        CharSequence tickerText = "Hello";
        long whem = System.currentTimeMillis();

        //intent to fire
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.google.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

        //Create the notification object through builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);


        notificationManager.notify(1, builder.setContentTitle("Test1")
                .setContentText(tickerText)
                .setSmallIcon(icon)
                .setWhen(whem)
                .setContentIntent(pendingIntent)
                .build());
    }
}
