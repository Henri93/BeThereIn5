package henrygarant.com.demomap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import henrygarant.com.demomap.MapActivities.ConnectionManager;
import henrygarant.com.demomap.MapActivities.MyLocationService;
import henrygarant.com.demomap.MapActivities.WaitingPage;

public class MyNotificationManager extends Service {

    private Context context;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        ConnectionManager connectionManager = new ConnectionManager(this);
        Log.d("NOTIFICATION: ", "inside notification");
        String phoneto = null;
        String sender = null;
        String message = null;
        int distance = 0;
        boolean finished = false;

        if (intent != null) {
            if (intent.getAction().equals(Config.NOTIF_STICKY)) {
                //status
                try {
                    phoneto = intent.getStringExtra("phoneto");
                    sender = intent.getStringExtra("sender");
                    distance = intent.getIntExtra("distance", 0);
                    finished = intent.getBooleanExtra("finished", false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stickyNotification(phoneto, sender, distance, finished, connectionManager.isConnected());
            } else if (intent.getAction().equals(Config.NOTIF_ACCEPT)) {
                //accept
                try {
                    phoneto = intent.getStringExtra("phoneto");
                    sender = intent.getStringExtra("sender");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                acceptNotification(sender, phoneto);
            } else if (intent.getAction().equals(Config.NOTIF_REG)) {
                try {
                    message = intent.getStringExtra("message");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                regularNotification(message);
            } else {
                //stop
                stopForeground(true);
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopSelf();
        stopForeground(true);
        super.onDestroy();
    }

    public void stickyNotification(String phoneto, String sender, int distance, boolean finished, boolean isConnected) {
        Intent i = new Intent(context, MapsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);

        Intent nextIntent = new Intent(context, MyLocationService.class);
        nextIntent.setAction(Config.ACTION_STOP);
        Log.d("LOCATION SERVICE: ", "put phone " + phoneto);
        nextIntent.putExtra("phoneto", phoneto);
        PendingIntent stopIntent = PendingIntent.getService(context, 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.notification);
        views.setOnClickPendingIntent(R.id.notif_stop, stopIntent);

        if (!finished) {
            if (sender != null && !sender.equals("Unknown") && distance != 0) {
                views.setTextViewText(R.id.notif_status, "Connected");
                views.setTextViewText(R.id.notif_info, sender + " is " + distance + "m away.");
            } else {
                if (!isConnected && (sender == null || sender.equals("Unknown"))) {
                    views.setTextViewText(R.id.notif_status, "Waiting for Ride Acceptance");
                    views.setTextViewText(R.id.notif_info, "");
                    MapsActivity.updateUI("Connecting...", 0);
                } else if (isConnected) {
                    views.setTextViewText(R.id.notif_status, "Connected");
                    views.setTextViewText(R.id.notif_info, "Remember to drive safe");
                } else {
                    views.setTextViewText(R.id.notif_status, "Obtaining Connection");
                    views.setTextViewText(R.id.notif_info, "");
                }

            }
        } else {
            views.setTextViewText(R.id.notif_status, "Congratulations!");
            views.setTextViewText(R.id.notif_info, sender + " is within 5");
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        if (!finished) {
            Notification note = builder.setContentIntent(pi)
                    .setSmallIcon(R.drawable.notification_icon_small).setTicker("Ride Status").setWhen(System.currentTimeMillis())
                    .setAutoCancel(false).setContentTitle("Be There In 5")
                    .setOngoing(true)
                    .setContent(views).build();
        } else {
            Notification note = builder.setContentIntent(pi)
                    .setSmallIcon(R.drawable.notification_icon_small).setTicker("Congratulations!").setWhen(System.currentTimeMillis())
                    .setAutoCancel(false).setContentTitle("Be There In 5")
                    .setOngoing(true)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setLights(Color.RED, 3000, 3000)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContent(views).build();
        }

        Log.d("NOTIFICATION: ", "sent notification");

        NotificationManager myNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        myNotificationManager.notify(
                1337,
                builder.build());
        startForeground(1337, builder.build());
    }

    public void regularNotification(String message) {
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);

        Intent nextIntent = new Intent(context, MyLocationService.class);
        nextIntent.setAction(Config.ACTION_STOP);
        PendingIntent stopIntent = PendingIntent.getService(context, 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.notification);
        views.setTextViewText(R.id.notif_status, message);
        views.setTextViewText(R.id.notif_info, "");
        views.setOnClickPendingIntent(R.id.notif_stop, stopIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);

        Notification note = builder.setContentIntent(pi)
                .setSmallIcon(R.drawable.notification_icon_small).setTicker(message).setWhen(System.currentTimeMillis())
                .setAutoCancel(true).setContentTitle("Be There In 5")
                .setOngoing(false)
                .setVibrate(new long[]{1000, 1000, 1000})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContent(views).build();

        NotificationManager myNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        myNotificationManager.notify(
                1337,
                builder.build());
        startForeground(1337, builder.build());
    }

    private void acceptNotification(String sender, String phonefrom) {
        Intent intent = new Intent(this, WaitingPage.class);

        intent.putExtra("sender", sender);
        intent.putExtra("phoneto", phonefrom);
        intent.putExtra("fromaccept", true);

        //Class to open when user clicks notification
        PendingIntent acceptIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.notification_accept);
        views.setOnClickPendingIntent(R.id.notif_accept_accept, acceptIntent);

        Intent stopIntent = new Intent(this, MyNotificationManager.class);
        stopIntent.setAction(Config.NOTIF_STOP);
        PendingIntent denyIntent = PendingIntent.getActivity(this, 0,
                stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        views.setOnClickPendingIntent(R.id.notif_accept_deny, denyIntent);
        views.setTextViewText(R.id.notif_riderequest, "Ride Request from " + sender);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        Notification note = builder.setSmallIcon(R.drawable.notification_icon_small).setTicker("Ride Request").setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Be There In 5")
                .setOngoing(false)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.BLUE, 3000, 3000)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContent(views).build();
        note.flags = note.FLAG_AUTO_CANCEL;

        NotificationManager myNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        myNotificationManager.notify(
                1337,
                note);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
