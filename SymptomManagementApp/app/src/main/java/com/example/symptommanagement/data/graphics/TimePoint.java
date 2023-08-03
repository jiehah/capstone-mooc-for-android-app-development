package com.example.symptommanagement.data.graphics;

import lombok.Data;

import java.util.Calendar;

/**
 * A class representing a time point with various time-related properties.
 */
@Data
public class TimePoint {

    /**
     * The time value in milliseconds representing the timestamp of the data point.
     */
    private long timeValue;

    /**
     * The actual date value in milliseconds representing the date portion of the data point's timestamp (timeValue).
     */
    private long actualDate;

    /**
     * The hour of the day (0-23) of the data point's timestamp (timeValue).
     */
    private int hour;

    /**
     * The minutes of the data point's timestamp (timeValue).
     */
    private int minutes;

    /**
     * The day of the week (1-7, where 1 represents Sunday) of the data point's timestamp (timeValue).
     */
    private int dayOfWeek;

    /**
     * The day of the month (1-31) of the data point's timestamp (timeValue).
     */
    private int dayOfMonth;

    /**
     * Constructor to create a TimePoint with the given timeValue.
     *
     * @param timeValue The time value in milliseconds representing the timestamp of the data point.
     */
    public TimePoint(long timeValue) {
        this.timeValue = timeValue;

        // Create a Calendar instance and set it to the given timeValue to extract time-related properties.
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeValue);

        // Set the hour, minutes, dayOfWeek, and dayOfMonth fields based on the extracted values from the Calendar.
        this.hour = cal.get(Calendar.HOUR_OF_DAY);
        this.minutes = cal.get(Calendar.MINUTE);
        this.dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        this.dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        // Set the time-related fields (hour, minutes, seconds, and milliseconds) of the Calendar to zero
        // to obtain the actualDate representing the date portion without the time.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        this.actualDate = cal.getTimeInMillis();
    }
}
