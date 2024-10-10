package com.example.ProtoDeliveryApp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ProtoDeliveryApp.R;
import com.example.ProtoDeliveryApp.modelsportal.Orders;
import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.utils.ApiManager;
import com.example.ProtoDeliveryApp.utils.LocalStorageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExpandableScannedListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> titlesList;
    private Map<String, List<Packs>> listCollection;
    private int maxWidth;
    ArrayList<Orders> orders;
    String orderNum;
    public ExpandableScannedListAdapter(Context context, int maxWidth, int run, String cltName) {
        this.context = context;
        int flip = 0;
        orders = LocalStorageManager.getInstance(context).getOrdersPerCltDone(run, cltName);
        List<Packs> mpL = LocalStorageManager.getInstance(context).getPacksPerOrder(orders,flip);

        List<Packs> wOutMasterPack = mpL.stream()
                .filter(p -> Objects.equals(p.getHasMPack(), "")).collect(Collectors.toList());
        this.listCollection =
                wOutMasterPack.stream().collect(Collectors.groupingBy(Packs::getPackId));

        List<Packs> wMasterPack = mpL.stream()
                .filter(p -> !Objects.equals(p.getHasMPack(), "")).collect(Collectors.toList());

        Map<String, List<Packs>> collectionWMasterPack =
                wMasterPack.stream().collect(Collectors.groupingBy(Packs::getHasMPack));

        this.listCollection.putAll(collectionWMasterPack);

        List<String> titles = new ArrayList<>(listCollection.keySet());
        Collections.sort(titles);
        this.maxWidth = maxWidth;
        this.titlesList = titles;
    }

    @Override
    public int getGroupCount() {
        return listCollection.size();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        if(Objects.equals(Objects.requireNonNull(listCollection
                .get(titlesList.get(listPosition))).get(0).getHasMPack(), "")){
            return 0;
        }else {
            return listCollection.get(titlesList.get(listPosition)).size();
        }
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.titlesList.get(listPosition);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return listCollection.get(titlesList.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String listTitle = getGroup(i).toString();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list_collectionchild, null);
        }
        TextView label = view.findViewById(R.id.ilcPack);
        TextView item = view.findViewById(R.id.ilcDesc);
        item.setWidth(maxWidth);

        ImageView newnote = view.findViewById(R.id.ilcnewnote);
        ImageView moreInfo = view.findViewById(R.id.ilcDeets);

        List<Packs> packs = listCollection.get(listTitle);
        assert packs != null;

        if (!Objects.equals(packs.get(0).getHasMPack(), "")) {
            newnote.setVisibility(View.INVISIBLE);
            label.setText(context.getString(R.string.masterpack));
            boolean bool = LocalStorageManager.getInstance(context)
                    .checkMasterPackScanStatus(listTitle,orderNum);
            if(bool){
                view.findViewById(R.id.ilcnewnote).setVisibility(View.INVISIBLE);
            }
        } else {
            writeNote(newnote, listTitle);
            label.setText(context.getString(R.string.pack));

            Packs pack = LocalStorageManager.getInstance(context).getPack(listTitle);
            if(pack.getIsScanned()==1){
                view.findViewById(R.id.ilcnewnote).setVisibility(View.INVISIBLE);
            }
            showPackInfo(moreInfo, listTitle);
        }

        item.setTypeface(null, Typeface.BOLD);
        item.setText(listTitle);
        return view;
    }
    private void showPackInfo(ImageView newnote, String packid){
        newnote.setOnClickListener(view1 -> {
            ApiManager.getInstance(context).onShowDialog(packid, "pop-up");
        });
    }
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        Packs model = (Packs) getChild(i, i1);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list_collectionchild, null);
        }
        TextView item = view.findViewById(R.id.ilcDesc);
        item.setText(model.getPackId());
        item.setWidth((int) (maxWidth / 2));

        Packs pack = LocalStorageManager.getInstance(context).getPack(model.getPackId());
        if(pack.getIsScanned()==1){
            view.findViewById(R.id.ilcnewnote).setVisibility(View.INVISIBLE);
        }

        ImageView newnote = view.findViewById(R.id.ilcnewnote);
        writeNote(newnote, model.getPackId());
        return view;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    private void writeNote(ImageView newnote, String packid){
        newnote.setOnClickListener(view1 -> {
            ApiManager.getInstance(context).onShowDialog(packid, "note");
        });
    }
}
