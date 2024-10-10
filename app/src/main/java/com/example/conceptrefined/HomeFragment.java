package com.example.ProtoDeliveryApp;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ProtoDeliveryApp.adapters.calendar.CalendarUtils.daysInWeekArray;
import static com.example.ProtoDeliveryApp.adapters.calendar.CalendarUtils.monthYearFromDate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ProtoDeliveryApp.adapters.LoadedRunsAdapter;
import com.example.ProtoDeliveryApp.adapters.calendar.CalendarAdapter;
import com.example.ProtoDeliveryApp.adapters.calendar.CalendarUtils;
import com.example.ProtoDeliveryApp.listeners.IDialogListener;
import com.example.ProtoDeliveryApp.listeners.IHideRun;
import com.example.ProtoDeliveryApp.modelsportal.Runs;
import com.example.ProtoDeliveryApp.services.NoteDialogFragment;
import com.example.ProtoDeliveryApp.services.RunSelectDialog;
import com.example.ProtoDeliveryApp.utils.ApiManager;
import com.example.ProtoDeliveryApp.utils.LocalStorageManager;
import com.example.ProtoDeliveryApp.utils.ValidationsManager;
import com.google.android.gms.common.api.Api;

import java.time.LocalDate;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements IDialogListener, CalendarAdapter.OnItemListener{
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private int empId;
    private void initWidgets(View view)
    {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
    }
    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this, empId);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View binding = inflater.inflate(R.layout.fragment_home, container, false);
        Context context = requireContext();
        View root = binding.getRootView();
        ArrayList<Runs> run = LocalStorageManager.getInstance(context).getRuns("");
        SharedPreferences sharedPreference1 = requireActivity().getSharedPreferences(ValidationsManager.sharedName(), MODE_PRIVATE);
        this.empId = Integer.parseInt(sharedPreference1.getString("empId",""));

        CalendarUtils.selectedDate = LocalDate.now();
        ApiManager.getInstance(context).setDialogListener(this);
        ListView list = root.findViewById(R.id.listNextRuns);
        list.setAdapter(new LoadedRunsAdapter(requireContext(),run));
        list.setOnItemClickListener((adapterView, view, i, l) -> {

            Bundle args = new Bundle();
            args.putInt("fkrun", run.get(i).getId());
            args.putString("runId", run.get(i).getRunid());
            Fragment listFrag = new DeliveryListFragment();
            listFrag.setArguments(args);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container,listFrag , null)
                    .addToBackStack("delilist")
                    .commit();
        });
        //Initiallizes the calendar
        initWidgets(root);
        setWeekView();
        Button btnPreviousWeek = root.findViewById(R.id.previousWeekAction);
        Button btnNextWeek = root.findViewById(R.id.nextWeekAction);
        btnPreviousWeek.setOnClickListener(view -> {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
            setWeekView();
        });
        btnNextWeek.setOnClickListener(view -> {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
            setWeekView();
        });

        return root;
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        //gets the clicked day info from  CalendarViewHolder and resets the week view widjet
        // to focus on the week containing the selected day
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    public void onShowDialog(String packid, String mode) {
        ApiManager.getInstance(requireActivity()).requestAvailableRuns(requireActivity(),packid, empId);
    }
    @Override
    public void onResult(String date, String runs) {
        Bundle args = new Bundle();
        args.putString("date", date);
        args.putString("sRuns", runs);
        Fragment frag = new RunSelectDialog();
        frag.setArguments(args);
        getChildFragmentManager().beginTransaction().add(frag, NoteDialogFragment.TAG)
                .commit();
    }

    @Override
    public void onAddDriverNote(String note) {

    }


}