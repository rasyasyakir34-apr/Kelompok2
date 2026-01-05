package com.example.provideruas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private List<TransaksiModel> transaksiList;

    public HistoryAdapter(Context context, List<TransaksiModel> transaksiList) {
        this.context = context;
        this.transaksiList = transaksiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransaksiModel transaksi = transaksiList.get(position);

        holder.tvJenisTransaksi.setText(transaksi.getJenisTransaksiFormatted());
        holder.tvNomorTujuan.setText(transaksi.getNomorTujuan());
        holder.tvNominal.setText("Rp" + formatRupiah(transaksi.getNominal()));
        holder.tvTanggal.setText(transaksi.getTanggal());
        holder.tvSaldoSebelum.setText("Saldo Sebelum: Rp" + formatRupiah(transaksi.getSaldoSebelum()));
        holder.tvSaldoSesudah.setText("Saldo Sesudah: Rp" + formatRupiah(transaksi.getSaldoSesudah()));

        // Set status color
        if (transaksi.getStatus().equals("success")) {
            holder.tvStatus.setText("Berhasil");
            holder.tvStatus.setTextColor(0xFF4CAF50); // Green
        } else if (transaksi.getStatus().equals("pending")) {
            holder.tvStatus.setText("Pending");
            holder.tvStatus.setTextColor(0xFFFFC107); // Orange
        } else {
            holder.tvStatus.setText("Gagal");
            holder.tvStatus.setTextColor(0xFFF44336); // Red
        }
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJenisTransaksi, tvNomorTujuan, tvNominal, tvTanggal;
        TextView tvSaldoSebelum, tvSaldoSesudah, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJenisTransaksi = itemView.findViewById(R.id.tvJenisTransaksi);
            tvNomorTujuan = itemView.findViewById(R.id.tvNomorTujuan);
            tvNominal = itemView.findViewById(R.id.tvNominal);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvSaldoSebelum = itemView.findViewById(R.id.tvSaldoSebelum);
            tvSaldoSesudah = itemView.findViewById(R.id.tvSaldoSesudah);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

    private String formatRupiah(int nominal) {
        return String.format("%,d", nominal).replace(",", ".");
    }
}