package com.example.hospital_system.Models;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Invoice {

    private StringProperty id = new SimpleStringProperty();
    private StringProperty nom = new SimpleStringProperty();
    private StringProperty prenom = new SimpleStringProperty();
    private StringProperty cin = new SimpleStringProperty();
    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private  DoubleProperty totalHt = new SimpleDoubleProperty();
    private List<InvoiceItem> items = new ArrayList<>();
    private IntegerProperty clientId = new SimpleIntegerProperty();

    public Invoice() {}

    public Invoice(String id, String nom, String prenom, String cin, LocalDate date,int clientId) {
        this.id.set(id);
        this.nom.set(nom);
        this.prenom.set(prenom);
        this.cin.set(cin);
        this.date.set(date);
        this.clientId.set(clientId);
    }

    public String getId() { return id.get(); }
    public void setId(String id) { this.id.set(id); }
    public StringProperty idProperty() { return id; }

    public String getNom() { return nom.get(); }
    public void setNom(String nom) { this.nom.set(nom); }
    public StringProperty nomProperty() { return nom; }

    public String getPrenom() { return prenom.get(); }
    public void setPrenom(String prenom) { this.prenom.set(prenom); }
    public StringProperty prenomProperty() { return prenom; }

    public String getCin() { return cin.get(); }
    public void setCin(String cin) { this.cin.set(cin); }
    public StringProperty cinProperty() { return cin; }

    public LocalDate getDate() { return date.get(); }
    public void setDate(LocalDate date) { this.date.set(date); }
    public ObjectProperty<LocalDate> dateProperty() { return date; }

    public List<InvoiceItem> getItems() { return items; }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public void addItem(InvoiceItem item) { items.add(item); }

    public double getTotalAmount() {
        return getTotalHt() + (getTotalHt() * 0.14);
    }


    public double getTotalHt() {
        return totalHt.get();
    }
    public DoubleProperty totalHtProperty() {
        return totalHt;
    }
    public void setTotalHt(double totalHt) {
        this.totalHt.set(totalHt);
    }

    public int getClientId() {
        return clientId.get();
    }

    public IntegerProperty clientIdProperty() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId.set(clientId);
    }
}
