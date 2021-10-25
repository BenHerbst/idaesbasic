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
	requires org.json;

	opens org.idaesbasic to javafx.fxml;
	opens org.idaesbasic.controllers to javafx.fxml;
	opens org.idaesbasic.controllers.todolist to javafx.fxml;
	opens org.idaesbasic.data to org.json;
	
	exports org.idaesbasic;
	exports org.idaesbasic.controllers;
	exports org.idaesbasic.controllers.todolist;
	exports org.idaesbasic.data;
}