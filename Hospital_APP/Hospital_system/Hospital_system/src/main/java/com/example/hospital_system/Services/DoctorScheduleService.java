package com.example.hospital_system.Services;

import com.example.hospital_system.Dao.DoctorScheduleDao;
import com.example.hospital_system.Models.DoctorSchedule;

import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.LocalTime;

public class DoctorScheduleService {

    private final DoctorScheduleDao scheduleDao = new DoctorScheduleDao();

    public ObservableList<DoctorSchedule> getSchedulesByDoctorId(int doctorId) {
        return scheduleDao.getSchedulesByDoctorId(doctorId);
    }

    public boolean addSchedule(DoctorSchedule schedule) {
        return scheduleDao.addSchedule(schedule);
    }

    public boolean updateSchedule(DoctorSchedule schedule) {
        return scheduleDao.updateSchedule(schedule);
    }

    public boolean deleteSchedule(int scheduleId) {
        return scheduleDao.deleteSchedule(scheduleId);
    }

    public ObservableList<LocalTime> getAvailableTimeSlots(int doctorId, LocalDate date) {
        return scheduleDao.getAvailableTimeSlots(doctorId, date);
    }
}