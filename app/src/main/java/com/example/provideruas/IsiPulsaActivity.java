package com.example.provideruas;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONObject;

public class IsiPulsaActivity extends AppCompatActivity {

    private static final String TAG = "IsiPulsaActivity";

    private ImageView btnBack;
    private TextView tvPulsaTersedia;
    private EditText etNomorHandphone;
    private TextView btnAmbilDariKontak;
    private CardView btn25k, btn50k, btn100k, btn200k, btn300k, btn500k, btn1jt;
    private Button btnLanjut;

    private String selectedNominal = "";
    private int selectedNominalValue = 0;
    private String nomorTeleponUser;
    private ApiManager apiManager;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isi_pulsa); // PERBAIKAN: Nama layout harus lowercase dengan underscore

        apiManager = ApiManager.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);

        initViews();
        loadUserData();
        setClickListeners();

        // Load saldo dari server
        loadSaldoFromServer();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvPulsaTersedia = findViewById(R.id.tvPulsaTersedia);
        etNomorHandphone = findViewById(R.id.etNomorHandphone);
        btnAmbilDariKontak = findViewById(R.id.btnAmbilDariKontak);
        btn25k = findViewById(R.id.btn25k);
        btn50k = findViewById(R.id.btn50k);
        btn100k = findViewById(R.id.btn100k);
        btn200k = findViewById(R.id.btn200k);
        btn300k = findViewById(R.id.btn300k);
        btn500k = findViewById(R.id.btn500k);
        btn1jt = findViewById(R.id.btn1jt);
        btnLanjut = findViewById(R.id.btnLanjut);
    }

    private void loadUserData() {
        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        nomorTeleponUser = pref.getString("phone", "");

        // Set nomor HP user sendiri sebagai default
        etNomorHandphone.setText(nomorTeleponUser);
    }

    private void loadSaldoFromServer() {
        progressDialog.show();

        apiManager.getSaldo(nomorTeleponUser, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressDialog.dismiss();

                try {
                    JSONObject data = response.getJSONObject("data");
                    int saldo = data.getInt("saldo_pulsa");

                    tvPulsaTersedia.setText("Rp" + formatRupiah(saldo));

                    Log.d(TAG, "Saldo loaded: " + saldo);

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing saldo", e);
                    tvPulsaTersedia.setText("Rp0");
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();

                Log.e(TAG, "Error loading saldo: " + error);
                tvPulsaTersedia.setText("Rp0");
                Toast.makeText(IsiPulsaActivity.this, "Gagal memuat saldo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnAmbilDariKontak.setOnClickListener(v ->
                Toast.makeText(this, "Fitur ambil dari kontak", Toast.LENGTH_SHORT).show()
        );

        btn25k.setOnClickListener(v -> selectNominal("25.000", 25000, btn25k));
        btn50k.setOnClickListener(v -> selectNominal("50.000", 50000, btn50k));
        btn100k.setOnClickListener(v -> selectNominal("100.000", 100000, btn100k));
        btn200k.setOnClickListener(v -> selectNominal("200.000", 200000, btn200k));
        btn300k.setOnClickListener(v -> selectNominal("300.000", 300000, btn300k));
        btn500k.setOnClickListener(v -> selectNominal("500.000", 500000, btn500k));
        btn1jt.setOnClickListener(v -> selectNominal("1.000.000", 1000000, btn1jt));

        btnLanjut.setOnClickListener(v -> prosesIsiPulsa());
    }

    private void selectNominal(String nominal, int value, CardView selectedCard) {
        resetAllCards();

        selectedCard.setCardBackgroundColor(0xFFE91E8C);
        selectedNominal = nominal;
        selectedNominalValue = value;
    }

    private void resetAllCards() {
        btn25k.setCardBackgroundColor(0xFFFFFFFF);
        btn50k.setCardBackgroundColor(0xFFFFFFFF);
        btn100k.setCardBackgroundColor(0xFFFFFFFF);
        btn200k.setCardBackgroundColor(0xFFFFFFFF);
        btn300k.setCardBackgroundColor(0xFFFFFFFF);
        btn500k.setCardBackgroundColor(0xFFFFFFFF);
        btn1jt.setCardBackgroundColor(0xFFFFFFFF);
    }

    private void prosesIsiPulsa() {
        String nomorTujuan = etNomorHandphone.getText().toString().trim();

        // Validasi
        if (nomorTujuan.isEmpty()) {
            Toast.makeText(this, "Masukkan nomor handphone", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedNominalValue == 0) {
            Toast.makeText(this, "Pilih nominal pulsa", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        progressDialog.setMessage("Memproses isi pulsa...");
        progressDialog.show();

        // Panggil API isi pulsa
        apiManager.isiPulsa(nomorTeleponUser, nomorTujuan, selectedNominalValue, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressDialog.dismiss();

                try {
                    JSONObject data = response.getJSONObject("data");
                    int saldoSebelum = data.getInt("saldo_sebelum");
                    int saldoSesudah = data.getInt("saldo_sesudah");
                    int nominal = data.getInt("nominal");

                    Log.d(TAG, "Isi pulsa success - Saldo: " + saldoSebelum + " -> " + saldoSesudah);

                    Toast.makeText(IsiPulsaActivity.this,
                            "Isi pulsa Rp" + formatRupiah(nominal) + " berhasil!\nSaldo: Rp" + formatRupiah(saldoSesudah),
                            Toast.LENGTH_LONG).show();

                    // Kembali ke MainActivity
                    finish();

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing response", e);
                    Toast.makeText(IsiPulsaActivity.this, "Isi pulsa berhasil!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();

                Log.e(TAG, "Isi pulsa error: " + error);
                Toast.makeText(IsiPulsaActivity.this, "Gagal: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String formatRupiah(int nominal) {
        return String.format("%,d", nominal).replace(",", ".");
    }
}