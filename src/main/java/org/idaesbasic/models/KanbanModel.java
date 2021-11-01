package org.idaesbasic.models;

public class KanbanModel {

    public String[] standardTaskRows = {"Todo", "In progess", "Done"};

    public String[] getStandardTaskRows() {
        return standardTaskRows;
    }

    public void setStandardTaskRows(String[] standardTaskRows) {
        this.standardTaskRows = standardTaskRows;
    }
}
