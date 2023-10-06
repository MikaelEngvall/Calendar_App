package se.lexicon;

import se.lexicon.controller.CalendarController;
import se.lexicon.dao.MeetingCalendarDao;
import se.lexicon.dao.MeetingDao;
import se.lexicon.dao.UserDao;
import se.lexicon.dao.impl.MeetingCalendarDaoImpl;
import se.lexicon.dao.impl.MeetingDaoImpl;
import se.lexicon.dao.impl.UserDaoImpl;
import se.lexicon.view.CalendarConsoleUI;
import se.lexicon.view.CalendarView;

public class App {
    public static void main(String[] args) {
        System.setProperty("log4j.configurationFile", "log4j2.properties");


//        Connection mysqlConnection = MeetingCalendarDBConnection.getConnection();
        UserDao userDao = new UserDaoImpl();
        MeetingCalendarDao meetingCalendarDao = new MeetingCalendarDaoImpl();
        CalendarView view = new CalendarConsoleUI(meetingCalendarDao);
        MeetingDao meetingDao = new MeetingDaoImpl();

        CalendarController controller = new CalendarController(view, userDao, meetingCalendarDao, meetingDao);
        controller.run();
    }
}
