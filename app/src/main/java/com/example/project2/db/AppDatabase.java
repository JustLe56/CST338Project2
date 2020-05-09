package com.example.project2.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.project2.FlightLog;
import com.example.project2.User;

@Database(entities = {FlightLog.class, User.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DB_NAME = "FLIGHTLOG_DATABASE";
    public static final String FLIGHTLOG_TABLE = "FLIGHTLOG_TABLE";
    public static final String USER_TABLE = "USER_TABLE";

    public abstract FlightLogDAO getFlightLogDAO();

}
