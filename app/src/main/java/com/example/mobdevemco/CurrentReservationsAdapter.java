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

import java.util.List;
import java.util.Objects;

public class CurrentReservationsAdapter extends RecyclerView.Adapter<CurrentReservationsAdapter.ViewHolder> {
    private List<ReservationData> reservationDataList;
    private Context context;

    public CurrentReservationsAdapter(List<ReservationData> reservationDataList, CurrentReservations mainActivity) {
        this.reservationDataList = reservationDataList;
        this.context = mainActivity;
    }

    @NonNull
    @Override
    public CurrentReservationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.reservation_item_list_current, parent, false);
        return new CurrentReservationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentReservationsAdapter.ViewHolder holder, int position) {
        final ReservationData reservationData = reservationDataList.get(position);
        holder.courtName.setText(reservationData.getCourtName());
        holder.reservationDate.setText("Date: " + reservationData.getReservationDate());
        holder.reservationTime.setText("Time: " + reservationData.getReservationTimeSlot());

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog before removing the reservation
                new AlertDialog.Builder(context)
                        .setTitle("Cancel Reservation")
                        .setMessage("Are you sure you want to cancel the reservation for " + reservationData.getCourtName() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Call the remove method if user confirms
                                removeItemById(reservationData.getId());
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
        return reservationDataList.size();
    }

    public void removeItemById(String reservationId) {
        for (int i = 0; i < reservationDataList.size(); i++) {
            if (Objects.equals(reservationDataList.get(i).getId(), reservationId)) {
                reservationDataList.remove(i);
                notifyItemRemoved(i); // Notify RecyclerView about the item removed
                break;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
