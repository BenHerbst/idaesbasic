package ainm;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
    	try {
    		Parent root = FXMLLoader.load(getClass().getResource("/MainView.fxml"));
        	Scene scene = new Scene(root, 640, 480);
        	stage.setScene(scene);
        	stage.setTitle("Ainm - 0.01 / Todolist");
        	stage.setMaximized(true);
        	stage.show();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    public static void main(String[] args) {
        launch();
    }
}