package com.example.symptommanagement.patient.Reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.example.symptommanagement.LoginActivity;
import com.example.symptommanagement.LoginUtility;
import com.example.symptommanagement.R;

/**
 * ReminderService is a Service responsible for creating and displaying a check-in notification.
 * This service is started by the system when it receives the start command and it runs in the
 * background until it is stopped. The notification created by this service reminds the user to
 * perform a check-in.
 */
public class ReminderService extends Service {

    private static final String LOG_TAG = ReminderService.class.getSimpleName();
    private static final int SYMPTOM_MANAGEMENT_NOTIFICATION_ID = 1111;
    private static final String CHANNEL_ID = "CheckInChannel";
    private static final int NOTIFICATION_ID = 1;

    /**
     * This method is called when a client binds to the service. Since this service does not support
     * binding, it returns null.
     *
     * @param intent The intent that was used to bind to this service.
     * @return Always returns null.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called when the service is first created. This method is used for one-time setup procedures.
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Called when the service is started. This method is used to handle the start command.
     *
     * @param intent  The intent supplied to start the service.
     * @param flags   Additional data about this start request.
     * @param startId A unique integer representing this specific request to start.
     * @return The return value specifies what the system should do with the service after the
     * onStartCommand() callback finishes executing.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Log a message and set the Check-In status to true.
        Log.d(LOG_TAG, "Creating Check-In Notification. Setting Check-In to true.");
        LoginUtility.setCheckin(getApplicationContext(), true);

        // Create the notification channel (for Android 8.0 and above) and show the Check-In notification.
        createNotificationChannel();
        showCheckInNotification();

        // Return the super implementation's return value.
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Called when the service is destroyed. This method is used for cleanup procedures.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Creates a notification channel for Android 8.0 (API level 26) and above.
     */
    private void createNotificationChannel() {
        // Check if the device version is compatible with notification channels.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Define the channel properties.
            CharSequence name = "channel-name";
            String description = "channel-description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Create and register the notification channel.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Shows the Check-In notification.
     */
    private void showCheckInNotification() {
        // Build the notification with required content.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.check_in_notification_title))
                .setContentText(getString(R.string.check_in_notification_text))
                .setAutoCancel(true);

        // Set up the pending intent for the notification.
        Intent resultIntent = new Intent(this, LoginActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this)
                .addParentStack(LoginActivity.class)
                .addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        // Show the notification.
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
