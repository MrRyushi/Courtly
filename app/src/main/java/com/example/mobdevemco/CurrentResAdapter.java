package com.example.mobdevemco;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CurrentResAdapter extends RecyclerView.Adapter<CurrentResAdapter.ViewHolder> {
    ReservationData[] reservationData;
    Context context;

    public CurrentResAdapter(ReservationData[] reservationData, ReservationsHistory mainActivity) {
        this.reservationData = reservationData;
        this.context = mainActivity;
    }


    @NonNull
    @Override
    public CurrentResAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.reservation_item_list_current, parent,false);
        CurrentResAdapter.ViewHolder viewHolder = new CurrentResAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentResAdapter.ViewHolder holder, int position) {
        final ReservationData reservationDataList = reservationData[position];
        holder.courtName.setText(reservationDataList.getCourtNname());
        holder.reservationDate.setText("Date: " + reservationDataList.getReservationDdate());
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
        Button cancelBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courtName = itemView.findViewById(R.id.courtName);
            reservationDate = itemView.findViewById(R.id.reservationDate);
            reservationTime = itemView.findViewById(R.id.reservationTime);
            cancelBtn = itemView.findViewById(R.id.cancelBtn);
        }
    }
}
