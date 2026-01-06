package com.example.hospital_system.Models;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;

public class InvoiceItem {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty designation = new SimpleStringProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final DoubleProperty unitPrice = new SimpleDoubleProperty();

    private final DoubleBinding total =
            Bindings.createDoubleBinding(
                    () -> getQuantity() * getUnitPrice(),
                    quantity, unitPrice
            );


    public InvoiceItem(String designation, int quantity, double unitPrice) {
        this.designation.set(designation);
        this.quantity.set(quantity);
        this.unitPrice.set(unitPrice);
    }

    public InvoiceItem(int id,String designation, int quantity, double unitPrice) {
        this.id.set(id);
        this.designation.set(designation);
        this.quantity.set(quantity);
        this.unitPrice.set(unitPrice);
    }

    public String getDesignation() { return designation.get(); }
    public void setDesignation(String value) { designation.set(value); }
    public StringProperty designationProperty() { return designation; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int value) { quantity.set(value); }
    public IntegerProperty quantityProperty() { return quantity; }

    public double getUnitPrice() { return unitPrice.get(); }
    public void setUnitPrice(double value) { unitPrice.set(value); }
    public DoubleProperty unitPriceProperty() { return unitPrice; }

    public double getTotal() {
        return total.get();
    }
    public DoubleBinding totalProperty() { return total; }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }
}

