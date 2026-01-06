package com.example.hospital_system.Dao;

import com.example.hospital_system.Database.DatabaseConnection;
import com.example.hospital_system.Models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public User getAdminAccount(String username,String password){
        String sqlCommande = "SELECT * FROM users WHERE username = ? and password = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement statement = conn.prepareStatement(sqlCommande)){

            statement.setString(1,username);
            statement.setString(2,password);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                updateLoginStatus(userId);

                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("loggedIn")
                );
            }

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }

        return null;
    }



    public void updateLoginStatus(int userId){

        String sql = "UPDATE users SET  loggedIn = true WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected < 1){
                System.out.println("Something went wrong!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}



