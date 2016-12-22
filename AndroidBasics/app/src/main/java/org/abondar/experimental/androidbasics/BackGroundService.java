package org.abondar.experimental.androidbasics;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by abondar on 12/14/16.
 */
public class BackGroundService extends Service {

    private static final String TAG = "BackgroundService";
    private NotificationManager notificationManager;
    private ThreadGroup threads = new ThreadGroup("ServiceWorker");


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, " onCreate()");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        displayNotificationMessage("Background Service is running");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, " onBind()");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        int counter = intent.getExtras().getInt("counter");
        Log.v(TAG, "onStartCommand(), counter = " + counter +
                ", startId= " + startId);

        new Thread(threads, new ServiceWorker(counter), "BackgroundService").start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy(). Interrupting threads and cancelling notifications");
        threads.interrupt();
        notificationManager.cancelAll();
        super.onDestroy();
    }

    private void displayNotificationMessage(String message) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(message)
                .setContentText("Touch to turn off service")
                .setSmallIcon(R.drawable.icon)
                .setTicker("Starting!!!")
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build();

        notificationManager.notify(0,notification);
    }

    class ServiceWorker implements Runnable {
        private int counter = -1;

        public ServiceWorker(int counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            final String TAG2 = "ServiceManager:" + Thread.currentThread().getId();

            //do processing
            try {
                Log.v(TAG2, "sleeping for 10 seconds. counter = " + counter);
                Thread.sleep(10000);
                Log.v(TAG2, "...waking up");
            } catch (InterruptedException e) {
                Log.v(TAG2, "...sleep interrupted");
            }
        }
    }
}
