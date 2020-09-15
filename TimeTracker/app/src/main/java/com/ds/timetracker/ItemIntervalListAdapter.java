package com.ds.timetracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ItemIntervalListAdapter extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<IntervalDetails> intervals;

    public ItemIntervalListAdapter(final Activity activity, final ArrayList<IntervalDetails> intervals) {
        this.activity = activity;
        this.intervals = intervals;
    }

    public int getCount(){
        return this.intervals.size();
    }

    public Object getItem(final int position){
        return this.intervals.get(position);
    }

    public long getItemId(final int position){
        return this.intervals.get(position).getId();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, final View contentView, final ViewGroup parent) {
        View view = contentView;

        if (contentView == null) {
            final LayoutInflater inflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(R.layout.ds_intervals_list_item_layout, null);
        }

        //Obtenim els elements de la llista
        final IntervalDetails interval = (IntervalDetails) this.getItem(position);


        final Typeface typeface =
                Typeface.createFromAsset(this.activity.getAssets(), "font/digital-readout.heavy.ttf");

        //Assignem què volem que aparegui a cada element del layout (ds_intervals_list_item_layout)
        final TextView duration_interval = view.findViewById(R.id.DurationInterval);
        duration_interval.setText(interval.getTimeSegString());

        duration_interval.setTypeface(typeface);

        if (interval.isRunning()){
            duration_interval.setBackground(this.activity.getDrawable(R.drawable.border_red));
            duration_interval.setTextColor(this.activity.getResources().getColor(R.color.dark_red));

        }

        final TextView start_date = view.findViewById(R.id.startDateInterval);
        start_date.setText(interval.getStartDateString());

        final TextView finish_date = view.findViewById(R.id.finishDateInterval);
        finish_date.setText(interval.getEndDateString());

        return view;
    }

    public void clear(){
        this.intervals.clear();
        this.notifyDataSetChanged();
    }

    public void add(final IntervalDetails intervalDetails){
        this.intervals.add(intervalDetails);
        this.notifyDataSetChanged();
    }

    public void removeItems(final List<IntervalDetails> list){

        for(final IntervalDetails id : list){
            final Intent intent = new Intent (IntervalsList.DELETE_INTERVAL);
            intent.putExtra("id",id.getId());
            this.activity.sendBroadcast(intent);
        }

        this.notifyDataSetChanged();
    }


}
