package com.example.hospital_system.Controllers;

import java.io.IOException;
import java.util.function.Consumer;

import com.example.hospital_system.Models.Doctor;
import com.example.hospital_system.SceneManager;
import com.example.hospital_system.Services.DoctorService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DoctorController {

    @FXML
    private TableView<Doctor> tableView;
    @FXML
    private Label lblTotalDoctors;
    @FXML
    private TableColumn<Doctor, Integer> colIndex;
    @FXML
    private TableColumn<Doctor, Number> colId;
    @FXML
    private TableColumn<Doctor, String> colFirstName;
    @FXML
    private TableColumn<Doctor, String> colLastName;
    @FXML
    private TableColumn<Doctor, String> colSpecialty;
    @FXML
    private TableColumn<Doctor, String> colPhone;
    @FXML
    private TableColumn<Doctor, String> colEmail;

    private DoctorService doctorService = new DoctorService();

    @FXML
    public void initialize() {
        colIndex.setCellFactory(col -> new TableCell<Doctor, Integer>() {
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
        colFirstName.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        colLastName.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        colSpecialty.setCellValueFactory(cellData -> cellData.getValue().specialtyProperty());
        colPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        tableView.setItems(doctorService.getDoctors());
        // bind count label
        if (lblTotalDoctors != null) {
            lblTotalDoctors.textProperty().bind(javafx.beans.binding.Bindings.size(tableView.getItems()).asString());
        }
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        com.example.hospital_system.Utils.AnimationUtils.fadeIn(tableView);
    }

    @FXML
    private void onAddRdvForDoctor() {
        Doctor selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucun médecin sélectionné", "Veuillez sélectionner un médecin pour créer un rendez-vous.");
            return;
        }

        try {
            // Pass the Doctor object directly instead of toString()
            Consumer<RendezVousFormController> config = controller -> controller.setInitialDoctor(selected);
            SceneManager.openModal("/com/example/hospital_system/View/AddRendezVous.fxml",
                    "Ajouter un rendez-vous",
                    config,
                    (Window) tableView.getScene().getWindow());
            // After modal close, refresh table if needed
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de rendez-vous.");
        }
    }

    @FXML
    private void ajouterDoctor() {
        ouvrirFormulaire(null);
    }

    @FXML
    private void modifierDoctor() {
        Doctor selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ouvrirFormulaire(selected);
        } else {
            showAlert("Aucun médecin sélectionné", "Veuillez sélectionner un médecin à modifier.");
        }
    }

    @FXML
    private void supprimerDoctor() {
        Doctor selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le médecin");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer " + selected + " ?");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                boolean success = doctorService.supprimerDoctor(selected);
                if (success) {
                    showInfo("Succès", "Médecin supprimé avec succès.");
                } else {
                    showAlert("Erreur", "Impossible de supprimer le médecin.");
                }
            }
        } else {
            showAlert("Aucun médecin sélectionné", "Veuillez sélectionner un médecin à supprimer.");
        }
    }

    private void ouvrirFormulaire(Doctor doctor) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/hospital_system/View/doctor-form.fxml"));
            Parent root = loader.load();

            DoctorFormController controller = loader.getController();
            controller.setDoctor(doctor);
            controller.setDoctorService(doctorService);
            controller.setTableView(tableView);

            Stage stage = new Stage();
            stage.setTitle(doctor == null ? "Ajouter un médecin" : "Modifier un médecin");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 420, 320));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire.");
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onDashboardClick() {
        try { SceneManager.changeScene("/com/example/hospital_system/View/Dashboard.fxml"); } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onClientsClick() {
        try { SceneManager.changeScene("/com/example/hospital_system/View/Clients.fxml"); } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onFacturesClick() {
        try { SceneManager.changeScene("/com/example/hospital_system/View/Facture.fxml"); } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onRendezVousClick() {
        try { SceneManager.changeScene("/com/example/hospital_system/View/RendezVous.fxml"); } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onLogoutClick() {
        try { SceneManager.changeScene("/com/example/hospital_system/View/Login.fxml"); } catch (IOException e) { e.printStackTrace(); }
    }

    public void onDoctorsClick(ActionEvent event) {
    }
}