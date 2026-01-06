package com.example.hospital_system.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.hospital_system.Database.DatabaseConnection;
import com.example.hospital_system.Models.Doctor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DoctorDao {

    public ObservableList<Doctor> getDoctorsQuery() {
        ObservableList<Doctor> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM doctors";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(new Doctor(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("specialty"),
                        rs.getString("phone"),
                        rs.getString("email")));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int editDoctorQuery(Doctor doctor) {
        String sql = "UPDATE doctors SET first_name = ?, last_name = ?, specialty = ?, phone = ?, email = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, doctor.getFirstName());
            statement.setString(2, doctor.getLastName());
            statement.setString(3, doctor.getSpecialty());
            statement.setString(4, doctor.getPhone());
            statement.setString(5, doctor.getEmail());
            statement.setInt(6, doctor.getId());

            int rowsAffected = statement.executeUpdate();

            return rowsAffected;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return 0;
        }

    }

    public boolean addDoctorQuery(Doctor doctor) {
        String sql = "INSERT INTO doctors (first_name,last_name,specialty,phone,email,created_at) Values (?,?,?,?,?,NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, doctor.getFirstName());
            statement.setString(2, doctor.getLastName());
            statement.setString(3, doctor.getSpecialty());
            statement.setString(4, doctor.getPhone());
            statement.setString(5, doctor.getEmail());

            int result = statement.executeUpdate();

            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDoctorQuery(Doctor doctor) {
        String sql = "DELETE FROM doctors WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, doctor.getId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<String> getSpecialtiesQuery() {
        java.util.List<String> specialties = new java.util.ArrayList<>();
        String sql = "SELECT DISTINCT specialty FROM doctors ORDER BY specialty";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String spec = rs.getString("specialty");
                if (spec != null && !spec.isEmpty()) {
                    specialties.add(spec);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return specialties;
    }
}
