package com.example.provideruas;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONObject;

public class Kuota_RekomendasiActivity extends AppCompatActivity {

    private Button btnHanyaUntukmu, btnForeverOnline, btnBronet;
    private LinearLayout layoutHanyaUntukmu, layoutForeverOnline, layoutBronet;
    private TextView btnBack;

    private ApiManager apiManager;
    private String nomorTeleponUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuota_rekomendasi);

        // Inisialisasi API Manager
        apiManager = ApiManager.getInstance();

        // Get nomor telepon dari session
        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        nomorTeleponUser = pref.getString("phone", "");

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);

        // Inisialisasi views
        btnBack = findViewById(R.id.btnBack);
        btnHanyaUntukmu = findViewById(R.id.btnHanyaUntukmu);
        btnForeverOnline = findViewById(R.id.btnForeverOnline);
        btnBronet = findViewById(R.id.btnBronet);

        layoutHanyaUntukmu = findViewById(R.id.layoutHanyaUntukmu);
        layoutForeverOnline = findViewById(R.id.layoutForeverOnline);
        layoutBronet = findViewById(R.id.layoutBronet);

        // Tampilkan tab pertama secara default
        showTab("hanyauntukmu");

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Tab buttons
        btnHanyaUntukmu.setOnClickListener(v -> showTab("hanyauntukmu"));
        btnForeverOnline.setOnClickListener(v -> showTab("foreveronline"));
        btnBronet.setOnClickListener(v -> showTab("bronet"));

        // ✅ SET ONCLICK UNTUK SEMUA CARD PAKET
        setupPaketClickListeners();
    }

    private void setupPaketClickListeners() {
        // HANYA UNTUKMU - Card 1: 3GB
        View card1 = layoutHanyaUntukmu.getChildAt(0); // CardView pertama
        card1.setOnClickListener(v ->
                showKonfirmasiDialog("3GB", 5300, "1 hari")
        );

        // HANYA UNTUKMU - Card 2: 2GB (TERCUCOK)
        View card2 = layoutHanyaUntukmu.getChildAt(1); // CardView kedua
        card2.setOnClickListener(v ->
                showKonfirmasiDialog("Bronet 1GB + Kuota di kotamu DISC30%", 7490, "5 hari")
        );

        // FOREVER ONLINE - WhatsApp 2GB
        View cardWA = layoutForeverOnline.getChildAt(1); // CardView di Forever Online
        cardWA.setOnClickListener(v ->
                showKonfirmasiDialog("WHATSAPP 2GB", 4000, "7 hari")
        );

        // BRONET - 1GB
        View cardBronet = layoutBronet.getChildAt(1); // CardView di Bronet
        cardBronet.setOnClickListener(v ->
                showKonfirmasiDialog("Bronet 24Jam 1GB", 4900, "1 hari")
        );
    }

    private void showKonfirmasiDialog(String namaPaket, int harga, String masaAktif) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Pembelian");

        String message = "Paket: " + namaPaket + "\n" +
                "Harga: Rp" + formatRupiah(harga) + "\n" +
                "Masa Aktif: " + masaAktif + "\n\n" +
                "Pembayaran menggunakan pulsa. Lanjutkan?";

        builder.setMessage(message);

        builder.setPositiveButton("Beli Sekarang", (dialog, which) -> {
            beliPaket(namaPaket, harga, masaAktif);
        });

        builder.setNegativeButton("Batal", null);

        builder.show();
    }

    private void beliPaket(String namaPaket, int harga, String masaAktif) {
        progressDialog.show();

        apiManager.beliPaket(nomorTeleponUser, namaPaket, harga, masaAktif, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressDialog.dismiss();

                // Tampilkan dialog sukses
                showSuksesDialog(namaPaket, harga);
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();

                // Tampilkan error
                new AlertDialog.Builder(Kuota_RekomendasiActivity.this)
                        .setTitle("Pembelian Gagal")
                        .setMessage(error)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private void showSuksesDialog(String namaPaket, int harga) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✅ Pembelian Berhasil!");

        String message = "Paket " + namaPaket + " sebesar Rp" + formatRupiah(harga) +
                " berhasil diaktifkan!\n\n" +
                "Saldo pulsa kamu telah dikurangi.";

        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Kembali ke MainActivity
            finish();
        });

        builder.show();
    }

    private void showTab(String tab) {
        // Sembunyikan semua layout
        layoutHanyaUntukmu.setVisibility(View.GONE);
        layoutForeverOnline.setVisibility(View.GONE);
        layoutBronet.setVisibility(View.GONE);

        // Reset semua button ke state unselected
        resetButtons();

        // Tampilkan layout yang dipilih dan highlight button
        switch (tab) {
            case "hanyauntukmu":
                layoutHanyaUntukmu.setVisibility(View.VISIBLE);
                btnHanyaUntukmu.setBackgroundColor(0xFFC8E815); // Yellow
                btnHanyaUntukmu.setTextColor(0xFF000000); // Black
                break;
            case "foreveronline":
                layoutForeverOnline.setVisibility(View.VISIBLE);
                btnForeverOnline.setBackgroundColor(0xFFC8E815); // Yellow
                btnForeverOnline.setTextColor(0xFF000000); // Black
                break;
            case "bronet":
                layoutBronet.setVisibility(View.VISIBLE);
                btnBronet.setBackgroundColor(0xFFC8E815); // Yellow
                btnBronet.setTextColor(0xFF000000); // Black
                break;
        }
    }

    private void resetButtons() {
        btnHanyaUntukmu.setBackgroundColor(0xFFFFFFFF); // White
        btnForeverOnline.setBackgroundColor(0xFFFFFFFF); // White
        btnBronet.setBackgroundColor(0xFFFFFFFF); // White

        btnHanyaUntukmu.setTextColor(0xFF666666); // Gray
        btnForeverOnline.setTextColor(0xFF666666); // Gray
        btnBronet.setTextColor(0xFF666666); // Gray
    }

    private String formatRupiah(int nominal) {
        return String.format("%,d", nominal).replace(",", ".");
    }
}