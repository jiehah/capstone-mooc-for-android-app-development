package com.example.symptommanagement.repository;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigInteger;

/**
 * Represents a medication entity in the symptom management application.
 * Medications are prescribed to patients for managing their symptoms.
 */
@Data
public class PainLog {

    /**
     * Enumeration for different severity levels of pain.
     */
    public enum Severity {
        NOT_DEFINED(0), // Severity level not defined
        WELL_CONTROLLED(100), // Mild pain or well-controlled pain
        MODERATE(200), // Moderate pain
        SEVERE(300); // Severe pain

        private final int value;

        Severity(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Severity findByValue(int val) {
            for (Severity s : values()) {
                if (s.getValue() == val) {
                    return s;
                }
            }
            return NOT_DEFINED;
        }
    }

    /**
     * Enumeration for different eating status levels.
     */
    public enum Eating {
        NOT_DEFINED(0), // Eating status not defined
        EATING(100), // Patient is eating
        SOME_EATING(200), // Patient is eating some food
        NOT_EATING(300); // Patient is not eating

        private final int value;

        Eating(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Eating findByValue(int val) {
            for (Eating e : values()) {
                if (e.getValue() == val) {
                    return e;
                }
            }
            return NOT_DEFINED;
        }
    }

    /**
     * The unique identifier for the pain log.
     */
    @Id
    private BigInteger id;

    /**
     * The timestamp of when the pain log was created.
     */
    private long created;

    /**
     * The severity level of pain (enum value).
     */
    private Severity severity = Severity.NOT_DEFINED;

    /**
     * The eating status of the patient (enum value).
     */
    private Eating eating = Eating.NOT_DEFINED;

    /**
     * The check-in ID associated with this pain log.
     */
    private long checkinId;
}
