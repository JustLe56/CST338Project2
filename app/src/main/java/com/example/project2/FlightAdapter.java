package com.example.project2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.db.FlightLogDAO;

import org.w3c.dom.Text;

import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {
    private FlightLogDAO mFlightLogDAO;

    private List<FlightLog> mFlightList;
    public static class FlightViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public FlightViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
        }
    }

    public FlightAdapter(List<FlightLog> flightList){
        mFlightList = flightList;


    }


    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.flight_item,parent,false);
        FlightViewHolder fvh = new FlightViewHolder(v);
        return fvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        FlightLog currentFlight = mFlightList.get(position);

        holder.mTextView1.setText(currentFlight.getFlightNum());
        holder.mTextView2.setText(currentFlight.getAvaliableSeats());
    }

    @Override
    public int getItemCount() {
        return mFlightList.size();
    }
}
