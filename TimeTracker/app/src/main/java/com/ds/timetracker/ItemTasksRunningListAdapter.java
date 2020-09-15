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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ItemTasksRunningListAdapter extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<GroupDetails> grups;
    protected View view;

    public ItemTasksRunningListAdapter(final Activity activity, final ArrayList<GroupDetails> l_grups) {
        this.activity = activity;
        grups = l_grups;
    }

    public int getCount(){
        return this.grups.size();
    }

    public Object getItem(final int position){
        return this.grups.get(position);
    }

    public long getItemId(final int position){
        return this.grups.get(position).getId();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, final View contentView, final ViewGroup parent) {
        this.view = contentView;

        if (contentView == null) {
            final LayoutInflater inflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            this.view = inflater.inflate(R.layout.ds_tasks_running_list_item_layout, null);
        }


        final GroupDetails grup_actual = (GroupDetails) this.getItem(position);

        final ImageView img = this.view.findViewById(R.id.i_logo);

        final TextView nameGrup = this.view.findViewById(R.id.t_nameGrup);
        nameGrup.setText(grup_actual.getName());

        final Typeface typeface =
                Typeface.createFromAsset(this.activity.getAssets(), "font/digital-readout.heavy.ttf");

        final TextView duration = this.view.findViewById(R.id.t_duration);
        duration.setText(grup_actual.getTimeSegString());
        duration.setTypeface(typeface);


        if (grup_actual.isRunning()){
            duration.setBackground(this.activity.getDrawable(R.drawable.border_red));
            duration.setTextColor(this.activity.getResources().getColor(R.color.dark_red));

        }

        ImageButton PlayStop_btn = this.view.findViewById(R.id.btn_PlayStop);
        PlayStop_btn.setOnClickListener(v -> {
            if (grup_actual.isTask() && grup_actual.isRunning()) {
                final Intent intent = new Intent (TasksRunning.STOP);
                intent.putExtra("id",grup_actual.getId());
                ItemTasksRunningListAdapter.this.activity.sendBroadcast(intent);
            }
            else if (grup_actual.isTask() && !grup_actual.isRunning()) {
                final Intent intent = new Intent (TasksRunning.RESUME);
                intent.putExtra("id",grup_actual.getId());
                ItemTasksRunningListAdapter.this.activity.sendBroadcast(intent);
            }
        });


        ImageButton Pause_btn = this.view.findViewById(R.id.btn_Pause);

        Pause_btn.setOnClickListener(v -> {
            if (grup_actual.isTask() && grup_actual.isRunning()) {
                final Intent intent = new Intent (TasksRunning.PAUSE);
                intent.putExtra("id",grup_actual.getId());
                ItemTasksRunningListAdapter.this.activity.sendBroadcast(intent);
            }

        });

        if(grup_actual.isTask()){
            PlayStop_btn.setVisibility(View.VISIBLE);
            img.setImageResource(R.drawable.ic_assignment);
            if(grup_actual.isRunning()){
                PlayStop_btn.setTag("Stop");
                PlayStop_btn.setImageResource(R.drawable.ic_stop);
                Pause_btn.setVisibility(View.VISIBLE);
            }
            else{
                PlayStop_btn.setTag("Play");
                PlayStop_btn.setImageResource(R.drawable.ic_play);
                Pause_btn.setVisibility(View.INVISIBLE);
            }

        }


        return this.view;
    }


    public void clear(){
        this.grups.clear();
        this.notifyDataSetChanged();
    }

    public void add(final GroupDetails groupDetails){
        this.grups.add(groupDetails);
        this.notifyDataSetChanged();
    }
}
