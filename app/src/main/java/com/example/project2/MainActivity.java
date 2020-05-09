package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.project2.db.AppDatabase;
import com.example.project2.db.FlightLogDAO;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button reserveButton;
    private Button flightsButton;
    private Button cancelButton;
    private Button createButton;

    private FlightLogDAO mFlightLogDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reserveButton = findViewById(R.id.reserveButton);
        flightsButton = findViewById(R.id.buttonFlights);
        cancelButton = findViewById(R.id.buttonCancelRes);
        createButton = findViewById(R.id.buttonCreateAcc);
        getDatabase();
        adminSetup();

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFlightLogDAO.getFlightLogs() == null){
                    Toast.makeText(MainActivity.this, "No flights available", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d("FLIGHT_LOG","test");
                    Intent intent = new Intent(getApplicationContext(), ReserveSeatActivity.class);
                    startActivity(intent);
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddUserActivity.class);
                startActivity(intent);
            }
        });

        flightsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class); //user has to log in to admin before modifying system
                intent.putExtra("needAdminAuth",true);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class); //user has to log in before cancelling
                startActivity(intent);
            }
        });


    }
    private void getDatabase(){
        mFlightLogDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getFlightLogDAO();
    }
    private void adminSetup()
    {
        List<User> users = mFlightLogDAO.getAllUsers();
        if(users.size() == 0){
            User admin = new User("admin123","admin123");
            mFlightLogDAO.insert(admin);
        }
    }
}
