package com.example.symptommanagement.patient.Reminder;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import com.example.symptommanagement.data.PatientDataManager;
import com.example.symptommanagement.data.Reminder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

public class ReminderManager {

    private static final String LOG_TAG = ReminderManager.class.getSimpleName();
    private static AlarmManager alarmManager = null;
    private static Collection<ActivatedAlarm> activatedAlarms = null;
    private static int nextAvailableResponseCode = 1;
    private static final long MILLISECONDS_IN_A_DAY = 86400000;

    /**
     * Gets the AlarmManager instance from the system service if not already initialized.
     *
     * @param context The application context.
     * @return The AlarmManager instance.
     */
    private static AlarmManager getAlarmManager(Context context) {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        return alarmManager;
    }

    /**
     * Gets the collection of activated alarms, initializing it if not already done.
     *
     * @return The collection of activated alarms.
     */
    private static Collection<ActivatedAlarm> getActivatedAlarms() {
        if (activatedAlarms == null) {
            activatedAlarms = new HashSet<>();
        }
        return activatedAlarms;
    }

    /**
     * Sorts a collection of reminders by time, in ascending order.
     *
     * @param reminders The collection of reminders to be sorted.
     * @return A sorted collection of reminders by time.
     */
    public static synchronized Collection<Reminder> sortRemindersByTime(Collection<Reminder> reminders) {
        if (reminders == null || reminders.isEmpty()) return null;
        TreeSet<Reminder> sorted = new TreeSet<>(new ReminderSorter());
        sorted.addAll(reminders);
        return sorted;
    }

    /**
     * Comparator class to compare two reminders based on their time.
     */
    public static class ReminderSorter implements Comparator<Reminder> {
        public synchronized int compare(Reminder x, Reminder y) {
            return Long.compare(x.getHour() * 60L + x.getMinutes(), y.getHour() * 60L + y.getMinutes());
        }
    }

    /**
     * Gets the time in milliseconds from the current time after adding the specified number of hours.
     *
     * @param hours The number of hours to add (negative hours for past time).
     * @return The time in milliseconds from now after adding the hours.
     */
    public static long getHoursFromNow(int hours) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, hours);
        return cal.getTimeInMillis();
    }

    /**
     * Gets the time in milliseconds representing the start of the current day (at 00:00).
     *
     * @return The time in milliseconds representing the start of the current day.
     */
    public static long getStartOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Starts patient reminders by setting up alarms for the active reminders.
     *
     * @param context The application context.
     * @param id      The patient ID to retrieve reminders.
     */
    public static void startPatientReminders(Context context, String id) {
        Collection<Reminder> reminders = PatientDataManager.loadReminderList(context, id);

        if (reminders == null || reminders.isEmpty()) {
            return;
        }
        Log.d(LOG_TAG, "There are " + reminders.size() + " reminders for id: " + id);
        Reminder[] reminderArray = reminders.toArray(new Reminder[0]);
        for (Reminder r : reminderArray) {
            Log.d(LOG_TAG, "Checking reminder " + r.getName() + " it is " + (r.isOn() ? "ON" : "OFF"));
            if (r.isOn()) {
                setSingleReminderAlarm(context, r);
            }
        }
    }

    /**
     * Sets a single reminder alarm for the specified reminder.
     * <p>
     * This method is responsible for setting up a repeating alarm for the given reminder if it is active (ON).
     * If the reminder is not active, the method cancels any existing alarm associated with the reminder.
     *
     * @param context The application context.
     * @param r       The reminder to set the alarm for.
     */
    public static void setSingleReminderAlarm(Context context, Reminder r) {
        // Log the attempt to set the alarm and its status
        Log.d(LOG_TAG, "Attempting to set Alarm for " + r.getName()
                + " is " + (r.isOn() ? "ON" : "OFF."));

        // If the reminder is not active, cancel the alarm and return
        if (!r.isOn()) {
            cancelSingleReminderAlarm(context, r);
            return;
        }

        // Create a new ActivatedAlarm with the current nextAvailableResponseCode and add it to the activatedAlarms
        ActivatedAlarm activatedAlarm = new ActivatedAlarm(r.getCreated(), nextAvailableResponseCode++);
        getActivatedAlarms().add(activatedAlarm);

        // Create a PendingIntent to be used for the alarm
        PendingIntent alarmIntent = createReminderPendingIntent(context, activatedAlarm.getResponseCode());

        // Log the creation of a new alarm for the reminder
        Log.d(LOG_TAG, "Creating a new Alarm with Interval DAY for reminder " + r.getName());

        // Calculate the alarm time in milliseconds from the hour and minutes
        long alarmTimeMillis = getAlarmTime(r.getHour(), r.getMinutes());

        // Set the alarm using setExact() to trigger at the specified alarm time
        setExactAlarm(context, alarmTimeMillis, alarmIntent);
    }

    // Method to set the alarm using setExact()
    private static void setExactAlarm(Context context, long alarmTimeMillis, PendingIntent alarmIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTimeMillis,
                    alarmIntent
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    alarmTimeMillis,
                    alarmIntent
            );
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    alarmTimeMillis,
                    alarmIntent
            );
        }
    }

    /**
     * Gets the time in milliseconds representing the next alarm time.
     * <p>
     * This method calculates the time in milliseconds for the next alarm based on the provided hour and minutes.
     * If the alarm time for the current day has already passed, the next alarm time will be set for the next day.
     *
     * @param hour    The hour of the reminder.
     * @param minutes The minutes of the reminder.
     * @return The time in milliseconds representing the next alarm time.
     */
    private static long getAlarmTime(int hour, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);

        // Check if the alarm time for the current day has already passed
        Calendar testCalendar = Calendar.getInstance();
        testCalendar.setTimeInMillis(System.currentTimeMillis());
        if (calendar.getTimeInMillis() <= testCalendar.getTimeInMillis()) {
            // If so, set the next alarm time for the next day
            long nextDay = calendar.getTimeInMillis() + MILLISECONDS_IN_A_DAY;
            calendar.setTimeInMillis(nextDay);
        }

        return calendar.getTimeInMillis();
    }

    /**
     * Cancels all patient reminders by deactivating the associated alarms.
     * <p>
     * This method cancels all patient reminders by iterating through the activated alarms and
     * canceling their associated PendingIntent alarms. If an alarm is successfully canceled,
     * it is removed from the collection of activated alarms.
     *
     * @param context The application context.
     */
    public static void cancelPatientReminders(Context context) {
        ActivatedAlarm[] alarmArray = getActivatedAlarms().toArray(new ActivatedAlarm[0]);
        for (ActivatedAlarm a : alarmArray) {
            if (isAlarmActivated(context, a)) {
                PendingIntent alarmIntent = createReminderPendingIntent(context, a.getResponseCode());
                cancelSingleReminderAlarm(alarmIntent);
                if (!isAlarmActivated(context, a)) {
                    // The alarm was successfully deactivated, remove it from the collection
                    Log.d(LOG_TAG, "The alarm was successfully deactivated.");
                    getActivatedAlarms().remove(a);
                    return;
                }
            }
        }
        // The alarm was not deactivated; it may not have been activated or might not exist
        Log.d(LOG_TAG, "The alarm was not deactivated. It may not have been activated or might not exist.");
    }

    /**
     * Cancels a single reminder alarm given its PendingIntent.
     * <p>
     * This method cancels the alarm represented by the provided PendingIntent using the alarmManager.
     *
     * @param alarmIntent The PendingIntent of the alarm to be canceled.
     */
    public static void cancelSingleReminderAlarm(PendingIntent alarmIntent) {
        if (alarmManager != null) {
            Log.d(LOG_TAG, "Actually Canceling the alarm!");
            alarmManager.cancel(alarmIntent);
        }
    }

    /**
     * Cancels a single reminder alarm associated with the given Reminder.
     * <p>
     * This method cancels the reminder alarm by iterating through the activated alarms
     * and matching the reminder's activation time to the activated alarms. If a matching
     * activated alarm is found, its associated alarm is canceled using the alarmManager.
     * The activated alarm is also removed from the collection of activated alarms.
     *
     * @param context  The application context.
     * @param reminder The Reminder for which the alarm should be canceled.
     */
    public static synchronized void cancelSingleReminderAlarm(Context context, Reminder reminder) {
        if (getActivatedAlarms() == null || getActivatedAlarms().size() == 0) {
            return;
        }

        ActivatedAlarm[] alarmArray = getActivatedAlarms().toArray(new ActivatedAlarm[0]);

        for (ActivatedAlarm s : alarmArray) {
            if (s.matches(reminder)) {
                Log.d(LOG_TAG, "Found Activated Alarm for the Reminder " + reminder.getName() + " - Canceling");
                cancelSingleReminderAlarm(createReminderPendingIntent(context, s.getResponseCode()));
                getActivatedAlarms().remove(s);
            }
            Log.d(LOG_TAG, "Tried to cancel this reminder but did not find it. " +
                    "Not necessarily an error ... may just be canceling before restarting on edit."
                    + reminder);
        }
    }

    /**
     * Creates a PendingIntent for the reminder alarm using the given responseCode.
     * <p>
     * This method creates a PendingIntent for the reminder alarm with the given responseCode
     * and associates it with the ReminderReceiver class.
     *
     * @param context      The application context.
     * @param responseCode The response code to associate with the PendingIntent.
     * @return The PendingIntent for the reminder alarm.
     */
    public static synchronized PendingIntent createReminderPendingIntent(Context context, int responseCode) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        return PendingIntent.getBroadcast(context, responseCode, intent, 0);
    }

    /**
     * Checks if the alarm associated with the given Reminder is currently activated.
     * <p>
     * This method checks if the reminder is present in the collection of activated alarms,
     * and if it is, it retrieves the associated alarm's response code and uses the
     * `isAlarmActivated(Context context, int requestCode)` method to determine if the alarm
     * is currently activated.
     *
     * @param context  The application context.
     * @param reminder The Reminder for which to check the alarm activation status.
     * @return True if the alarm is activated for the Reminder, false otherwise.
     */
    public static boolean isAlarmActivated(Context context, Reminder reminder) {
        Collection<ActivatedAlarm> activatedAlarms = getActivatedAlarms();
        if (activatedAlarms != null && !activatedAlarms.isEmpty()) {
            for (ActivatedAlarm activatedAlarm : activatedAlarms) {
                if (activatedAlarm.matches(reminder)) {
                    return isAlarmActivated(context, activatedAlarm.getResponseCode());
                }
            }
        }
        return false;
    }

    /**
     * Checks if the alarm associated with the given ActivatedAlarm is currently activated.
     * <p>
     * This method uses the response code of the ActivatedAlarm to check if the alarm
     * is currently activated.
     *
     * @param context        The application context.
     * @param activatedAlarm The ActivatedAlarm for which to check the alarm activation status.
     * @return True if the alarm is activated for the ActivatedAlarm, false otherwise.
     */
    public static boolean isAlarmActivated(Context context, ActivatedAlarm activatedAlarm) {
        return isAlarmActivated(context, activatedAlarm.getResponseCode());
    }

    /**
     * Checks if an alarm with the given requestCode is currently activated.
     * <p>
     * This method checks if a PendingIntent with the provided requestCode exists,
     * indicating that the associated alarm is currently activated.
     *
     * @param context     The application context.
     * @param requestCode The request code associated with the PendingIntent.
     * @return True if the alarm is activated, false otherwise.
     */
    public static boolean isAlarmActivated(Context context, int requestCode) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
        return alarmIntent != null;
    }

    /**
     * Inner class representing an activated alarm.
     * It holds the creation time of the reminder and the associated response code.
     */
    @Data
    @AllArgsConstructor
    public static class ActivatedAlarm {
        private long reminderCreated;
        private int responseCode;

        /**
         * Checks if this ActivatedAlarm matches the given Reminder based on the creation time.
         *
         * @param reminder The Reminder to compare with this ActivatedAlarm.
         * @return True if the reminder's creation time matches this ActivatedAlarm's creation time, false otherwise.
         */
        public boolean matches(Reminder reminder) {
            Log.d(LOG_TAG, "Does " + reminderCreated + " = "
                    + reminder.getCreated() + " "
                    + (reminderCreated == reminder.getCreated()));
            return reminderCreated == reminder.getCreated();
        }
    }

    /**
     * Generates a string containing information about the activated alarms associated with a patient's reminders.
     * <p>
     * This method prints the names of the reminders for which the alarms are activated.
     *
     * @param context The application context.
     * @param id      The ID of the patient.
     * @return A string containing information about the activated alarms.
     */
    public static String printAlarms(Context context, String id) {
        if (id == null || id.isEmpty()) {
            return "Invalid ID: Unable to print alarms.";
        }
        Collection<ActivatedAlarm> activatedAlarms = getActivatedAlarms();
        if (activatedAlarms == null || activatedAlarms.isEmpty()) {
            return "No alarms set.";
        }

        Collection<Reminder> reminders = PatientDataManager.loadReminderList(context, id);
        if (reminders == null || reminders.isEmpty()) {
            return "No reminders to print.";
        }

        Reminder[] reminderArray = reminders.toArray(new Reminder[0]);
        int count = 0;
        String answer = "The activated alarms are: \n";
        for (ActivatedAlarm activatedAlarm : activatedAlarms) {
            String info = "Activated Alarm " + count++;
            for (Reminder r : reminderArray) {
                if (activatedAlarm.matches(r)) {
                    info += " name = " + r.getName() + "\n";
                    break;
                }
            }
            answer += info;
        }
        Log.d(LOG_TAG, "There are " + reminders.size() + " reminders in the database. There are " + activatedAlarms.size() + " alarms activated.");
        Log.d(LOG_TAG, "There are " + activatedAlarms.size() + " alarms set at this time.");
        Log.d(LOG_TAG, answer);
        return answer;
    }
}
