package com.daterangepicker;

public enum CalendarType {
    SINGLE("single"),
    RANGE("range");

    private final String value;

    CalendarType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}