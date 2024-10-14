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

public class CourtAdapter extends RecyclerView.Adapter<CourtAdapter.ViewHolder> {
    CourtData[] courtData;
    Context context;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    public CourtAdapter(CourtData[] courtData, Home mainActivity, ActivityResultLauncher<Intent> activityResultLauncher) {
        this.courtData = courtData;
        this.context = mainActivity;
        this.activityResultLauncher = activityResultLauncher;
    }


    @NonNull
    @Override
    public CourtAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.court_item_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CourtAdapter.ViewHolder holder, int position) {
        final CourtData courtDataList = courtData[position];
        holder.courtName.setText(courtDataList.getCourtName());
        holder.courtLocation.setText(courtDataList.getCourtLocation());
        holder.courtImage.setImageResource(courtDataList.getCourtImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Remove this and replace it with an intent call*/
                Intent i = new Intent(context, CourtReservation.class);
                i.putExtra("courtName", courtDataList.getCourtName());
                i.putExtra("courtLocation", courtDataList.getCourtLocation());
                i.putExtra("courtImage", courtDataList.getCourtImage());
                activityResultLauncher.launch(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courtData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView courtImage;
        TextView courtName;
        TextView courtLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courtImage = itemView.findViewById(R.id.courtImage);
            courtName = itemView.findViewById(R.id.courtName);
            courtLocation = itemView.findViewById(R.id.courtLocation);
        }
    }
}
