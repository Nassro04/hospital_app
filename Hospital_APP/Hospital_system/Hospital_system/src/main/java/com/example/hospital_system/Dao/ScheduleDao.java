package com.example.hospital_system.Dao;

import com.example.hospital_system.Database.DatabaseConnection;
import com.example.hospital_system.Models.ScheduleEvent;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDao {

    public ScheduleDao() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS schedule (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "description TEXT, " +
                "start_time DATETIME NOT NULL, " +
                "end_time DATETIME NOT NULL, " +
                "color VARCHAR(20) DEFAULT '#2196F3')";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addEvent(ScheduleEvent event) {
        String sql = "INSERT INTO schedule (title, description, start_time, end_time, color) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(event.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(event.getEndTime()));
            ps.setString(5, event.getColor());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(ScheduleEvent event) {
        String sql = "UPDATE schedule SET title = ?, description = ?, start_time = ?, end_time = ?, color = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(event.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(event.getEndTime()));
            ps.setString(5, event.getColor());
            ps.setInt(6, event.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(int id) {
        String sql = "DELETE FROM schedule WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ScheduleEvent> getEventsForWeek(LocalDateTime startOfWeek, LocalDateTime endOfWeek) {
        List<ScheduleEvent> list = new ArrayList<>();
        String sql = "SELECT * FROM schedule WHERE start_time >= ? AND start_time < ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startOfWeek));
            ps.setTimestamp(2, Timestamp.valueOf(endOfWeek));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ScheduleEvent(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time").toLocalDateTime(),
                        rs.getString("color")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
