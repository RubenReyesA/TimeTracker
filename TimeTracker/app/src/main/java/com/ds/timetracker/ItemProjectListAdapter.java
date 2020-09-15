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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ItemProjectListAdapter extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<GroupDetails> projects;

    public ItemProjectListAdapter(final Activity activity, final ArrayList<GroupDetails> projects) {
        this.activity = activity;
        this.projects = projects;
    }

    public int getCount(){
        return this.projects.size();
    }

    public Object getItem(final int position){
        return this.projects.get(position);
    }

    public long getItemId(final int position){
        return this.projects.get(position).getId();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, final View contentView, final ViewGroup parent) {
        View view = contentView;

        if (contentView == null) {
            final LayoutInflater inflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(R.layout.ds_projects_list_item_layout, null);
        }


        final GroupDetails project_actual = (GroupDetails) this.getItem(position);


        final ImageView img = view.findViewById(R.id.logo);

        img.setImageResource(R.drawable.ic_folder);

        final TextView nameProject = view.findViewById(R.id.nameProject);
        nameProject.setText(project_actual.getName());

        final Typeface typeface =
                Typeface.createFromAsset(this.activity.getAssets(), "font/digital-readout.heavy.ttf");

        final TextView durationProject = view.findViewById(R.id.t_duration);
        durationProject.setText(project_actual.getTimeSegString());
        durationProject.setTypeface(typeface);

        if (project_actual.isRunning()){
            durationProject.setTextColor(this.activity.getResources().getColor(R.color.dark_red));
            durationProject.setBackground(this.activity.getDrawable(R.drawable.border_red));
        }
        return view;
    }

    public void removeItems(final List<GroupDetails> list){
        for(final GroupDetails gd : list){
            final Intent intent = new Intent (ProjectsList.DELETE_PROJECT);
            intent.putExtra("id",gd.getId());
            this.activity.sendBroadcast(intent);
        }

        this.notifyDataSetChanged();
    }

    public void clear(){
        this.projects.clear();
        this.notifyDataSetChanged();
    }

    public void add(final GroupDetails groupDetails){
        this.projects.add(groupDetails);
        this.notifyDataSetChanged();
    }

}
