package id.daur.model;

public class FoodComposition {
    private String nama_makanan, ket_makanan, nama_menu;
    private String gambar_url, detail_link;
    private int total_kal;

    public FoodComposition() {
    }

    public FoodComposition(String nama_makanan, String ket_makanan, String nama_menu, String gambar_url, String detail_link, int total_kal) {
        this.nama_makanan = nama_makanan;
        this.ket_makanan = ket_makanan;
        this.nama_menu = nama_menu;
        this.gambar_url = gambar_url;
        this.detail_link = detail_link;
        this.total_kal = total_kal;
    }

    public String getGambar_url() {
        return gambar_url;
    }

    public void setGambar_url(String gambar_url) {
        this.gambar_url = gambar_url;
    }

    public String getDetail_link() {
        return detail_link;
    }

    public void setDetail_link(String detail_link) {
        this.detail_link = detail_link;
    }

    public String getNama_makanan() {
        return nama_makanan;
    }

    public void setNama_makanan(String nama_makanan) {
        this.nama_makanan = nama_makanan;
    }

    public String getKet_makanan() {
        return ket_makanan;
    }

    public void setKet_makanan(String ket_makanan) {
        this.ket_makanan = ket_makanan;
    }

    public String getNama_menu() {
        return nama_menu;
    }

    public void setNama_menu(String nama_menu) {
        this.nama_menu = nama_menu;
    }

    public int getTotal_kal() {
        return total_kal;
    }

    public void setTotal_kal(int total_kal) {
        this.total_kal = total_kal;
    }
}
