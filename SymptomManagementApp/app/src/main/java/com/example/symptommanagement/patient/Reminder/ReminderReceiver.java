package com.example.symptommanagement.patient.Reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * ReminderReceiver is a BroadcastReceiver responsible for receiving reminders and starting the
 * ReminderService to display the check-in notification.
 */
public class ReminderReceiver extends BroadcastReceiver {

    /**
     * This method is called when a reminder is received. It starts the ReminderService to display
     * the check-in notification.
     *
     * @param context The context in which the receiver is running.
     * @param intent  The intent containing the reminder information.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Start the ReminderService to display the check-in notification
        Intent intentReminderService = new Intent(context, ReminderService.class);
        context.startService(intentReminderService);
        rescheduleAlarm(context, intent);
    }

    private void rescheduleAlarm(Context context, Intent intent) {
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Calculate the time for the next alarm (24 hours later)
        long currentTimeMillis = System.currentTimeMillis();
        long nextAlarmTimeMillis = currentTimeMillis + AlarmManager.INTERVAL_DAY;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarmTimeMillis,
                    alarmIntent
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarmTimeMillis,
                    alarmIntent
            );
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarmTimeMillis,
                    alarmIntent
            );
        }
    }
}
