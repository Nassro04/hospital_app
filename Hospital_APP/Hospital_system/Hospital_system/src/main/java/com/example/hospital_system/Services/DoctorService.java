package com.example.hospital_system.Services;

import com.example.hospital_system.Dao.DoctorDao;
import com.example.hospital_system.Models.Doctor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DoctorService {

    private final DoctorDao doctorDao = new DoctorDao();
    private final ObservableList<Doctor> doctors = FXCollections.observableArrayList();

    public ObservableList<Doctor> getDoctors() {
        doctors.setAll(doctorDao.getDoctorsQuery());
        return doctors;
    }

    public boolean ajouterDoctor(Doctor doctor) {
        boolean ok = doctorDao.addDoctorQuery(doctor);
        if (ok) {
            doctors.add(doctor);
            // re-sync from DB to get generated id
            getDoctors();
        }
        return ok;
    }

    public boolean modifierDoctor(Doctor doctor) {
        int rows = doctorDao.editDoctorQuery(doctor);
        if (rows > 0) {
            getDoctors();
            return true;
        }
        return false;
    }

    public boolean supprimerDoctor(Doctor doctor) {
        boolean ok = doctorDao.deleteDoctorQuery(doctor);
        if (ok) {
            doctors.remove(doctor);
        }
        return ok;
    }
}
