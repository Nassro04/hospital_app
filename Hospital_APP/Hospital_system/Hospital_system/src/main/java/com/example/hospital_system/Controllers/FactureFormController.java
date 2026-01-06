package com.example.hospital_system.Controllers;

import com.example.hospital_system.Models.Client;
import com.example.hospital_system.Models.Invoice;
import com.example.hospital_system.Services.FactureService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.UUID;

public class FactureFormController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField cinField;

    FactureService factureService = new FactureService();

    private Client client;
    private Invoice invoice;
    private boolean isEditMode = false;

    public void setClient(Client client) {
        this.client = client;
        this.isEditMode = false;

        // Pre-fill the fields with client info
        nomField.setText(client.getNom());
        prenomField.setText(client.getPrenom());
        cinField.setText(client.getCin());

        // We want them editable if the user wants to change them on the fly
        nomField.setEditable(true);
        prenomField.setEditable(true);
        cinField.setEditable(true);
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        this.isEditMode = true;

        nomField.setText(invoice.getNom());
        prenomField.setText(invoice.getPrenom());
        cinField.setText(invoice.getCin());

        nomField.setEditable(true);
        prenomField.setEditable(true);
        cinField.setEditable(true);
    }

    @FXML
    public void onSave(ActionEvent event) {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String cin = cinField.getText();

        if (nom.isEmpty() || prenom.isEmpty() || cin.isEmpty()) {
            showAlert("Validation", "Tous les champs sont obligatoires.");
            return;
        }

        try {
            if (isEditMode) {
                if (invoice != null) {
                    invoice.setNom(nom);
                    invoice.setPrenom(prenom);
                    invoice.setCin(cin);
                    if (factureService.updateFacture(invoice)) {
                        showInfo("Succès", "Facture modifiée avec succès.");
                        closeDialog();
                    } else {
                        showAlert("Erreur", "Échec de la modification.");
                    }
                }
            } else {
                // Create new Invoice
                // Handle case where client might be null (Global Add)
                int clientId = (client != null) ? client.getId() : 0; // Use 0 or appropriate default/error

                String id = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                invoice = new Invoice(id, nom, prenom, cin, LocalDate.now(), clientId);
                factureService.addFacture(invoice);
                showInfo("Succès", "Facture ajoutée avec succès.");
                closeDialog();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur Exception", e.getMessage());
        }
    }

    @FXML
    public void onCancel(ActionEvent event) {
        closeDialog();
    }

    private void closeDialog() {
        if (nomField.getScene() != null && nomField.getScene().getWindow() != null) {
            ((javafx.stage.Stage) nomField.getScene().getWindow()).close();
        }
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
