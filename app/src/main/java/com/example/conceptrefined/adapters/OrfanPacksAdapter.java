package com.example.ProtoDeliveryApp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ProtoDeliveryApp.R;
import com.example.ProtoDeliveryApp.modelsportal.Packs;

import java.util.ArrayList;

public class OrfanPacksAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Packs> items;

    public OrfanPacksAdapter(Context context, ArrayList<Packs> items){
        this.context = context;
        this.items = items;
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
            view = inflater.inflate(R.layout.field_listsubmit, null);

        OrfanPacksAdapter.ViewHolderList viewHolder = (OrfanPacksAdapter.ViewHolderList) view.getTag();
        if (viewHolder == null) {
            viewHolder = new OrfanPacksAdapter.ViewHolderList(view, context);
            view.setTag(viewHolder);
        }

        viewHolder.update(items.get(i));

        return view;

    }

    private class ViewHolderList {
        private final TextView tvPackId,tvPackNote;

        public ViewHolderList(View view, Context context) {
            tvPackId = view.findViewById(R.id.tvSubmitPackId);
            tvPackNote = view.findViewById(R.id.tvSubmitStatusText);

        }
        @SuppressLint("SetTextI18n")
        public void update(Packs p) {
            tvPackId.setText(p.getPackId());
            tvPackNote.setText(p.getNotes());
        }
    }
}
