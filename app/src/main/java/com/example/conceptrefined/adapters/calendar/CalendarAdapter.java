package com.example.ProtoDeliveryApp.adapters.calendar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ProtoDeliveryApp.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>{
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;
    private final int empId;
    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener, int empId)
    {
        this.days = days;
        this.onItemListener = onItemListener;
        this.empId = empId;
    }
    public interface  OnItemListener
    {
        void onItemClick(int position, LocalDate date);
    }
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        return new CalendarViewHolder(view, onItemListener, days, empId);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        final LocalDate date = days.get(position);
        if(date == null)
            holder.dayOfMonth.setText("");
        else
        {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if(date.equals(CalendarUtils.selectedDate)){
                holder.parentView.setBackgroundColor(Color.parseColor("#015444"));
                holder.dayOfMonth.setTextColor(Color.WHITE);
            }
        }
    }
    @Override
    public int getItemCount()
    {
        return days.size();
    }
}
