package com.example.hospital_system.Controllers;

import com.example.hospital_system.Models.Client;
import com.example.hospital_system.Services.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ClientFormController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField cinField;

    private Client client;
    private ClientService clientService;
    private TableView<Client> tableView;

    @FXML
    public void initialize() {
        nomField.setOnAction(e -> prenomField.requestFocus());
        prenomField.setOnAction(e -> emailField.requestFocus());
        emailField.setOnAction(e -> telephoneField.requestFocus());
        telephoneField.setOnAction(e -> cinField.requestFocus());
        cinField.setOnAction(e -> enregistrer());
    }

    public void setClient(Client client) {
        this.client = client;
        if (client != null) {
            nomField.setText(client.getNom());
            prenomField.setText(client.getPrenom());
            emailField.setText(client.getEmail());
            telephoneField.setText(client.getTelephone());
            cinField.setText(client.getCin());
        }
    }

    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    public void setTableView(TableView<Client> tableView) {
        this.tableView = tableView;
    }

    @FXML
    private void enregistrer() {
        if (validerFormulaire()) {
            if (client == null) {
                // Ajout
                Client nouveauClient = new Client();
                nouveauClient.setNom(nomField.getText());
                nouveauClient.setPrenom(prenomField.getText());
                nouveauClient.setEmail(emailField.getText());
                nouveauClient.setTelephone(telephoneField.getText());
                nouveauClient.setCin(cinField.getText());
                clientService.ajouterClient(nouveauClient);
            } else {
                // Modification
                client.setNom(nomField.getText());
                client.setPrenom(prenomField.getText());
                client.setEmail(emailField.getText());
                client.setTelephone(telephoneField.getText());
                client.setCin(cinField.getText());
                clientService.modifierClient(client);
                tableView.refresh();
            }
            fermer();
        }
    }

    @FXML
    private void annuler() {
        fermer();
    }

    private boolean validerFormulaire() {
        StringBuilder erreurs = new StringBuilder();

        if (nomField.getText().trim().isEmpty()) {
            erreurs.append("• Le nom est obligatoire\n");
        }

        if (prenomField.getText().trim().isEmpty()) {
            erreurs.append("• Le prénom est obligatoire\n");
        }

        if (!emailField.getText().trim().isEmpty() &&
                !emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            erreurs.append("• Format d'email invalide\n");
        }

        if (erreurs.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText("Veuillez corriger les erreurs suivantes :");
            alert.setContentText(erreurs.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    private void fermer() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}