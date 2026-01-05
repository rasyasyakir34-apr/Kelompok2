package com.example.provideruas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HiburanActivity extends AppCompatActivity {

    private CardView bannerRekreaxis, cardUnlimitedGaming;
    private CardView cardFreeFire, cardMlbb, cardHonorKings, cardPubg;
    private LinearLayout tabGames, tabPaketNonton, tabPaketPremium;
    private TextView txtGames, txtPaketNonton, txtPaketPremium;
    private View indicatorGames;
    private TextView btnLihatSemuaGames;
    private LinearLayout navBeranda, navPlayground, navPaket, navHiburan, navAlifetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hiburan);

        initViews();
        setupClickListeners();
        handleBackPress();
    }

    private void initViews() {
        // Banner & Cards
        bannerRekreaxis = findViewById(R.id.bannerRekreaxis);
        cardUnlimitedGaming = findViewById(R.id.cardUnlimitedGaming);
        cardFreeFire = findViewById(R.id.cardFreeFire);
        cardMlbb = findViewById(R.id.cardMlbb);
        cardHonorKings = findViewById(R.id.cardHonorKings);
        cardPubg = findViewById(R.id.cardPubg);

        // Tabs
        tabGames = findViewById(R.id.tabGames);
        tabPaketNonton = findViewById(R.id.tabPaketNonton);
        tabPaketPremium = findViewById(R.id.tabPaketPremium);
        txtGames = findViewById(R.id.txtGames);
        txtPaketNonton = findViewById(R.id.txtPaketNonton);
        txtPaketPremium = findViewById(R.id.txtPaketPremium);
        indicatorGames = findViewById(R.id.indicatorGames);

        // Buttons
        btnLihatSemuaGames = findViewById(R.id.btnLihatSemuaGames);

        // Bottom Navigation
        navBeranda = findViewById(R.id.navBeranda);
        navPlayground = findViewById(R.id.navPlayground);
        navPaket = findViewById(R.id.navPaket);
        navHiburan = findViewById(R.id.navHiburan);
        navAlifetime = findViewById(R.id.navAlifetime);
    }

    private void setupClickListeners() {
        // Banner RekreAXIS
        bannerRekreaxis.setOnClickListener(v ->
                Toast.makeText(this, "RekreAXIS - Seru-seruan!", Toast.LENGTH_SHORT).show()
        );

        // Unlimited Gaming Card
        cardUnlimitedGaming.setOnClickListener(v ->
                Toast.makeText(this, "Kuota Unlimited Gaming", Toast.LENGTH_SHORT).show()
        );

        // Tabs
        tabGames.setOnClickListener(v ->
                Toast.makeText(this, "Tab Games aktif", Toast.LENGTH_SHORT).show()
        );

        tabPaketNonton.setOnClickListener(v ->
                Toast.makeText(this, "Paket Nonton", Toast.LENGTH_SHORT).show()
        );

        tabPaketPremium.setOnClickListener(v ->
                Toast.makeText(this, "Paket Premium", Toast.LENGTH_SHORT).show()
        );

        // Lihat Semua Games
        btnLihatSemuaGames.setOnClickListener(v ->
                Toast.makeText(this, "Lihat Semua Game Top Up", Toast.LENGTH_SHORT).show()
        );

        // Game Cards
        cardFreeFire.setOnClickListener(v ->
                Toast.makeText(this, "FREE FIRE - Top Up", Toast.LENGTH_SHORT).show()
        );

        cardMlbb.setOnClickListener(v ->
                Toast.makeText(this, "Mobile Legends - Top Up", Toast.LENGTH_SHORT).show()
        );

        cardHonorKings.setOnClickListener(v ->
                Toast.makeText(this, "Honor of Kings Voucher", Toast.LENGTH_SHORT).show()
        );

        cardPubg.setOnClickListener(v ->
                Toast.makeText(this, "PUBG Mobile - Top Up", Toast.LENGTH_SHORT).show()
        );

        // Bottom Navigation
        navBeranda.setOnClickListener(v -> {
            Intent intent = new Intent(HiburanActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        navPlayground.setOnClickListener(v -> {
            Intent intent = new Intent(HiburanActivity.this, PlaygroundActivity.class);
            startActivity(intent);
            finish();
        });

        navPaket.setOnClickListener(v -> {
            Intent intent = new Intent(HiburanActivity.this, PaketActivity.class);
            startActivity(intent);
            finish();
        });

        navHiburan.setOnClickListener(v ->
                Toast.makeText(this, "Sudah di Hiburan", Toast.LENGTH_SHORT).show()
        );

        navAlifetime.setOnClickListener(v ->
                Toast.makeText(this, "Halaman Alifetime", Toast.LENGTH_SHORT).show()
        );

        navAlifetime.setOnClickListener(v -> {
            Intent intent = new Intent(HiburanActivity.this, AlifetimeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Kembali ke MainActivity (Beranda)
                Intent intent = new Intent(HiburanActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}