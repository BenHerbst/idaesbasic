package org.idaesbasic.controllers.calendar;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.DetailedDayView;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.validate.ValidationException;

public class CalendarController {
    
    class CalendarEventItem {
        java.util.Calendar startDate;
        java.util.Calendar endDate;
        String summary;
        String id;
        
        public CalendarEventItem(String summary, java.util.Calendar startDate, java.util.Calendar endDate, String id) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.summary = summary;
            this.id = id;
        }
    }
    
    public Path openedFile;
    
    List<CalendarEventItem> events = new ArrayList<>();
    
    private DateControl calendarView;
    
    @FXML
    private ToggleButton switchToDayButton;

    @FXML
    private ToggleButton switchToMonthButton;

    @FXML
    private ToggleButton switchToWeekButton;

    @FXML
    private ToggleButton switchToYearButton;
    
    @FXML
    private ToggleButton[] toggleButtons;

    @FXML
    private AnchorPane viewContainer;

    @FXML
    void initialize() throws IOException {
        ToggleButton[] initToggleButtons = {switchToDayButton, switchToMonthButton, switchToWeekButton, switchToYearButton};
        toggleButtons = initToggleButtons;
        // Load day view
        Node dayView = FXMLLoader.load(getClass().getResource("/fxml/views/calendar/views/DayView.fxml"));
        // Add day view
        calendarView = (DateControl) ((AnchorPane) dayView).getChildren().get(0);
        viewContainer.getChildren().add(dayView);
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
        EventHandler<CalendarEvent> handler = evt -> {
            if (evt.getEventType().getSuperType() == CalendarEvent.ENTRY_CHANGED) {
                //If an event entry got added, add it to the ics/ical list to save to ical
                //Create two calendars to save date and time
                java.util.Calendar startTime = new GregorianCalendar();
                java.util.Calendar endTime = new GregorianCalendar();
                //Set the calendars date in time to the start and end millis of the entry
                startTime.setTimeInMillis(evt.getEntry().getStartMillis());
                endTime.setTimeInMillis(evt.getEntry().getEndMillis());
                //Remove current entry, to re-add it
                for (int i = 0; i < events.size(); i++) {
                    CalendarEventItem calendarEventItem = events.get(i);
                    if(calendarEventItem.id == evt.getEntry().getId()) {
                        events.remove(i);
                    }
                }
                //Add a calendareventitem with the summary and start / and end calendar to the events list
                events.add(new CalendarEventItem(evt.getEntry().getTitle(), startTime, endTime, evt.getEntry().getId()));
                System.out.println(events);
            }
        };
        calendarView.getCalendars().get(0).addEventHandler(handler);
    }

    @FXML
    void switchToDay(ActionEvent event) throws IOException {
        selectToggleButton(switchToDayButton);
        // Remove old one
        viewContainer.getChildren().remove(0);
        // Load day view
        Node dayView = FXMLLoader.load(getClass().getResource("/fxml/views/calendar/views/DayView.fxml"));
        // Add day view
        calendarView = (DateControl) ((AnchorPane) dayView).getChildren().get(0);
        AnchorPane.setLeftAnchor(dayView, 0.0);
        AnchorPane.setRightAnchor(dayView, 0.0);
        AnchorPane.setTopAnchor(dayView, 0.0);
        AnchorPane.setBottomAnchor(dayView, 0.0);
        viewContainer.getChildren().add(dayView);
    }

    @FXML
    void switchToMonth(ActionEvent event) throws IOException {
        selectToggleButton(switchToMonthButton);
        // Remove old one
        viewContainer.getChildren().remove(0);
        // Load day view
        Node monthView = FXMLLoader.load(getClass().getResource("/fxml/views/calendar/views/MonthView.fxml"));
        // Add day view
        calendarView = (DateControl) ((AnchorPane) monthView).getChildren().get(0);
        viewContainer.getChildren().add(monthView);
    }

    @FXML
    void switchToWeek(ActionEvent event) throws IOException {
        selectToggleButton(switchToWeekButton);
        // Remove old one
        viewContainer.getChildren().remove(0);
        // Load day view
        Node weekView = FXMLLoader.load(getClass().getResource("/fxml/views/calendar/views/WeekView.fxml"));
        // Add day view
        calendarView = (DateControl) ((AnchorPane) weekView).getChildren().get(0);
        viewContainer.getChildren().add(weekView);
    }

    @FXML
    void switchToYear(ActionEvent event) throws IOException {
        selectToggleButton(switchToYearButton);
        // Remove old one
        viewContainer.getChildren().remove(0);
        // Load day view
        Node yearView = FXMLLoader.load(getClass().getResource("/fxml/views/calendar/views/YearView.fxml"));
        // Add day view
        calendarView = (DateControl) ((AnchorPane) yearView).getChildren().get(0);
        viewContainer.getChildren().add(yearView);
    }
    
    public void loadFile(String filePath) throws IOException, ParserException {
        // Read ics file
        FileInputStream icsFile = new FileInputStream(filePath);
        CalendarBuilder builder = new CalendarBuilder();
        net.fortuna.ical4j.model.Calendar calendar = builder.build(icsFile);
        // Get each event from ics file
        for(CalendarComponent component:calendar.getComponents()) {
            //Get the event from component of calendar
            VEvent event = (VEvent) component;
            //Create a calendar entry for calendarfx to display it with summary from ics
            Entry calendarEntry = new Entry(event.getSummary().getValue());
            //Get and set start and end time from ics to entry
            LocalDateTime startTime = Instant.ofEpochMilli(event.getStartDate().getDate().getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime endTime = Instant.ofEpochMilli(event.getEndDate().getDate().getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            calendarEntry.setInterval(startTime, endTime);
            //Add the entry
            calendarView.getCalendars().get(0).addEntry(calendarEntry);
        }
        //Set current file
        openedFile = Paths.get(filePath);
    }
    
    public void saveCurrentFile() throws ValidationException, IOException {
        //Save to the current file
        saveFile(openedFile.toString());
    }
    
    public void saveFile(String filePath) throws ValidationException, IOException {
        //Create an ics calendar
        net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
        //Create an entry for each calendar entry in the ics calendar
        for (int i = 0; i < events.size(); i++) {
            CalendarEventItem calendarEventItem = events.get(i);
            String eventName = calendarEventItem.summary;
            DateTime start = new DateTime(calendarEventItem.startDate.getTime());
            DateTime end = new DateTime(calendarEventItem.endDate.getTime());
            VEvent event = new VEvent(start, end, eventName);
            event.getProperties().add(new Uid(calendarEventItem.id));
            icsCalendar.getComponents().add(event);
        }
        //Set some properties
        icsCalendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        icsCalendar.getProperties().add(CalScale.GREGORIAN);
        icsCalendar.getProperties().add(Version.VERSION_2_0);
        //Save it to file
        FileOutputStream fout = new FileOutputStream(filePath);
        CalendarOutputter outputter = new CalendarOutputter();
        outputter.output(icsCalendar, fout);
    }

    void selectToggleButton(ToggleButton selectButton) {
        // Unselect all buttons
        for(int i = 0; i < toggleButtons.length; i++) {
            toggleButtons[i].setSelected(false);
        }
        // Select the button which needs to be selected
        selectButton.setSelected(true);
    }
}
