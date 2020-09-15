package com.ds.timetracker;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ActivitiesList extends AppCompatActivity {
    private final String tag = getClass().getSimpleName();

    private final List<GroupDetails> userSelection = new ArrayList<>();
    private final ArrayList<GroupDetails> groupList = new ArrayList<>();
    private final ItemActivitiesListAdapter adapter =
            new ItemActivitiesListAdapter(this, this.groupList);

    private GroupDetails actual;

    public static final String START = "start";
    public static final String STOP = "stop";


    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_activities_list);

        Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);

        final Intent intent = this.getIntent();
        String title = intent.getStringExtra("Title");
        Objects.requireNonNull(this.getSupportActionBar()).setTitle(title);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {

                case R.id.action_info:
                    this.openInfo(this.actual);
                    break;
                case R.id.action_report:
                    final Intent intent1;
                    intent1 = new Intent(this,
                            GenerateReport.class);
                    intent1.putExtra("Project", title);
                    this.startActivity(intent1);
                    break;
                case R.id.action_sort:
                    this.openSort();
                    break;
                default:
                    break;
            }
            return true;

        });

        ListView listView = this.findViewById(R.id.listViewReports);


        listView.setEmptyView(this.findViewById(R.id.emptyActivities));

        listView.setNestedScrollingEnabled(true);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this.modeListener);

        listView.setAdapter(this.adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            Log.i(this.tag, "onItemClick");
            Log.d(this.tag, "pos = " + position + ", id = " + id);

            final Intent intent12 = new Intent(ProjectsList.GO_FORWARD);
            intent12.putExtra("posicio", position);
            this.sendBroadcast(intent12);

            final GroupDetails actualGroup = this.groupList.get(position);

            if (actualGroup.isProject()) {
                final Intent intent2 =
                        new Intent(this, ActivitiesList.class);
                intent2.putExtra("Title", actualGroup.getName());
                this.startActivity(intent2);
            } else if (actualGroup.isTask()) {
                final Intent intent2 =
                        new Intent(this, IntervalsList.class);
                intent2.putExtra("Title", actualGroup.getName());
                this.startActivity(intent2);
            }
        });

        FloatingActionsMenu fabAdd = this.findViewById(R.id.add);

        fabAdd.setOnFloatingActionsMenuUpdateListener(
                new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                ActivitiesList.this.findViewById(R.id.obstructor).setVisibility(View.VISIBLE);
                final Animation animFadeIn = AnimationUtils.
                        loadAnimation(ActivitiesList.this.getApplicationContext(), R.anim.fade_in);
                ActivitiesList.this.findViewById(R.id.obstructor).startAnimation(animFadeIn);

                final MenuItem item = toolbar.getMenu().findItem(R.id.action_report);
                item.setVisible(false);
                final MenuItem item2 = toolbar.getMenu().findItem(R.id.action_sort);
                item2.setVisible(false);
                final MenuItem item3 = toolbar.getMenu().findItem(R.id.action_info);
                item3.setVisible(false);

            }

            @Override
            public void onMenuCollapsed() {
                ActivitiesList.this.findViewById(R.id.obstructor).setVisibility(View.INVISIBLE);
                final Animation animFadeOut = AnimationUtils.
                        loadAnimation(ActivitiesList.this.getApplicationContext(), R.anim.fade_out);
                ActivitiesList.this.findViewById(R.id.obstructor).startAnimation(animFadeOut);

                final MenuItem item = toolbar.getMenu().findItem(R.id.action_report);
                item.setVisible(true);
                final MenuItem item2 = toolbar.getMenu().findItem(R.id.action_sort);
                item2.setVisible(true);
                final MenuItem item3 = toolbar.getMenu().findItem(R.id.action_info);
                item3.setVisible(true);

            }
        });

        final FloatingActionButton fabProject = this.findViewById(R.id.fabProject);
        fabProject.setOnClickListener(v -> {
            fabAdd.collapse();
            final Intent intent2 = new Intent(
                    this, CreateProject.class);
            intent2.putExtra("project_root", title);
            this.startActivity(intent2);

        });

        final FloatingActionButton fabTask = this.findViewById(R.id.fabTask);
        fabTask.setOnClickListener(v -> {
            fabAdd.collapse();
            final Intent intent2 = new Intent(
                    this, CreateTask.class);
            intent2.putExtra("project_root", title);
            this.startActivity(intent2);
        });


    }

    private final AbsListView.MultiChoiceModeListener modeListener =
            new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode,
                                              int position, long id,
                                              boolean checked) {

            FloatingActionsMenu fab = ActivitiesList.this.findViewById(R.id.add);

            if (fab.getVisibility() == View.VISIBLE) {
                fab.setVisibility(View.INVISIBLE);
                final Animation animFadeOut = AnimationUtils.
                        loadAnimation(ActivitiesList.this.getApplicationContext(),
                                R.anim.fade_out_fab);
                fab.startAnimation(animFadeOut);
            }


            if (ActivitiesList.this.userSelection.contains(ActivitiesList.this.groupList.get(position))) {

                ActivitiesList.this.userSelection.remove(ActivitiesList.this.groupList.get(position));
            } else {
                ActivitiesList.this.userSelection.add(ActivitiesList.this.groupList.get(position));

            }

            mode.setTitle(
                    ActivitiesList.this.userSelection.size() + " "
                            + ActivitiesList.this.getString(R.string.items_selected));

            mode.invalidate();

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode,
                                          Menu menu) {
            final MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.activities_list_context_menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode,
                                           Menu menu) {
            if (ActivitiesList.this.userSelection.size() > 1) {
                final MenuItem item = menu.findItem(R.id.action_edit);
                item.setVisible(false);
                final MenuItem item2 = menu.findItem(R.id.action_info);
                item2.setVisible(false);
                return true;
            } else {
                final MenuItem item = menu.findItem(R.id.action_edit);
                item.setVisible(true);
                final MenuItem item2 = menu.findItem(R.id.action_info);
                item2.setVisible(true);
                return true;
            }

        }

        @SuppressLint("RestrictedApi")
        @Override
        public boolean onActionItemClicked(ActionMode mode,
                                           MenuItem item) {


            switch (item.getItemId()) {

                case R.id.action_info:
                    ActivitiesList.this.openInfo(ActivitiesList.this.userSelection.get(0));
                    mode.finish();
                    return false;

                case R.id.action_edit:
                    ActivitiesList.this.openEdit(ActivitiesList.this.userSelection.get(0));
                    mode.finish();
                    return false;

                case R.id.action_delete:
                    final MaterialDialog mDialog = new MaterialDialog.Builder(ActivitiesList.this)
                            .setTitle(getString(R.string.DeleteQuestion))
                            .setMessage(getString(R.string.DeleteActivity))
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.Delete), R.drawable.ic_delete, (dialogInterface, which) -> {
                                // Delete Operation
                                ActivitiesList.this.adapter.removeItems(ActivitiesList.this.userSelection);
                                dialogInterface.dismiss();
                                mode.finish();
                            })
                            .setNegativeButton(getString(R.string.Cancel), R.drawable.ic_close,
                                    (dialogInterface, which) -> dialogInterface.dismiss())
                            .build();

                    // Show Dialog
                    mDialog.show();
                default:
                    break;

            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            ActivitiesList.this.userSelection.clear();

            FloatingActionsMenu fab = ActivitiesList.this.findViewById(R.id.add);
            fab.setVisibility(View.VISIBLE);
            final Animation animFadeIn = AnimationUtils.
                    loadAnimation(ActivitiesList.this.getApplicationContext(), R.anim.fade_in_fab);
            fab.startAnimation(animFadeIn);

        }
    };

    @Override
    public final boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }

    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.activities_list_menu, menu);
        return true;
    }

    public static final String ALPHA_COMPARATOR = "alpha_comparator";
    public static final String RECENT_COMPARATOR = "recent_comparator";
    public static final String TYPE_COMPARATOR = "type_comparator";


    public final void openSort() {

        final AlertDialog.Builder orderByDialog = new AlertDialog.Builder(this);
        orderByDialog.setTitle(this.getString(R.string.choose_order));
        orderByDialog.setCancelable(true);
        orderByDialog.setItems(R.array.orders_activities, (dialog, which) -> {
            switch (which) {
                case 0:
                    Collections.sort(this.groupList, GroupDetails.AlphaComparator);

                    this.sendBroadcast(new Intent(ActivitiesList.ALPHA_COMPARATOR));
                    this.adapter.notifyDataSetChanged();
                    break;
                case 1:
                    Collections.sort(this.groupList, GroupDetails.RecentComparator);

                    this.sendBroadcast(new Intent(ActivitiesList.RECENT_COMPARATOR));
                    this.adapter.notifyDataSetChanged();
                    break;
                case 2:
                    Collections.sort(this.groupList, GroupDetails.TypeComparator);

                    this.sendBroadcast(new Intent(ActivitiesList.TYPE_COMPARATOR));
                    this.adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        });
        orderByDialog.show();

    }

    public class Receptor extends BroadcastReceiver {

        @Override
        public final void onReceive(Context context,
                                    Intent intent) {

            Log.d(ActivitiesList.this.tag, "onReceive Receptor LlistaActivitats");

            if (intent.getAction().equals(TreeManagerService.SEND_ITEMS)) {
                final ArrayList<GroupDetails> groupDetailsList =
                        (ArrayList<GroupDetails>) intent
                                .getSerializableExtra(
                                        "llista_dades_activitats");
                ActivitiesList.this.adapter.clear();

                assert groupDetailsList != null;
                ActivitiesList.this.actual = groupDetailsList.remove(groupDetailsList.size() - 1);

                for (final GroupDetails groupDetails : groupDetailsList) {
                    ActivitiesList.this.adapter.add(groupDetails);
                }
                ActivitiesList.this.adapter.notifyDataSetChanged();
            }
            Log.i(ActivitiesList.this.tag, "final de onReceive LlistaActivitats");
        }
    }

    private ActivitiesList.Receptor receptor;

    @Override
    public final void onBackPressed() {
        Log.i(this.tag, "onBackPressed");
        this.sendBroadcast(new Intent(ProjectsList.GO_BACK));
        Log.d(this.tag, "enviat intent PUJA_NIVELL");
        super.onBackPressed();
    }

    @Override
    public final void onResume() {
        Log.i(this.tag, "onResume activities");

        final IntentFilter filter;
        filter = new IntentFilter();
        filter.addAction(TreeManagerService.SEND_ITEMS);
        this.receptor = new ActivitiesList.Receptor();
        this.registerReceiver(this.receptor, filter);

        this.sendBroadcast(new Intent(ProjectsList.GIVE_ITEMS));
        Log.d(this.tag, "enviat intent DONAM_FILLS");

        super.onResume();
    }

    @Override
    public final void onPause() {
        Log.i(this.tag, "onPause activities");

        this.unregisterReceiver(this.receptor);

        super.onPause();
    }
    @Override
    public final void onDestroy() {
        Log.i(this.tag, "onDestroy activities");
        super.onDestroy();
    }

    @Override
    public final void onStart() {
        Log.i(this.tag, "onStart activities");
        super.onStart();
    }

    @Override
    public final void onStop() {
        Log.i(this.tag, "onStop activities");
        super.onStop();
    }

    @Override
    public final void onRestart() {
        Log.i(this.tag, "onRestart activities");
        super.onRestart();
    }

    @Override
    public final void onSaveInstanceState(
            @NonNull Bundle savedInstanceState) {
        Log.i(this.tag, "onSaveInstanceState activities");
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public final void onRestoreInstanceState(
            @NonNull Bundle savedInstanceState) {
        Log.i(this.tag, "onRestoreInstanceState activities");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public final void onConfigurationChanged(
            @NonNull Configuration newConfig) {
        Log.i(this.tag, "onConfigurationChanged activities");
        if (Log.isLoggable(this.tag, Log.VERBOSE)) {
            Log.v(this.tag, newConfig.toString());
        }
    }

    public final void openInfo(GroupDetails gd) {

        if (gd.isProject()) {
            final Intent intent = new Intent(this, InfoProject.class);

            intent.putExtra(InfoProject.NAME, gd.getName());
            intent.putExtra(InfoProject.DESCRIPTION, gd.getDescription());
            intent.putExtra(InfoProject.PROJECT_ROOT, gd.getRoot());
            intent.putExtra(InfoProject.START_DATE, gd.getStartDateString());
            intent.putExtra(InfoProject.END_DATE, gd.getEndDateString());
            intent.putExtra(InfoProject.DURATION, gd.getTimeSegString());
            intent.putExtra(InfoProject.NTASKS, gd.getNTasks());
            intent.putExtra(InfoProject.NSUBPROJECTS, gd.getNSubProjects());
            intent.putExtra(InfoProject.ROUTE, gd.getRoute());


            this.startActivity(intent);
        } else if (gd.isTask()) {

            final Intent intent = new Intent(this, InfoTask.class);

            intent.putExtra(InfoTask.NAME, gd.getName());
            intent.putExtra(InfoTask.DESCRIPTION, gd.getDescription());
            intent.putExtra(InfoTask.PROJECT_ROOT, gd.getRoot());
            intent.putExtra(InfoTask.START_DATE, gd.getStartDateString());
            intent.putExtra(InfoTask.END_DATE, gd.getEndDateString());
            intent.putExtra(InfoTask.DURATION, gd.getTimeSegString());
            intent.putExtra(InfoTask.NINTERVALS, gd.getNIntervals());
            intent.putExtra(InfoTask.ROUTE, gd.getRoute());


            this.startActivity(intent);

        }

    }

    public final void openEdit(GroupDetails gd) {

        if (gd.isProject()) {
            final Intent intent = new Intent(this, EditProject.class);
            intent.putExtra("id", gd.getId());

            intent.putExtra(InfoProject.NAME, gd.getName());
            intent.putExtra(InfoProject.DESCRIPTION, gd.getDescription());
            intent.putExtra(InfoProject.PROJECT_ROOT, gd.getRoot());
            intent.putExtra(InfoProject.START_DATE, gd.getStartDateString());
            intent.putExtra(InfoProject.END_DATE, gd.getEndDateString());
            intent.putExtra(InfoProject.DURATION, gd.getTimeSegString());
            intent.putExtra(InfoProject.NTASKS, gd.getNTasks());
            intent.putExtra(InfoProject.NSUBPROJECTS, gd.getNSubProjects());

            this.startActivity(intent);
        } else if (gd.isTask()) {

            final Intent intent = new Intent(this, EditTask.class);
            intent.putExtra("id", gd.getId());

            intent.putExtra(InfoTask.NAME, gd.getName());
            intent.putExtra(InfoTask.DESCRIPTION, gd.getDescription());
            intent.putExtra(InfoTask.PROJECT_ROOT, gd.getRoot());
            intent.putExtra(InfoTask.START_DATE, gd.getStartDateString());
            intent.putExtra(InfoTask.END_DATE, gd.getEndDateString());
            intent.putExtra(InfoTask.DURATION, gd.getTimeSegString());
            intent.putExtra(InfoTask.NINTERVALS, gd.getNIntervals());

            this.startActivity(intent);

        }

    }

}
