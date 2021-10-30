package org.idaesbasic.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CalendarEventItem {
    public java.util.Calendar startDate;
    public java.util.Calendar endDate;
    public String summary;
    private final StringProperty id = new SimpleStringProperty();

    public CalendarEventItem(String summary, java.util.Calendar startDate, java.util.Calendar endDate, String id) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.summary = summary;
        this.id.set(id);
    }
    
    public final String getId() {
        return id.getValue();
    }
    
    public final void setId(String value) {
        id.set(value);
    }
}