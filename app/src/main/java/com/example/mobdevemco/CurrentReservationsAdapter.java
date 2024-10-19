package com.example.mobdevemco;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CurrentReservationsAdapter extends RecyclerView.Adapter<CurrentReservationsAdapter.ViewHolder>{
    ReservationData[] reservationData;
    Context context;

    public CurrentReservationsAdapter(ReservationData[] reservationData, CurrentReservations mainActivity) {
        this.reservationData = reservationData;
        this.context = mainActivity;
    }


    @NonNull
    @Override
    public CurrentReservationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.reservation_item_list_current,parent,false);
        CurrentReservationsAdapter.ViewHolder viewHolder = new CurrentReservationsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentReservationsAdapter.ViewHolder holder, int position) {
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courtName = itemView.findViewById(R.id.courtName);
            reservationDate = itemView.findViewById(R.id.reservationDate);
            reservationTime = itemView.findViewById(R.id.reservationTime);
        }
    }
}
