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

public class FlightInfoActivity extends AppCompatActivity {

    private TextView mDisplay;

    private EditText mFlightNum;
    private EditText mDepartLoc;
    private EditText mDepartTime;
    private EditText mArrivalLoc;
    private EditText mAvaliableSeats;
    private EditText mPricePerSeat;

    private Button mSubmitButton;

    private FlightLogDAO mFlightLogDAO;

    private List<FlightLog> mFlightLogs;

    private int mUserId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flight_info);

        mDisplay = findViewById(R.id.textViewDisplayFlightReserve);

        mDisplay.setMovementMethod(new ScrollingMovementMethod());

        mFlightNum = findViewById(R.id.editTextNumber);
        mDepartLoc = findViewById(R.id.editTextDepartLocation);
        mDepartTime = findViewById(R.id.editTextDepartTime);
        mArrivalLoc = findViewById(R.id.editTextArrivalLocation);
        mAvaliableSeats = findViewById(R.id.editTextSeats);
        mPricePerSeat = findViewById(R.id.editTextFlightIDReserve);

        mSubmitButton = findViewById(R.id.buttonSubmitReservation);

        mFlightLogDAO = Room.databaseBuilder(this, AppDatabase.class,AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getFlightLogDAO();

        refreshDisplay();

         mSubmitButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FlightLog log = getValuesFromDisplay();
                 flightConfirmAlert(log);
             }
         });
    }

    private FlightLog getValuesFromDisplay(){
        int flightNum = 0;
        String departLoc = "default depart location";
        String departTime = "default depart time";
        String arrivalLoc = "default arrival time";
        int avaliableSeats = 0;
        double pricePerSeat = 0.00;

        try{
            flightNum = Integer.parseInt(mFlightNum.getText().toString());
        } catch (NumberFormatException e){
            Log.d("GYMLOG", "Couldn't convert flight number");
        }

        departLoc = mDepartLoc.getText().toString();
        departTime = mDepartTime.getText().toString();
        arrivalLoc = mArrivalLoc.getText().toString();

        try{
            avaliableSeats = Integer.parseInt(mAvaliableSeats.getText().toString());
        }catch (NumberFormatException e){
            Log.d("FLIGHT_LOG", "Couldn't convert seat amount");
        }

        try{
            pricePerSeat = Double.parseDouble(mPricePerSeat.getText().toString());
        } catch (NumberFormatException e){
            Log.d("FLIGHT_LOG","Couldn't convert price");
        }
        FlightLog log = new FlightLog(flightNum,departLoc,departTime,arrivalLoc,avaliableSeats,pricePerSeat,mUserId);
        return log;
    }

    private void refreshDisplay(){
        mFlightLogs = mFlightLogDAO.getFlightLogs();

        if(mFlightLogs.size() >= 0){
            mDisplay.setText(R.string.noFlightMessage);
        }

        StringBuilder sb = new StringBuilder();
        for(FlightLog log : mFlightLogs){
            sb.append("LogID: " + log.getLogID());
            sb.append("\n");
            sb.append(log);
            sb.append("\n");
            sb.append("------------------");
            sb.append("\n");
        }
        mDisplay.setText(sb.toString());
    }

    private void flightConfirmAlert(final FlightLog log){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        alertBuilder.setMessage("CONFIRM NEW FLIGHT?\n" +log);

        alertBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mFlightLogDAO.insert(log); //insert new flight into database
                Intent intent = new Intent(getApplicationContext(),MainActivity.class); //take user back to main screen
                startActivity(intent);
            }
        });

        alertBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Flight Creation Canceled", Toast.LENGTH_SHORT).show();
            }
        });

        alertBuilder.setCancelable(true);
        alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(getApplicationContext(), "Flight Creation Canceled", Toast.LENGTH_SHORT).show();
            }
        });

        alertBuilder.create().show();
    }
}
