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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

        // selected date
        // Get the selected date from CalendarView
        long selectedDateInMillis = calendarView.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDateInMillis);

        // Format the date to "day/month/year"
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                (calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.YEAR);

        // Initialize calendar view and set the date change listener
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Format the date to "day/month/year"
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

            // Clear all time slots to reset availability
            enableAllTimeSlots();

            // Refresh the availability for the selected date
            addReservationListener();
        });

        // Add the listener to monitor reservations
        addReservationListener();

        // Prefill the time slots
        prefillTimeSlots();
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

                        // Remove redundant date fetching logic from this part
                        CalendarView calendarView = findViewById(R.id.calendarView);
                        long selectedDateInMillis = calendarView.getDate();  // Potential issue
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selectedDateInMillis);

                        // Instead, use the already-updated `selectedDate`
                        System.out.println("selected date: " + selectedDate);


                        // Set the timezone to Hong Kong (Asia/Hong_Kong)
                        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));

                        // Get current date and time in Hong Kong timezone
                        String reservationDateTime = dateTimeFormat.format(new Date()); // e.g., "16/11/2024 15:30:45"

                        // Create a single reservation for all time slots
                        Reservations reservation = new Reservations(
                                getIntent().getStringExtra("courtName"),
                                selectedTimeSlots,  // Pass the entire list of selected time slots
                                selectedDate,       // Reserved date (selected by the user)
                                user.getUid(),
                                reservationDateTime // Reservation creation date and time in Hong Kong timezone
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
                .setPositiveButton("OK", (dialog, which) -> resetForm()) // Reset form on dialog dismissal
                .show();
    }


    // Method to add the Firebase listener
    private void addReservationListener() {
        // Get the selected court name from the intent
        String courtName = getIntent().getStringExtra("courtName");

        mDatabase.child("reservations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot reservationSnapshot : snapshot.getChildren()) {
                        Reservations reservation = reservationSnapshot.getValue(Reservations.class);

                        // Ensure the reservation is valid and matches the selected date and court name
                        if (reservation != null
                                && selectedDate.equals(reservation.getDate())
                                && courtName.equals(reservation.getCourtName())) {
                            // Disable reserved time slots
                            disableReservedTimeSlots(reservation.getTimeSlots());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReserveACourt.this, "Failed to load reservations", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to disable timeslots based on reserved time slots
    private void disableReservedTimeSlots(List<String> reservedTimeSlots) {
        // Clear all timeslots first (optional, depends on your logic)
        enableAllTimeSlots();

        // Iterate through the reserved times and disable the checkboxes
        for (String timeSlot : reservedTimeSlots) {
            switch (timeSlot) {
                case "6:00 AM - 7:00 AM":
                    time6amTo7am.setEnabled(false);
                    break;
                case "7:00 AM - 8:00 AM":
                    time7amTo8am.setEnabled(false);
                    break;
                case "8:00 AM - 9:00 AM":
                    time8amTo9am.setEnabled(false);
                    break;
                case "9:00 AM - 10:00 AM":
                    time9amTo10am.setEnabled(false);
                    break;
                case "10:00 AM - 11:00 AM":
                    time10amTo11am.setEnabled(false);
                    break;
                case "11:00 AM - 12:00 PM":
                    time11amTo12pm.setEnabled(false);
                    break;
                case "12:00 PM - 1:00 PM":
                    time12pmTo1pm.setEnabled(false);
                    break;
                case "1:00 PM - 2:00 PM":
                    time1pmTo2pm.setEnabled(false);
                    break;
                case "2:00 PM - 3:00 PM":
                    time2pmTo3pm.setEnabled(false);
                    break;
                case "3:00 PM - 4:00 PM":
                    time3pmTo4pm.setEnabled(false);
                    break;
                case "4:00 PM - 5:00 PM":
                    time4pmTo5pm.setEnabled(false);
                    break;
                case "5:00 PM - 6:00 PM":
                    time5pmTo6pm.setEnabled(false);
                    break;
                case "6:00 PM - 7:00 PM":
                    time6pmTo7pm.setEnabled(false);
                    break;
                case "7:00 PM - 8:00 PM":
                    time7pmTo8pm.setEnabled(false);
                    break;
            }
        }
    }

    // Method to enable all timeslots
    private void enableAllTimeSlots() {
        time6amTo7am.setEnabled(true);
        time7amTo8am.setEnabled(true);
        time8amTo9am.setEnabled(true);
        time9amTo10am.setEnabled(true);
        time10amTo11am.setEnabled(true);
        time11amTo12pm.setEnabled(true);
        time12pmTo1pm.setEnabled(true);
        time1pmTo2pm.setEnabled(true);
        time2pmTo3pm.setEnabled(true);
        time3pmTo4pm.setEnabled(true);
        time4pmTo5pm.setEnabled(true);
        time5pmTo6pm.setEnabled(true);
        time6pmTo7pm.setEnabled(true);
        time7pmTo8pm.setEnabled(true);
    }

    private void resetForm() {
        time6amTo7am.setChecked(false);
        time7amTo8am.setChecked(false);
        time8amTo9am.setChecked(false);
        time9amTo10am.setChecked(false);
        time10amTo11am.setChecked(false);
        time11amTo12pm.setChecked(false);
        time12pmTo1pm.setChecked(false);
        time1pmTo2pm.setChecked(false);
        time2pmTo3pm.setChecked(false);
        time3pmTo4pm.setChecked(false);
        time4pmTo5pm.setChecked(false);
        time5pmTo6pm.setChecked(false);
        time6pmTo7pm.setChecked(false);
        time7pmTo8pm.setChecked(false);
    }

    private void prefillTimeSlots(){
        // Get the selected time slots from the intent
        String timeSlots = getIntent().getStringExtra("timeSlots");
        if(timeSlots != null){
            String[] slots = timeSlots.split(",");
            for(String slot : slots){
                switch (slot.trim()) {
                    case "6:00 AM - 7:00 AM":
                        time6amTo7am.setChecked(true);
                        break;
                    case "7:00 AM - 8:00 AM":
                        time7amTo8am.setChecked(true);
                        break;
                    case "8:00 AM - 9:00 AM":
                        time8amTo9am.setChecked(true);
                        break;
                    case "9:00 AM - 10:00 AM":
                        time9amTo10am.setChecked(true);
                        break;
                    case "10:00 AM - 11:00 AM":
                        time10amTo11am.setChecked(true);
                        break;
                    case "11:00 AM - 12:00 PM":
                        time11amTo12pm.setChecked(true);
                        break;
                    case "12:00 PM - 1:00 PM":
                        time12pmTo1pm.setChecked(true);
                        break;
                    case "1:00 PM - 2:00 PM":
                        time1pmTo2pm.setChecked(true);
                        break;
                    case "2:00 PM - 3:00 PM":
                        time2pmTo3pm.setChecked(true);
                        break;
                    case "3:00 PM - 4:00 PM":
                        time3pmTo4pm.setChecked(true);
                        break;
                    case "4:00 PM - 5:00 PM":
                        time4pmTo5pm.setChecked(true);
                        break;
                    case "5:00 PM - 6:00 PM":
                        time5pmTo6pm.setChecked(true);
                        break;
                    case "6:00 PM - 7:00 PM":
                        time6pmTo7pm.setChecked(true);
                        break;
                    case "7:00 PM - 8:00 PM":
                        time7pmTo8pm.setChecked(true);
                        break;
                }
            }
        }
    }

}