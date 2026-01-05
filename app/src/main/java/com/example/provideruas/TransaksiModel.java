package com.example.provideruas;

public class TransaksiModel {
    private int id;
    private String nomorTujuan;
    private int nominal;
    private String jenisTransaksi;
    private String status;
    private int saldoSebelum;
    private int saldoSesudah;
    private String tanggal;

    // Constructors
    public TransaksiModel() {
        // Default constructor
    }

    public TransaksiModel(int id, String nomorTujuan, int nominal, String jenisTransaksi,
                          String status, int saldoSebelum, int saldoSesudah, String tanggal) {
        this.id = id;
        this.nomorTujuan = nomorTujuan;
        this.nominal = nominal;
        this.jenisTransaksi = jenisTransaksi;
        this.status = status;
        this.saldoSebelum = saldoSebelum;
        this.saldoSesudah = saldoSesudah;
        this.tanggal = tanggal;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNomorTujuan() {
        return nomorTujuan != null ? nomorTujuan : "-";
    }

    public int getNominal() {
        return nominal;
    }

    public String getJenisTransaksi() {
        return jenisTransaksi != null ? jenisTransaksi : "TRANSAKSI";
    }

    public String getStatus() {
        return status != null ? status : "success";
    }

    public int getSaldoSebelum() {
        return saldoSebelum;
    }

    public int getSaldoSesudah() {
        return saldoSesudah;
    }

    public String getTanggal() {
        return tanggal != null ? tanggal : "-";
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNomorTujuan(String nomorTujuan) {
        this.nomorTujuan = nomorTujuan;
    }

    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    public void setJenisTransaksi(String jenisTransaksi) {
        this.jenisTransaksi = jenisTransaksi;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSaldoSebelum(int saldoSebelum) {
        this.saldoSebelum = saldoSebelum;
    }

    public void setSaldoSesudah(int saldoSesudah) {
        this.saldoSesudah = saldoSesudah;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    // ✅ Helper method untuk format jenis transaksi
    public String getJenisTransaksiFormatted() {
        if (jenisTransaksi == null || jenisTransaksi.isEmpty()) {
            return "TRANSAKSI";
        }

        // Handle berbagai format (uppercase, lowercase, dengan/tanpa underscore)
        String jenis = jenisTransaksi.toUpperCase();

        switch (jenis) {
            case "ISI_PULSA":
            case "ISIPULSA":
                return "ISI PULSA";

            case "BELI_PAKET":
            case "BELIPAKET":
                return "BELI PAKET";

            case "TRANSFER":
            case "TRANSFER_PULSA":
            case "TRANSFERPULSA":
                return "TRANSFER PULSA";

            case "CONVERT_PULSA":
            case "CONVERTPULSA":
                return "CONVERT PULSA";

            case "PULSA_DARURAT":
            case "PULSADARURAT":
                return "PULSA DARURAT";

            case "PERPANJANGAN_MASA_AKTIF":
            case "PERPANJANGANMASAAKTIF":
                return "PERPANJANGAN MASA AKTIF";

            case "VOUCHER":
            case "ISI_VOUCHER":
                return "ISI VOUCHER";

            default:
                // Fallback: replace underscore dengan spasi dan capitalize
                return jenis.replace("_", " ");
        }
    }

    // ✅ Helper method untuk format status
    public String getStatusFormatted() {
        if (status == null || status.isEmpty()) {
            return "Berhasil";
        }

        String statusLower = status.toLowerCase();

        switch (statusLower) {
            case "success":
            case "berhasil":
            case "sukses":
                return "Berhasil";

            case "pending":
            case "proses":
                return "Pending";

            case "failed":
            case "gagal":
            case "error":
                return "Gagal";

            default:
                return status;
        }
    }

    // ✅ Helper method untuk cek status transaksi
    public boolean isSuccess() {
        if (status == null) return true;
        String statusLower = status.toLowerCase();
        return statusLower.equals("success") || statusLower.equals("berhasil") || statusLower.equals("sukses");
    }

    public boolean isPending() {
        if (status == null) return false;
        String statusLower = status.toLowerCase();
        return statusLower.equals("pending") || statusLower.equals("proses");
    }

    public boolean isFailed() {
        if (status == null) return false;
        String statusLower = status.toLowerCase();
        return statusLower.equals("failed") || statusLower.equals("gagal") || statusLower.equals("error");
    }

    // ✅ Override toString untuk debugging
    @Override
    public String toString() {
        return "TransaksiModel{" +
                "id=" + id +
                ", nomorTujuan='" + nomorTujuan + '\'' +
                ", nominal=" + nominal +
                ", jenisTransaksi='" + jenisTransaksi + '\'' +
                ", status='" + status + '\'' +
                ", saldoSebelum=" + saldoSebelum +
                ", saldoSesudah=" + saldoSesudah +
                ", tanggal='" + tanggal + '\'' +
                '}';
    }
}