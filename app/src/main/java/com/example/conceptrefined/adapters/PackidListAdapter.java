package com.example.ProtoDeliveryApp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.ProtoDeliveryApp.R;
import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.utils.ApiManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class PackidListAdapter extends BaseAdapter {
    private final ArrayList<Packs> items;
    public Context context;
    private LayoutInflater inflater;

    public PackidListAdapter(Context context, ArrayList<Packs> items){
        Log.i("PackidListAdapter", "PackidListAdapter: start");
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
            view = inflater.inflate(R.layout.item_list_collectionchild, null);

        ViewHolderList viewHolder = (ViewHolderList) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolderList(view, context);
            view.setTag(viewHolder);
        }

        viewHolder.update(items.get(i));

        ImageView newnote= view.findViewById(R.id.ilcnewnote);

        newnote.setOnClickListener(view1 -> {
            if (!Objects.equals(items.get(i).getPackId(), "")){
                ApiManager.getInstance(context).onShowDialog(items.get(i).getPackId(), "note");
            }

        });
        return view;
    }

    public class ViewHolderList {
        private final TextView tvIdPackId;
        private final View bg;
        private final ImageView moreInfo;

        public ViewHolderList(View view, Context context) {
            tvIdPackId = view.findViewById(R.id.ilcDesc);
            bg = view.findViewById(R.id.singlePackBG);
            moreInfo = view.findViewById(R.id.ilcDeets);
        }

        public void update(Packs item) {
            if (item.isFlag()){
                bg.getRootView().setBackgroundColor(ContextCompat.getColor(context, R.color.vivid_red));
            } else if (Objects.equals(item.getNotes(), "extra")) {
                bg.getRootView().setBackgroundColor(ContextCompat.getColor(context, R.color.teal_200));
            }

            tvIdPackId.setText(item.getPackId());
            if (!Objects.equals(item.getNotes(), "extra"))
                showPackInfo(moreInfo, item.getPackId());
        }
    }

    private void showPackInfo(ImageView newnote, String packid){
        newnote.setOnClickListener(view1 -> {
            ApiManager.getInstance(context).onShowDialog(packid, "pop-up");
        });
    }
}
