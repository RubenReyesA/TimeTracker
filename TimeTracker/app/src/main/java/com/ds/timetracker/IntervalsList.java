package com.ds.timetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;

public class IntervalsList extends AppCompatActivity {
    private final String tag = getClass().getSimpleName();

    public static final String DELETE_INTERVAL = "delete_interval";

    private final ArrayList<IntervalDetails> UserSelection = new ArrayList<>();
    private final ArrayList<IntervalDetails> intervalDetailsArray = new ArrayList<>();
    private final ItemIntervalListAdapter adapter = new ItemIntervalListAdapter(this, this.intervalDetailsArray);

    private GroupDetails actual;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_intervals_list);

        final Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {

            if (item.getItemId()==R.id.action_info){
                IntervalsList.this.openInfo(IntervalsList.this.actual);
            }
            return true;

        });

        final Intent intent = this.getIntent();
        String title = intent.getStringExtra("Title");
        Objects.requireNonNull(this.getSupportActionBar()).setTitle(title);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ListView listView = this.findViewById(R.id.listViewIntervals);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this.modeListener);
        listView.setAdapter(this.adapter);
        listView.setEmptyView(this.findViewById(R.id.emptyIntervals));
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }

    public class Receptor extends BroadcastReceiver {

        @Override
        public final void onReceive(Context context,
                                    Intent intent) {

            Log.d(IntervalsList.this.tag, "onReceive Receptor LlistaIntervals");

            if (intent.getAction().equals(TreeManagerService.SEND_ITEMS)) {
                final ArrayList<IntervalDetails> intervalDetailsList =
                        (ArrayList<IntervalDetails>) intent
                                .getSerializableExtra("llista_dades_intervals");

                IntervalsList.this.adapter.clear();

                IntervalsList.this.actual = (GroupDetails) intent.getSerializableExtra("actual");

                assert intervalDetailsList != null;
                for (final IntervalDetails intervalDetails : intervalDetailsList) {
                    IntervalsList.this.adapter.add(intervalDetails);
                }
                IntervalsList.this.adapter.notifyDataSetChanged();

                String subtitle = IntervalsList.this.getResources().getString(R.string.NumberofIntervals_Interval)+ " " + IntervalsList.this.actual.getNIntervals();
                Objects.requireNonNull(IntervalsList.this.getSupportActionBar()).setSubtitle(subtitle);

            }
            Log.i(IntervalsList.this.tag, "final de onReceive LlistaIntervals");
        }
    }

    private IntervalsList.Receptor receptor;

    @Override
    public final void onBackPressed() {
        Log.i(this.tag, "onBackPressed");
        this.sendBroadcast(new Intent(ProjectsList.GO_BACK));
        Log.d(this.tag, "enviat intent PUJA_NIVELL");
        super.onBackPressed();
    }

    @Override
    public final void onResume() {
        Log.i(this.tag, "onResume intervals");

        final IntentFilter filter;
        filter = new IntentFilter();
        filter.addAction(TreeManagerService.SEND_ITEMS);
        this.receptor = new IntervalsList.Receptor();
        this.registerReceiver(this.receptor, filter);

        this.sendBroadcast(new Intent(ProjectsList.GIVE_ITEMS));
        Log.d(this.tag, "enviat intent DONAM_FILLS");

        super.onResume();
    }

    @Override
    public final void onPause() {
        Log.i(this.tag, "onPause intervals");

        this.unregisterReceiver(this.receptor);

        super.onPause();
    }

    @Override
    public final void onDestroy() {
        Log.i(this.tag, "onDestroy intervals");
        super.onDestroy();
    }

    @Override
    public final void onStart() {
        Log.i(this.tag, "onStart intervals");
        super.onStart();
    }

    @Override
    public final void onStop() {
        Log.i(this.tag, "onStop intervals");
        super.onStop();
    }

    @Override
    public final void onRestart() {
        Log.i(this.tag, "onRestart intervals");
        super.onRestart();
    }

    @Override
    public final void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        Log.i(this.tag, "onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public final void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.i(this.tag, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public final void onConfigurationChanged(@NonNull Configuration newConfig) {
        Log.i(this.tag, "onConfigurationChanged");
        if (Log.isLoggable(this.tag, Log.VERBOSE)) {
            Log.v(this.tag, newConfig.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.intervals_list_menu, menu);
        return true;
    }

    public void openInfo(final GroupDetails gd){

            final Intent intent = new Intent(this, InfoTask.class);

            intent.putExtra(InfoTask.NAME, gd.getName());
            intent.putExtra(InfoTask.DESCRIPTION, gd.getDescription());
            intent.putExtra(InfoTask.PROJECT_ROOT, gd.getRoot());
            intent.putExtra(InfoTask.START_DATE, gd.getStartDateString());
            intent.putExtra(InfoTask.END_DATE, gd.getEndDateString());
            intent.putExtra(InfoTask.DURATION, gd.getTimeSegString());
            intent.putExtra(InfoTask.NINTERVALS, gd.getNIntervals());
            intent.putExtra(InfoTask.ROUTE,gd.getRoute());

        this.startActivity(intent);

    }

    AbsListView.MultiChoiceModeListener modeListener = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(final ActionMode mode, final int position, final long id, final boolean checked) {


            if(IntervalsList.this.UserSelection.contains(IntervalsList.this.intervalDetailsArray.get(position))){

                IntervalsList.this.UserSelection.remove(IntervalsList.this.intervalDetailsArray.get(position));
            }
            else{
                IntervalsList.this.UserSelection.add(IntervalsList.this.intervalDetailsArray.get(position));

            }

            mode.setTitle(IntervalsList.this.UserSelection.size() + " "+ IntervalsList.this.getString(R.string.items_selected));

            mode.invalidate();

        }

        @Override
        public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
            final MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.intervals_list_context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {


           if(item.getItemId()==R.id.action_delete){
               IntervalsList.this.adapter.removeItems(IntervalsList.this.UserSelection);
                mode.finish();
                return true;

           }
           return false;
        }

        @Override
        public void onDestroyActionMode(final ActionMode mode) {
            IntervalsList.this.UserSelection.clear();
        }
    };
}
