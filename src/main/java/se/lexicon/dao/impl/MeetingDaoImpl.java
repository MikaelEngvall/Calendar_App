package se.lexicon.dao.impl;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.lexicon.dao.MeetingCalendarDao;
import se.lexicon.dao.MeetingDao;
import se.lexicon.dao.impl.db.MeetingCalendarDBConnection;
import se.lexicon.exception.MySQLException;
import se.lexicon.model.Meeting;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class MeetingDaoImpl implements MeetingDao {

    private static final Logger log = LogManager.getLogger(MeetingDao.class);

/*    private Connection connection;

    public MeetingDaoImpl(Connection connection) {
        this.connection = connection;
    }*/ // TODO: 03/10/2023 Re-introduce this?

    @Override
    public Meeting createMeeting(Meeting meeting) {
        String query = "INSERT INTO meetings(title, start_time, end_time, _description, calendar_id) VALUES(?, ?, ?, ?, ?)";
        log.info("Creating meeting: {}", meeting.getTitle());
        try (
                Connection connection = MeetingCalendarDBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, meeting.getTitle());
            preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(meeting.getStartTime()));
            preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(meeting.getEndTime()));
            preparedStatement.setString(4, meeting.getDescription());
            preparedStatement.setInt(5, meeting.getCalendar().getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                String errorMessage = "Creating meeting failed, now rows affected.";
                log.error(errorMessage + meeting.getTitle());
                throw new MySQLException(errorMessage);
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int meetingId = generatedKeys.getInt(1);
                    meeting.setId(meetingId);
                    new Meeting(
                            meetingId,
                            meeting.getTitle(),
                            meeting.getStartTime(),
                            meeting.getEndTime(),
                            meeting.getDescription(),
                            meeting.getCalendar());

                    return meeting;

                } else {
                    String errorMessage = "Creating meeting failed, no ID obtained.";
                    log.error(errorMessage + meeting.getTitle());
                    throw new MySQLException(errorMessage);
                }
            }

        } catch (SQLException e) {
            String errorMessage = "Error occurred while creating meeting: ";
            e.printStackTrace();
            log.error(errorMessage + meeting.getTitle());
            throw new MySQLException(errorMessage + meeting.getTitle(), e);
        }
    }

    @Override
    public Optional<Meeting> findById(int meetingId) {
        MeetingCalendarDao meetingCalendarDao = new MeetingCalendarDaoImpl();
        String query = "SELECT * FROM meetings WHERE id = ?";
        try (
                Connection connection = MeetingCalendarDBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {

            preparedStatement.setInt(1, meetingId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int foundId = resultSet.getInt("id");
                String foundTitle = resultSet.getString("title");
                LocalDateTime foundStart_time = resultSet.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime foundEnd_time = resultSet.getTimestamp("end_time").toLocalDateTime();
                String foundDescription = resultSet.getString("_description");

                Meeting meeting = new Meeting(foundId, foundTitle, foundStart_time, foundEnd_time, foundDescription);
                return Optional.of(meeting);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new MySQLException("Error occurred while trying to find meeting by id: " + meetingId, e);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Meeting> findAllMeetingsByCalendarId(int calendarId) {
        String query = "SELECT * FROM meetings WHERE calendar_id = ?";
        try (
                Connection connection = MeetingCalendarDBConnection.getConnection();
                PreparedStatement preparedStatements = connection.prepareStatement(query)
        ) {

            preparedStatements.setInt(1, calendarId);

            ResultSet resultSet = preparedStatements.executeQuery();

            Collection<Meeting> foundByCalendarId = new ArrayList<>();
            while (resultSet.next()) {
                int foundId = resultSet.getInt("id");
                String foundTitle = resultSet.getString("title");
                LocalDateTime foundStart_time = resultSet.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime foundEnd_time = resultSet.getTimestamp("end_time").toLocalDateTime();
                String foundDescription = resultSet.getString("_description");

                Meeting meeting = new Meeting(foundId, foundTitle, foundStart_time, foundEnd_time, foundDescription);
                foundByCalendarId.add(meeting);
            }

            return foundByCalendarId;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new MySQLException("Error occurred while trying find all meetings by calendar id: " + calendarId, e);
        }
    }


    @Override
    public boolean deleteMeeting(int meetingId) {
        String query = "DELETE FROM meetings WHERE id = ?";
        try (
                Connection connection = MeetingCalendarDBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {

            preparedStatement.setInt(1, meetingId);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new MySQLException("Deleting Meeting failed, no rows affected");
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new MySQLException("Error occurred while trying to delete meeting by id: " + meetingId, e);
        }
    }
}
