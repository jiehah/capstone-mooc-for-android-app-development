package com.example.symptommanagement.data;

import lombok.Data;

import java.math.BigInteger;

/**
 * The Reminder class represents a reminder with various attributes, including its unique identifier,
 * name, day of the week, time (hour and minutes), alarm sound, status (on/off), creation timestamp,
 * and reminder type.
 */
@Data
public class Reminder {

    /**
     * The unique identifier for the reminder (BigInteger type).
     */
    private BigInteger Id;

    /**
     * A transient variable representing the database identifier for the reminder.
     */
    private transient long dbId;

    /**
     * The name of the reminder.
     */
    private String name;

    /**
     * The day of the week for the reminder (0: Sunday, 1: Monday, ..., 6: Saturday).
     */
    private int dayOfWeek;

    /**
     * The hour of the reminder (0-23).
     */
    private int hour;

    /**
     * The minutes of the reminder (0-59).
     */
    private int minutes;

    /**
     * The alarm sound associated with the reminder.
     */
    private String alarm;

    /**
     * A boolean flag indicating if the reminder is turned on (true) or off (false).
     */
    private boolean on;

    /**
     * The timestamp of when the reminder was created.
     */
    private long created;

    /**
     * The type of reminder, which can be one of the following: PAIN, MED, GENERIC.
     */
    private ReminderType reminderType;

    /**
     * An enumeration representing different types of reminders.
     */
    public enum ReminderType {
        PAIN(1), MED(2), GENERIC(3);

        private final int value;

        ReminderType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ReminderType findByValue(int val) {
            for (ReminderType r : values()) {
                if (r.getValue() == val) {
                    return r;
                }
            }
            return GENERIC;
        }
    }

    /**
     * Default constructor for the Reminder class.
     * Initializes some properties to default values.
     */
    public Reminder() {
        this.hour = -1;
        this.minutes = -1;
        this.on = false;
        this.reminderType = ReminderType.GENERIC;
    }
}
