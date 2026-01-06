package com.example.hospital_system.Controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.example.hospital_system.Models.Doctor;
import com.example.hospital_system.Models.RendezVous;
import com.example.hospital_system.Services.DoctorService;
import com.example.hospital_system.Services.DoctorScheduleService;
import com.example.hospital_system.Services.RendezVousService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class RendezVousFormController {
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField cinField;
    @FXML
    private ComboBox<String> genreComboBox;
    @FXML
    private TextField ageField;
    @FXML
    private DatePicker dateField;
    @FXML
    private javafx.scene.control.ComboBox<Doctor> doctorComboBox;
    @FXML
    private ComboBox<LocalTime> timeComboBox;
    @FXML
    private ComboBox<String> departmentComboBox;

    private DoctorService doctorService;
    private DoctorScheduleService scheduleService;
    private RendezVousService rendezVousService;

    @FXML
    public void initialize() {
        doctorService = new DoctorService();
        scheduleService = new DoctorScheduleService();
        rendezVousService = new RendezVousService();

        // Initialize gender ToggleGroup
        // Initialize Genre
        genreComboBox.getItems().addAll("Masculin", "Féminin");

        // Force numeric input
        ageField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                ageField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Initialize departments
        departmentComboBox.getItems().addAll(
                "Cardiology", "Oncology", "Dermatology",
                "Gynaecology", "Neurology", "Orthopaedics", "Surgery");

        // Configure doctor ComboBox
        setupDoctorComboBox();

        // Configure time ComboBox
        setupTimeComboBox();

        // Disable date and time initially
        dateField.setDisable(true);
        timeComboBox.setDisable(true);

        // Setup listeners
        setupListeners();

        // Setup focus traversal
        setupFocusTraversal();

        // Disable past dates in DatePicker
        dateField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    private void setupDoctorComboBox() {
        try {
            doctorComboBox.setItems(doctorService.getDoctors());
            doctorComboBox.setConverter(new StringConverter<Doctor>() {
                @Override
                public String toString(Doctor doctor) {
                    if (doctor == null)
                        return "";
                    return "Dr. " + doctor.getFirstName() + " " + doctor.getLastName() +
                            " - " + doctor.getSpecialty();
                }

                @Override
                public Doctor fromString(String string) {
                    return null;
                }
            });

            if (!doctorComboBox.getItems().isEmpty()) {
                doctorComboBox.getSelectionModel().select(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible de charger la liste des médecins.");
        }
    }

    private void setupTimeComboBox() {
        timeComboBox.setConverter(new StringConverter<LocalTime>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            @Override
            public String toString(LocalTime time) {
                return time != null ? time.format(formatter) : "";
            }

            @Override
            public LocalTime fromString(String string) {
                try {
                    return LocalTime.parse(string, formatter);
                } catch (Exception e) {
                    return null;
                }
            }
        });
    }

    private void setupListeners() {
        // When doctor is selected, enable date picker
        doctorComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                dateField.setDisable(false);
                dateField.setValue(null);
                timeComboBox.getItems().clear();
                timeComboBox.setDisable(true);
            } else {
                dateField.setDisable(true);
                timeComboBox.setDisable(true);
            }
        });

        // When date is selected, load available time slots
        dateField.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && doctorComboBox.getValue() != null) {
                loadAvailableTimeSlots();
            } else {
                timeComboBox.getItems().clear();
                timeComboBox.setDisable(true);
            }
        });

        // Filter doctors by department
        departmentComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterDoctorsByDepartment(newVal);
            }
        });
    }

    private void filterDoctorsByDepartment(String department) {
        try {
            var allDoctors = doctorService.getDoctors();
            var filteredDoctors = allDoctors.filtered(
                    doctor -> doctor.getSpecialty() != null &&
                            doctor.getSpecialty().equalsIgnoreCase(department));

            if (filteredDoctors.isEmpty()) {
                doctorComboBox.setItems(allDoctors);
            } else {
                doctorComboBox.setItems(filteredDoctors);
            }

            if (!doctorComboBox.getItems().isEmpty()) {
                doctorComboBox.getSelectionModel().select(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAvailableTimeSlots() {
        Doctor selectedDoctor = doctorComboBox.getValue();
        LocalDate selectedDate = dateField.getValue();

        if (selectedDoctor != null && selectedDate != null) {
            var availableSlots = scheduleService.getAvailableTimeSlots(
                    selectedDoctor.getId(),
                    selectedDate);

            timeComboBox.setItems(availableSlots);
            timeComboBox.setDisable(availableSlots.isEmpty());

            if (availableSlots.isEmpty()) {
                String dayName = getDayOfWeekInFrench(selectedDate.getDayOfWeek().toString());
                showWarning("Aucun créneau disponible",
                        "Le médecin n'a pas de disponibilités le " + dayName + ".\n" +
                                "Veuillez vérifier son planning ou choisir une autre date.");
            } else if (!availableSlots.isEmpty()) {
                timeComboBox.getSelectionModel().select(0);
            }
        }
    }

    private String getDayOfWeekInFrench(String englishDay) {
        switch (englishDay) {
            case "MONDAY":
                return "LUNDI";
            case "TUESDAY":
                return "MARDI";
            case "WEDNESDAY":
                return "MERCREDI";
            case "THURSDAY":
                return "JEUDI";
            case "FRIDAY":
                return "VENDREDI";
            case "SATURDAY":
                return "SAMEDI";
            case "SUNDAY":
                return "DIMANCHE";
            default:
                return englishDay;
        }
    }

    private void setupFocusTraversal() {
        nomField.setOnAction(e -> prenomField.requestFocus());
        prenomField.setOnAction(e -> telephoneField.requestFocus());
        telephoneField.setOnAction(e -> cinField.requestFocus());
        cinField.setOnAction(e -> ageField.requestFocus());
        // ageField is a TextField, so it triggers onAction when Enter is pressed
        ageField.setOnAction(e -> genreComboBox.requestFocus());
    }

    @FXML
    private void submitInvoiceForm(ActionEvent event) throws IOException {
        if (!validateForm()) {
            return;
        }

        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String cin = cinField.getText().trim();
        String genre = genreComboBox.getValue();
        // Map long to short if service requires "M"/"F"
        if ("Masculin".equals(genre))
            genre = "M";
        if ("Féminin".equals(genre))
            genre = "F";

        int age = 0;
        try {
            age = Integer.parseInt(ageField.getText().trim());
        } catch (NumberFormatException e) {
            age = 0;
        }
        LocalDate date = dateField.getValue();
        String department = departmentComboBox.getValue();
        if (department == null)
            department = "General";

        Doctor selectedDoctor = doctorComboBox.getValue();
        String doctorName = "Dr. " + selectedDoctor.getFirstName() + " " + selectedDoctor.getLastName();

        LocalTime selectedTime = timeComboBox.getValue();
        String time = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        boolean result = rendezVousService.addRendezVous(
                nom, prenom, telephone, cin, genre, age,
                date, department, doctorName, time);

        if (result) {
            showInfo("Succès", "Rendez-vous ajouté avec succès!");
            clearForm();
            refreshParentTable(event);
            closeDialog(event);
        } else {
            showError("Erreur", "Impossible d'ajouter le rendez-vous. Veuillez réessayer.");
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (nomField.getText().trim().isEmpty()) {
            errors.append("• Le nom est obligatoire\n");
        }

        if (prenomField.getText().trim().isEmpty()) {
            errors.append("• Le prénom est obligatoire\n");
        }

        if (telephoneField.getText().trim().isEmpty()) {
            errors.append("• Le téléphone est obligatoire\n");
        }

        if (cinField.getText().trim().isEmpty()) {
            errors.append("• Le CIN est obligatoire\n");
        }

        if (genreComboBox.getValue() == null) {
            errors.append("• Le genre est obligatoire\n");
        }

        if (ageField.getText().trim().isEmpty()) {
            errors.append("• L'âge est obligatoire\n");
        }

        if (doctorComboBox.getValue() == null) {
            errors.append("• Veuillez sélectionner un médecin\n");
        }

        if (dateField.getValue() == null) {
            errors.append("• La date est obligatoire\n");
        } else if (dateField.getValue().isBefore(LocalDate.now())) {
            errors.append("• La date ne peut pas être dans le passé\n");
        }

        if (timeComboBox.getValue() == null) {
            errors.append("• Veuillez sélectionner un créneau horaire\n");
        }

        if (errors.length() > 0) {
            showError("Erreur de validation", errors.toString());
            return false;
        }

        return true;
    }

    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        telephoneField.clear();
        cinField.clear();
        genreComboBox.setValue(null);
        ageField.clear();
        dateField.setValue(null);
        departmentComboBox.setValue(null);
        doctorComboBox.setValue(null);
        timeComboBox.getItems().clear();
        dateField.setDisable(true);
        timeComboBox.setDisable(true);
    }

    private void refreshParentTable(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage parentStage = (Stage) stage.getOwner();
            Scene parentScene = parentStage.getScene();
            Parent root = parentScene.getRoot();

            TableView<RendezVous> tableView = (TableView<RendezVous>) root.lookup("#tableRendezVous");
            if (tableView != null) {
                tableView.setItems(rendezVousService.getAllRendezVous());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeDialog(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setInitialDateAndTime(LocalDate date, LocalTime time) {
        if (date != null) {
            dateField.setValue(date);
        }
        if (time != null && !timeComboBox.getItems().isEmpty()) {
            timeComboBox.setValue(time);
        }
    }

    public void setInitialDoctor(Doctor doctor) {
        if (doctor != null && doctorComboBox.getItems().contains(doctor)) {
            doctorComboBox.setValue(doctor);
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        closeDialog(event);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}