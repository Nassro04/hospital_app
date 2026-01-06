package com.example.hospital_system.Controllers;

import java.io.IOException;

import com.example.hospital_system.Models.Client;
import com.example.hospital_system.SceneManager;
import com.example.hospital_system.Services.ClientService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClientController {

    @FXML
    private TableView<Client> tableView;
    @FXML
    private TableColumn<Client, Integer> colIndex;
    @FXML
    private TableColumn<Client, Number> colId;
    @FXML
    private TableColumn<Client, String> colNom;
    @FXML
    private TableColumn<Client, String> colPrenom;
    @FXML
    private TableColumn<Client, String> colEmail;
    @FXML
    private TableColumn<Client, String> colTelephone;
    @FXML
    private TableColumn<Client, String> colCin;
    @FXML
    private TableColumn<Client, Void> colFacture;

    @FXML
    private Label lblTotalPatients;

    private ClientService clientService = new ClientService();

    @FXML
    public void initialize() {
        // Configurer les colonnes
        colIndex.setCellFactory(col -> new TableCell<Client, Integer>() {
            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);
                if (isEmpty() || index < 0) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(Integer.toString(index + 1));
                    setStyle("-fx-text-fill: #004d40; -fx-font-weight: bold; -fx-alignment: CENTER;");
                }
            }
        });

        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        colPrenom.setCellValueFactory(cellData -> cellData.getValue().prenomProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colTelephone.setCellValueFactory(cellData -> cellData.getValue().telephoneProperty());
        colCin.setCellValueFactory(cellData -> cellData.getValue().cinProperty());

        // Charger les données
        tableView.setItems(clientService.getClients());

        // Bind count label to list size
        lblTotalPatients.textProperty().bind(javafx.beans.binding.Bindings.size(tableView.getItems()).asString());

        // Sélection simple
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        addFactureButton();

        // Add animation
        com.example.hospital_system.Utils.AnimationUtils.fadeIn(tableView);
    }

    private void addFactureButton() {
        colFacture.setCellFactory(column -> new TableCell<Client, Void>() {
            private final Button btn = new Button("Afficher");

            {
                btn.setStyle("-fx-background-color:#FF9800; -fx-text-fill:white; -fx-font-weight:bold;");
                btn.setOnAction(event -> {
                    Client client = getTableView().getItems().get(getIndex());
                    try {
                        openFacture(client);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void openFacture(Client client) throws IOException {
        SceneManager.changeScene("/com/example/hospital_system/View/facture.fxml", client, tableView);

    }

    @FXML
    private void ajouterClient() {
        ouvrirFormulaire(null);
    }

    @FXML
    private void modifierClient() {
        Client clientSelectionne = tableView.getSelectionModel().getSelectedItem();
        if (clientSelectionne != null) {
            ouvrirFormulaire(clientSelectionne);
        } else {
            afficherAlerte("Aucun client sélectionné", "Veuillez sélectionner un client à modifier.");
        }
    }

    @FXML
    private void supprimerClient() {
        Client clientSelectionne = tableView.getSelectionModel().getSelectedItem();
        if (clientSelectionne != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le client");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer " +
                    clientSelectionne.getNom() + " " +
                    clientSelectionne.getPrenom() + " ?");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                clientService.supprimerClient(clientSelectionne);
            }
        } else {
            afficherAlerte("Aucun client sélectionné", "Veuillez sélectionner un client à supprimer.");
        }
    }

    private void ouvrirFormulaire(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/hospital_system/View/client-form.fxml"));
            Parent root = loader.load();

            ClientFormController controller = loader.getController();
            controller.setClient(client);
            controller.setClientService(clientService);
            controller.setTableView(tableView);

            Stage stage = new Stage();
            stage.setTitle(client == null ? "Ajouter un client" : "Modifier un client");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 400, 360));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible d'ouvrir le formulaire.");
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onDashboardClick() {
        try {
            SceneManager.changeScene("/com/example/hospital_system/View/Dashboard.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClientsClick() {
        try {
            SceneManager.changeScene("/com/example/hospital_system/View/Clients.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onFacturesClick() {
        try {
            SceneManager.changeScene("/com/example/hospital_system/View/Facture.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDoctorsClick() {
        try {
            SceneManager.changeScene("/com/example/hospital_system/View/Doctors.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRendezVousClick() {
        try {
            SceneManager.changeScene("/com/example/hospital_system/View/RendezVous.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogoutClick() {
        try {
            SceneManager.changeScene("/com/example/hospital_system/View/Login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}