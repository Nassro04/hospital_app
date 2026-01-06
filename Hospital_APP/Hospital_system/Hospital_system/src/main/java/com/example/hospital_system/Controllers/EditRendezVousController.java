package com.example.hospital_system.Controllers;

import com.example.hospital_system.Models.Doctor;
import com.example.hospital_system.Models.RendezVous;
import com.example.hospital_system.Services.DoctorService;
import com.example.hospital_system.Services.DoctorScheduleService;
import com.example.hospital_system.Services.RendezVousService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EditRendezVousController {

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
    private Label errorText;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<Doctor> doctorComboBox;
    @FXML
    private ComboBox<LocalTime> timeComboBox;
    @FXML
    private ComboBox<String> departmentComboBox;

    private RendezVous rendezVous;
    private boolean saveClicked = false;
    private DoctorService doctorService;
    private DoctorScheduleService scheduleService;
    private RendezVousService rendezVousService;

    @FXML
    public void initialize() {
        doctorService = new DoctorService();
        scheduleService = new DoctorScheduleService();
        rendezVousService = new RendezVousService();

        // Initialize departments
        departmentComboBox.getItems().addAll(
                "Cardiology", "Oncology", "Dermatology",
                "Gynaecology", "Neurology", "Orthopaedics", "Surgery");

        // Initialize departments

        // Force numeric input for ageField
        ageField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                ageField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Setup doctor ComboBox
        setupDoctorComboBox();

        // Setup time ComboBox
        setupTimeComboBox();

        // Setup listeners
        setupListeners();

        errorText.setVisible(false);

        // Focus Traversal Logic
        nomField.setOnAction(e -> prenomField.requestFocus());
        prenomField.setOnAction(e -> telephoneField.requestFocus());
        telephoneField.setOnAction(e -> cinField.requestFocus());
        cinField.setOnAction(e -> ageField.requestFocus());
        // When Age loses focus or enter is pressed, ideally go to Genre (rbMale)
        // But Spinners capture Enter. relying on Tab order is often safer.
        // We can add an explicit listener if needed, but for now we set focus to
        // ageSpinner from cinField.

        // Remove the old listener that jumped from Age to Department, as Genre is now
        // in between.
        /*
         * ageSpinner.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
         * if (!isNowFocused) {
         * departmentComboBox.requestFocus();
         * }
         * });
         */

        // Disable past dates
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
        } catch (Exception e) {
            e.printStackTrace();
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
        // When doctor changes, reload available time slots for the current date
        doctorComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dateField.getValue() != null) {
                loadAvailableTimeSlots(true);
            }
        });

        // When date changes, reload available time slots
        dateField.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && doctorComboBox.getValue() != null) {
                loadAvailableTimeSlots(true);
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

            Doctor currentSelection = doctorComboBox.getValue();

            if (filteredDoctors.isEmpty()) {
                doctorComboBox.setItems(allDoctors);
            } else {
                doctorComboBox.setItems(filteredDoctors);
            }

            // Try to keep current selection if still in list
            if (currentSelection != null && doctorComboBox.getItems().contains(currentSelection)) {
                doctorComboBox.setValue(currentSelection);
            } else if (!doctorComboBox.getItems().isEmpty()) {
                doctorComboBox.getSelectionModel().select(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAvailableTimeSlots(boolean includeCurrentSlot) {
        Doctor selectedDoctor = doctorComboBox.getValue();
        LocalDate selectedDate = dateField.getValue();

        if (selectedDoctor != null && selectedDate != null) {
            var rawSlots = scheduleService.getAvailableTimeSlots(
                    selectedDoctor.getId(),
                    selectedDate);

            // Wrap in modifiable list to be safe
            javafx.collections.ObservableList<LocalTime> availableSlots = javafx.collections.FXCollections
                    .observableArrayList(rawSlots);

            // If editing, include the current appointment time ONLY if it's the same
            // date/doctor as the original appointment
            // This prevents "moving" a slot to a booked date/time by force
            boolean isSameDate = rendezVous != null && selectedDate.equals(rendezVous.getDate());
            // Also check doctor (though usually we filter by doctor) - if user changes
            // doctor, we shouldn't force old time
            // String doctorName = "Dr. " + selectedDoctor.getFirstName() + " " +
            // selectedDoctor.getLastName();
            // simple check: if we are loading slots for the doctor of the RV

            if (includeCurrentSlot && isSameDate && rendezVous != null && rendezVous.getTime() != null) {
                try {
                    LocalTime currentTime = LocalTime.parse(rendezVous.getTime(),
                            DateTimeFormatter.ofPattern("HH:mm"));
                    if (!availableSlots.contains(currentTime)) {
                        availableSlots.add(currentTime);
                        availableSlots.sort(LocalTime::compareTo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Verify if current selection is still valid in the new list, otherwise clear
            // or select first
            LocalTime currentSelection = timeComboBox.getValue();

            timeComboBox.setItems(availableSlots);
            timeComboBox.setDisable(availableSlots.isEmpty());

            if (availableSlots.isEmpty()) {
                showWarning("Aucun créneau disponible",
                        "Il n'y a pas de créneaux disponibles pour ce médecin à cette date.");
            } else {
                if (currentSelection != null && availableSlots.contains(currentSelection)) {
                    timeComboBox.setValue(currentSelection);
                }
            }
        }
    }

    public void setRendezVous(RendezVous rendezVous) {
        this.rendezVous = rendezVous;
        populateFields();
    }

    private void populateFields() {
        if (rendezVous != null) {
            nomField.setText(rendezVous.getNom());
            prenomField.setText(rendezVous.getPrenom());
            telephoneField.setText(rendezVous.getTelephone());
            cinField.setText(rendezVous.getCin());
            cinField.setText(rendezVous.getCin());

            // Set Genre
            if (rendezVous.getGenre() != null) {
                // Initialize Genre
                genreComboBox.getItems().addAll("Masculin", "Féminin");

                if (rendezVous.getGenre() != null) {
                    genreComboBox.setValue(rendezVous.getGenre());
                    // Map legacy values if needed
                    if ("M".equalsIgnoreCase(rendezVous.getGenre()))
                        genreComboBox.setValue("Masculin");
                    if ("F".equalsIgnoreCase(rendezVous.getGenre()))
                        genreComboBox.setValue("Féminin");
                }

                if (rendezVous.getAge() > 0) {
                    ageField.setText(String.valueOf(rendezVous.getAge()));
                }
            }
            dateField.setValue(rendezVous.getDate());
            departmentComboBox.setValue(rendezVous.getDepartment());

            // Find and select the doctor
            String doctorName = rendezVous.getDoctor();
            if (doctorName != null) {
                for (Doctor doctor : doctorComboBox.getItems()) {
                    String fullName = "Dr. " + doctor.getFirstName() + " " + doctor.getLastName();
                    if (fullName.equals(doctorName)) {
                        doctorComboBox.setValue(doctor);
                        break;
                    }
                }
            }

            // Load available time slots and select current time
            loadAvailableTimeSlots(true);

            if (rendezVous.getTime() != null) {
                try {
                    LocalTime time = LocalTime.parse(rendezVous.getTime(),
                            DateTimeFormatter.ofPattern("HH:mm"));
                    timeComboBox.setValue(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInput()) {
            // Update the rendez-vous object
            rendezVous.setNom(nomField.getText().trim());
            rendezVous.setPrenom(prenomField.getText().trim());
            rendezVous.setTelephone(telephoneField.getText().trim());
            rendezVous.setCin(cinField.getText().trim());
            rendezVous.setCin(cinField.getText().trim());
            rendezVous.setGenre(genreComboBox.getValue());
            try {
                int age = Integer.parseInt(ageField.getText().trim());
                rendezVous.setAge(age);
            } catch (NumberFormatException e) {
                // Should be handled by generic validation but safe to have default
                rendezVous.setAge(0);
            }
            rendezVous.setDate(dateField.getValue());
            rendezVous.setDepartment(departmentComboBox.getValue());

            Doctor selectedDoctor = doctorComboBox.getValue();
            String doctorName = "Dr. " + selectedDoctor.getFirstName() + " " + selectedDoctor.getLastName();
            rendezVous.setDoctor(doctorName);

            LocalTime selectedTime = timeComboBox.getValue();
            String time = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            rendezVous.setTime(time);

            boolean result = rendezVousService.updateRendezVous(rendezVous);

            if (result) {
                saveClicked = true;
                showInfo("Succès", "Rendez-vous modifié avec succès!");
                closeDialog();
            } else {
                showError("Erreur", "Impossible de modifier le rendez-vous.");
            }
        }
    }

    @FXML
    private void handleCancel() {
        saveClicked = false;
        closeDialog();
    }

    private boolean validateInput() {
        errorText.setVisible(false);
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

        if (dateField.getValue() == null) {
            errors.append("• La date est obligatoire\n");
        } else if (dateField.getValue().isBefore(LocalDate.now())) {
            errors.append("• La date ne peut pas être dans le passé\n");
        }

        if (doctorComboBox.getValue() == null) {
            errors.append("• Veuillez sélectionner un médecin\n");
        }

        if (timeComboBox.getValue() == null) {
            errors.append("• Veuillez sélectionner un créneau horaire\n");
        }

        if (errors.length() > 0) {
            errorText.setText(errors.toString());
            errorText.setVisible(true);
            return false;
        }

        return true;
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    public RendezVous getUpdatedRendezVous() {
        return rendezVous;
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