package com.example.mobdevemco;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReserveACourt extends AppCompatActivity {

    private CheckBox time6amTo7am, time7amTo8am, time8amTo9am, time9amTo10am, time10amTo11am, time11amTo12pm, time12pmTo1pm,
            time1pmTo2pm, time2pmTo3pm, time3pmTo4pm, time4pmTo5pm, time5pmTo6pm, time6pmTo7pm, time7pmTo8pm;

    private DatabaseReference mDatabase;
    private CalendarView calendarView;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_court_reservation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize checkboxes
        time6amTo7am = findViewById(R.id.time6amTo7am);
        time7amTo8am = findViewById(R.id.time7amTo8am);
        time8amTo9am = findViewById(R.id.time8amTo9am);
        time9amTo10am = findViewById(R.id.time9amTo10am);
        time10amTo11am = findViewById(R.id.time10amTo11am);
        time11amTo12pm = findViewById(R.id.time11amTo12pm);
        time12pmTo1pm = findViewById(R.id.time12pmTo1pm);
        time1pmTo2pm = findViewById(R.id.time1pmTo2pm);
        time2pmTo3pm = findViewById(R.id.time2pmTo3pm);
        time3pmTo4pm = findViewById(R.id.time3pmTo4pm);
        time4pmTo5pm = findViewById(R.id.time4pmTo5pm);
        time5pmTo6pm = findViewById(R.id.time5pmTo6pm);
        time6pmTo7pm = findViewById(R.id.time6pmTo7pm);
        time7pmTo8pm = findViewById(R.id.time7pmTo8pm);

        // Initialize calendar view
        calendarView = findViewById(R.id.calendarView);

        // court name
        TextView courtName = findViewById(R.id.courtName);
        courtName.setText(getIntent().getStringExtra("courtName"));

        // reserve button
        Button reserveBtn = findViewById(R.id.reserveBtn);
        reserveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationPopup();
            }
        });

    }

    public void handleBackBtnClick(View v){
        finish();
    }

    private void showConfirmationPopup() {
        // Retrieve selected time slots
        ArrayList<String> selectedTimeSlots = new ArrayList<>();

        if (time6amTo7am.isChecked()) {
            selectedTimeSlots.add("6:00 AM - 7:00 AM");
        }
        if (time7amTo8am.isChecked()) {
            selectedTimeSlots.add("7:00 AM - 8:00 AM");
        }
        if (time8amTo9am.isChecked()) {
            selectedTimeSlots.add("8:00 AM - 9:00 AM");
        }
        if (time9amTo10am.isChecked()) {
            selectedTimeSlots.add("9:00 AM - 10:00 AM");
        }
        if (time10amTo11am.isChecked()) {
            selectedTimeSlots.add("10:00 AM - 11:00 AM");
        }
        if (time11amTo12pm.isChecked()) {
            selectedTimeSlots.add("11:00 AM - 12:00 PM");
        }
        if (time12pmTo1pm.isChecked()) {
            selectedTimeSlots.add("12:00 PM - 1:00 PM");
        }
        if (time1pmTo2pm.isChecked()) {
            selectedTimeSlots.add("1:00 PM - 2:00 PM");
        }
        if (time2pmTo3pm.isChecked()) {
            selectedTimeSlots.add("2:00 PM - 3:00 PM");
        }
        if (time3pmTo4pm.isChecked()) {
            selectedTimeSlots.add("3:00 PM - 4:00 PM");
        }
        if (time4pmTo5pm.isChecked()) {
            selectedTimeSlots.add("4:00 PM - 5:00 PM");
        }
        if (time5pmTo6pm.isChecked()) {
            selectedTimeSlots.add("5:00 PM - 6:00 PM");
        }
        if (time6pmTo7pm.isChecked()) {
            selectedTimeSlots.add("6:00 PM - 7:00 PM");
        }
        if (time7pmTo8pm.isChecked()) {
            selectedTimeSlots.add("7:00 PM - 8:00 PM");
        }

        if (selectedTimeSlots.isEmpty()) {
            // If no slots selected, show a warning and return
            new AlertDialog.Builder(this)
                    .setTitle("No Time Slots Selected")
                    .setMessage("Please select at least one time slot to reserve.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Build confirmation message
        StringBuilder message = new StringBuilder("You have selected the following time slots:\n\n");
        for (String timeSlot : selectedTimeSlots) {
            message.append(timeSlot).append("\n");
        }
        message.append("\nDo you want to confirm this reservation?");

        // Show confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Reservation");
        builder.setMessage(message.toString());
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Code to handle reservation confirmation
                dialogInterface.dismiss();

                try {
                    // Save reservation to database
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        CalendarView calendarView = findViewById(R.id.calendarView);

                        // Get the selected date from CalendarView
                        long selectedDateInMillis = calendarView.getDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selectedDateInMillis);

                        // Format the date to "day/month/year"
                        String selectedDate = calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                                (calendar.get(Calendar.MONTH) + 1) + "/" +
                                calendar.get(Calendar.YEAR);

                        // Create a single reservation for all time slots
                        Reservations reservation = new Reservations(
                                getIntent().getStringExtra("courtName"),
                                selectedTimeSlots,  // Pass the entire list of selected time slots
                                selectedDate,
                                user.getUid()
                        );

                        // Save the single reservation
                        mDatabase.child("reservations").push().setValue(reservation)
                                .addOnSuccessListener(aVoid -> {
                                    showSuccessMessage();  // Display success message upon successful save
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    Toast toast = Toast.makeText(ReserveACourt.this, "An error occurred while reserving the court", Toast.LENGTH_SHORT);
                                    toast.show();
                                });

                    } else {
                        // No user is signed in
                        Toast.makeText(ReserveACourt.this, "Please sign in to reserve a court", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ReserveACourt.this, "An error occurred while reserving the court", Toast.LENGTH_SHORT).show();
                }



            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // Just close the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSuccessMessage() {
        new AlertDialog.Builder(this)
                .setTitle("Reservation Successful")
                .setMessage("Your time slots have been reserved successfully!")
                .setPositiveButton("OK", null)
                .show();
    }
}