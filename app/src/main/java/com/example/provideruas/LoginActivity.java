package com.example.provideruas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextInputEditText etPhoneNumber, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ApiManager apiManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cek session
        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        if (session.getBoolean("isLoggedIn", false)) {
            Log.d(TAG, "User already logged in");
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // Inisialisasi
        apiManager = ApiManager.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login...");
        progressDialog.setCancelable(false);

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Auto-fill dari register
        Intent intent = getIntent();
        if (intent.hasExtra("registered_phone")) {
            String registeredPhone = intent.getStringExtra("registered_phone");
            etPhoneNumber.setText(registeredPhone);
        }

        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            });
        }

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String phone = etPhoneNumber.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        // Validasi
        if (phone.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Nomor & password wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() < 10) {
            Toast.makeText(this, "Nomor telepon minimal 10 digit", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        progressDialog.show();

        // Panggil API login
        apiManager.login(phone, pass, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressDialog.dismiss();

                try {
                    JSONObject data = response.getJSONObject("data");
                    String nama = data.getString("nama");
                    String email = data.getString("email");
                    String nomorTelepon = data.getString("nomor_telepon");

                    Log.d(TAG, "Login success: " + nama);

                    // Simpan session
                    SharedPreferences.Editor editor = getSharedPreferences("UserSession", MODE_PRIVATE).edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("phone", nomorTelepon);
                    editor.putString("nama", nama);
                    editor.putString("email", email);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Login berhasil! Selamat datang " + nama, Toast.LENGTH_SHORT).show();

                    // Pindah ke MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("phone", nomorTelepon);
                    intent.putExtra("nama", nama);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing login response", e);
                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();

                Log.e(TAG, "Login error: " + error);
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();

                // Clear password
                etPassword.setText("");
                etPassword.requestFocus();
            }
        });
    }
}