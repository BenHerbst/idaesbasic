package ainm;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

public class FXMLController {

    @FXML
    private Button date_button;

    @FXML
    private Button time_button;

    @FXML
    private void initialize() {
    	date_button.setText(LocalDate.now().toString());
    	Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				//Updates the time button every 2 seconds
				int minutes = LocalTime.now().getMinute();
				int hours = LocalTime.now().getHour();
				Platform.runLater(() -> time_button.setText(Integer.toString(hours) + ((minutes < 10) ? ":0" : ":") + Integer.toString(minutes)));
			}
        }, 0, 2000);
    }
}