package com.example.project2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.FlightLog;
import com.example.project2.LoginActivity;
import com.example.project2.MainActivity;
import com.example.project2.R;
import com.example.project2.User;
import com.example.project2.db.AppDatabase;
import com.example.project2.db.FlightLogDAO;

import java.util.ArrayList;
import java.util.List;
//@TODO refer to sticky note for current progress
public class CancelResActivity extends AppCompatActivity {
    private FlightLogDAO mFlightLogDAO;

    private TextView userFlightsDisplay;

    private EditText mFlightNumberToCancelField;
    private Button mCancelButton;

    int seatsToCancel = 0;
    boolean noFlights = false;
    FlightLog flightToCancel = new FlightLog();
    private User mUser;
    private int mFlightNumberToCancel;
    private List<FlightLog> mUserReservedFlights= new ArrayList<>();
    ArrayList<Integer> reservedFlightSeats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_res);

        getDatabase();
        Intent intent = getIntent();
        final String USERNAME = intent.getStringExtra("username");

        userFlightsDisplay = findViewById(R.id.textViewUserFlights);
        userFlightsDisplay.setMovementMethod(new ScrollingMovementMethod());

        mFlightNumberToCancelField = findViewById(R.id.editTextCancelFlightNum);
        mCancelButton = findViewById(R.id.buttonCancelRes);


        refreshDisplay(USERNAME);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFlightToCancel();
                if (!noFlights && foundFlightToCancel(mFlightNumberToCancel)){
                    cancelConfirmAlert(mFlightNumberToCancel);
                }else{
                    Toast.makeText(getApplicationContext(), "Flight " +mFlightNumberToCancel+ " not found", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void getDatabase(){
        mFlightLogDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getFlightLogDAO();
    }
    private void getFlightToCancel(){
        try{
            mFlightNumberToCancel = Integer.parseInt(mFlightNumberToCancelField.getText().toString());
        } catch (NumberFormatException e){
            Log.d("FLIGHT_LOG", "Couldn't convert flight number"); //@TODO Convert all log errors to toast errors
        }
    }
    private void refreshDisplay(String username){
        mUser = mFlightLogDAO.getUserByUsername(username); //look up username in database
        String reservationInfo = mUser.getReservedFlightIDs();//extract reservation info //flightNumber:seats;3752:3;
        if (reservationInfo.equals("")){
            noFlights = true;
            userFlightsDisplay.setText("No flights reserved");
        } else{
            String[] reservationEntries = reservationInfo.split(";");//splits each reservation into single string entry 3752:3

            for(String entry : reservationEntries){
                String[] entryContent = entry.split(":"); //breaks up 3752 and 3;
                //Log.d("FLIGHT_LOG", "EntryContent0: " +entryContent[0]);
                reservedFlightSeats.add(Integer.parseInt(entryContent[1]));
                Integer flightNumber = Integer.parseInt(entryContent[0]); //3752
                FlightLog flight = mFlightLogDAO.getFlightLogsByNumber(flightNumber); //looks up all the other info about individual flight
                mUserReservedFlights.add(flight); //adds to list of user reserved flights
            }

            StringBuilder sb = new StringBuilder();
            int counter = 0;
            for(FlightLog log : mUserReservedFlights){
                //sb.append("LogID: " + log.getLogID());
                sb.append("\n");
                sb.append(log);
                sb.append("\n");
                sb.append("Seats reserved: " +reservedFlightSeats.get(counter));
                sb.append("\n");
                sb.append("------------------");
                sb.append("\n");
                counter+=1;
            }
            userFlightsDisplay.setText(sb.toString());
        }
    }
    private FlightLog flightLookup(int flightNumber){
        return mFlightLogDAO.getFlightLogsByNumber(flightNumber);
    }
    private void toastMaker(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    private boolean foundFlightToCancel(int flightNumber){
        for(int i = 0; i < mUserReservedFlights.size();i++){
            Log.d("FLIGHT_LOG","searching");
            if(flightNumber == mUserReservedFlights.get(i).getFlightNum()){
                //if (log.equals(mUserReservedFlights.get(i))){
                flightToCancel = mUserReservedFlights.get(i);
                Log.d("FLIGHT_LOG", "found flight in array");
                seatsToCancel = reservedFlightSeats.get(i);
                return true;
            }
        }
        return false;
    }

    private void cancelConfirmAlert(int flightNumber){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        alertBuilder.setMessage("CONFIRM CANCELLATION?\n"
                +flightToCancel.getDepartLoc() + " to " + flightToCancel.getArrivalLoc() +"\n"
                +"Depart at: " +flightToCancel.getDepartTime() +"\n"
                +"Ordered " +seatsToCancel +" seat(s) at $" +flightToCancel.getPricePerSeat()+ " per seat\n"
                +"Total: $"+ String.format("%.2f",(seatsToCancel*flightToCancel.getPricePerSeat())));

        final int finalSeatsToCancel = seatsToCancel;
        final FlightLog finalFlightToCancel = flightToCancel;
        alertBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finalFlightToCancel.addSeats(finalSeatsToCancel); //adds seats back to flight
                mFlightLogDAO.update(finalFlightToCancel); //updates database

                mUser.removeFlightNumber(finalFlightToCancel.getFlightNum(),finalSeatsToCancel); //removes flight entry from reservation string
                mFlightLogDAO.update(mUser); //update database

                //Log.d("FLIGHT_LOG",mUser.toString());
                Toast.makeText(getApplicationContext(), "Reservation cancelled", Toast.LENGTH_SHORT).show();
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
