package com.example.provideruas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AlifetimeActivity extends AppCompatActivity {

    private Button btnAlifetime, btnSettings, btnCare;
    private Button btnDetail, btnNaikLevel;
    private CardView cardCaraMain;
    private CardView cardBonus1, cardBonus2;
    private LinearLayout navBeranda, navPlayground, navPaket, navHiburan, navAlifetime;

    // Carousel banner
    private ImageView imgBanner;
    private ImageView btnPrevious, btnNext;
    private int[] bannerImages = {
            R.drawable.banner_1,
            R.drawable.banner_2,
            R.drawable.banner_3,
            R.drawable.banner_4,
            R.drawable.banner_5
    };
    private int currentBannerIndex = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alifetime);

        initViews();
        setupClickListeners();
        setupCarousel();
        handleBackPress();
    }

    private void initViews() {
        // Top tabs
        btnAlifetime = findViewById(R.id.btnAlifetime);
        btnSettings = findViewById(R.id.btnSettings);
        btnCare = findViewById(R.id.btnCare);

        // Buttons
        btnDetail = findViewById(R.id.btnDetail);
        btnNaikLevel = findViewById(R.id.btnNaikLevel);

        // Cards
        cardCaraMain = findViewById(R.id.cardCaraMain);
        cardBonus1 = findViewById(R.id.cardBonus1);
        cardBonus2 = findViewById(R.id.cardBonus2);

        // Banner carousel
        imgBanner = findViewById(R.id.imgBanner);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        // Bottom Navigation
        navBeranda = findViewById(R.id.navBeranda);
        navPlayground = findViewById(R.id.navPlayground);
        navPaket = findViewById(R.id.navPaket);
        navHiburan = findViewById(R.id.navHiburan);
        navAlifetime = findViewById(R.id.navAlifetime);
    }

    private void setupCarousel() {
        // Set gambar pertama
        imgBanner.setImageResource(bannerImages[0]);

        // Auto slide
        startAutoSlide();

        // Manual slide dengan tombol
        btnPrevious.setOnClickListener(v -> {
            currentBannerIndex = (currentBannerIndex - 1 + bannerImages.length) % bannerImages.length;
            imgBanner.setImageResource(bannerImages[currentBannerIndex]);
        });

        btnNext.setOnClickListener(v -> {
            currentBannerIndex = (currentBannerIndex + 1) % bannerImages.length;
            imgBanner.setImageResource(bannerImages[currentBannerIndex]);
        });

        // Klik gambar untuk next
        imgBanner.setOnClickListener(v -> {
            currentBannerIndex = (currentBannerIndex + 1) % bannerImages.length;
            imgBanner.setImageResource(bannerImages[currentBannerIndex]);
        });
    }

    private void startAutoSlide() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentBannerIndex = (currentBannerIndex + 1) % bannerImages.length;
                imgBanner.setImageResource(bannerImages[currentBannerIndex]);
                handler.postDelayed(this, 3000); // Auto slide setiap 3 detik
            }
        }, 3000);
    }

    private void setupClickListeners() {
        // Top Tabs
        btnAlifetime.setOnClickListener(v ->
                Toast.makeText(this, "Sudah di Alifetime", Toast.LENGTH_SHORT).show()
        );

        // Settings - Pindah ke SettingsActivity
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(AlifetimeActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        btnCare.setOnClickListener(v ->
                Toast.makeText(this, "Care", Toast.LENGTH_SHORT).show()
        );

        // Detail & Naik Level
        btnDetail.setOnClickListener(v ->
                Toast.makeText(this, "Detail AXIS Coin", Toast.LENGTH_SHORT).show()
        );

        btnNaikLevel.setOnClickListener(v ->
                Toast.makeText(this, "Naik ke Level Temenan", Toast.LENGTH_SHORT).show()
        );

        // Cara Main Card
        cardCaraMain.setOnClickListener(v ->
                Toast.makeText(this, "Cara Main ALIFETIME", Toast.LENGTH_SHORT).show()
        );

        // Bonus Cards
        cardBonus1.setOnClickListener(v ->
                Toast.makeText(this, "Bonus 1", Toast.LENGTH_SHORT).show()
        );

        cardBonus2.setOnClickListener(v ->
                Toast.makeText(this, "Bonus 2", Toast.LENGTH_SHORT).show()
        );

        // Bottom Navigation
        navBeranda.setOnClickListener(v -> {
            Intent intent = new Intent(AlifetimeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        navPlayground.setOnClickListener(v -> {
            Intent intent = new Intent(AlifetimeActivity.this, PlaygroundActivity.class);
            startActivity(intent);
            finish();
        });

        navPaket.setOnClickListener(v -> {
            Intent intent = new Intent(AlifetimeActivity.this, PaketActivity.class);
            startActivity(intent);
            finish();
        });

        navHiburan.setOnClickListener(v -> {
            Intent intent = new Intent(AlifetimeActivity.this, HiburanActivity.class);
            startActivity(intent);
            finish();
        });

        // Alifetime sudah aktif, tidak perlu pindah halaman
        navAlifetime.setOnClickListener(v ->
                Toast.makeText(this, "Sudah di Alifetime", Toast.LENGTH_SHORT).show()
        );
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(AlifetimeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}