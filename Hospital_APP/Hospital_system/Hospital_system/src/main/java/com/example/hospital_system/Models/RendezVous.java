package com.example.hospital_system.Models;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;

public class RendezVous {
    private IntegerProperty id;
    private StringProperty nom;
    private StringProperty prenom;
    private StringProperty telephone;
    private StringProperty cin;
    private StringProperty genre;
    private IntegerProperty age;
    private ObjectProperty<LocalDate> date;

    private StringProperty department;
    private StringProperty doctor;
    private StringProperty time;

    public RendezVous(int id, String nom, String prenom, String telephone, String cin, String genre, int age,
            LocalDate date, String department, String doctor, String time) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.telephone = new SimpleStringProperty(telephone);
        this.cin = new SimpleStringProperty(cin);
        this.genre = new SimpleStringProperty(genre);
        this.age = new SimpleIntegerProperty(age);
        this.date = new SimpleObjectProperty<LocalDate>(date);
        this.department = new SimpleStringProperty(department);
        this.doctor = new SimpleStringProperty(doctor);
        this.time = new SimpleStringProperty(time);
    }

    public RendezVous(int id, String nom, String prenom, String telephone, String cin, String genre, int age,
            LocalDate date) {
        this(id, nom, prenom, telephone, cin, genre, age, date, "General", "Unknown", "09:00");
    }

    public RendezVous(int id, String nom, String prenom, String telephone, String cin, String genre, int age,
            LocalDate date, String department) {
        this(id, nom, prenom, telephone, cin, genre, age, date, department, "Unknown", "09:00");
    }

    // ... existing getters and setters ...

    public String getDoctor() {
        return doctor.get();
    }

    public StringProperty doctorProperty() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor.set(doctor);
    }

    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNom() {
        return nom.get();
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public String getPrenom() {
        return prenom.get();
    }

    public StringProperty prenomProperty() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom.set(prenom);
    }

    public String getTelephone() {
        return telephone.get();
    }

    public StringProperty telephoneProperty() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone.set(telephone);
    }

    public String getCin() {
        return cin.get();
    }

    public StringProperty cinProperty() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin.set(cin);
    }

    public String getGenre() {
        return genre.get();
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public int getAge() {
        return age.get();
    }

    public IntegerProperty ageProperty() {
        return age;
    }

    public void setAge(int age) {
        this.age.set(age);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public ObservableValue<LocalDate> dateProperty() {
        return date;
    }

    public String getDepartment() {
        return department.get();
    }

    public StringProperty departmentProperty() {
        return department;
    }

    public void setDepartment(String department) {
        this.department.set(department);
    }
}
