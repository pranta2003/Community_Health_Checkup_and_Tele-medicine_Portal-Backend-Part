package com.Community.demo.payload;

public class AdminStats {
    private int userCount;
    private int appointmentCount;
    private int screeningCount;

    public AdminStats() {}

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public int getAppointmentCount() {
        return appointmentCount;
    }

    public void setAppointmentCount(int appointmentCount) {
        this.appointmentCount = appointmentCount;
    }

    public int getScreeningCount() {
        return screeningCount;
    }

    public void setScreeningCount(int screeningCount) {
        this.screeningCount = screeningCount;
    }
}
