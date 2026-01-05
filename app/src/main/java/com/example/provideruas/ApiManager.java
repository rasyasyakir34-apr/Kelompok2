package com.example.provideruas;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiManager {
    private static final String TAG = "ApiManager";

    // URL Ngrok Anda
    private static final String BASE_URL = "https://findable-christie-encinal.ngrok-free.dev/provider_uas_api/";

    private static final String REGISTER_URL = BASE_URL + "register.php";
    private static final String LOGIN_URL = BASE_URL + "login.php";
    private static final String GET_SALDO_URL = BASE_URL + "get_saldo.php";
    private static final String ISI_PULSA_URL = BASE_URL + "isi_pulsa.php";
    private static final String GET_HISTORY_URL = BASE_URL + "get_history.php";
    private static final String TRANSFER_PULSA_URL = BASE_URL + "transfer_pulsa.php";
    private static final String BELI_PAKET_URL = BASE_URL + "beli_paket.php";
    // URL BARU untuk getUserData
    private static final String GET_USER_DATA_URL = BASE_URL + "get_user_data.php";


    private final ExecutorService executor;
    private final Handler mainHandler;

    private static ApiManager instance;

    public static synchronized ApiManager getInstance() {
        if (instance == null) {
            instance = new ApiManager();
        }
        return instance;
    }

    private ApiManager() {
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    // Interface untuk callback
    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    // Helper method untuk setup connection
    private HttpURLConnection setupConnection(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestProperty("ngrok-skip-browser-warning", "true");
        conn.setRequestProperty("User-Agent", "ProviderUAS-Android");
        return conn;
    }

    // Helper method untuk baca response
    private String readResponse(HttpURLConnection conn) throws Exception {
        BufferedReader reader;
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    // Helper method untuk validasi JSON response
    private boolean isValidJSON(String response) {
        if (response == null || response.isEmpty()) return false;
        String trimmed = response.trim();
        return !trimmed.startsWith("<!DOCTYPE") &&
                !trimmed.startsWith("<html") &&
                !trimmed.startsWith("<br") &&
                (trimmed.startsWith("{") || trimmed.startsWith("["));
    }

    // Method untuk register
    public void register(String nama, String email, String nomorTelepon, String password, ApiCallback callback) {
        executor.execute(() -> {
            try {
                HttpURLConnection conn = setupConnection(REGISTER_URL);

                String postData = "nama=" + URLEncoder.encode(nama, "UTF-8") +
                        "&email=" + URLEncoder.encode(email, "UTF-8") +
                        "&nomor_telepon=" + URLEncoder.encode(nomorTelepon, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                }

                String response = readResponse(conn);
                Log.d(TAG, "Register response: " + response);

                if (!isValidJSON(response)) {
                    mainHandler.post(() -> callback.onError("Server error: Response bukan JSON"));
                    return;
                }

                JSONObject jsonResponse = new JSONObject(response);

                mainHandler.post(() -> {
                    if (jsonResponse.optBoolean("success", false)) {
                        callback.onSuccess(jsonResponse);
                    } else {
                        callback.onError(jsonResponse.optString("message", "Registrasi gagal"));
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Register error", e);
                mainHandler.post(() -> callback.onError("Error: " + e.getMessage()));
            }
        });
    }

    // Method untuk login
    public void login(String nomorTelepon, String password, ApiCallback callback) {
        executor.execute(() -> {
            try {
                HttpURLConnection conn = setupConnection(LOGIN_URL);

                String postData = "nomor_telepon=" + URLEncoder.encode(nomorTelepon, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                }

                String response = readResponse(conn);
                Log.d(TAG, "Login response: " + response);

                if (!isValidJSON(response)) {
                    mainHandler.post(() -> callback.onError("Server error: Response bukan JSON"));
                    return;
                }

                JSONObject jsonResponse = new JSONObject(response);

                mainHandler.post(() -> {
                    if (jsonResponse.optBoolean("success", false)) {
                        callback.onSuccess(jsonResponse);
                    } else {
                        callback.onError(jsonResponse.optString("message", "Login gagal"));
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Login error", e);
                mainHandler.post(() -> callback.onError("Error: " + e.getMessage()));
            }
        });
    }

    // Method untuk get saldo
    public void getSaldo(String nomorTelepon, ApiCallback callback) {
        executor.execute(() -> {
            try {
                HttpURLConnection conn = setupConnection(GET_SALDO_URL);

                String postData = "nomor_telepon=" + URLEncoder.encode(nomorTelepon, "UTF-8");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                }

                String response = readResponse(conn);
                Log.d(TAG, "Get saldo response: " + response);

                if (!isValidJSON(response)) {
                    mainHandler.post(() -> callback.onError("Server error: Response bukan JSON"));
                    return;
                }

                JSONObject jsonResponse = new JSONObject(response);

                mainHandler.post(() -> {
                    if (jsonResponse.optBoolean("success", false)) {
                        callback.onSuccess(jsonResponse);
                    } else {
                        callback.onError(jsonResponse.optString("message", "Gagal ambil saldo"));
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Get saldo error", e);
                mainHandler.post(() -> callback.onError("Error: " + e.getMessage()));
            }
        });
    }

    // Method untuk isi pulsa
    public void isiPulsa(String nomorTelepon, String nomorTujuan, int nominal, ApiCallback callback) {
        executor.execute(() -> {
            try {
                HttpURLConnection conn = setupConnection(ISI_PULSA_URL);

                String postData = "nomor_telepon=" + URLEncoder.encode(nomorTelepon, "UTF-8") +
                        "&nomor_tujuan=" + URLEncoder.encode(nomorTujuan, "UTF-8") +
                        "&nominal=" + nominal;

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                }

                String response = readResponse(conn);
                Log.d(TAG, "Isi pulsa response: " + response);

                if (!isValidJSON(response)) {
                    mainHandler.post(() -> callback.onError("Server error: Response bukan JSON"));
                    return;
                }

                JSONObject jsonResponse = new JSONObject(response);

                mainHandler.post(() -> {
                    if (jsonResponse.optBoolean("success", false)) {
                        callback.onSuccess(jsonResponse);
                    } else {
                        callback.onError(jsonResponse.optString("message", "Isi pulsa gagal"));
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Isi pulsa error", e);
                mainHandler.post(() -> callback.onError("Error: " + e.getMessage()));
            }
        });
    }

    // Method untuk get history transaksi
    public void getHistory(String nomorTelepon, ApiCallback callback) {
        executor.execute(() -> {
            try {
                HttpURLConnection conn = setupConnection(GET_HISTORY_URL);

                String postData = "nomor_telepon=" + URLEncoder.encode(nomorTelepon, "UTF-8") +
                        "&limit=50";

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                }

                String response = readResponse(conn);
                Log.d(TAG, "Get history response: " + response);

                if (!isValidJSON(response)) {
                    mainHandler.post(() -> callback.onError("Server error: Response bukan JSON"));
                    return;
                }

                JSONObject jsonResponse = new JSONObject(response);

                mainHandler.post(() -> {
                    if (jsonResponse.optBoolean("success", false)) {
                        callback.onSuccess(jsonResponse);
                    } else {
                        callback.onError(jsonResponse.optString("message", "Gagal ambil history"));
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Get history error", e);
                mainHandler.post(() -> callback.onError("Error: " + e.getMessage()));
            }
        });
    }

    // Method untuk transfer pulsa
    public void transferPulsa(String nomorPengirim, String nomorPenerima, int nominal, ApiCallback callback) {
        executor.execute(() -> {
            try {
                HttpURLConnection conn = setupConnection(TRANSFER_PULSA_URL);

                String postData = "nomor_pengirim=" + URLEncoder.encode(nomorPengirim, "UTF-8") +
                        "&nomor_penerima=" + URLEncoder.encode(nomorPenerima, "UTF-8") +
                        "&nominal=" + nominal;

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                }

                String response = readResponse(conn);
                Log.d(TAG, "Transfer pulsa response: " + response);

                if (!isValidJSON(response)) {
                    mainHandler.post(() -> callback.onError("Server error: Response bukan JSON"));
                    return;
                }

                JSONObject jsonResponse = new JSONObject(response);

                mainHandler.post(() -> {
                    if (jsonResponse.optBoolean("success", false)) {
                        callback.onSuccess(jsonResponse);
                    } else {
                        callback.onError(jsonResponse.optString("message", "Transfer pulsa gagal"));
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Transfer pulsa error", e);
                mainHandler.post(() -> callback.onError("Error: " + e.getMessage()));
            }
        });
    }

    // ✅ METHOD getUserData YANG SUDAH DIPERBAIKI
    // Menggunakan HttpURLConnection agar konsisten dengan method lain
    public void getUserData(String nomorTelepon, ApiCallback callback) {
        executor.execute(() -> {
            try {
                HttpURLConnection conn = setupConnection(GET_USER_DATA_URL);

                String postData = "nomor_telepon=" + URLEncoder.encode(nomorTelepon, "UTF-8");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                }

                String response = readResponse(conn);
                Log.d(TAG, "Get user data response: " + response);

                if (!isValidJSON(response)) {
                    mainHandler.post(() -> callback.onError("Server error: Response bukan JSON"));
                    return;
                }

                JSONObject jsonResponse = new JSONObject(response);

                mainHandler.post(() -> {
                    if (jsonResponse.optBoolean("success", false)) {
                        callback.onSuccess(jsonResponse);
                    } else {
                        callback.onError(jsonResponse.optString("message", "Gagal mengambil data pengguna"));
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Get user data error", e);
                mainHandler.post(() -> callback.onError("Error: " + e.getMessage()));
            }
        });
    }

    // Method untuk beli paket
    public void beliPaket(String nomorTelepon, String namaPaket, int harga, String masaAktif, ApiCallback callback) {
        executor.execute(() -> {
            try {
                HttpURLConnection conn = setupConnection(BELI_PAKET_URL);

                String postData = "nomor_telepon=" + URLEncoder.encode(nomorTelepon, "UTF-8") +
                        "&nama_paket=" + URLEncoder.encode(namaPaket, "UTF-8") +
                        "&harga=" + harga +
                        "&masa_aktif=" + URLEncoder.encode(masaAktif, "UTF-8");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                }

                String response = readResponse(conn);
                Log.d(TAG, "Beli paket response: " + response);

                if (!isValidJSON(response)) {
                    mainHandler.post(() -> callback.onError("Server error: Response bukan JSON. Pastikan file beli_paket.php sudah ada dan benar."));
                    return;
                }

                JSONObject jsonResponse = new JSONObject(response);

                mainHandler.post(() -> {
                    if (jsonResponse.optBoolean("success", false)) {
                        callback.onSuccess(jsonResponse);
                    } else {
                        callback.onError(jsonResponse.optString("message", "Pembelian paket gagal"));
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Beli paket error", e);
                mainHandler.post(() -> callback.onError("Error: " + e.getMessage()));
            }
        });
    }
}
