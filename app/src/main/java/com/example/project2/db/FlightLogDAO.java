package com.example.project2.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project2.FlightLog;
import com.example.project2.User;

import java.util.List;

@Dao
public interface FlightLogDAO {

    @Insert
    void insert(FlightLog... flightLogs);

    @Update
    void update(FlightLog... flightLogs);

    @Delete
    void delete(FlightLog flightLog);

    @Query("SELECT * FROM " + AppDatabase.FLIGHTLOG_TABLE + " ORDER BY mLogID DESC")
    List<FlightLog> getFlightLogs();

    @Query("SELECT * FROM " + AppDatabase.FLIGHTLOG_TABLE + " WHERE mLogID = :logID")
    FlightLog getFlightLogsByID(int logID);

    @Query("SELECT * FROM " + AppDatabase.FLIGHTLOG_TABLE + " WHERE mFlightNum = :flightNumber")
    FlightLog getFlightLogsByNumber(int flightNumber);

    @Query("SELECT * FROM " + AppDatabase.FLIGHTLOG_TABLE + " WHERE mUserId =:userId ORDER BY  mLogID DESC")
    List<FlightLog> getFlightLogsByUserId(int userId);

    @Insert
    void insert(User...users);

    @Update
    void update(User...users);

    @Delete
    void delete(User...users);

    @Query("SELECT * FROM " + AppDatabase.USER_TABLE)
    List<User> getAllUsers();

    @Query("SELECT * FROM " + AppDatabase.USER_TABLE + " WHERE mUsername =:username ")
    User getUserByUsername(String username);

    @Query("SELECT * FROM " + AppDatabase.USER_TABLE + " WHERE mUserId =:userId ")
    User getUserByUserId(int userId);
}
