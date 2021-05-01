package com.sb.guesthouse.model;

public class Guest {
    private String GuestName;
    private String Department;
    private String Company;
    private String date;
    private String time;
    private String days;
    private String absenceDates;

    public String getAbsenceDates() {
        return absenceDates;
    }

    public void setAbsenceDates(String absenceDates) {
        this.absenceDates = absenceDates;
    }

    private String enddate;
    private String status;
    private boolean isTimeLimit;

    public String getGuestName() {
        return GuestName;
    }

    public void setGuestName(String guestName) {
        GuestName = guestName;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isTimeLimit() {
        return isTimeLimit;
    }

    public void setTimeLimit(boolean timeLimit) {
        isTimeLimit = timeLimit;
    }

    public Guest(String guestName, String department, String company, String date, String time, String days,String absDates, String enddate, String status, boolean isTimeLimit) {
        GuestName = guestName;
        Department = department;
        Company = company;
        this.date = date;
        this.time = time;
        this.days = days;
        this.absenceDates = absDates;
        this.enddate = enddate;
        this.status = status;
        this.isTimeLimit = isTimeLimit;
    }
}
