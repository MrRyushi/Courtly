package com.example.mobdevemco;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {
    ReservationData[] reservationData;
    Context context;

    public ReservationAdapter(ReservationData[] reservationData, ReservationsHistory mainActivity) {
        this.reservationData = reservationData;
        this.context = mainActivity;
    }


    @NonNull
    @Override
    public ReservationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.reservation_item_list,parent,false);
        ReservationAdapter.ViewHolder viewHolder = new ReservationAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationAdapter.ViewHolder holder, int position) {
        final ReservationData reservationDataList = reservationData[position];
        holder.courtName.setText(reservationDataList.getCourtName());
        holder.reservationDate.setText("Date: " + reservationDataList.getReservationDate());
        holder.reservationTime.setText("Time: " + reservationDataList.getReservationTime());
    }

    @Override
    public int getItemCount() {
        return reservationData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView courtName;
        TextView reservationDate;
        TextView reservationTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courtName = itemView.findViewById(R.id.courtName);
            reservationDate = itemView.findViewById(R.id.reservationDate);
            reservationTime = itemView.findViewById(R.id.reservationTime);
        }
    }
}
