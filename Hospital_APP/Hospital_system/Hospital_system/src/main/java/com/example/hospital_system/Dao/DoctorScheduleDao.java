package com.example.hospital_system.Dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.example.hospital_system.Database.DatabaseConnection;
import com.example.hospital_system.Models.DoctorSchedule;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DoctorScheduleDao {

    public ObservableList<DoctorSchedule> getSchedulesByDoctorId(int doctorId) {
        ObservableList<DoctorSchedule> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM doctor_schedules WHERE doctor_id = ? ORDER BY " +
                "FIELD(day_of_week, 'LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI', 'DIMANCHE'), start_time";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, doctorId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(new DoctorSchedule(
                        rs.getInt("id"),
                        rs.getInt("doctor_id"),
                        rs.getString("day_of_week"),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime(),
                        rs.getBoolean("is_available")));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean addSchedule(DoctorSchedule schedule) {
        String sql = "INSERT INTO doctor_schedules (doctor_id, day_of_week, start_time, end_time, is_available) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, schedule.getDoctorId());
            statement.setString(2, schedule.getDayOfWeek());
            statement.setTime(3, Time.valueOf(schedule.getStartTime()));
            statement.setTime(4, Time.valueOf(schedule.getEndTime()));
            statement.setBoolean(5, schedule.isAvailable());

            return statement.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSchedule(DoctorSchedule schedule) {
        String sql = "UPDATE doctor_schedules SET day_of_week = ?, start_time = ?, end_time = ?, is_available = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, schedule.getDayOfWeek());
            statement.setTime(2, Time.valueOf(schedule.getStartTime()));
            statement.setTime(3, Time.valueOf(schedule.getEndTime()));
            statement.setBoolean(4, schedule.isAvailable());
            statement.setInt(5, schedule.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSchedule(int scheduleId) {
        String sql = "DELETE FROM doctor_schedules WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, scheduleId);
            return statement.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean hasScheduleConfigured(int doctorId) {
        String sql = "SELECT COUNT(*) FROM doctor_schedules WHERE doctor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, doctorId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<LocalTime> getAvailableTimeSlots(int doctorId, LocalDate date) {
        ObservableList<LocalTime> availableSlots = FXCollections.observableArrayList();
        String dayOfWeek = getDayOfWeekInFrench(date.getDayOfWeek().toString());

        String sql = "SELECT s.start_time, s.end_time " +
                "FROM doctor_schedules s " +
                "WHERE s.doctor_id = ? AND s.day_of_week = ? AND s.is_available = TRUE";

        boolean hasConfig = hasScheduleConfigured(doctorId);
        boolean foundSlots = false;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, doctorId);
            statement.setString(2, dayOfWeek);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                foundSlots = true;
                LocalTime start = rs.getTime("start_time").toLocalTime();
                LocalTime end = rs.getTime("end_time").toLocalTime();

                // Generate 30-minute time slots
                LocalTime current = start;
                while (current.isBefore(end)) {
                    if (!isTimeSlotBooked(doctorId, date, current)) {
                        availableSlots.add(current);
                    }
                    current = current.plusMinutes(30);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Fallback: If no configuration exists for this doctor AT ALL, assume 09:00 -
        // 17:00
        if (!hasConfig && !foundSlots) {
            // Default Business Hours: 09:00 to 17:00
            LocalTime start = LocalTime.of(9, 0);
            LocalTime end = LocalTime.of(17, 0);

            LocalTime current = start;
            while (current.isBefore(end)) {
                if (!isTimeSlotBooked(doctorId, date, current)) {
                    availableSlots.add(current);
                }
                current = current.plusMinutes(30);
            }
        }

        return availableSlots;
    }

    private boolean isTimeSlotBooked(int doctorId, LocalDate date, LocalTime time) {
        // Get doctor's full name first
        String doctorName = getDoctorFullName(doctorId);
        if (doctorName == null)
            return false;

        String sql = "SELECT COUNT(*) FROM rendez_vous " +
                "WHERE doctor = ? AND date = ? AND time = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, doctorName);
            statement.setDate(2, Date.valueOf(date));
            statement.setString(3, time.format(DateTimeFormatter.ofPattern("HH:mm")));

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String getDoctorFullName(int doctorId) {
        String sql = "SELECT first_name, last_name FROM doctors WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, doctorId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return "Dr. " + rs.getString("first_name") + " " + rs.getString("last_name");
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getDayOfWeekInFrench(String englishDay) {
        switch (englishDay) {
            case "MONDAY":
                return "LUNDI";
            case "TUESDAY":
                return "MARDI";
            case "WEDNESDAY":
                return "MERCREDI";
            case "THURSDAY":
                return "JEUDI";
            case "FRIDAY":
                return "VENDREDI";
            case "SATURDAY":
                return "SAMEDI";
            case "SUNDAY":
                return "DIMANCHE";
            default:
                return englishDay;
        }
    }
}