package com.example.hospital_system.Controllers;

import com.example.hospital_system.Models.Doctor;
import com.example.hospital_system.Services.DoctorService;
import com.example.hospital_system.Services.DoctorScheduleService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DoctorFormController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private ComboBox<String> specialtyComboBox;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private Button manageScheduleBtn;

    private Doctor doctor;
    private DoctorService doctorService;
    private DoctorScheduleService scheduleService;
    private TableView<Doctor> tableView;

    @FXML
    public void initialize() {
        // Initialize specialties
        specialtyComboBox.getItems().addAll(
                "Cardiology", "Oncology", "Dermatology",
                "Gynaecology", "Neurology", "Orthopaedics", "Surgery");

        firstNameField.setOnAction(e -> lastNameField.requestFocus());
        lastNameField.setOnAction(e -> specialtyComboBox.requestFocus());
        // ComboBox doesn't always trigger onAction same way, but we can set it
        specialtyComboBox.setOnAction(e -> {
            // Only move focus if item is selected? or just on enter?
            // Usually onAction on ComboBox is selection change.
            // We can use KeyListener or just let user Tab.
            phoneField.requestFocus();
        });
        phoneField.setOnAction(e -> emailField.requestFocus());
        emailField.setOnAction(e -> enregistrer());

        scheduleService = new DoctorScheduleService();

        // Disable schedule button for new doctors
        if (manageScheduleBtn != null) {
            manageScheduleBtn.setDisable(doctor == null);
        }
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        if (doctor != null) {
            firstNameField.setText(doctor.getFirstName());
            lastNameField.setText(doctor.getLastName());
            specialtyComboBox.setValue(doctor.getSpecialty());
            phoneField.setText(doctor.getPhone());
            emailField.setText(doctor.getEmail());

            // Enable schedule button for existing doctors
            if (manageScheduleBtn != null) {
                manageScheduleBtn.setDisable(false);
            }
        }
    }

    public void setDoctorService(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    public void setTableView(TableView<Doctor> tableView) {
        this.tableView = tableView;
    }

    @FXML
    private void enregistrer() {
        if (validerFormulaire()) {
            if (doctor == null) {
                Doctor d = new Doctor();
                d.setFirstName(firstNameField.getText());
                d.setLastName(lastNameField.getText());
                d.setSpecialty(specialtyComboBox.getValue());
                d.setPhone(phoneField.getText());
                d.setEmail(emailField.getText());
                doctorService.ajouterDoctor(d);
            } else {
                doctor.setFirstName(firstNameField.getText());
                doctor.setLastName(lastNameField.getText());
                doctor.setSpecialty(specialtyComboBox.getValue());
                doctor.setPhone(phoneField.getText());
                doctor.setEmail(emailField.getText());
                doctorService.modifierDoctor(doctor);
                tableView.refresh();
            }
            fermer();
        }
    }

    @FXML
    private void manageSchedule() {
        if (doctor != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/hospital_system/View/DoctorScheduleForm.fxml"));
                Scene scene = new Scene(loader.load());

                DoctorScheduleFormController controller = loader.getController();
                controller.setScheduleService(scheduleService);
                controller.setDoctor(doctor);
                // controller.setScheduleService(scheduleService);

                Stage stage = new Stage();
                stage.setTitle("Gestion du Planning - Dr. " + doctor.getFirstName() + " " + doctor.getLastName());
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
                showError("Erreur", "Impossible d'ouvrir la fenêtre de gestion du planning.");
            }
        }
    }

    @FXML
    private void annuler() {
        fermer();
    }

    private boolean validerFormulaire() {
        StringBuilder erreurs = new StringBuilder();

        if (firstNameField.getText().trim().isEmpty())
            erreurs.append("• Le prénom est obligatoire\n");
        if (lastNameField.getText().trim().isEmpty())
            erreurs.append("• Le nom est obligatoire\n");
        if (specialtyComboBox.getValue() == null)
            erreurs.append("• La spécialité est obligatoire\n");

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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void fermer() {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }
}