package org.idaesbasic.controllers.calendar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.controlsfx.control.textfield.AutoCompletionBinding.AutoCompletionEvent;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DetailedDayView;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.effect.Effect;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;
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
    
    String openedFile;
    
    List<CalendarEventItem> events = new ArrayList<>();
    
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
    void switchToDay(ActionEvent event) {

    }

    @FXML
    void switchToMonth(ActionEvent event) {

    }

    @FXML
    void switchToWeek(ActionEvent event) {

    }

    @FXML
    void switchToYear(ActionEvent event) {

    }

    public void saveFile() throws ValidationException, IOException {
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
        FileOutputStream fout = new FileOutputStream("mycalendar.ics");
        CalendarOutputter outputter = new CalendarOutputter();
        outputter.output(icsCalendar, fout);
    }
}
