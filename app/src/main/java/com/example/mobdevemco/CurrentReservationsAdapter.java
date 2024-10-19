package com.example.mobdevemco;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        holder.courtName.setText(reservationDataList.getCourtName());
        holder.reservationDate.setText("Date: " + reservationDataList.getReservationDate());
        holder.reservationTime.setText("Time: " + reservationDataList.getReservationTime());
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog before removing the reservation
                new AlertDialog.Builder(context)
                        .setTitle("Cancel Reservation")
                        .setMessage("Are you sure you want to cancel the reservation for " + reservationDataList.getCourtName() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Call the remove method if user confirms
                                removeItemById(reservationDataList.getId());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss(); // Close dialog without removing
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservationData.length;
    }

    public void removeItemById(int reservationId) {
        List<ReservationData> tempList = new ArrayList<>(Arrays.asList(reservationData));
        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).getId() == reservationId) {
                tempList.remove(i);
                break;
            }
        }
        reservationData = tempList.toArray(new ReservationData[0]);
        notifyDataSetChanged();
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
