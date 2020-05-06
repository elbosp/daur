package id.daur.model;

public class FeedRule {
    private int batas_bawah, batas_atas;
    private String id_menu;

    public FeedRule() {
    }

    public FeedRule(int batas_bawah, int batas_atas, String id_menu) {
        this.batas_bawah = batas_bawah;
        this.batas_atas = batas_atas;
        this.id_menu = id_menu;
    }

    public int getBatas_bawah() {
        return batas_bawah;
    }

    public void setBatas_bawah(int batas_bawah) {
        this.batas_bawah = batas_bawah;
    }

    public int getBatas_atas() {
        return batas_atas;
    }

    public void setBatas_atas(int batas_atas) {
        this.batas_atas = batas_atas;
    }

    public String getId_menu() {
        return id_menu;
    }

    public void setId_menu(String id_menu) {
        this.id_menu = id_menu;
    }
}
