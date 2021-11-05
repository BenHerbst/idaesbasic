module idaesbasic {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires com.calendarfx.view;
    requires com.fasterxml.jackson.core;
    requires java.logging;
    requires java.base;
    requires org.mnode.ical4j.core;
    requires kotlin.stdlib;

    opens org.idaesbasic to javafx.fxml;
    opens org.idaesbasic.controllers to javafx.fxml;
    opens org.idaesbasic.controllers.todolist to javafx.fxml;
    opens org.idaesbasic.models to com.fasterxml.jackson.core;
    opens org.idaesbasic.controllers.calendar to javafx.fxml;
    opens org.idaesbasic.controllers.kanban to javafx.fxml;

    exports org.idaesbasic;
    exports org.idaesbasic.controllers;
    exports org.idaesbasic.controllers.todolist;
    exports org.idaesbasic.controllers.calendar;
    exports org.idaesbasic.controllers.kanban;
    exports org.idaesbasic.models;
}
