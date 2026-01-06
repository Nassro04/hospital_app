package com.example.hospital_system.Database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseMigration {

    public static void updateSchema() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null)
                return;

            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "invoices", "clientId");

            if (!rs.next()) {
                System.out.println("⚠️ Column 'clientId' is missing. Adding it now...");
                try (Statement stmt = conn.createStatement()) {
                    String sql = "ALTER TABLE invoices ADD COLUMN clientId INT";
                    stmt.executeUpdate(sql);
                    System.out.println("✅ Column 'clientId' added successfully.");
                }
            } else {
                System.out.println("✅ Database schema is up to date.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
