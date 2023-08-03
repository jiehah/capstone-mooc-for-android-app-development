package com.example.symptommanagement.repository;

import lombok.Data;

import java.math.BigInteger;

/**
 * Represents a reminder entity in the symptom management application.
 * Reminders are used to schedule notifications for patients.
 */
@Data
public class Reminder {

    /**
     * The unique identifier for the reminder.
     */
    BigInteger Id;

    /**
     * The name of the reminder.
     */
    private String name;

    /**
     * The day of the week for the reminder.
     */
    private int dayOfWeek;

    /**
     * The hour for the reminder.
     */
    private int hour;

    /**
     * The minutes for the reminder.
     */
    private int minutes;

    /**
     * The alarm sound for the reminder.
     */
    private String alarm;

    /**
     * Indicates if the reminder is turned on or off.
     */
    private boolean on;

    /**
     * The timestamp of when the reminder was created.
     */
    private long created;

    /**
     * The type of the reminder (PAIN, MED, GENERIC).
     */
    private ReminderType reminderType;

    /**
     * Enum representing the type of the reminder.
     */
    public enum ReminderType {
        PAIN(1),
        MED(2),
        GENERIC(3);

        private final int value;

        ReminderType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
