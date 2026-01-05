package com.example.provideruas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    private TextView tvNamaUser, tvNomorTelepon;
    private LinearLayout btnKeamanan, btnHistory, btnVoucher, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        // Inisialisasi views
        tvNamaUser = findViewById(R.id.tv_nama_user);
        tvNomorTelepon = findViewById(R.id.tv_nomor_telepon);
        btnKeamanan = findViewById(R.id.btn_keamanan);
        btnHistory = findViewById(R.id.btn_history);
        btnVoucher = findViewById(R.id.btn_voucher);
        btnLogout = findViewById(R.id.btn_logout);

        // Ambil data user dari SharedPreferences
        SharedPreferences userData = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);

        String nama = userData.getString("nama", "AXISer");
        String phone = session.getString("phone", userData.getString("phone", ""));

        // Tampilkan data user
        tvNamaUser.setText(nama);
        tvNomorTelepon.setText(phone);

        // Set click listeners
        btnKeamanan.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur Keamanan/Login Cepat", Toast.LENGTH_SHORT).show();
        });

        // PERBAIKAN: Hapus nested listener, langsung ke HistoryActivity
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        btnVoucher.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur Voucher", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            // 1. Ambil SharedPreferences sesi
            SharedPreferences.Editor editor = session.edit();

            // 2. Hapus data sesi yang tersimpan
            editor.clear();
            editor.apply();

            // 3. Tampilkan pesan dan pindah ke LoginActivity
            Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show();

            // 4. Buat intent untuk pindah ke LoginActivity
            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);

            // 5. Tambahkan flags untuk membersihkan "history" activity sebelumnya
            // Ini akan mencegah pengguna kembali ke SettingActivity dengan menekan tombol "back"
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }
}