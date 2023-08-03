package com.example.symptommanagement.data;


import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class HistoryLog {

    private long id;
    private LogType type;
    private String info;
    private long created;

    public enum LogType {
        GENERIC(0),
        PAIN_LOG(10),
        CHECK_IN_PAIN_LOG(15),
        MED_LOG(20),
        CHECK_IN_MED_LOG(25),
        STATUS_LOG(30),
        CHECK_IN_LOG(40);

        private final int value;

        LogType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static LogType findByValue(int val) {
            for (LogType l : values()) {
                if (l.getValue() == val) {
                    return l;
                }
            }
            return GENERIC;
        }
    }

    /**
     * Formats a given date in milliseconds into a human-readable string representation.
     *
     * @param dt  The date in milliseconds.
     * @param fmt The desired format pattern for the date.
     * @return The formatted date string.
     */
    public String getFormattedDate(long dt, String fmt) {
        if (dt <= 0L) return "";
        Date date = new Date(dt);
        SimpleDateFormat format = new SimpleDateFormat(fmt);
        return format.format(date);
    }

    /**
     * Formats the creation date of the object into a human-readable string representation.
     *
     * @return The formatted creation date string.
     */
    public String getFormattedCreatedDate() {
        return getFormattedDate(this.created, "E, MMM d yyyy 'at' hh:mm a");
    }
}
