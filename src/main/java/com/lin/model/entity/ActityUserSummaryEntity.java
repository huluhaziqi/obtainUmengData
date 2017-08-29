package com.lin.model.entity;

public class ActityUserSummaryEntity {
    /**
     * yesterday_data : 458
     * last_7_days_data : 2021
     * last_7_days_rate : 22.7
     * last_30_days_data : 5556
     * last_30_days_rate : 8.2
     */

    private int yesterday_data;
    private int last_7_days_data;
    private String last_7_days_rate;
    private int last_30_days_data;
    private String last_30_days_rate;


    public int getYesterday_data() {
        return yesterday_data;
    }

    public void setYesterday_data(int yesterday_data) {
        this.yesterday_data = yesterday_data;
    }

    public int getLast_7_days_data() {
        return last_7_days_data;
    }

    public void setLast_7_days_data(int last_7_days_data) {
        this.last_7_days_data = last_7_days_data;
    }

    public String getLast_7_days_rate() {
        return last_7_days_rate;
    }

    public void setLast_7_days_rate(String last_7_days_rate) {
        this.last_7_days_rate = last_7_days_rate;
    }

    public int getLast_30_days_data() {
        return last_30_days_data;
    }

    public void setLast_30_days_data(int last_30_days_data) {
        this.last_30_days_data = last_30_days_data;
    }

    public String getLast_30_days_rate() {
        return last_30_days_rate;
    }

    public void setLast_30_days_rate(String last_30_days_rate) {
        this.last_30_days_rate = last_30_days_rate;
    }
}
