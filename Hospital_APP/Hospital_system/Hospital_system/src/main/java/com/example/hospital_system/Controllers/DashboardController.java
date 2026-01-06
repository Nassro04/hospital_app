package com.example.hospital_system.Controllers;

import com.example.hospital_system.Dao.DashboardDao;
import com.example.hospital_system.Dao.RendezVousDao;
import com.example.hospital_system.Models.RendezVous;
import com.example.hospital_system.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.Map;

public class DashboardController {

    @FXML
    private javafx.scene.layout.HBox cardKpiPatients;
    @FXML
    private javafx.scene.layout.HBox cardKpiDoctors;
    @FXML
    private javafx.scene.layout.HBox cardKpiAppointments;
    @FXML
    private javafx.scene.layout.HBox cardKpiRevenue;

    @FXML
    private javafx.scene.layout.VBox cardRecentPatients;
    @FXML
    private javafx.scene.layout.VBox cardQuickActions;

    @FXML
    private Label lblTotalPatients;
    @FXML
    private Label lblTotalDoctors;
    @FXML
    private Label lblTotalAppointments;
    @FXML
    private Label lblTotalRevenue;

    private DashboardDao dashboardDao;

    // --- TABLE ---
    @FXML
    private TableView<RendezVous> appointmentsTable;

    @FXML
    private TableColumn<RendezVous, String> colNom;
    @FXML
    private TableColumn<RendezVous, Integer> colId;
    @FXML
    private TableColumn<RendezVous, String> colDate;
    @FXML
    private TableColumn<RendezVous, String> colDoctor;
    @FXML
    private TableColumn<RendezVous, Void> colActions;

    // --- NAVIGATION ---
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnClients;
    @FXML
    private Button btnDoctors;
    @FXML
    private Button btnRendezVous;
    @FXML
    private Button btnFactures;
    @FXML
    private Button btnLogout;

    @FXML
    public void initialize() {
        System.out.println("DashboardController initialized (Light Theme)");

        try {
            dashboardDao = new DashboardDao();
            // New Animation calls
            applyAnimations();

            loadKPIs();
            setupTable();
            loadRecentPatients();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing dashboard: " + e.getMessage());
        }

        // TEMP: Print Specialties for User
        try {
            com.example.hospital_system.Dao.DoctorDao docDao = new com.example.hospital_system.Dao.DoctorDao();
            System.out.println("\n--- LISTE DES SPECIALITES ---");
            for (String s : docDao.getSpecialtiesQuery()) {
                System.out.println("- " + s);
            }
            System.out.println("-----------------------------\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadKPIs() {
        DashboardDao dao = new DashboardDao();
        com.example.hospital_system.Services.DoctorService doctorService = new com.example.hospital_system.Services.DoctorService();
        Map<String, Integer> stats = dao.getDashboardStats();

        if (lblTotalPatients != null)
            lblTotalPatients.setText(String.valueOf(stats.get("totalClients").intValue()));

        if (lblTotalAppointments != null)
            lblTotalAppointments.setText(String.valueOf(stats.get("totalRendezVous").intValue()));

        if (lblTotalRevenue != null) {
            // Now showing Total Factures (Count) instead of Revenue
            int invoices = stats.get("totalInvoices");
            lblTotalRevenue.setText(String.valueOf(invoices));
        }

        if (lblTotalDoctors != null)
            lblTotalDoctors.setText(String.valueOf(doctorService.getDoctors().size()));
    }

    private void setupTable() {
        // Bind columns to Model properties
        // RendezVous model has: nom, prenom, date, doctor...

        colNom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getNom() + " " + cellData.getValue().getPrenom()));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate().toString()));
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctor"));

        // Custom Action Button Cell
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnView = new Button("Voir");

            {
                btnView.setStyle(
                        "-fx-background-color: #e0f2fe; -fx-text-fill: #0369a1; -fx-background-radius: 5; -fx-font-size: 11px; -fx-cursor: hand;");
                btnView.setOnAction(event -> {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    try {
                        SceneManager.changeScene("/com/example/hospital_system/View/Clients.fxml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnView);
                }
            }
        });
    }

    private void loadRecentPatients() {
        // We'll reuse RendezVousDao to get recent appointments
        // In a real app, we might want a specific query "ORDER BY date DESC LIMIT 5"
        // For now, let's fetch all and take first 5

        RendezVousDao rdvDao = new RendezVousDao();
        // Assuming this method exists or similar (it was used in Calendar)
        // We need to check RendezVousDao methods.
        // Previously used: service.getAllRendezVous()

        com.example.hospital_system.Services.RendezVousService service = new com.example.hospital_system.Services.RendezVousService();
        ObservableList<RendezVous> allRdv = service.getAllRendezVous();

        // Take top 10
        ObservableList<RendezVous> recent = FXCollections.observableArrayList();
        for (int i = 0; i < Math.min(allRdv.size(), 10); i++) {
            recent.add(allRdv.get(i));
        }

        appointmentsTable.setItems(recent);
    }

    // --- NAVIGATION ACTIONS ---
    @FXML
    private void onDashboardClick() {
        // Current
    }

    @FXML
    private void onClientsClick() {
        navigateTo("/com/example/hospital_system/View/Clients.fxml");
    }

    @FXML
    private void onDoctorsClick() {
        navigateTo("/com/example/hospital_system/View/Doctors.fxml");
    }

    @FXML
    private void onRendezVousClick() {
        navigateTo("/com/example/hospital_system/View/RendezVous.fxml");
    }

    @FXML
    private void onFacturesClick() {
        navigateTo("/com/example/hospital_system/View/Facture.fxml");
    }

    @FXML
    private void onLogoutClick() {
        navigateTo("/com/example/hospital_system/View/Login.fxml");
    }

    private void navigateTo(String fxml) {
        try {
            SceneManager.changeScene(fxml);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setContentText("Could not navigate to: " + fxml + "\n" + e.getMessage());
            alert.show();
        }
    }

    private void navigateTo(String fxml, javafx.event.ActionEvent event) {
        // Overload to handle event source if needed, or just redirect
        navigateTo(fxml);
    }

    private void applyAnimations() {
        // Staggered entry for KPIs
        if (cardKpiPatients != null)
            com.example.hospital_system.Utils.AnimationUtils.scaleUp(cardKpiPatients, 400);
        if (cardKpiDoctors != null) {
            javafx.animation.PauseTransition p = new javafx.animation.PauseTransition(javafx.util.Duration.millis(100));
            p.setOnFinished(e -> com.example.hospital_system.Utils.AnimationUtils.scaleUp(cardKpiDoctors, 400));
            p.play();
        }
        if (cardKpiAppointments != null) {
            javafx.animation.PauseTransition p = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
            p.setOnFinished(e -> com.example.hospital_system.Utils.AnimationUtils.scaleUp(cardKpiAppointments, 400));
            p.play();
        }
        if (cardKpiRevenue != null) {
            javafx.animation.PauseTransition p = new javafx.animation.PauseTransition(javafx.util.Duration.millis(300));
            p.setOnFinished(e -> com.example.hospital_system.Utils.AnimationUtils.scaleUp(cardKpiRevenue, 400));
            p.play();
        }

        // Slide up for bottom sections
        if (cardRecentPatients != null)
            com.example.hospital_system.Utils.AnimationUtils.slideInFromRight(cardRecentPatients, 600);
        if (cardQuickActions != null)
            com.example.hospital_system.Utils.AnimationUtils.slideInFromRight(cardQuickActions, 800);

        // Table Fade In
        if (appointmentsTable != null)
            com.example.hospital_system.Utils.AnimationUtils.fadeIn(appointmentsTable, 1000);
    }
}
