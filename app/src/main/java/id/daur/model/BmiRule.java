package id.daur.model;

public class BmiRule {
    private int batas_atas, batas_bawah;
    private String status;

    public BmiRule() {

    }

    public BmiRule(int batas_atas, int batas_bawah, String status) {
        this.batas_atas = batas_atas;
        this.batas_bawah = batas_bawah;
        this.status = status;
    }

    public int getBatas_atas() {
        return batas_atas;
    }

    public void setBatas_atas(int batas_atas) {
        this.batas_atas = batas_atas;
    }

    public int getBatas_bawah() {
        return batas_bawah;
    }

    public void setBatas_bawah(int batas_bawah) {
        this.batas_bawah = batas_bawah;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
