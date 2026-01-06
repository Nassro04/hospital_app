package com.example.hospital_system.Controllers;

import com.example.hospital_system.Models.Client;
import com.example.hospital_system.Models.Invoice;
import com.example.hospital_system.SceneManager;
import com.example.hospital_system.Services.FactureService;
import com.example.hospital_system.Utils.DataStore;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;

import java.io.IOException;

public class FactureListController {

    @FXML
    private TableView<Invoice> invoiceTable;
    @FXML
    private TableColumn<Invoice, Integer> colIndex;
    @FXML
    private TableColumn<Invoice, String> colId;
    @FXML
    private TableColumn<Invoice, String> colName;
    @FXML
    private TableColumn<Invoice, String> colDate;
    @FXML
    private TableColumn<Invoice, Double> colTotal;
    @FXML
    private javafx.scene.control.Button editButton;
    @FXML
    private javafx.scene.control.Button deleteButton;

    FactureService factureService = new FactureService();

    private Client client;

    public void setClient(Client client) {
        this.client = client;
        loadClientInvoices();
    }

    @FXML
    public void initialize() {
        // Row Number Column
        // Row Number Column
        colIndex.setCellFactory(col -> new javafx.scene.control.TableCell<Invoice, Integer>() {
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

        colId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId()));

        colName.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().getNom() + " " + cell.getValue().getPrenom()));
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate().toString()));

        // Format Total to 2 decimal places
        colTotal.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTotalAmount()));
        colTotal.setCellFactory(tc -> new javafx.scene.control.TableCell<Invoice, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f MAD", item));
                }
            }
        });

        invoiceTable.setItems(factureService.getClients());

        invoiceTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && invoiceTable.getSelectionModel().getSelectedItem() != null) {
                Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
                Invoice fullInvoice = factureService.getFactureById(selected.getId());

                try {
                    SceneManager.changeScene("/com/example/hospital_system/View/FactureDetails.fxml", fullInvoice,
                            invoiceTable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadClientInvoices() {
        if (client != null) {
            invoiceTable.setItems(factureService.getFactureByClient(client));
        }
    }

    @FXML
    public void onAddInvoice(ActionEvent event) {
        try {
            // Open as Modal
            SceneManager.openModal("/com/example/hospital_system/View/FactureForm.fxml",
                    "Ajouter une Facture",
                    (com.example.hospital_system.Controllers.FactureFormController controller) -> {
                        if (client != null) {
                            controller.setClient(client);
                        }
                        // If client is null (Global view), the controller handles it or we might need a
                        // workaround.
                        // For now, we allow opening the form.
                    },
                    invoiceTable.getScene().getWindow());

            // Refresh table after modal closes
            if (client != null) {
                loadClientInvoices();
            } else {
                invoiceTable.setItems(factureService.getClients()); // Refresh global list
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditInvoice(ActionEvent event) {
        Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucun sélection", "Veuillez sélectionner une facture à modifier.");
            return;
        }

        try {
            SceneManager.openModal("/com/example/hospital_system/View/FactureForm.fxml",
                    "Modifier la Facture",
                    (com.example.hospital_system.Controllers.FactureFormController controller) -> {
                        controller.setInvoice(selected);
                        if (client != null)
                            controller.setClient(client);
                    },
                    invoiceTable.getScene().getWindow());

            // Refresh table
            invoiceTable.refresh();
            if (client != null) {
                loadClientInvoices();
            } else {
                invoiceTable.setItems(factureService.getClients());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteInvoice(ActionEvent event) {
        Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucun sélection", "Veuillez sélectionner une facture à supprimer.");
            return;
        }

        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la facture");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la facture " + selected.getId() + " ?");

        if (alert.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            if (factureService.deleteFacture(selected.getId())) {
                invoiceTable.getItems().remove(selected);
            }
        }
    }

    private void showAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
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
