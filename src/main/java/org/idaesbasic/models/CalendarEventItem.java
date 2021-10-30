package org.idaesbasic.models;

public class CalendarEventItem {
    public java.util.Calendar startDate;
    public java.util.Calendar endDate;
    public String summary;
    public String id;

    public CalendarEventItem(String summary, java.util.Calendar startDate, java.util.Calendar endDate, String id) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.summary = summary;
        this.id = id;
    }
}