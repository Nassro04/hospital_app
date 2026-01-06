package com.example.hospital_system.Controllers;

import com.example.hospital_system.Models.Doctor;
import com.example.hospital_system.Models.DoctorSchedule;
import com.example.hospital_system.Services.DoctorScheduleService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DoctorScheduleFormController {

    @FXML
    private ComboBox<String> dayOfWeekCombo;
    @FXML
    private TextField startTimeField;
    @FXML
    private TextField endTimeField;
    @FXML
    private CheckBox availableCheckBox;
    @FXML
    private TableView<DoctorSchedule> scheduleTable;
    @FXML
    private TableColumn<DoctorSchedule, String> dayColumn;
    @FXML
    private TableColumn<DoctorSchedule, String> startTimeColumn;
    @FXML
    private TableColumn<DoctorSchedule, String> endTimeColumn;
    @FXML
    private TableColumn<DoctorSchedule, Boolean> availableColumn;

    private Doctor doctor;
    private DoctorScheduleService scheduleService;
    private DoctorSchedule selectedSchedule;

    @FXML
    public void initialize() {
        // Initialize day of week combo
        dayOfWeekCombo.setItems(FXCollections.observableArrayList(
                "LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"
        ));

        // Set up table columns
        dayColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDayOfWeek()));

        startTimeColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"))));

        endTimeColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))));

        availableColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isAvailable()));

        availableColumn.setCellFactory(column -> new TableCell<DoctorSchedule, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Disponible" : "Indisponible");
                }
            }
        });

        // Handle table selection
        scheduleTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedSchedule = newSelection;
                        fillFormWithSchedule(newSelection);
                    }
                });

        availableCheckBox.setSelected(true);

    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        refreshTable();
    }

    public void setScheduleService(DoctorScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    private void fillFormWithSchedule(DoctorSchedule schedule) {
        dayOfWeekCombo.setValue(schedule.getDayOfWeek());
        startTimeField.setText(schedule.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        endTimeField.setText(schedule.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        availableCheckBox.setSelected(schedule.isAvailable());
    }

    @FXML
    private void addSchedule() {
        if (validateForm()) {
            DoctorSchedule schedule = new DoctorSchedule();
            schedule.setDoctorId(doctor.getId());
            schedule.setDayOfWeek(dayOfWeekCombo.getValue());
            schedule.setStartTime(LocalTime.parse(startTimeField.getText(),
                    DateTimeFormatter.ofPattern("HH:mm")));
            schedule.setEndTime(LocalTime.parse(endTimeField.getText(),
                    DateTimeFormatter.ofPattern("HH:mm")));
            schedule.setAvailable(availableCheckBox.isSelected());

            if (scheduleService.addSchedule(schedule)) {
                showInfo("Succès", "Horaire ajouté avec succès.");
                clearForm();
                refreshTable();
            } else {
                showError("Erreur", "Impossible d'ajouter l'horaire.");
            }
        }
    }

    @FXML
    private void updateSchedule() {
        if (selectedSchedule != null && validateForm()) {
            selectedSchedule.setDayOfWeek(dayOfWeekCombo.getValue());
            selectedSchedule.setStartTime(LocalTime.parse(startTimeField.getText(),
                    DateTimeFormatter.ofPattern("HH:mm")));
            selectedSchedule.setEndTime(LocalTime.parse(endTimeField.getText(),
                    DateTimeFormatter.ofPattern("HH:mm")));
            selectedSchedule.setAvailable(availableCheckBox.isSelected());

            if (scheduleService.updateSchedule(selectedSchedule)) {
                showInfo("Succès", "Horaire modifié avec succès.");
                clearForm();
                refreshTable();
                selectedSchedule = null;
            } else {
                showError("Erreur", "Impossible de modifier l'horaire.");
            }
        }
    }

    @FXML
    private void deleteSchedule() {
        DoctorSchedule selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer l'horaire");
            confirm.setContentText("Voulez-vous vraiment supprimer cet horaire ?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                if (scheduleService.deleteSchedule(selected.getId())) {
                    showInfo("Succès", "Horaire supprimé avec succès.");
                    refreshTable();
                } else {
                    showError("Erreur", "Impossible de supprimer l'horaire.");
                }
            }
        }
    }

    @FXML
    private void clearForm() {
        dayOfWeekCombo.setValue(null);
        startTimeField.clear();
        endTimeField.clear();
        availableCheckBox.setSelected(true);
        selectedSchedule = null;
        scheduleTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void close() {
        Stage stage = (Stage) scheduleTable.getScene().getWindow();
        stage.close();
    }

    private void refreshTable() {
        if (doctor != null && scheduleService != null) {
            System.out.println(doctor.getId());
            scheduleTable.setItems(scheduleService.getSchedulesByDoctorId(doctor.getId()));
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (dayOfWeekCombo.getValue() == null) {
            errors.append("• Veuillez sélectionner un jour\n");
        }

        if (startTimeField.getText().trim().isEmpty()) {
            errors.append("• L'heure de début est obligatoire\n");
        } else {
            try {
                LocalTime.parse(startTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e) {
                errors.append("• Format d'heure de début invalide (utilisez HH:mm)\n");
            }
        }

        if (endTimeField.getText().trim().isEmpty()) {
            errors.append("• L'heure de fin est obligatoire\n");
        } else {
            try {
                LocalTime endTime = LocalTime.parse(endTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime startTime = LocalTime.parse(startTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
                if (endTime.isBefore(startTime)) {
                    errors.append("• L'heure de fin doit être après l'heure de début\n");
                }
            } catch (Exception e) {
                errors.append("• Format d'heure de fin invalide (utilisez HH:mm)\n");
            }
        }

        if (errors.length() > 0) {
            showError("Erreur de validation", errors.toString());
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

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}