package com.example.provideruas;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";

    private ImageView btnBack;
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    private TextView tvEmpty;

    private ApiManager apiManager;
    private ProgressDialog progressDialog;
    private HistoryAdapter adapter;
    private List<TransaksiModel> transaksiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        apiManager = ApiManager.getInstance();
        transaksiList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memuat history...");
        progressDialog.setCancelable(false);

        initViews();
        loadHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ✅ Refresh history setiap kali activity di-resume
        loadHistory();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerView);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvEmpty = findViewById(R.id.tvEmpty);

        btnBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(this, transaksiList);
        recyclerView.setAdapter(adapter);
    }

    private void loadHistory() {
        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String phone = pref.getString("phone", "");

        if (phone.isEmpty()) {
            Toast.makeText(this, "Nomor telepon tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        apiManager.getHistory(phone, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressDialog.dismiss();

                try {
                    Log.d(TAG, "Response: " + response.toString());

                    JSONArray data = response.getJSONArray("data");

                    transaksiList.clear();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);

                        TransaksiModel transaksi = new TransaksiModel();
                        transaksi.setId(item.getInt("id"));

                        // ✅ Ambil jenis transaksi dulu
                        String jenisTransaksi = item.getString("jenis_transaksi");
                        transaksi.setJenisTransaksi(jenisTransaksi);

                        // ✅ Handle berbagai jenis transaksi
                        if (jenisTransaksi.equals("BELI_PAKET")) {
                            // Untuk BELI_PAKET, ambil dari keterangan
                            String keterangan = item.optString("keterangan", "Pembelian Paket");
                            transaksi.setNomorTujuan(keterangan); // Simpan keterangan di field nomor_tujuan
                            transaksi.setStatus("BERHASIL"); // Default status
                        } else {
                            // Untuk transaksi lain (TRANSFER, ISI_PULSA, dll)
                            transaksi.setNomorTujuan(item.optString("nomor_tujuan", "-"));
                            transaksi.setStatus(item.optString("status", "BERHASIL"));
                        }

                        transaksi.setNominal(item.getInt("nominal"));
                        transaksi.setSaldoSebelum(item.optInt("saldo_sebelum", 0));
                        transaksi.setSaldoSesudah(item.optInt("saldo_sesudah", 0));
                        transaksi.setTanggal(item.getString("tanggal"));

                        transaksiList.add(transaksi);
                    }

                    adapter.notifyDataSetChanged();

                    // Show/hide empty state
                    if (transaksiList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText("Belum ada riwayat transaksi");
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                    }

                    Log.d(TAG, "History loaded: " + transaksiList.size() + " items");

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing history", e);
                    Toast.makeText(HistoryActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    // Show empty state
                    recyclerView.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();

                Log.e(TAG, "Error loading history: " + error);
                Toast.makeText(HistoryActivity.this, "Gagal memuat history: " + error, Toast.LENGTH_SHORT).show();

                // Show empty state
                recyclerView.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Gagal memuat riwayat transaksi");
            }
        });
    }
}