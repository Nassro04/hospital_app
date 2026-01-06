package com.example.hospital_system.Services;

import com.example.hospital_system.Dao.RendezVousDao;
import com.example.hospital_system.Models.RendezVous;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.time.LocalDate;

public class RendezVousService {

    RendezVousDao rendezVousDao = new RendezVousDao();

    public boolean addRendezVous(String nom, String prenom, String telephone, String cin, String genre, int age,
            LocalDate date, String department, String doctor, String time) {
        boolean result = rendezVousDao.addRendezVousQuery(nom, prenom, telephone, cin, genre, age, date, department,
                doctor, time);

        if (result) {
            return true;
        }

        return false;
    }

    public ObservableList<RendezVous> getAllRendezVous() {
        ObservableList<RendezVous> rendezVous = rendezVousDao.getRendezVousQuery();
        return rendezVous;
    }

    public boolean updateRendezVous(RendezVous rendezVous) {
        int result = rendezVousDao.updateQuery(rendezVous);
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteRendezVous(RendezVous selectedRendezVous) {
        if (selectedRendezVous == null) {
            return false;
        }
        return rendezVousDao.deleteQuery(selectedRendezVous);
    }

}
