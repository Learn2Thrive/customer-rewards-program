package org.testcompany.customerrewards.domain;

public class MonthlyPoints {

    private int points;
    private int month;
    private int year;

    public MonthlyPoints(int points, int month, int year) {
        this.points = points;
        this.month = month;
        this.year = year;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
