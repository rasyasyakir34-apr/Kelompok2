package com.example.provideruas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView tvPhoneNumber;
    private TextView btnFavorite;
    private TextView btnChat;
    private TextView tvBalance;
    private TextView tvQuota;
    private TextView tvActiveUntil;
    private TextView tvDetailPaket;
    private Button btnNotification;
    private Button btnLogout;
    private LinearLayout menuVoucher;
    private LinearLayout menuBonusNonStop;
    private LinearLayout menuConvert;
    private LinearLayout menuPaketSukaSuka;
    private CardView cardOrangeBanner;
    private Button btnRekomendasi;
    private Button btnMendadakDiskon;
    private Button btnDate;
    private CardView cardPromo1;
    private CardView cardPromo2;
    private CardView cardPromo3;
    private LinearLayout navBeranda;
    private LinearLayout navPlayground;
    private LinearLayout navPaket;
    private LinearLayout navHiburan;
    private LinearLayout navAlifetime;

    private ApiManager apiManager;
    private String nomorTeleponUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);

        if (!pref.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        apiManager = ApiManager.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memuat data...");
        progressDialog.setCancelable(false);

        initViews();
        loadUserData();
        setClickListeners();
        handleBackPress();

        // ✅ Load data user (saldo + kuota) dari server
        loadUserDataFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ✅ Refresh data setiap kali kembali ke MainActivity
        loadUserDataFromServer();
    }

    private void initViews() {
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnChat = findViewById(R.id.btnChat);
        tvBalance = findViewById(R.id.tvBalance);
        tvQuota = findViewById(R.id.tvQuota);
        tvActiveUntil = findViewById(R.id.tvActiveUntil);
        tvDetailPaket = findViewById(R.id.tvDetailPaket);
        btnNotification = findViewById(R.id.btnNotification);
        btnLogout = findViewById(R.id.btnLogout);
        menuVoucher = findViewById(R.id.menuVoucher);
        menuBonusNonStop = findViewById(R.id.menuBonusNonStop);
        menuConvert = findViewById(R.id.menuConvert);
        menuPaketSukaSuka = findViewById(R.id.menuPaketSukaSuka);
        cardOrangeBanner = findViewById(R.id.cardOrangeBanner);
        btnRekomendasi = findViewById(R.id.btnRekomendasi);
        btnMendadakDiskon = findViewById(R.id.btnMendadakDiskon);
        btnDate = findViewById(R.id.btnDate);
        cardPromo1 = findViewById(R.id.cardPromo1);
        cardPromo2 = findViewById(R.id.cardPromo2);
        cardPromo3 = findViewById(R.id.cardPromo3);
        navBeranda = findViewById(R.id.navBeranda);
        navPlayground = findViewById(R.id.navPlayground);
        navPaket = findViewById(R.id.navPaket);
        navHiburan = findViewById(R.id.navHiburan);
        navAlifetime = findViewById(R.id.navAlifetime);
    }

    private void loadUserData() {
        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String phone = pref.getString("phone", "0831 4313 4024");
        nomorTeleponUser = phone;

        tvPhoneNumber.setText(phone);
    }

    // ✅ METHOD BARU: Load Saldo + Kuota dari Server
    private void loadUserDataFromServer() {
        progressDialog.show();

        apiManager.getUserData(nomorTeleponUser, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressDialog.dismiss();

                try {
                    JSONObject data = response.getJSONObject("data");
                    int saldo = data.getInt("saldo_pulsa");
                    double kuota = data.optDouble("kuota_internet", 0);
                    String masaAktif = data.optString("masa_aktif_kuota", null);

                    // ✅ Update UI dengan data dari server
                    tvBalance.setText("Rp" + formatRupiah(saldo));

                    // Format kuota
                    if (kuota > 0) {
                        if (kuota >= 1) {
                            tvQuota.setText(String.format("%.1f GB", kuota));
                        } else {
                            // Kalau kurang dari 1GB, tampilkan dalam MB
                            tvQuota.setText(String.format("%.0f MB", kuota * 1024));
                        }
                    } else {
                        tvQuota.setText("0 GB");
                    }

                    // Update masa aktif kalau ada
                    if (masaAktif != null && !masaAktif.equals("null")) {
                        tvActiveUntil.setText("Aktif s/d " + formatTanggal(masaAktif));
                    } else {
                        tvActiveUntil.setText("Tidak ada masa aktif");
                    }

                    Log.d(TAG, "User data loaded - Saldo: " + saldo + ", Kuota: " + kuota);

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing user data", e);
                    tvBalance.setText("Rp0");
                    tvQuota.setText("0 GB");
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();

                Log.e(TAG, "Error loading user data: " + error);
                tvBalance.setText("Rp0");
                tvQuota.setText("0 GB");
                Toast.makeText(MainActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setClickListeners() {
        btnFavorite.setOnClickListener(v -> showToast("Favorit"));
        btnChat.setOnClickListener(v -> showToast("Chat"));

        // UBAH: Klik Menu Pulsa Lainnya munculkan Bottom Sheet
        tvActiveUntil.setOnClickListener(v -> showPulsaBottomSheet());

        tvDetailPaket.setOnClickListener(v -> showToast("Detail Paket"));

        // ✅ BUTTON AKTIFKAN - PINDAH KE kuota_rekomendasiActivity
        btnNotification.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Kuota_RekomendasiActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());
        menuVoucher.setOnClickListener(v -> showToast("Isi Voucher AIGO"));
        menuBonusNonStop.setOnClickListener(v -> showToast("Bonus Non Stop"));
        menuConvert.setOnClickListener(v -> showToast("Convert Pulsa ke Kuota"));
        menuPaketSukaSuka.setOnClickListener(v -> showToast("Paket Suka-Suka"));
        cardOrangeBanner.setOnClickListener(v -> showToast("Segera isi pulsa atau aktivasi paket!"));
        btnRekomendasi.setOnClickListener(v -> selectTab(btnRekomendasi));
        btnMendadakDiskon.setOnClickListener(v -> selectTab(btnMendadakDiskon));
        btnDate.setOnClickListener(v -> selectTab(btnDate));
        cardPromo1.setOnClickListener(v -> showToast("Promo 2.5GB - Rp7.695"));
        cardPromo2.setOnClickListener(v -> showToast("Promo 3.5GB - Rp9.695"));
        cardPromo3.setOnClickListener(v -> showToast("Promo 6GB"));

        // Bottom Navigation
        navBeranda.setOnClickListener(v -> showToast("Sudah di Beranda"));

        navPlayground.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlaygroundActivity.class);
            startActivity(intent);
            finish();
        });

        navPaket.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaketActivity.class);
            startActivity(intent);
            finish();
        });

        navHiburan.setOnClickListener(v -> {
            Intent intent = new Intent(this, HiburanActivity.class);
            startActivity(intent);
            finish();
        });

        navAlifetime.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlifetimeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // METHOD BARU: Tampilkan Bottom Sheet Pulsa
    private void showPulsaBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.buttom_sheet_pulsa, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Set data pulsa dari tvBalance
        TextView tvPulsaAmount = bottomSheetView.findViewById(R.id.tvPulsaAmount);
        TextView tvPulsaExpiry = bottomSheetView.findViewById(R.id.tvPulsaExpiry);

        // Ambil saldo dari tvBalance
        tvPulsaAmount.setText(tvBalance.getText().toString());
        tvPulsaExpiry.setText("Exp on 26 Dec 2025");

        // Tombol close (X)
        bottomSheetView.findViewById(R.id.btnCloseBottomSheet).setOnClickListener(v ->
                bottomSheetDialog.dismiss()
        );

        // ✅ TOMBOL ISI PULSA - BUKA IsiPulsaActivity
        bottomSheetView.findViewById(R.id.btnIsiPulsa).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, IsiPulsaActivity.class);
            startActivity(intent);
            bottomSheetDialog.dismiss(); // Tutup bottom sheet
        });

        // Menu Transfer Pulsa
        bottomSheetView.findViewById(R.id.menuTransferPulsa).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransferPulsaActivity.class);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });

        // Menu Pulsa Darurat
        bottomSheetView.findViewById(R.id.menuPulsaDarurat).setOnClickListener(v -> {
            showToast("Pulsa Darurat");
            bottomSheetDialog.dismiss();
        });

        // Menu Perpanjangan Masa Aktif
        bottomSheetView.findViewById(R.id.menuPerpanjanganMasaAktif).setOnClickListener(v -> {
            showToast("Perpanjangan Masa Aktif");
            bottomSheetDialog.dismiss();
        });

        // Menu Convert Pulsa
        bottomSheetView.findViewById(R.id.menuConvertPulsaBottomSheet).setOnClickListener(v -> {
            showToast("Convert Pulsa ke Kuota");
            bottomSheetDialog.dismiss();
        });

        // Tampilkan bottom sheet
        bottomSheetDialog.show();
    }

    private void selectTab(Button selected) {
        int white = 0xFFFFFFFF;
        int pink = 0xFFE91E8C;
        int purple = 0xFF8B3FA0;

        btnRekomendasi.setBackgroundTintList(android.content.res.ColorStateList.valueOf(white));
        btnMendadakDiskon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(white));
        btnDate.setBackgroundTintList(android.content.res.ColorStateList.valueOf(white));

        btnRekomendasi.setTextColor(pink);
        btnMendadakDiskon.setTextColor(pink);
        btnDate.setTextColor(pink);

        selected.setBackgroundTintList(android.content.res.ColorStateList.valueOf(purple));
        selected.setTextColor(white);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Yakin ingin keluar?");
        builder.setPositiveButton("Ya", (d, w) -> logout());
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void logout() {
        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();
    }

    private void handleBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(MainActivity.this, "Silakan logout melalui tombol CEK BONUS", Toast.LENGTH_SHORT).show();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private String formatRupiah(int nominal) {
        return String.format("%,d", nominal).replace(",", ".");
    }

    // ✅ METHOD BARU: Format tanggal dari YYYY-MM-DD ke format Indonesia
    private String formatTanggal(String tanggal) {
        try {
            String[] parts = tanggal.split("-");
            if (parts.length == 3) {
                String[] bulan = {"", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
                        "Jul", "Agu", "Sep", "Okt", "Nov", "Des"};
                int bulanInt = Integer.parseInt(parts[1]);
                return parts[2] + " " + bulan[bulanInt] + " " + parts[0];
            }
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date", e);
        }
        return tanggal;
    }
}