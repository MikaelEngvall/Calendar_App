package se.lexicon.model;

import java.time.LocalDateTime;
import java.util.Calendar;

public class Meeting {

    private int id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private MeetingCalendar calendar;

    public Meeting(int id, String title, LocalDateTime startTime, LocalDateTime endTime, String description) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    public Meeting(int id, String title, LocalDateTime startTime, LocalDateTime endTime, String description, MeetingCalendar calendar) {
        this(id, title, startTime, endTime, description);
        this.calendar = calendar;
    }

    public Meeting(String title, LocalDateTime startTime, LocalDateTime endTime, String description) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    public Meeting(String title, LocalDateTime startTime, LocalDateTime endTime, String description, MeetingCalendar calendar) {
        this(title, startTime, endTime, description);
        this.calendar = calendar;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getDescription() {
        return description;
    }

    public MeetingCalendar getCalendar() {
        return calendar;
    }

    public String meetingInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Meeting Info:").append("\n");
        stringBuilder.append("Id:").append(id).append("\n");
        stringBuilder.append("Title:").append("\n");
        stringBuilder.append("Starting time:").append("\n");
        stringBuilder.append("Ending time:").append("\n");
        stringBuilder.append("Description:").append("\n");
        stringBuilder.append("Calendar:").append("\n");
        return stringBuilder.toString();
    }
    
    private void timeValidation() {
        // TODO: 02/10/2023 implement later
    }


}
