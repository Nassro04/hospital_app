package com.example.hospital_system.Dao;

import com.example.hospital_system.Database.DatabaseConnection;
import com.example.hospital_system.Models.RendezVous;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class RendezVousDao {

    public RendezVousDao() {
        checkColumns();
    }

    private void checkColumns() {
        // Ensure all new columns exist
        addColumnIfNotExists("department", "VARCHAR(255) DEFAULT 'General'");
        addColumnIfNotExists("doctor", "VARCHAR(255) DEFAULT 'Unknown'");
        addColumnIfNotExists("time", "VARCHAR(50) DEFAULT '09:00'");
    }

    private void addColumnIfNotExists(String colName, String colDef) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            try {
                stmt.execute("SELECT " + colName + " FROM rendez_vous LIMIT 1");
            } catch (Exception e) {
                // Column doesn't exist
                stmt.execute("ALTER TABLE rendez_vous ADD COLUMN " + colName + " " + colDef);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addRendezVousQuery(String nom, String prenom, String telephone, String cin, String genre, int age,
            LocalDate date, String department, String doctor, String time) {

        String sql = "INSERT INTO rendez_vous (nom,prenom,telephone,cin,genre,age,date,department,doctor,time) Values (?,?,?,?,?,?,?,?,?,?)";

        java.sql.Date sqlDate = java.sql.Date.valueOf(date);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setString(3, telephone);
            statement.setString(4, cin);
            statement.setString(5, genre);
            statement.setInt(6, age);
            statement.setDate(7, sqlDate);
            statement.setString(8, department);
            statement.setString(9, doctor);
            statement.setString(10, time);

            int result = statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ObservableList<RendezVous> getRendezVousQuery() {
        ObservableList<RendezVous> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM rendez_vous";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String dept = "General";
                String doctor = "Unknown";
                String time = "09:00";
                try {
                    dept = rs.getString("department");
                } catch (Exception e) {
                }
                try {
                    doctor = rs.getString("doctor");
                } catch (Exception e) {
                }
                try {
                    time = rs.getString("time");
                } catch (Exception e) {
                }

                if (dept == null)
                    dept = "General";
                if (doctor == null)
                    doctor = "Unknown";
                if (time == null)
                    time = "09:00";

                list.add(new RendezVous(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("telephone"),
                        rs.getString("cin"),
                        rs.getString("genre"),
                        rs.getInt("age"),
                        rs.getDate("date").toLocalDate(),
                        dept, doctor, time));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int updateQuery(RendezVous rendezVous) {
        String sql = "UPDATE rendez_vous SET nom = ?, prenom = ?, telephone = ?, " +
                "cin = ?, genre = ?, age = ?, date = ?, department = ?, doctor = ?, time = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, rendezVous.getNom());
            statement.setString(2, rendezVous.getPrenom());
            statement.setString(3, rendezVous.getTelephone());
            statement.setString(4, rendezVous.getCin());
            statement.setString(5, rendezVous.getGenre());
            statement.setInt(6, rendezVous.getAge());
            statement.setDate(7, Date.valueOf(rendezVous.getDate()));
            statement.setString(8, rendezVous.getDepartment());
            statement.setString(9, rendezVous.getDoctor());
            statement.setString(10, rendezVous.getTime());
            statement.setInt(11, rendezVous.getId());

            return statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean deleteQuery(RendezVous rendezVous) {
        String sql = "DELETE FROM rendez_vous WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, rendezVous.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
