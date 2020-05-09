package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project2.db.AppDatabase;
import com.example.project2.db.FlightLogDAO;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameField;
    private EditText mPasswordField;

    private Button mButton;

    private FlightLogDAO mFlightLogDAO;

    private String mUsername;
    private String mPassword;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        wireupDisplay();

        getDatabase();

    }

    private void wireupDisplay(){
        mUsernameField = findViewById(R.id.usernameInput);
        mPasswordField = findViewById(R.id.passwordInput);

        mButton = findViewById(R.id.signButton);
        Intent intent = getIntent();
        final boolean ADMIN = intent.getBooleanExtra("needAdminAuth",false);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValuesFromDisplay();
                if (ADMIN){ //trying to access flight system
                    User mAdmin = mFlightLogDAO.getUserByUsername("admin123");
                    if(!mAdmin.getPassword().equals(mPassword)){
                        Toast.makeText(LoginActivity.this, "Invalid password" , Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Welcome, " +mAdmin.getUsername() , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),FlightInfoActivity.class);
                        startActivity(intent);
                    }
                }

                else if (checkForUserInDatabase()){ //regular user trying to access reservation cancel
                    if (!validatePassword()){
                        Toast.makeText(LoginActivity.this, "Invalid password" , Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Welcome, " +mUser.getUsername() , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),CancelResActivity.class); //user has to log in to admin before modifying system
                        intent.putExtra("username",mUser.getUsername().toString());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private boolean validatePassword(){
        return mUser.getPassword().equals(mPassword);
    }

    private void getValuesFromDisplay(){
        mUsername = mUsernameField.getText().toString();
        mPassword = mPasswordField.getText().toString();
    }

    private boolean checkForUserInDatabase(){
        mUser = mFlightLogDAO.getUserByUsername(mUsername);
        if(mUser == null){
            Toast.makeText(this,"no user " + mUsername + "found",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void getDatabase(){
        mFlightLogDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getFlightLogDAO();
    }

    public static Intent intentFactory(Context context){
        Intent intent = new Intent(context, LoginActivity.class);

        return intent;
    }
}
