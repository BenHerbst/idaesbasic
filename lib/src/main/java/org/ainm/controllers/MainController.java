package org.ainm.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

public class MainController {

    @FXML
    private Button date_button;

    @FXML
    private Button time_button;

    @FXML
    private TreeView file_explorer;

    @FXML
    private void initialize() {
    	//Set date from date button
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