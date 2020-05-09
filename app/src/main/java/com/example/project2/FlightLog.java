package com.example.project2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.project2.db.AppDatabase;

@Entity(tableName = AppDatabase.FLIGHTLOG_TABLE)
public class FlightLog {

    @PrimaryKey(autoGenerate = true)
    private int mLogID;

    private int mFlightNum;
    private String mDepartLoc;
    private String mDepartTime;
    private String mArrivalLoc;
    private int mAvaliableSeats;
    private double mPricePerSeat;

    private int mUserId;

    public FlightLog(){
        mFlightNum = 0;
        mDepartLoc = "default";
        mDepartTime = "default";
        mArrivalLoc = "default";
        mAvaliableSeats = 0;
        mPricePerSeat = 0.00;
    }

    public FlightLog(int flightNum, String departLoc, String departTime, String arrivalLoc, int avaliableSeats, double pricePerSeat, int userId) {
        mFlightNum = flightNum;
        mDepartLoc = departLoc;
        mDepartTime = departTime;
        mArrivalLoc = arrivalLoc;
        mAvaliableSeats = avaliableSeats;
        mPricePerSeat = pricePerSeat;
    }

    public void setPricePerSeat(double pricePerSeat) {
        mPricePerSeat = pricePerSeat;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public int getLogID() {
        return mLogID;
    }

    public void setLogID(int logID) {
        mLogID = logID;
    }

    public int getFlightNum() {
        return mFlightNum;
    }

    public void setFlightNum(int flightNum) {
        mFlightNum = flightNum;
    }

    public String getDepartLoc() {
        return mDepartLoc;
    }

    public void setDepartLoc(String departLoc) {
        mDepartLoc = departLoc;
    }

    public String getDepartTime() {
        return mDepartTime;
    }

    public void setDepartTime(String departTime) {
        mDepartTime = departTime;
    }

    public String getArrivalLoc() {
        return mArrivalLoc;
    }

    public void setArrivalLoc(String arrivalLoc) {
        mArrivalLoc = arrivalLoc;
    }

    public int getAvaliableSeats() {
        return mAvaliableSeats;
    }

    public void setAvaliableSeats(int avaliableSeats) {
        mAvaliableSeats = avaliableSeats;
    }

    public double getPricePerSeat() {
        return mPricePerSeat;
    }

    public boolean enoughSeats(int seatsNeeded){
        if ((mAvaliableSeats-seatsNeeded) < 0){
            return false;
        }
        return true;
    }

    public void removeSeats(int seatsNeeded){
        mAvaliableSeats -= seatsNeeded;
    }

    public void addSeats(int seatsToAdd){
        mAvaliableSeats += seatsToAdd;
    }

    @Override
    public String toString() {
        String output;

        output = "Flight Number: "+mFlightNum;
        output += "\n";
        output += mDepartLoc + " at " + mDepartTime + " to " +mArrivalLoc;
        output += "\n";
        output += mAvaliableSeats + " available seats for $" + mPricePerSeat +" each";


        return output;
    }
}
