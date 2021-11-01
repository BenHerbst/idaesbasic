package org.idaesbasic.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TaskRowModel {

    private StringProperty title = new SimpleStringProperty("");

    public void setTitle(String value) {
        title.set(value);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }
}