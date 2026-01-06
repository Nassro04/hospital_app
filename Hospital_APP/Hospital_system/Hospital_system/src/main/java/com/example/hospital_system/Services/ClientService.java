package com.example.hospital_system.Services;

import com.example.hospital_system.Dao.ClientsDao;
import com.example.hospital_system.Models.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientService {
    private ObservableList<Client> clients = FXCollections.observableArrayList();
    private ClientsDao clientsDao = new ClientsDao();

    private int nextId = 1;


    public ObservableList<Client> getClients() {
        clients.setAll(clientsDao.getClientsQuery());
        return clients;
    }

    public void ajouterClient(Client client) {
        clientsDao.addClientsQuery(client);
        getClients();
    }

    public void modifierClient(Client client) {
        clientsDao.editClientQuery(client);
        getClients();
    }

    public void supprimerClient(Client client) {
        clientsDao.deleteClientQuery(client);
        getClients();
    }

    public Client getClientById(int id) {
        return clients.stream()
                .filter(client -> client.getId() == id)
                .findFirst()
                .orElse(null);
    }
}