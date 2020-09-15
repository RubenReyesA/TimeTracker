package com.ds.timetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;


public class TasksRunning extends AppCompatActivity {

    private final ArrayList<GroupDetails> tasksRunningList = new ArrayList<>();
    private final ItemTasksRunningListAdapter adapter = new ItemTasksRunningListAdapter(this, this.tasksRunningList);
    private boolean to_change;


    public static final String STOP = "stop_tasks";
    public static final String PAUSE = "pause_tasks";
    public static final String RESUME = "resume_tasks";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_tasks_running_list);

        final Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setTitle(R.string.TasksRunning);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setOnMenuItemClickListener(item -> {

            final Intent intent;

            switch (item.getItemId()){

                case R.id.action_resume:
                    intent = new Intent (RESUME);
                    intent.putExtra("id",-1);
                    TasksRunning.this.sendBroadcast(intent);
                    break;
                case R.id.action_pause:
                    intent = new Intent (PAUSE);
                    intent.putExtra("id",-1);
                    TasksRunning.this.sendBroadcast(intent);
                    break;
                case R.id.action_stop:
                    intent = new Intent (STOP);
                    intent.putExtra("id",-1);
                    TasksRunning.this.sendBroadcast(intent);
                    break;
                default:
                    break;
            }
            return true;

        });


        ListView listView = this.findViewById(R.id.listViewTasksRunning);


        listView.setEmptyView(this.findViewById(R.id.emptyTasksRunning));
        listView.setNestedScrollingEnabled(true);
        listView.setAdapter(this.adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            final GroupDetails gd = TasksRunning.this.tasksRunningList.get(position);

            final Intent intent = new Intent(TasksRunning.this, InfoTask.class);

            intent.putExtra(InfoTask.NAME, gd.getName());
            intent.putExtra(InfoTask.DESCRIPTION, gd.getDescription());
            intent.putExtra(InfoTask.PROJECT_ROOT, gd.getRoot());
            intent.putExtra(InfoTask.START_DATE, gd.getStartDateString());
            intent.putExtra(InfoTask.END_DATE, gd.getEndDateString());
            intent.putExtra(InfoTask.DURATION, gd.getTimeSegString());
            intent.putExtra(InfoTask.NINTERVALS, gd.getNIntervals());

            TasksRunning.this.startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.tasks_running_list_menu, menu);
        this.edit_toolbar_menu();
        this.to_change =true;
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }

    @Override
    public final void onBackPressed() {
        this.sendBroadcast(new Intent(ProjectsList.CHANGE_TASKS_RUNNING));
        super.onBackPressed();
    }

    public class Receptor extends BroadcastReceiver {

        @Override
        public final void onReceive(Context context,
                                    Intent intent) {

            if (Objects.equals(intent.getAction(), TreeManagerService.SEND_TASKS_RUNNING)) {

                final ArrayList<GroupDetails> groupDetailsList =
                        (ArrayList<GroupDetails>) intent.getSerializableExtra("llista_tasques_running");

                TasksRunning.this.adapter.clear();

                assert groupDetailsList != null;
                for (final GroupDetails groupDetails : groupDetailsList) {
                    TasksRunning.this.adapter.add(groupDetails);
                }

                String subtitle = TasksRunning.this.getResources().getString(R.string.NumberofTasks_Running)+ " " + (TasksRunning.this.tasksRunningList.size());
                Objects.requireNonNull(TasksRunning.this.getSupportActionBar()).setSubtitle(subtitle);

                if(TasksRunning.this.to_change){
                    TasksRunning.this.edit_toolbar_menu();
                }

                TasksRunning.this.adapter.notifyDataSetChanged();
            }
        }
    }

    private TasksRunning.Receptor receptor;

    public static final String GIVE_TASKS_RUNNING = "give_tasks_running";

    @Override
    public final void onResume() {

        final IntentFilter filter;
        filter = new IntentFilter();
        filter.addAction(TreeManagerService.SEND_TASKS_RUNNING);
        this.receptor = new TasksRunning.Receptor();
        this.registerReceiver(this.receptor, filter);

        this.sendBroadcast(new Intent(TasksRunning.GIVE_TASKS_RUNNING));

        super.onResume();
    }

    @Override
    public final void onPause() {

        this.unregisterReceiver(this.receptor);

        super.onPause();
    }


    private void edit_toolbar_menu(){
        final Toolbar toolbar = this.findViewById(R.id.appbar);
        final Menu menu = toolbar.getMenu();
        final MenuItem item1 = menu.findItem(R.id.action_resume);
        final MenuItem item2 = menu.findItem(R.id.action_pause);
        final MenuItem item3 = menu.findItem(R.id.action_stop);

        if(this.tasksRunningList.size()==0){
            item1.setIcon(R.drawable.ic_play_grey20);
            item2.setIcon(R.drawable.ic_pause_grey20);
            item3.setIcon(R.drawable.ic_stop_grey20);
            item1.setEnabled(false);
            item2.setEnabled(false);
            item3.setEnabled(false);
        }
        else if(this.all_running()){
            item1.setIcon(R.drawable.ic_play_grey20);
            item2.setIcon(R.drawable.ic_pause_white);
            item3.setIcon(R.drawable.ic_stop_white);
            item1.setEnabled(false);
            item2.setEnabled(true);
            item3.setEnabled(true);
        }
        else if(this.all_stop()){
            item1.setIcon(R.drawable.ic_play_white);
            item2.setIcon(R.drawable.ic_pause_grey20);
            item3.setIcon(R.drawable.ic_stop_grey20);
            item1.setEnabled(true);
            item2.setEnabled(false);
            item3.setEnabled(false);
        }
        else{
            item1.setIcon(R.drawable.ic_play_white);
            item2.setIcon(R.drawable.ic_pause_white);
            item3.setIcon(R.drawable.ic_stop_white);
            item1.setEnabled(true);
            item2.setEnabled(true);
            item3.setEnabled(true);
        }
    }

    private boolean all_stop(){
        boolean found = false;
        int index = 0;

        while (index< this.tasksRunningList.size() && !found){
            if(this.tasksRunningList.get(index).isRunning()){
                found=true;
            }
            else{
                index++;
            }
        }


        return !found;
    }

    private boolean all_running(){
        boolean found = false;
        int index = 0;

        while (index< this.tasksRunningList.size() && !found){
            if(!this.tasksRunningList.get(index).isRunning()){
                found=true;
            }
            else{
                index++;
            }
        }


        return !found;
    }
}