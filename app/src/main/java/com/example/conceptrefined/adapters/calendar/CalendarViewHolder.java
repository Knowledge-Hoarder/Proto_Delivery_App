package com.example.ProtoDeliveryApp.adapters.calendar;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ProtoDeliveryApp.R;
import com.example.ProtoDeliveryApp.utils.ApiManager;
import com.example.ProtoDeliveryApp.utils.ValidationsManager;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private final ArrayList<LocalDate> days;
    public final View parentView;
    public final TextView dayOfMonth;
    private final CalendarAdapter.OnItemListener onItemListener;
    private final int empId;
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener,
                              ArrayList<LocalDate> days, int empId)
    {
        super(itemView);
        parentView = itemView.findViewById(R.id.parentView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
        this.days = days;
        this.empId = empId;
    }

    @Override
    public void onClick(View view)
    {
        LocalDate date = days.get(getAdapterPosition());
//        ApiManager.getInstance(view.getContext()).requestrunsApi(view.getContext(),empId, date,"");
        ApiManager.getInstance(view.getContext()).onShowDialog(date.toString(), "run");
        onItemListener.onItemClick(getAdapterPosition(), date);
    }
}
