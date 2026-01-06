package com.example.hospital_system.Services;

import com.example.hospital_system.Dao.DashboardDao;
import java.util.Map;
import java.util.List;
import com.example.hospital_system.Models.RendezVous;

public class DashboardService {
    private DashboardDao dashboardDao = new DashboardDao();

    public Map<String, Integer> getDashboardData() {
        return dashboardDao.getDashboardStats();
    }

    public Map<Integer, Integer> getMonthlyAppointmentStats() {
        return dashboardDao.getMonthlyAppointmentStats();
    }

    public List<RendezVous> getUpcomingAppointments() {
        return dashboardDao.getUpcomingAppointments();
    }
}
