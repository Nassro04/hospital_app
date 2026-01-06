package com.example.hospital_system.Services;

import com.example.hospital_system.Dao.FactureDao;
import com.example.hospital_system.Models.Client;
import com.example.hospital_system.Models.Invoice;
import com.example.hospital_system.Models.InvoiceItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class FactureService {
    private ObservableList<Invoice> invoices = FXCollections.observableArrayList();

    FactureDao factureDao = new FactureDao();

    public void addFacture(Invoice facture) {
        factureDao.addFactureQuery(facture);
        // getFacture();
    }

    public ObservableList<Invoice> getClients() {
        invoices.setAll(factureDao.getFacturesQuery());
        return invoices;
    }

    public void addItem(InvoiceItem item, String invoiceId) {
        factureDao.addItemQuery(item, invoiceId);
    }

    public void updateItem(InvoiceItem item) {
        factureDao.updateItemQuery(item);
    }

    public void deleteItem(int id) {
        factureDao.deleteItemQuery(id);
    }

    public ObservableList<Invoice> getFactureByClient(Client client) {
        ObservableList<Invoice> factures = factureDao.getFactureByClient(client);

        return factures;
    }

    public InvoiceItem getItemById(int id) {
        return factureDao.getItemById(id);
    }

    public Invoice getFactureById(String id) {
        Invoice facture = factureDao.getFactureById(id);

        if (facture != null) {
            List<InvoiceItem> items = factureDao.findItemsByFactureIdQuery(id);
            facture.setItems(items);
        }

        return facture;
    }

    public boolean deleteFacture(String id) {
        return factureDao.deleteFactureQuery(id);
    }

    public boolean updateFacture(Invoice invoice) {
        return factureDao.updateFactureQuery(invoice);
    }
}
