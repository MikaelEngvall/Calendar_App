package se.lexicon.controller;

import se.lexicon.dao.MeetingCalendarDao;
import se.lexicon.dao.MeetingDao;
import se.lexicon.dao.UserDao;
import se.lexicon.exception.CalendarExceptionHandler;
import se.lexicon.exception.DuplicateEntryException;
import se.lexicon.model.Meeting;
import se.lexicon.model.MeetingCalendar;
import se.lexicon.model.User;
import se.lexicon.view.CalendarView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class CalendarController {

    // dependencies
    private final CalendarView view;
    private final UserDao userDao;
    private final MeetingCalendarDao meetingCalendarDao;
    private final MeetingDao meetingDao;
    // fields
    private boolean isLoggedIn;
    private String username;


    // constructor(s)
    public CalendarController(CalendarView view, UserDao userDao, MeetingCalendarDao meetingCalendarDao, MeetingDao meetingDao) {
        this.view = view;
        this.userDao = userDao;
        this.meetingCalendarDao = meetingCalendarDao;
        this.meetingDao = meetingDao;
    }

    // methods

    public void run() {

        while (true) {
            view.displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 0:
                    try {
                        register();
                    } catch (DuplicateEntryException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    login();
                    break;
                case 2:
                    createCalendar();
                    break;
                case 3:
                    createMeeting();
                    break;
                case 4:
                    displayCalendar();
                    break;
                case 5:
                    deleteCalendar();
                    break;
                case 6:
                    isLoggedIn = false;
                    view.displayMessage("You've successfully logged out.");
                    break;
                case 7:
                    System.exit(0);
                    break;

                default:
                    view.displayWarningMessage("Invalid choice. Please select a valid option.");

            }
        }
    }

    private int getUserChoice() {
        String operationType = view.promoteString();
        int choice = -1;
        try {
            choice = Integer.parseInt(operationType);
        } catch (NumberFormatException e) {
            view.displayErrorMessage("Invalid input. Please enter a number.");
        }
        return choice;
    }

    private void register() throws DuplicateEntryException {
        try {
            //register new user
            view.displayMessage("Enter your username: ");
            String username = view.promoteString();
            User registeredUser = userDao.createUser(username);
            view.displayUser(registeredUser);
            //feedback
            view.displaySuccessMessage("Successfully registered new user!");
        } catch (Exception e) {
            CalendarExceptionHandler.handleException(e);
        }
    }

    private void login() {
        User user = view.promoteUserForm();
        try {
            //authenticate user
            isLoggedIn = userDao.authenticate(user);
            username = user.getUsername();
            //feedback
            view.displaySuccessMessage("Login successful.");
        } catch (Exception e) {
            CalendarExceptionHandler.handleException(e);
        }
    }

    private void createCalendar() {
        if (!isLoggedIn) {
            view.displayWarningMessage("You need to login first");
            return;
        }
        try {
            //creating calendar
            String calendarName = view.promoteCalendarForm();
            MeetingCalendar createdMeetingCalendar = meetingCalendarDao.createMeetingCalendar(calendarName, username);
            //feedback
            view.displaySuccessMessage("Calendar created successfully!");
            view.displayCalendar(createdMeetingCalendar);
        } catch (Exception e) {
            CalendarExceptionHandler.handleException(e);
        }
    }

    private void createMeeting() {
        if (!isLoggedIn) {
            view.displayWarningMessage("You need to login first");
            return;
        }
        try {
            //create meeting
            Meeting meeting = view.promoteMeetingForm(username);
            Meeting createdMeeting = meetingDao.createMeeting(meeting);
            //feedback
            view.displaySuccessMessage("Meeting created successfully!");
            createdMeeting.meetingInfo();
        } catch (Exception e) {
            CalendarExceptionHandler.handleException(e);
        }
    }

    private void deleteCalendar() {
        if (!isLoggedIn) {
            view.displayWarningMessage("You need to login first");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        try {
            //find all calendars by username
            ArrayList<MeetingCalendar> foundCalendars = new ArrayList<>(meetingCalendarDao.findByUserName(username));
            int i;
            for (i = 0; i < foundCalendars.size(); i++) {
                System.out.println("Enter the number for the calendar you want to remove:");
                System.out.print(i + ": ");
                System.out.println(foundCalendars.get(i).getTitle());
            }

            //menu for user to choose what calendar to delete
            int choice = scanner.nextInt();
            while (choice < 0 || choice > i) {
                System.out.println("Enter a valid number between 0 and " + i);
                choice = scanner.nextInt();
            }

            //store name of calendar for feedback at the end of the method
            String calendarName = foundCalendars.get(choice).getTitle();

            //delete all meetings related to the calendar (calendar is FOREIGN KEY to these meetings)
            Collection<Meeting> allMeetingsByCalendarId = meetingDao.findAllMeetingsByCalendarId(foundCalendars.get(choice).getId());
            if (!allMeetingsByCalendarId.isEmpty()) {
                for (Meeting meeting : meetingDao.findAllMeetingsByCalendarId(foundCalendars.get(choice).getId())) {
                    meetingDao.deleteMeeting(meeting.getId());
                }
            }
            //delete the calendar
            meetingCalendarDao.deleteCalendar(foundCalendars.get(choice).getId());
            view.displaySuccessMessage("Deletion \"" + calendarName + " was successful!");
        } catch (Exception e) {
            CalendarExceptionHandler.handleException(e);
        }
    }

    private void displayCalendar() {
        if (!isLoggedIn) {
            view.displayWarningMessage("You need to login first");
            return;
        }
        try {
            //find all calendars by username
            ArrayList<MeetingCalendar> foundCalendars = new ArrayList<>(meetingCalendarDao.findByUserName(username));
            for (MeetingCalendar foundCalendar : foundCalendars) {
                //print out calendar title
                System.out.println("**********");
                System.out.println(foundCalendar.getTitle());
                System.out.println("*********");
                //for every calendar, print out each meeting related to that calendar
                ArrayList<Meeting> foundMeetings = new ArrayList<>(meetingDao.findAllMeetingsByCalendarId(foundCalendar.getId()));
                view.displayMeetings(foundMeetings);
                System.out.println("*********");
            }
        } catch (Exception e) {
            CalendarExceptionHandler.handleException(e);
        }
    }
}