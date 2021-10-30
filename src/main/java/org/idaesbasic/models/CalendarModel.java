package org.idaesbasic.models;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CalendarModel {

    private final ListProperty<CalendarEventItem> events = new SimpleListProperty<CalendarEventItem>();
    
    public CalendarModel() {
        //Create the events property
        events.set(FXCollections.observableArrayList(new ArrayList<CalendarEventItem>()));
    }

    public final ListProperty<CalendarEventItem> eventsProperty() {
        return this.events;
    }
    

    public final ObservableList<CalendarEventItem> getEvents() {
        return this.eventsProperty().get();
    }
    

    public final void setEvents(final ObservableList<CalendarEventItem> events) {
        this.eventsProperty().set(events);
    }

    public final void removeEvent(int index) {
        this.eventsProperty().remove(index);
    }
    
    public final void addEvent(CalendarEventItem event) {
        this.eventsProperty().add(event);
    }
}
