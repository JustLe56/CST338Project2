package com.example.project2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.project2.db.AppDatabase;

import java.util.List;

@Entity (tableName = AppDatabase.USER_TABLE)
public class User {
    @PrimaryKey (autoGenerate = true)
    private int mUserId;

    private String mUsername;
    private String mPassword;

    private String reservedFlightIDs = ""; //since the database can't hold lists, put any flight ids into a string then parse through and modify when needed
                                      //I don't have enough time to use type converters

    public User(String username, String password) {

        mUsername = username;
        mPassword = password;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getReservedFlightIDs() {
        return reservedFlightIDs;
    }

    public void setReservedFlightIDs(String reservedFlightIDs) {
        this.reservedFlightIDs = reservedFlightIDs;
    }
    public void addFlightNumber(Integer flightNum, Integer seatsReserved){
        reservedFlightIDs += (flightNum +":" + seatsReserved +";");
    }

    public void removeFlightNumber(Integer flightNum, Integer seats){
        String entryToRemove = ""+flightNum +":"+ seats +";";
        reservedFlightIDs = reservedFlightIDs.replace(entryToRemove,"");
    }

    @Override
    public String toString() {
        String output;

        output = "Username: "+mUsername;
        output += "\n";
        output += "Password: "+mPassword;
        output += "\n";
        output += "Flights Reserved: " +reservedFlightIDs;

        return output;
    }
}
