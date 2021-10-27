package org.idaesbasic.controllers.calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DetailedDayView;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class CalendarController {
    @FXML
    private DetailedDayView calendarView;

    @FXML
    void initialize() {
        // Create work calendars
        Calendar meetingsCalendar = new Calendar("Meetings");
        Calendar pausesCalendar = new Calendar("Pauses");
        Calendar vacationsCalendar = new Calendar("Vacation");
        meetingsCalendar.setStyle(Style.STYLE2);
        pausesCalendar.setStyle(Style.STYLE1);
        vacationsCalendar.setStyle(Style.STYLE3);
        CalendarSource workCalendarSource = new CalendarSource("Work");
        workCalendarSource.getCalendars().addAll(meetingsCalendar, pausesCalendar, vacationsCalendar);
        // Create private calendars
        Calendar birthdaysCalendar = new Calendar("Birthdays");
        Calendar appointmentsCalendar = new Calendar("Appointments");
        Calendar eventsCalendar = new Calendar("Events");
        birthdaysCalendar.setStyle(Style.STYLE5);
        appointmentsCalendar.setStyle(Style.STYLE6);
        eventsCalendar.setStyle(Style.STYLE4);
        CalendarSource privateCalendarSource = new CalendarSource("Private");
        privateCalendarSource.getCalendars().addAll(birthdaysCalendar, appointmentsCalendar, eventsCalendar);
        // Create sport calendars
        Calendar cyclingCalendar = new Calendar("Cylcling");
        Calendar joggingCalendar = new Calendar("Jogging");
        Calendar swimingCalendar = new Calendar("Swiming");
        cyclingCalendar.setStyle(Style.STYLE4);
        joggingCalendar.setStyle(Style.STYLE7);
        swimingCalendar.setStyle(Style.STYLE2);
        CalendarSource sportCalendarSource = new CalendarSource("Sport");
        sportCalendarSource.getCalendars().addAll(cyclingCalendar, joggingCalendar, swimingCalendar);
        // Add calendars to view
        calendarView.getCalendarSources().addAll(workCalendarSource, privateCalendarSource, sportCalendarSource);
        // Update current calendar time
        calendarView.setRequestedTime(LocalTime.now());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//              Update the calendar view current day and time every 10 secounds
                Platform.runLater(() -> {
                    calendarView.setToday(LocalDate.now());
                    calendarView.setTime(LocalTime.now());
                });
            }
        }, 0, 2000);
    }
}
