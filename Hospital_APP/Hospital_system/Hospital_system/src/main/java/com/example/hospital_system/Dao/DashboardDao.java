package com.example.hospital_system.Dao;

import com.example.hospital_system.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class DashboardDao {

    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Count total clients
            String clientsSql = "SELECT COUNT(*) as count FROM clients";
            try (PreparedStatement ps = conn.prepareStatement(clientsSql)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    stats.put("totalClients", rs.getInt("count"));
                }
            }

            // Count total invoices
            String invoicesSql = "SELECT COUNT(*) as count FROM invoices";
            try (PreparedStatement ps = conn.prepareStatement(invoicesSql)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    stats.put("totalInvoices", rs.getInt("count"));
                }
            }

            // Count total rendez-vous
            String rdvSql = "SELECT COUNT(*) as count FROM rendez_vous";
            try (PreparedStatement ps = conn.prepareStatement(rdvSql)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    stats.put("totalRendezVous", rs.getInt("count"));
                }
            }

            // Calculate total revenue from invoices
            String revenueSql = "SELECT COALESCE(SUM(quantity * unitPrice), 0) as total FROM invoiceitems";
            try (PreparedStatement ps = conn.prepareStatement(revenueSql)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    stats.put("totalRevenue", (int) rs.getDouble("total"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Return default values on error
            stats.put("totalClients", 0);
            stats.put("totalInvoices", 0);
            stats.put("totalRendezVous", 0);
            stats.put("totalRevenue", 0);
        }

        return stats;
    }

    public Map<Integer, Integer> getMonthlyAppointmentStats() {
        Map<Integer, Integer> stats = new HashMap<>();
        // Get count of appointments per month for current year
        String sql = "SELECT MONTH(date) as month_num, COUNT(*) as count FROM rendez_vous GROUP BY MONTH(date) ORDER BY MONTH(date)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                stats.put(rs.getInt("month_num"), rs.getInt("count"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    public java.util.List<com.example.hospital_system.Models.RendezVous> getUpcomingAppointments() {
        java.util.List<com.example.hospital_system.Models.RendezVous> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM rendez_vous WHERE date >= CURRENT_DATE() ORDER BY date ASC LIMIT 5";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new com.example.hospital_system.Models.RendezVous(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("telephone"),
                        rs.getString("cin"),
                        rs.getString("genre"),
                        rs.getInt("age"),
                        rs.getDate("date").toLocalDate()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, Integer> getAppointmentsByDepartment() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT department, COUNT(*) as count FROM rendez_vous GROUP BY department";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String dept = rs.getString("department");
                if (dept == null || dept.isEmpty())
                    dept = "General";
                stats.put(dept, rs.getInt("count"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Default mock data if DB fails or empty
            stats.put("General", 0);
        }
        return stats;
    }

    public Map<String, Number> getWaitingTimesByDepartment() {
        Map<String, Number> stats = new HashMap<>();
        // Calculate "Wait Time" based on appointment density (e.g., 15 mins per
        // appointment)
        String sql = "SELECT department, COUNT(*) * 15 as wait_time FROM rendez_vous GROUP BY department";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String dept = rs.getString("department");
                if (dept == null || dept.isEmpty())
                    dept = "General";
                stats.put(dept, rs.getInt("wait_time"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    public Map<String, Integer> getStaffByDivision() {
        Map<String, Integer> stats = new HashMap<>();
        // Count distinct doctors per department
        String sql = "SELECT department, COUNT(DISTINCT doctor) as doc_count FROM rendez_vous GROUP BY department";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String dept = rs.getString("department");
                if (dept == null || dept.isEmpty())
                    dept = "General";
                stats.put(dept, rs.getInt("doc_count"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
}
