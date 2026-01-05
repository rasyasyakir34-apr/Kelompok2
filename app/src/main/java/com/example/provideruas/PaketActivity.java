package com.example.provideruas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaketActivity extends AppCompatActivity {

    private ImageView btnBack;
    private LinearLayout navBeranda;
    private LinearLayout navPlayground;
    private LinearLayout navPaket;
    private LinearLayout navHiburan;
    private LinearLayout navAlifetime;
    private LinearLayout categoryInternetPromo;
    private LinearLayout categoryInternet;
    private LinearLayout categoryLifestyle;
    private LinearLayout categoryTelpSms;
    private LinearLayout categoryAxisZone;
    private LinearLayout categoryBoostr;
    private LinearLayout categoryTengGo;
    private LinearLayout categoryRoaming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paket);

        initViews();
        setClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        categoryInternetPromo = findViewById(R.id.categoryInternetPromo);
        categoryInternet = findViewById(R.id.categoryInternet);
        categoryLifestyle = findViewById(R.id.categoryLifestyle);
        categoryTelpSms = findViewById(R.id.categoryTelpSms);
        categoryAxisZone = findViewById(R.id.categoryAxisZone);
        categoryBoostr = findViewById(R.id.categoryBoostr);
        categoryTengGo = findViewById(R.id.categoryTengGo);
        categoryRoaming = findViewById(R.id.categoryRoaming);
        navBeranda = findViewById(R.id.navBeranda);
        navPlayground = findViewById(R.id.navPlayground);
        navPaket = findViewById(R.id.navPaket);
        navHiburan = findViewById(R.id.navHiburan);
        navAlifetime = findViewById(R.id.navAlifetime);
    }

    private void setClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Buka KuotaRekomendasiActivity saat klik Internet Promo
        categoryInternetPromo.setOnClickListener(v -> {
            Intent intent = new Intent(PaketActivity.this, Kuota_RekomendasiActivity.class);
            startActivity(intent);
        });

        categoryInternet.setOnClickListener(v -> showToast("Internet"));
        categoryLifestyle.setOnClickListener(v -> showToast("Lifestyle"));
        categoryTelpSms.setOnClickListener(v -> showToast("Telp & SMS"));
        categoryAxisZone.setOnClickListener(v -> showToast("AXIS Zone"));
        categoryBoostr.setOnClickListener(v -> showToast("Boostr"));
        categoryTengGo.setOnClickListener(v -> showToast("TENG-GO"));
        categoryRoaming.setOnClickListener(v -> showToast("Roaming"));

        navBeranda.setOnClickListener(v -> {
            Intent intent = new Intent(PaketActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        navPlayground.setOnClickListener(v -> {
            Intent intent = new Intent(PaketActivity.this, PlaygroundActivity.class);
            startActivity(intent);
            finish();
        });

        navPaket.setOnClickListener(v -> showToast("Sudah di Paket"));

        navHiburan.setOnClickListener(v -> {
            Intent intent = new Intent(PaketActivity.this, HiburanActivity.class);
            startActivity(intent);
            finish();
        });

        navAlifetime.setOnClickListener(v -> {
            Intent intent = new Intent(PaketActivity.this, AlifetimeActivity.class);
            startActivity(intent);
            finish();
        });
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}