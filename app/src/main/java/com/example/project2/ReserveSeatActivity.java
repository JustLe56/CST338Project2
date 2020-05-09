package com.example.project2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.db.AppDatabase;
import com.example.project2.db.FlightLogDAO;

import java.util.List;

public class ReserveSeatActivity extends AppCompatActivity {

    TextView mFlightDisplay;
    EditText mFlightID;
    EditText mFlightSeats;

    Button mReserveButton;
    
    String username;
    String password;
    User mUser;

    Integer flightNumber;
    Integer seatsNeeded;

    private FlightLogDAO mFlightLogDAO;
    private List<FlightLog> mFlightEntries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_seat);
        
        mFlightLogDAO = Room.databaseBuilder(this, AppDatabase.class,AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getFlightLogDAO();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Log.d("FLIGHT_LOG",username);
        mFlightEntries = mFlightLogDAO.getFlightLogs();
        
        mFlightDisplay = findViewById(R.id.textViewDisplayFlightReserve);
        mFlightDisplay.setMovementMethod(new ScrollingMovementMethod());
        
        mFlightSeats = findViewById(R.id.editTextFlightIDReserveSeats);
        mFlightID = findViewById(R.id.editTextFlightIDReserve);
        //mUsername = findViewById(R.id.editTextReserveUser);
        //mPass = findViewById(R.id.editTextReservePass);
        
        mReserveButton = findViewById(R.id.buttonSubmitReservation);
        
        refreshDisplay();



        mReserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValuesFromDisplay();
                if (mFlightLogDAO.getFlightLogsByNumber(flightNumber) != null){ //check if flight number is valid
                    FlightLog desiredFlight = mFlightLogDAO.getFlightLogsByNumber(flightNumber);
                    Log.d("FLIGHT_LOG", desiredFlight.toString());
                    if (desiredFlight.enoughSeats(seatsNeeded)) { //check if flight has enough seats, possible that unable to get specific flight leading to no seats to look up
                            reserveConfirmAlert(desiredFlight);
                    }else {
                        Toast.makeText(ReserveSeatActivity.this, "Not enough seats available", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Flight " +flightNumber+ " not found", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

//    private boolean checkForUserInDatabase(){
//        mUser = mFlightLogDAO.getUserByUsername(username);
//        if(mUser == null){
//            Toast.makeText(this,"no user " + username + "found",Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        return true;
//    }

//    private boolean validatePassword(){
//        return mUser.getPassword().equals(password);
//    }

    private void getValuesFromDisplay(){
        try{
            flightNumber = Integer.parseInt(mFlightID.getText().toString());
        } catch (NumberFormatException e){
            Log.d("FLIGHT_LOG", "Couldn't convert flight number");
        }

        try{
            seatsNeeded = Integer.parseInt(mFlightSeats.getText().toString());
        } catch (NumberFormatException e){
            Log.d("FLIGHT_LOG", "Couldn't convert seats number");
        }
        mUser = mFlightLogDAO.getUserByUsername(username);
        username = mUser.getUsername();
        password = mUser.getPassword();
    }

    private void refreshDisplay(){
        mFlightEntries = mFlightLogDAO.getFlightLogs();

        if(mFlightEntries.size() >= 0){
            mFlightDisplay.setText(R.string.noFlightMessage);
        }

        StringBuilder sb = new StringBuilder();
        for(FlightLog log : mFlightEntries){
            //sb.append("LogID: " + log.getLogID());
            if (log.getAvaliableSeats() > 0){
                sb.append("\n");
                sb.append(log);
                sb.append("\n");
                sb.append("------------------");
                sb.append("\n");
            }
        }
        mFlightDisplay.setText(sb.toString());
        
    }

    private void reserveConfirmAlert(final FlightLog log){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        alertBuilder.setMessage("CONFIRM RESERVATION?\n"
                +log.getDepartLoc() + " to " + log.getArrivalLoc() +"\n"
                +"Depart at: " +log.getDepartTime() +"\n"
                +"Ordered " +seatsNeeded +" seat(s) at $" +log.getPricePerSeat()+ " per seat\n"
                +"Total: $"+ String.format("%.2f",(seatsNeeded*log.getPricePerSeat())));

        alertBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                log.removeSeats(seatsNeeded);
                mFlightLogDAO.update(log);

                mUser.addFlightNumber(flightNumber,seatsNeeded);
                mFlightLogDAO.update(mUser);

                Log.d("FLIGHT_LOG",mUser.toString());
                Toast.makeText(getApplicationContext(), "Reservation created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        alertBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Reservation Canceled", Toast.LENGTH_SHORT).show();
            }
        });
        alertBuilder.create().show();
    }
}
