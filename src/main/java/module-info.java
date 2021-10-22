module idaesbasic {
	requires java.desktop;
	requires javafx.controls;
	requires javafx.fxml;

	opens org.idaesbasic to javafx.fxml;
	opens org.idaesbasic.controllers to javafx.fxml;
	opens org.idaesbasic.controllers.todolist to javafx.fxml;
	
	exports org.idaesbasic;
	exports org.idaesbasic.controllers;
	exports org.idaesbasic.controllers.todolist;
}