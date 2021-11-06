package org.idaesbasic.models;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.idaesbasic.models.kanban.kanbanTaskModel;
import org.idaesbasic.models.kanban.taskRowModel;

import java.util.ArrayList;
import java.util.List;

public class KanbanModel {

    List<taskRowModel> standardTaskRows = new ArrayList<taskRowModel>(FXCollections.observableArrayList(new taskRowModel("Todo"), new taskRowModel("In progess"), new taskRowModel("Done")));

    public List<taskRowModel> getStandardTaskRows() {
        return this.standardTaskRows;
    }

    public void setStandardTaskRows(taskRowModel[] standardTaskRows) {
        this.standardTaskRows = FXCollections.observableArrayList(standardTaskRows);
    }

    public void addStandardTaskRows(taskRowModel row) {
        standardTaskRows.add(row);
    }
}
