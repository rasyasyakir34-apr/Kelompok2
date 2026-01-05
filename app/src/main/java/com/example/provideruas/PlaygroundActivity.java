package com.example.provideruas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PlaygroundActivity extends AppCompatActivity {

    private Button btnKenalan, btnInfo;
    private Button btnLihatParade, btnLihatSureprize, btnLihatMystery, btnLihatTopUp;
    private CardView cardParadeBonus, cardSureprize, cardMysteryBox, cardTopUpSureprize;
    private LinearLayout navBeranda, navPlayground, navPaket, navHiburan, navAlifetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);

        // Inisialisasi views
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Handle back press
        handleBackPress();
    }

    private void initViews() {
        // Top buttons
        btnKenalan = findViewById(R.id.btnKenalan);
        btnInfo = findViewById(R.id.btnInfo);

        // Bonus cards
        cardParadeBonus = findViewById(R.id.cardParadeBonus);
        cardSureprize = findViewById(R.id.cardSureprize);
        cardMysteryBox = findViewById(R.id.cardMysteryBox);
        cardTopUpSureprize = findViewById(R.id.cardTopUpSureprize);

        // Lihat bonus buttons
        btnLihatParade = findViewById(R.id.btnLihatParade);
        btnLihatSureprize = findViewById(R.id.btnLihatSureprize);
        btnLihatMystery = findViewById(R.id.btnLihatMystery);
        btnLihatTopUp = findViewById(R.id.btnLihatTopUp);

        // Bottom navigation
        navBeranda = findViewById(R.id.navBeranda);
        navPlayground = findViewById(R.id.navPlayground);
        navPaket = findViewById(R.id.navPaket);
        navHiburan = findViewById(R.id.navHiburan);
        navAlifetime = findViewById(R.id.navAlifetime);
    }

    private void setupClickListeners() {
        // Top buttons
        btnKenalan.setOnClickListener(v ->
                Toast.makeText(this, "Kenalan", Toast.LENGTH_SHORT).show()
        );

        btnInfo.setOnClickListener(v ->
                Toast.makeText(this, "Info Playground", Toast.LENGTH_SHORT).show()
        );

        // Parade Bonus
        btnLihatParade.setOnClickListener(v ->
                Toast.makeText(this, "Detail Parade Bonus", Toast.LENGTH_SHORT).show()
        );

        cardParadeBonus.setOnClickListener(v ->
                Toast.makeText(this, "Parade Bonus", Toast.LENGTH_SHORT).show()
        );

        // Sureprize
        btnLihatSureprize.setOnClickListener(v ->
                Toast.makeText(this, "Detail Sureprize", Toast.LENGTH_SHORT).show()
        );

        cardSureprize.setOnClickListener(v ->
                Toast.makeText(this, "Sureprize", Toast.LENGTH_SHORT).show()
        );

        // Mystery Box
        btnLihatMystery.setOnClickListener(v ->
                Toast.makeText(this, "Detail Mystery Box", Toast.LENGTH_SHORT).show()
        );

        cardMysteryBox.setOnClickListener(v ->
                Toast.makeText(this, "Mystery Box", Toast.LENGTH_SHORT).show()
        );

        // Top Up Sureprize
        btnLihatTopUp.setOnClickListener(v ->
                Toast.makeText(this, "Detail Top Up Sureprize", Toast.LENGTH_SHORT).show()
        );

        cardTopUpSureprize.setOnClickListener(v ->
                Toast.makeText(this, "Top Up Sureprize", Toast.LENGTH_SHORT).show()
        );

        // ========== TIDAK ADA PERUBAHAN PADA BOTTOM NAVIGATION ==========
        // Kode sudah benar, sudah ada intent ke AlifetimeActivity

        // Bottom Navigation
        navBeranda.setOnClickListener(v -> {
            Intent intent = new Intent(PlaygroundActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        navPlayground.setOnClickListener(v ->
                Toast.makeText(this, "Sudah di Playground", Toast.LENGTH_SHORT).show()
        );

        navPaket.setOnClickListener(v -> {
            Intent intent = new Intent(PlaygroundActivity.this, PaketActivity.class);
            startActivity(intent);
            finish();
        });

        navHiburan.setOnClickListener(v -> {
            Intent intent = new Intent(PlaygroundActivity.this, HiburanActivity.class);
            startActivity(intent);
            finish();
        });

        navAlifetime.setOnClickListener(v -> {
            Intent intent = new Intent(PlaygroundActivity.this, AlifetimeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Kembali ke MainActivity (Beranda)
                Intent intent = new Intent(PlaygroundActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}