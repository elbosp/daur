package id.daur.model;

public class Taskz {
    private int sarapan, makan_siang, makan_malam;

    public Taskz() {
        this.sarapan = 0;
        this.makan_siang = 0;
        this.makan_malam = 0;
    }

    public Taskz(int sarapan, int makan_siang, int makan_malam) {
        this.sarapan = sarapan;
        this.makan_siang = makan_siang;
        this.makan_malam = makan_malam;
    }

    public int getSarapan() {
        return sarapan;
    }

    public void setSarapan(int sarapan) {
        this.sarapan = sarapan;
    }

    public int getMakan_siang() {
        return makan_siang;
    }

    public void setMakan_siang(int makan_siang) {
        this.makan_siang = makan_siang;
    }

    public int getMakan_malam() {
        return makan_malam;
    }

    public void setMakan_malam(int makan_malam) {
        this.makan_malam = makan_malam;
    }
}
