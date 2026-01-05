package com.example.provideruas;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONObject;

public class TransferPulsaActivity extends AppCompatActivity {

    private static final String TAG = "TransferPulsaActivity";

    private ImageView btnBack;
    private TextView tvSisaPulsa;
    private EditText etNomorTujuan;
    private EditText etJumlahPulsa;
    private TextView btnAmbilKontak;
    private CardView btn5000, btn10000, btn15000, btn20000, btn25000, btn30000;
    private Button btnLanjut;

    private String nomorTeleponUser;
    private int selectedNominal = 0;
    private int saldoSaatIni = 0;
    private ApiManager apiManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_pulsa);

        apiManager = ApiManager.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);

        initViews();
        loadUserData();
        setClickListeners();
        loadSaldoFromServer();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvSisaPulsa = findViewById(R.id.tvSisaPulsa);
        etNomorTujuan = findViewById(R.id.etNomorTujuan);
        etJumlahPulsa = findViewById(R.id.etJumlahPulsa);
        btnAmbilKontak = findViewById(R.id.btnAmbilKontak);
        btn5000 = findViewById(R.id.btn5000);
        btn10000 = findViewById(R.id.btn10000);
        btn15000 = findViewById(R.id.btn15000);
        btn20000 = findViewById(R.id.btn20000);
        btn25000 = findViewById(R.id.btn25000);
        btn30000 = findViewById(R.id.btn30000);
        btnLanjut = findViewById(R.id.btnLanjut);
    }

    private void loadUserData() {
        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        nomorTeleponUser = pref.getString("phone", "");

        Log.d(TAG, "Nomor user: " + nomorTeleponUser);
    }

    private void loadSaldoFromServer() {
        progressDialog.show();

        apiManager.getSaldo(nomorTeleponUser, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressDialog.dismiss();

                try {
                    Log.d(TAG, "Response getSaldo: " + response.toString());

                    JSONObject data = response.getJSONObject("data");
                    saldoSaatIni = data.getInt("saldo_pulsa");

                    tvSisaPulsa.setText("Rp" + formatRupiah(saldoSaatIni));

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing saldo", e);
                    tvSisaPulsa.setText("Rp0");
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                tvSisaPulsa.setText("Rp0");
                Log.e(TAG, "Error getSaldo: " + error);
                Toast.makeText(TransferPulsaActivity.this, "Gagal memuat saldo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnAmbilKontak.setOnClickListener(v ->
                Toast.makeText(this, "Fitur ambil dari kontak", Toast.LENGTH_SHORT).show()
        );

        btn5000.setOnClickListener(v -> selectNominal(5000, btn5000));
        btn10000.setOnClickListener(v -> selectNominal(10000, btn10000));
        btn15000.setOnClickListener(v -> selectNominal(15000, btn15000));
        btn20000.setOnClickListener(v -> selectNominal(20000, btn20000));
        btn25000.setOnClickListener(v -> selectNominal(25000, btn25000));
        btn30000.setOnClickListener(v -> selectNominal(30000, btn30000));

        btnLanjut.setOnClickListener(v -> prosesTransferPulsa());
    }

    private void selectNominal(int nominal, CardView selectedCard) {
        resetAllCards();

        selectedCard.setCardBackgroundColor(0xFFE91E8C);
        selectedNominal = nominal;
        etJumlahPulsa.setText(String.valueOf(nominal));
    }

    private void resetAllCards() {
        btn5000.setCardBackgroundColor(0xFFFFFFFF);
        btn10000.setCardBackgroundColor(0xFFFFFFFF);
        btn15000.setCardBackgroundColor(0xFFFFFFFF);
        btn20000.setCardBackgroundColor(0xFFFFFFFF);
        btn25000.setCardBackgroundColor(0xFFFFFFFF);
        btn30000.setCardBackgroundColor(0xFFFFFFFF);
    }

    private void prosesTransferPulsa() {
        String nomorTujuan = etNomorTujuan.getText().toString().trim();
        String jumlahStr = etJumlahPulsa.getText().toString().trim();

        Log.d(TAG, "Proses transfer - Nomor tujuan: " + nomorTujuan + ", Jumlah: " + jumlahStr);

        // Validasi
        if (nomorTujuan.isEmpty()) {
            Toast.makeText(this, "Masukkan nomor tujuan", Toast.LENGTH_SHORT).show();
            return;
        }

        if (jumlahStr.isEmpty()) {
            Toast.makeText(this, "Masukkan jumlah pulsa", Toast.LENGTH_SHORT).show();
            return;
        }

        int jumlah = Integer.parseInt(jumlahStr);

        if (jumlah < 5000) {
            Toast.makeText(this, "Minimal transfer Rp5.000", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi saldo mencukupi
        if (jumlah > saldoSaatIni) {
            Toast.makeText(this, "Saldo pulsa tidak mencukupi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tampilkan dialog konfirmasi
        showConfirmationDialog(nomorTujuan, jumlah);
    }

    private void showConfirmationDialog(String nomorTujuan, int jumlah) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Transfer")
                .setMessage("Transfer pulsa Rp" + formatRupiah(jumlah) + " ke nomor " + nomorTujuan + "?")
                .setPositiveButton("Ya", (dialog, which) -> executeTransfer(nomorTujuan, jumlah))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void executeTransfer(String nomorTujuan, int jumlah) {
        Log.d(TAG, "Execute transfer ke: " + nomorTujuan + " sejumlah: " + jumlah);

        // Show loading
        progressDialog.setMessage("Memproses transfer pulsa...");
        progressDialog.show();

        // Panggil API transfer pulsa
        apiManager.transferPulsa(nomorTeleponUser, nomorTujuan, jumlah, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressDialog.dismiss();

                try {
                    Log.d(TAG, "Response transfer: " + response.toString());

                    JSONObject data = response.getJSONObject("data");
                    int saldoBaru = data.getInt("saldo_sesudah");

                    // Update saldo lokal
                    saldoSaatIni = saldoBaru;
                    tvSisaPulsa.setText("Rp" + formatRupiah(saldoBaru));

                    // Tampilkan dialog sukses
                    showSuccessDialog(nomorTujuan, jumlah, saldoBaru);

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing response", e);
                    Toast.makeText(TransferPulsaActivity.this, "Transfer berhasil!", Toast.LENGTH_SHORT).show();

                    // Reset form
                    resetForm();
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();

                Log.e(TAG, "Transfer error: " + error);

                // Tampilkan pesan error yang lebih user-friendly
                String errorMessage = error;
                if (error.contains("tidak terdaftar")) {
                    errorMessage = "Nomor tujuan tidak terdaftar";
                } else if (error.contains("saldo")) {
                    errorMessage = "Saldo tidak mencukupi";
                } else if (error.contains("DOCTYPE")) {
                    errorMessage = "Server error. Pastikan API endpoint sudah benar";
                }

                Toast.makeText(TransferPulsaActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSuccessDialog(String nomorTujuan, int jumlah, int saldoBaru) {
        new AlertDialog.Builder(this)
                .setTitle("Transfer Berhasil!")
                .setMessage("Pulsa Rp" + formatRupiah(jumlah) + " berhasil ditransfer ke " + nomorTujuan +
                        "\n\nSaldo Anda sekarang: Rp" + formatRupiah(saldoBaru))
                .setPositiveButton("OK", (dialog, which) -> {
                    resetForm();
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void resetForm() {
        etNomorTujuan.setText("");
        etJumlahPulsa.setText("");
        selectedNominal = 0;
        resetAllCards();
    }

    private String formatRupiah(int nominal) {
        return String.format("%,d", nominal).replace(",", ".");
    }
}