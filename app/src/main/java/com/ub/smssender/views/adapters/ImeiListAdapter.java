package com.ub.smssender.views.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ub.smssender.R;
import com.ub.smssender.views.models.ImeiViewModel;

import java.util.List;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 2/04/17.
 */

public class ImeiListAdapter extends RecyclerView.Adapter<ImeiListAdapter.ImeiViewHolder> {

    List<ImeiViewModel> imeiList;

    public ImeiListAdapter(List<ImeiViewModel> products){
        this.imeiList = products;
    }

    @Override
    public ImeiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_imei, parent, false);
        ImeiViewHolder pvh = new ImeiViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ImeiViewHolder holder, int i) {
        holder.imeiNumber.setText(imeiList.get(i).getImei());
        holder.imeiCounter.setText(String.valueOf(imeiList.get(i).getCounter()));
    }

    @Override
    public int getItemCount() {
        return imeiList.size();
    }

    public static class ImeiViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        protected TextView imeiNumber;
        protected TextView imeiCounter;

        ImeiViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            imeiNumber = (TextView)itemView.findViewById(R.id.lbImeiNumber);
            imeiCounter = (TextView)itemView.findViewById(R.id.lbContadorEnviados);

        }
    }
}