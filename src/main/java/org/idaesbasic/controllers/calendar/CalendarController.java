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

import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.idaesbasic.models.CalendarEventItem;
import org.idaesbasic.models.CalendarModel;
import org.idaesbasic.models.ViewModel;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.DetailedDayView;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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

    public ViewModel viewModel = new ViewModel();
    
    public CalendarModel calendarModel = new CalendarModel();
    
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
    private AnchorPane pane;

    @FXML
    void initialize() throws IOException {
        ToggleButton[] initToggleButtons = {switchToDayButton, switchToMonthButton, switchToWeekButton, switchToYearButton};
        toggleButtons = initToggleButtons;
        switchCalendarView("DayView");
    }

    void switchCalendarView(String viewName) throws IOException {
        // Remove old one
        viewContainer.getChildren().clear();
        // Load day view
        Node view = FXMLLoader.load(getClass().getResource("/fxml/views/calendar/views/" + viewName + ".fxml"));
        // Add day view
        calendarView = (DateControl) ((AnchorPane) view).getChildren().get(0);
        // Set resize consents
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
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
                for (int i = 0; i < calendarModel.getEvents().size(); i++) {
                    CalendarEventItem calendarEventItem = calendarModel.getEvents().get(i);
                    if(calendarEventItem.getId() == evt.getEntry().getId()) {
                        calendarModel.removeEvent(i);
                    }
                }
                // Check if the entry is deleted
                if (evt.getEntry().getCalendar() != null) {
                    //Add a calendareventitem with the summary and start / and end calendar to the events list
                    calendarModel.addEvent(new CalendarEventItem(evt.getEntry().getTitle(), startTime, endTime, evt.getEntry().getId()));
                }
            }
        };
        viewContainer.getChildren().add(view);
        ObservableList<CalendarEventItem> calendarEventList = calendarModel.getEvents();
        for(CalendarEventItem eventItem:calendarEventList) {
            // Create a new Calendar entry, with the given props from the event item
            Entry calendarEntry = new Entry(eventItem.summary);
            calendarEntry.setId(eventItem.getId());
            LocalDateTime startTime = eventItem.startDate.getTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime endTime = eventItem.endDate.getTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            calendarEntry.setInterval(startTime, endTime);
            calendarView.getCalendars().get(0).addEntry(calendarEntry);
        }
        calendarView.getCalendars().get(0).addEventHandler(handler);
    }

    @FXML
    void switchToDay(ActionEvent event) throws IOException {
        selectToggleButton(switchToDayButton);
        switchCalendarView("DayView");
    }

    @FXML
    void switchToMonth(ActionEvent event) throws IOException {
        selectToggleButton(switchToMonthButton);
        switchCalendarView("MonthView");
    }

    @FXML
    void switchToWeek(ActionEvent event) throws IOException {
        selectToggleButton(switchToWeekButton);
        switchCalendarView("WeekView");
    }

    @FXML
    void switchToYear(ActionEvent event) throws IOException {
        selectToggleButton(switchToYearButton);
        switchCalendarView("YearView");
    }

    @FXML
    void deleteCurrentFileAction(ActionEvent event) throws IOException {
        viewModel.deleteCurrentFile();
        // Remove the todo panel
        TabPane tabPane = ((TabPane) pane.getParent().getParent());
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.getSelectedItem().setContent(null);
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
        viewModel.setOpenedFile(Paths.get(filePath));
    }
    
    public void saveCurrentFile() throws ValidationException, IOException {
        //Save to the current file
        saveFile(viewModel.getOpenedFile().toString());
    }
    
    public void saveFile(String filePath) throws ValidationException, IOException {
        //Create an ics calendar
        net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
        //Create an entry for each calendar entry in the ics calendar
        for (int i = 0; i < calendarModel.getEvents().size(); i++) {
            CalendarEventItem calendarEventItem = calendarModel.getEvents().get(i);
            String eventName = calendarEventItem.summary;
            DateTime start = new DateTime(calendarEventItem.startDate.getTime());
            DateTime end = new DateTime(calendarEventItem.endDate.getTime());
            VEvent event = new VEvent(start, end, eventName);
            event.getProperties().add(new Uid(calendarEventItem.getId()));
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
