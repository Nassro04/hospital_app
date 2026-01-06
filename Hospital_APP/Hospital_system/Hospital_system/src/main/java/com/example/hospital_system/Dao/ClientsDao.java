package com.example.hospital_system.Dao;

import com.example.hospital_system.Database.DatabaseConnection;
import com.example.hospital_system.Models.Client;
import com.example.hospital_system.Models.RendezVous;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ClientsDao {


    public ObservableList<Client> getClientsQuery(){

        ObservableList<Client> list = FXCollections.observableArrayList();

        String sql = "SELECT * FROM clients";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(new Client(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("telephone"),
                        rs.getString("cin"),
                        rs.getString("email"),
                        rs.getDate("date").toLocalDate()
                ));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int editClientQuery(Client client){

        String sql = "UPDATE clients SET nom = ?, prenom = ?, telephone = ?, cin = ?, email = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, client.getNom());
            statement.setString(2, client.getPrenom());
            statement.setString(3, client.getTelephone());
            statement.setString(4, client.getCin());
            statement.setString(5, client.getEmail());
            statement.setInt(6, client.getId());


            int rowsAffected = statement.executeUpdate();

            return rowsAffected;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return 0;
        }

    }


    public boolean addClientsQuery(Client client){
        String sql = "INSERT INTO clients (nom,prenom,email,telephone,cin,date) Values (?,?,?,?,?,NOW())";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)){

            statement.setString(1,client.getNom());
            statement.setString(2,client.getPrenom());
            statement.setString(3,client.getEmail());
            statement.setString(4,client.getTelephone());
            statement.setString(5,client.getCin());

            int result = statement.executeUpdate();

            if (result > 0) {
                return true;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public boolean deleteClientQuery(Client client){
        String sql = "DELETE FROM clients WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, client.getId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }



}
