package com.patan.gimnasio.domain;

public class ExFromRoutine {
    private long id;
    private int repetitions;
    private int series;
    private double relaxTime;

    public ExFromRoutine (long id, int rep, int ser, double rT) {
        this.id = id;
        this.repetitions = rep;
        this.series = ser;
        this.relaxTime = rT;
    }
    public void setId(long id) {this.id = id;}

    public void setRelxTime(double relxTime) {
        this.relaxTime = relxTime;
    }

    public void setRep(int rep) {
        this.repetitions = rep;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public long getId() {return id;}

    public double getRelxTime() {
        return relaxTime;
    }

    public int getRep() {
        return repetitions;
    }

    public int getSeries() {
        return series;
    }

}
