package com.example.provideruas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText etNama, etEmail, etNomorTelepon, etPassword, etKonfirmasiPassword;
    private Button btnRegister;
    private TextView tvSudahPunyaAkun;
    private ApiManager apiManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Inisialisasi ApiManager
        apiManager = ApiManager.getInstance();

        // Inisialisasi progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mendaftar...");
        progressDialog.setCancelable(false);

        // Inisialisasi views
        etNama = findViewById(R.id.et_nama);
        etEmail = findViewById(R.id.et_email);
        etNomorTelepon = findViewById(R.id.et_nomor_telepon);
        etPassword = findViewById(R.id.et_password);
        etKonfirmasiPassword = findViewById(R.id.et_konfirmasi_password);
        btnRegister = findViewById(R.id.btn_register);
        tvSudahPunyaAkun = findViewById(R.id.tv_sudah_punya_akun);

        btnRegister.setOnClickListener(v -> registerUser());

        tvSudahPunyaAkun.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String nama = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String nomorTelepon = etNomorTelepon.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String konfirmasiPassword = etKonfirmasiPassword.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(nama)) {
            etNama.setError("Nama harus diisi");
            etNama.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email harus diisi");
            etEmail.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            etEmail.setError("Format email tidak valid");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nomorTelepon)) {
            etNomorTelepon.setError("Nomor telepon harus diisi");
            etNomorTelepon.requestFocus();
            return;
        }

        if (nomorTelepon.length() < 10) {
            etNomorTelepon.setError("Nomor telepon minimal 10 digit");
            etNomorTelepon.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password harus diisi");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(konfirmasiPassword)) {
            etKonfirmasiPassword.setError("Konfirmasi password harus diisi");
            etKonfirmasiPassword.requestFocus();
            return;
        }

        if (!password.equals(konfirmasiPassword)) {
            etKonfirmasiPassword.setError("Password tidak cocok");
            etKonfirmasiPassword.requestFocus();
            return;
        }

        // Show loading
        progressDialog.show();

        // Panggil API register
        apiManager.register(nama, email, nomorTelepon, password, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressDialog.dismiss();

                Log.d(TAG, "Register success: " + response.toString());
                Toast.makeText(RegisterActivity.this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show();

                // Clear input
                etNama.setText("");
                etEmail.setText("");
                etNomorTelepon.setText("");
                etPassword.setText("");
                etKonfirmasiPassword.setText("");

                // Pindah ke login
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("registered_phone", nomorTelepon);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();

                Log.e(TAG, "Register error: " + error);
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}