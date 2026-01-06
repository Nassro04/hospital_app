package com.example.hospital_system.Controllers;

import com.example.hospital_system.Models.RendezVous;
import com.example.hospital_system.SceneManager;
import com.example.hospital_system.Services.RendezVousService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class RendezVousTableController {

    @FXML
    private TableView<RendezVous> tableRendezVous;
    @FXML
    private TableColumn<RendezVous, Integer> colIndex;
    @FXML
    private TableColumn<RendezVous, Integer> colId;
    @FXML
    private TableColumn<RendezVous, String> colNom;
    @FXML
    private TableColumn<RendezVous, String> colPrenom;
    @FXML
    private TableColumn<RendezVous, String> colTelephone;
    @FXML
    private TableColumn<RendezVous, String> colCin;
    @FXML
    private TableColumn<RendezVous, String> colGenre;
    @FXML
    private TableColumn<RendezVous, Integer> colAge;
    @FXML
    private TableColumn<RendezVous, LocalDate> colDate;
    @FXML
    private TableColumn<RendezVous, String> colDepartment;
    @FXML
    private TableColumn<RendezVous, String> colTime;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    public void initialize() {
        // Row Number Column
        colIndex.setCellFactory(col -> new javafx.scene.control.TableCell<RendezVous, Integer>() {
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

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(data -> data.getValue().nomProperty());
        colPrenom.setCellValueFactory(data -> data.getValue().prenomProperty());
        colTelephone.setCellValueFactory(data -> data.getValue().telephoneProperty());
        colCin.setCellValueFactory(data -> data.getValue().cinProperty());
        colGenre.setCellValueFactory(data -> data.getValue().genreProperty());
        colAge.setCellValueFactory(data -> data.getValue().ageProperty().asObject());
        colDate.setCellValueFactory(data -> data.getValue().dateProperty());
        colDepartment.setCellValueFactory(data -> data.getValue().departmentProperty());
        colTime.setCellValueFactory(data -> data.getValue().timeProperty());

        loadRendezVous();

        // Add animation
        com.example.hospital_system.Utils.AnimationUtils.fadeIn(tableRendezVous);
    }

    @FXML
    private void showAddInvoiceForm() throws IOException {
        SceneManager.openModal("/com/example/hospital_system/View/AddRendezVous.fxml", "Ajouter un Rendez-vous");
    }

    @FXML
    private void showEditRendezVousForm() {
        // Get the selected rendez-vous from the table
        RendezVous selectedRendezVous = tableRendezVous.getSelectionModel().getSelectedItem();

        // Check if an item is selected
        if (selectedRendezVous == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun rendez-vous sélectionné", null,
                    "Veuillez sélectionner un rendez-vous à modifier.");
            return;
        }

        try {
            // Open edit modal
            SceneManager.<EditRendezVousController>openModal(
                    "/com/example/hospital_system/View/EditRendezVousForm.fxml",
                    "Edit Rendez-Vous - " + selectedRendezVous.getNom() + " " + selectedRendezVous.getPrenom(),
                    editController -> {
                        // Pass the selected rendez-vous to edit
                        editController.setRendezVous(selectedRendezVous);
                    },
                    tableRendezVous.getScene().getWindow());

            loadRendezVous(); // Refresh the rendez vous table

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot Open Edit Form");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    // Helper method for alerts
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to refresh the table
    private void refreshRendezVousTable() {
        try {
            // Clear and reload data from your service/database
            ObservableList<RendezVous> rendezVousList = FXCollections.observableArrayList(
            // Load your data here, e.g.:
            // rendezVousService.getAllRendezVous()
            );
            tableRendezVous.setItems(rendezVousList);

            // Optional: Keep selection after refresh
            tableRendezVous.getSelectionModel().selectFirst();
        } catch (Exception e) {
            showAlert(Alert.AlertType.WARNING,
                    "Refresh Error",
                    "Cannot refresh data",
                    "Error refreshing table: " + e.getMessage());
        }
    }

    @FXML
    private void deleteRendezVous() {
        RendezVous selectedRendezVous = tableRendezVous.getSelectionModel().getSelectedItem();

        // Check if an item is selected
        if (selectedRendezVous == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun rendez-vous sélectionné", null,
                    "Veuillez sélectionner un rendez-vous à supprimer.");
            return;
        }

        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Rendez-Vous");
        confirmAlert.setContentText("Are you sure you want to delete the rendez-vous for:\n" +
                selectedRendezVous.getNom() + " " + selectedRendezVous.getPrenom() +
                "\nCIN: " + selectedRendezVous.getCin() +
                "\nDate: " + selectedRendezVous.getDate());

        // Wait for user response
        ButtonType result = confirmAlert.showAndWait().orElse(ButtonType.CANCEL);

        // If user confirms deletion
        if (result == ButtonType.OK) {
            try {
                // Create service and delete the rendez-vous
                RendezVousService service = new RendezVousService();
                boolean success = service.deleteRendezVous(selectedRendezVous);

                if (success) {
                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("Rendez-Vous Deleted");
                    successAlert.setContentText("The rendez-vous has been successfully deleted.");
                    successAlert.showAndWait();

                    // Refresh the table
                    loadRendezVous();
                } else {
                    // Show error message if deletion failed
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Deletion Failed");
                    errorAlert.setContentText("Failed to delete the rendez-vous.");
                    errorAlert.showAndWait();
                }

            } catch (Exception e) {
                // Show exception message
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Delete Error");
                errorAlert.setContentText("An error occurred while deleting: " + e.getMessage());
                errorAlert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    public void loadRendezVous() {
        try {
            RendezVousService service = new RendezVousService();
            tableRendezVous.setItems(service.getAllRendezVous());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private void onRendezVousClick() {
        try {
            SceneManager.changeScene("/com/example/hospital_system/View/RendezVous.fxml");
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
    private void onLogoutClick() {
        try {
            SceneManager.changeScene("/com/example/hospital_system/View/Login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
