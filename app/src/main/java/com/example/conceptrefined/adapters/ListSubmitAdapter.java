package com.example.ProtoDeliveryApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ProtoDeliveryApp.R;
import com.example.ProtoDeliveryApp.models.ListSubmitResponse;

import java.util.ArrayList;

public class ListSubmitAdapter extends BaseAdapter {
    private final ArrayList<ListSubmitResponse> items;
    private Context context;
    private LayoutInflater inflater;

    public ListSubmitAdapter(Context context, ArrayList<ListSubmitResponse> items){
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

        ViewHolderList viewHolder = (ViewHolderList) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolderList(view, context);
            view.setTag(viewHolder);
        }

        viewHolder.update(items.get(i));

        return view;
    }

    public static class ViewHolderList {
        private final TextView tvPackId,tvStatus;
        public ViewHolderList(View view, Context context) {
            tvPackId = view.findViewById(R.id.tvSubmitPackId);
            tvStatus = view.findViewById(R.id.tvSubmitStatus);

        }

        public void update(ListSubmitResponse item) {
            tvPackId.setText("PackId: "+item.getPackId());
            tvStatus.setText(item.getMessage());

        }
    }
}
