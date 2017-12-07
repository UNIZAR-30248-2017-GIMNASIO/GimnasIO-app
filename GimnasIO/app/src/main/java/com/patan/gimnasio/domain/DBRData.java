package com.patan.gimnasio.domain;


public class DBRData {
    private double imageSize;
    private double dataSize;
    private double totalSize;
    private String lastUpdate;

    public DBRData (double is, double ds, double ts, String lu) {
        imageSize = is;
        dataSize = ds;
        totalSize = ts;
        lastUpdate = lu;
    }

    public double getDataSize() {
        return dataSize;
    }

    public double getImageSize() {
        return imageSize;
    }

    public double getTotalSize() {
        return totalSize;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setDataSize(double dataSize) {
        this.dataSize = dataSize;
    }

    public void setImageSize(double imageSize) {
        this.imageSize = imageSize;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setTotalSize(double totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
