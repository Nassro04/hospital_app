package com.example.hospital_system.Controllers;

import com.example.hospital_system.Models.Client;
import com.example.hospital_system.Models.Invoice;
import com.example.hospital_system.Models.InvoiceItem;
import com.example.hospital_system.SceneManager;
import com.example.hospital_system.Services.FactureService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import com.example.hospital_system.Utils.AnimationUtils;

import java.io.IOException;

public class FactureDetailsController {

    // UI Elements for A4 Invoice
    @FXML
    private AnchorPane invoiceContainer;
    @FXML
    private Label clientNameLabel;
    @FXML
    private Label clientAddressLabel; // Using for CIN
    @FXML
    private Label invoiceDateLabel;
    @FXML
    private Label invoiceIdLabel;
    @FXML
    private Label totalHtLabel;
    @FXML
    private Label tvaLabel;
    @FXML
    private Label totalTtcLabel;
    @FXML
    private Text amountInWords;

    // Table inside A4
    @FXML
    private TableView<InvoiceItem> itemsTable;
    @FXML
    private TableColumn<InvoiceItem, String> colDesignation;
    @FXML
    private TableColumn<InvoiceItem, Integer> colQty;
    @FXML
    private TableColumn<InvoiceItem, Double> colPrice;
    @FXML
    private TableColumn<InvoiceItem, Double> colTotal;

    // Add Item Form
    @FXML
    private TextField itemDesignation;
    @FXML
    private TextField itemQty;
    @FXML
    private TextField itemPrice;
    @FXML
    private Button btnAddItem;
    @FXML
    private Button btnDeleteItem;

    private Invoice currentInvoice;
    private InvoiceItem selectedItem;
    private boolean isEditMode = false;
    private final FactureService factureService = new FactureService();

    // -----------------------------
    // Method to inject the invoice
    // -----------------------------
    public void setInvoice(Invoice invoice) {
        this.currentInvoice = invoice;
        populateInvoiceDetails();
    }

    // -----------------------------
    // Initialize Table Columns
    // -----------------------------
    @FXML
    public void initialize() {
        colDesignation.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDesignation()));
        colQty.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getUnitPrice()).asObject());
        colTotal.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getTotal()).asObject());

        itemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFormWithItem(newSelection);
            } else {
                clearForm();
            }
        });

        setupFocusTraversal();
        applyAnimations();
    }

    private void applyAnimations() {
        // Fade in the invoice container
        AnimationUtils.fadeIn(invoiceContainer, 600);

        // Add hover effects to buttons
        AnimationUtils.addButtonHover(btnAddItem);
        AnimationUtils.addButtonHover(btnDeleteItem);
    }

    private void populateFormWithItem(InvoiceItem item) {
        selectedItem = item;
        isEditMode = true;
        itemDesignation.setText(item.getDesignation());
        itemQty.setText(String.valueOf(item.getQuantity()));
        itemPrice.setText(String.valueOf(item.getUnitPrice()));
        btnAddItem.setText("Modifier");
        btnDeleteItem.setVisible(true);
        AnimationUtils.fadeIn(btnDeleteItem, 200);
    }

    private void clearForm() {
        selectedItem = null;
        isEditMode = false;
        itemDesignation.clear();
        itemQty.clear();
        itemPrice.clear();
        btnAddItem.setText("Ajouter");
        btnDeleteItem.setVisible(false);
    }

    private void setupFocusTraversal() {
        itemDesignation.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    itemQty.requestFocus();
                    break;
                default:
                    break;
            }
        });

        itemQty.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    itemPrice.requestFocus();
                    break;
                default:
                    break;
            }
        });

        itemPrice.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    onAddItem(new ActionEvent());
                    itemDesignation.requestFocus(); // Return focus to start for rapid entry
                    break;
                default:
                    break;
            }
        });
    }

    // -----------------------------
    // Populate invoice details in UI
    // -----------------------------
    private void populateInvoiceDetails() {
        if (currentInvoice == null)
            return;

        invoiceIdLabel.setText(currentInvoice.getId());
        invoiceDateLabel.setText(currentInvoice.getDate().toString());
        clientNameLabel.setText(currentInvoice.getNom().toUpperCase() + " " + currentInvoice.getPrenom().toUpperCase());
        clientAddressLabel.setText("CIN: " + currentInvoice.getCin());

        refreshTable();
    }

    // -----------------------------
    // Refresh Table and Totals
    // -----------------------------
    private void refreshTable() {
        itemsTable.getItems().setAll(currentInvoice.getItems());
        updateTotals();
    }

    private void updateTotals() {
        double totalHT = currentInvoice.getItems().stream().mapToDouble(InvoiceItem::getTotal).sum();
        double tva = totalHT * 0.14;
        double totalTTC = totalHT + tva;

        totalHtLabel.setText(String.format("%.2f MAD", totalHT));
        tvaLabel.setText(String.format("%.2f MAD", tva));
        totalTtcLabel.setText(String.format("%.2f MAD", totalTTC));

        amountInWords.setText("(Montant en lettres ici...)");
    }

    // -----------------------------
    // Add Item
    // -----------------------------
    @FXML
    public void onAddItem(ActionEvent event) {
        try {
            String desc = itemDesignation.getText().trim();
            String qtyStr = itemQty.getText().trim();
            String priceStr = itemPrice.getText().trim();

            if (desc.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty())
                return;

            int qty = Integer.parseInt(qtyStr);
            double price = Double.parseDouble(priceStr);

            if (qty <= 0 || price <= 0)
                return;

            if (isEditMode && selectedItem != null) {
                // Update existing item
                selectedItem.setDesignation(desc);
                selectedItem.setQuantity(qty);
                selectedItem.setUnitPrice(price);
                factureService.updateItem(selectedItem);
                AnimationUtils.successPulse(btnAddItem);
            } else {
                // Add new item
                InvoiceItem item = new InvoiceItem(desc, qty, price);
                currentInvoice.addItem(item);
                factureService.addItem(item, currentInvoice.getId());
                AnimationUtils.successPulse(btnAddItem);
                // We need to re-fetch to get the generated ID for the item if we want to edit
                // it immediately
                // However, the refreshTable will use the items from currentInvoice.
                // To be safe, we should probably update the invoice object or re-load it.
            }

            clearForm();
            refreshTable();
            itemsTable.getSelectionModel().clearSelection();

        } catch (NumberFormatException e) {
            System.err.println("Invalid number format");
        }
    }

    @FXML
    public void onDeleteItem(ActionEvent event) {
        if (selectedItem != null) {
            factureService.deleteItem(selectedItem.getId());
            currentInvoice.getItems().remove(selectedItem);
            clearForm();
            refreshTable();
            itemsTable.getSelectionModel().clearSelection();
        }
    }

    // -----------------------------
    // Print Invoice
    // -----------------------------
    @FXML
    public void onPrint(ActionEvent event) {
        Printer printer = Printer.getDefaultPrinter();
        if (printer == null)
            return;

        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT,
                Printer.MarginType.HARDWARE_MINIMUM);
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null && job.showPrintDialog(invoiceContainer.getScene().getWindow())) {
            boolean success = job.printPage(pageLayout, invoiceContainer);
            if (success)
                job.endJob();
        }
    }

    // -----------------------------
    // Back to Facture List
    // -----------------------------
    @FXML
    public void onBack(ActionEvent event) {
        if (currentInvoice == null)
            return;

        try {
            // Create a Client object from the invoice
            Client client = new Client();
            client.setNom(currentInvoice.getNom());
            client.setPrenom(currentInvoice.getPrenom());
            client.setCin(currentInvoice.getCin());
            client.setId(currentInvoice.getClientId());
            // Optionally set other fields if needed

            // Go back to Facture list for that client
            SceneManager.changeScene("/com/example/hospital_system/View/facture.fxml", client, invoiceContainer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
