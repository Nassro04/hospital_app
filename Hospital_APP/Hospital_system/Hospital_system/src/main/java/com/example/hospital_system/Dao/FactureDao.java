package com.example.hospital_system.Dao;

import com.example.hospital_system.Database.DatabaseConnection;
import com.example.hospital_system.Models.Client;
import com.example.hospital_system.Models.Invoice;
import com.example.hospital_system.Models.InvoiceItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FactureDao {

    public boolean addFactureQuery(Invoice facture) {
        String sql = "INSERT INTO invoices (id,nom,prenom,cin,clientId,date) Values (?,?,?,?,?,NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, facture.getId());
            statement.setString(2, facture.getNom());
            statement.setString(3, facture.getPrenom());
            statement.setString(4, facture.getCin());
            statement.setInt(5, facture.getClientId());

            int result = statement.executeUpdate();

            if (result > 0) {
                return true;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public ObservableList<Invoice> getFacturesQuery() {

        ObservableList<Invoice> list = FXCollections.observableArrayList();

        String sql = "SELECT i.*, COALESCE(SUM(it.quantity * it.unitPrice),0) AS total_ht FROM invoices i LEFT JOIN invoiceitems it ON i.id = it.invoiceId GROUP BY i.id";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {

                Invoice inv = new Invoice(
                        rs.getString("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("cin"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("clientId"));

                inv.setTotalHt(rs.getDouble("total_ht"));

                list.add(inv);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void addItemQuery(InvoiceItem item, String invoiceId) {

        String sql = "INSERT INTO invoiceitems (invoiceId,designation, quantity, unitPrice) VALUES (?, ?, ?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoiceId);
            ps.setString(2, item.getDesignation());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getUnitPrice());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateItemQuery(InvoiceItem item) {
        String sql = "UPDATE invoiceitems SET designation = ?, quantity = ?, unitPrice = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getDesignation());
            ps.setInt(2, item.getQuantity());
            ps.setDouble(3, item.getUnitPrice());
            ps.setInt(4, item.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteItemQuery(int id) {
        String sql = "DELETE FROM invoiceitems WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * public void updateFactureTotalQuery(String invoiceId){
     * String sql = "UPDATE invoices SET ";
     * 
     * try(Connection conn = DatabaseConnection.getConnection();
     * PreparedStatement ps = conn.prepareStatement(sql)) {
     * 
     * ps.setString(1, invoiceId);
     * ps.setString(2, item.getDesignation());
     * ps.setInt(3, item.getQuantity());
     * ps.setDouble(4, item.getUnitPrice());
     * 
     * ps.executeUpdate();
     * 
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     */

    public InvoiceItem getItemById(int id) {

        InvoiceItem item = null;

        String sql = "SELECT * FROM invoiceitems WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                item = new InvoiceItem(
                        rs.getString("designation"),
                        rs.getInt("quantity"),
                        rs.getDouble("unitPrice"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public Invoice getFactureById(String id) {
        Invoice facture = null;
        String sql = "SELECT * FROM invoices WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                facture = new Invoice(
                        rs.getString("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("cin"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("clientId"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return facture;
    }

    public List<InvoiceItem> findItemsByFactureIdQuery(String factureId) {

        List<InvoiceItem> list = new ArrayList<>();

        String sql = "SELECT * FROM invoiceitems WHERE invoiceId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, factureId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InvoiceItem item = new InvoiceItem(
                        rs.getInt("id"),
                        rs.getString("designation"),
                        rs.getInt("quantity"),
                        rs.getDouble("unitPrice"));

                list.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean deleteFactureQuery(String id) {
        // First delete items
        String deleteItemsSql = "DELETE FROM invoiceitems WHERE invoiceId = ?";
        String deleteInvoiceSql = "DELETE FROM invoices WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psItems = conn.prepareStatement(deleteItemsSql);
                    PreparedStatement psInvoice = conn.prepareStatement(deleteInvoiceSql)) {

                psItems.setString(1, id);
                psItems.executeUpdate();

                psInvoice.setString(1, id);
                int result = psInvoice.executeUpdate();

                conn.commit();
                return result > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFactureQuery(Invoice invoice) {
        String sql = "UPDATE invoices SET nom = ?, prenom = ?, cin = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, invoice.getNom());
            statement.setString(2, invoice.getPrenom());
            statement.setString(3, invoice.getCin());
            statement.setString(4, invoice.getId());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<Invoice> getFactureByClient(Client client) {
        ObservableList<Invoice> list = FXCollections.observableArrayList();

        String sql = "SELECT i.*, COALESCE(SUM(it.quantity * it.unitPrice),0) AS total_ht FROM invoices i LEFT JOIN invoiceitems it ON i.id = it.invoiceId WHERE clientId = ? GROUP BY i.id";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, client.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Invoice inv = new Invoice(
                        rs.getString("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("cin"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("clientId"));

                inv.setTotalHt(rs.getDouble("total_ht"));

                list.add(inv);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

}
