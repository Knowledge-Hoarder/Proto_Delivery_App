package com.example.ProtoDeliveryApp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.ProtoDeliveryApp.R;
import com.example.ProtoDeliveryApp.modelsportal.Runs;
import com.example.ProtoDeliveryApp.utils.ApiManager;
import com.example.ProtoDeliveryApp.utils.LocalStorageManager;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LoadedRunsAdapter  extends BaseAdapter {
    private ArrayList<Runs> items;
    private Context context;
    private LayoutInflater inflater;

    public LoadedRunsAdapter(Context context, ArrayList<Runs> items){
        this.context = context;
        this.items = new ArrayList<>();
        for (Runs item:
             items) {
           if(LocalStorageManager.getInstance(context)
                    .orderCount(item.getId())>0)
               this.items.add(item);
        }
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return items.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.field_loadedrun, null);

        LoadedRunsAdapter.ViewHolderList viewHolder = (LoadedRunsAdapter.ViewHolderList) view.getTag();
        if (viewHolder == null) {
            viewHolder = new LoadedRunsAdapter.ViewHolderList(view, context);
            view.setTag(viewHolder);
        }

        viewHolder.update(items.get(i));

        return view;
    }

    public class ViewHolderList {
        TextView tvRunId, tvNStops, tvInfo;
        Button btnHideRun;
        public ViewHolderList(View view, Context context) {
            tvRunId = view.findViewById(R.id.tvRunIdLoaded);
            tvNStops = view.findViewById(R.id.tvNStopsLoaded);
            tvInfo = view.findViewById(R.id.tvRunInfoLoaded);
            btnHideRun = view.findViewById(R.id.btnHideRun);
        }

        public void update(Runs item) {
            tvRunId.setText(item.getRunid());
            tvNStops.setText(String.valueOf(LocalStorageManager.getInstance(context)
                    .orderCount(item.getId())));

            String utcDateString = item.getShipByDate();

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
            ZonedDateTime utcDateTime = ZonedDateTime.parse(utcDateString, inputFormatter);
            ZonedDateTime estDateTime = utcDateTime.withZoneSameInstant(java.time.ZoneId.of("Europe/Lisbon"));
            DateTimeFormatter outputDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String estDateString = estDateTime.format(outputDateFormatter);

            tvInfo.setText(estDateString);

            btnHideRun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("LoadedRuns", "onClick: "+item.toJSON().toString());
                    ApiManager.getInstance(context).hideRun(item,context);
                }
            });
        }
    }
}
