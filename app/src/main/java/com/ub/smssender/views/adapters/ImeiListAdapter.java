package com.ub.smssender.views.adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ub.smssender.R;
import com.ub.smssender.activities.MainActivity;
import com.ub.smssender.entities.ImeiRealm;
import com.ub.smssender.views.models.ImeiViewModel;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 2/04/17.
 */

public class ImeiListAdapter extends RecyclerView.Adapter<ImeiListAdapter.ImeiViewHolder> {

    List<ImeiViewModel> imeiList;

    private MainActivity parentActivity;

    public ImeiListAdapter(List<ImeiViewModel> products, MainActivity activity){
        this.imeiList = products;
        this.parentActivity = activity;
    }

    @Override
    public ImeiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_imei, parent, false);
        ImeiViewHolder pvh = new ImeiViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final ImeiViewHolder holder, final int i) {
        holder.imeiNumber.setText(imeiList.get(i).getImei());
        holder.imeiCounter.setText(String.valueOf(imeiList.get(i).getCounter()));
        holder.imeiActive.setChecked(imeiList.get(i).isActivo());

        holder.imeiActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println(imeiList.get(i).getImei() + " está: " + isChecked );

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                ImeiRealm imeiRealm = realm.where(ImeiRealm.class).equalTo("imei", imeiList.get(i).getImei()).findFirst();
                imeiRealm.setActivo(isChecked);
                realm.copyToRealmOrUpdate(imeiRealm);
                realm.commitTransaction();
                realm.close();

                parentActivity.restartService();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imeiList.size();
    }

    public static class ImeiViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        protected TextView imeiNumber;
        protected TextView imeiCounter;
        protected CheckBox imeiActive;

        ImeiViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            imeiNumber = (TextView)itemView.findViewById(R.id.lbImeiNumber);
            imeiCounter = (TextView)itemView.findViewById(R.id.lbContadorEnviados);
            imeiActive = (CheckBox) itemView.findViewById(R.id.chkActivo);
        }
    }
}
