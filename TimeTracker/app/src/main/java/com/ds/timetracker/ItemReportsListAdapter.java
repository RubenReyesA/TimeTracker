package com.ds.timetracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ItemReportsListAdapter extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<ReportsDetails> reports;

    public ItemReportsListAdapter(final Activity activity, final ArrayList<ReportsDetails> reports) {
        this.activity = activity;
        this.reports = reports;
    }

    public int getCount(){
        return this.reports.size();
    }

    public Object getItem(final int position){
        return this.reports.get(position);
    }

    public long getItemId(final int position){
        return this.reports.get(position).getId();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, final View contentView, final ViewGroup parent) {
        View view = contentView;

        if (contentView == null) {
            final LayoutInflater inflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(R.layout.ds_reports_list_item_layout, null);
        }


        final ReportsDetails report_actual = (ReportsDetails) this.getItem(position);

        final TextView nameReport = view.findViewById(R.id.NameReport);
        nameReport.setText(report_actual.getName());



        return view;
    }


    public static final String DELETE_REPORT = "delete_report";

    public void removeItems(final List<ReportsDetails> list){

        for(final ReportsDetails rd :list){
            this.reports.remove(rd);
            final Intent to_delete = new Intent(DELETE_REPORT);
            to_delete.putExtra("id",rd.getId());
            to_delete.putExtra("path", rd.getPath());
            this.activity.sendBroadcast(to_delete);
        }

        this.notifyDataSetChanged();
    }

    public void clear(){
        this.reports.clear();
        this.notifyDataSetChanged();
    }

    public void add(final ReportsDetails reportsDetails){
        this.reports.add(reportsDetails);
        this.notifyDataSetChanged();
    }

}
