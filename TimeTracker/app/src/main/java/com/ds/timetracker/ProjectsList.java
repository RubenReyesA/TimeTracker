package com.ds.timetracker;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class ProjectsList extends AppCompatActivity {
    private final String tag = getClass().getSimpleName(); // Para los logs

    private final List<GroupDetails> UserSelection = new ArrayList<>();
    private final ArrayList<GroupDetails> list_project = new ArrayList<>();
    private final ItemProjectListAdapter adapter = new ItemProjectListAdapter(this, this.list_project);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_projects_list);

        ListView listView = this.findViewById(R.id.listViewProjects);

        listView.setNestedScrollingEnabled(true);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this.modeListener);
        listView.setEmptyView(this.findViewById(R.id.emptyProjects));

        final Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()){

                case R.id.action_save:
                    final Intent save = new Intent(ProjectsList.SAVE_TREE);
                    ProjectsList.this.sendBroadcast(save);
                    break;
                case R.id.action_sort:
                    ProjectsList.this.openSort();
                    break;
                case R.id.action_settings:
                    ProjectsList.this.startActivity(new Intent (ProjectsList.this, Settings.class));
                    break;
                case R.id.action_about:
                    ProjectsList.this.startActivity(new Intent (ProjectsList.this, About.class));
                    break;
                case R.id.action_tasks_running:
                    final Intent intent_tasks = new Intent(ProjectsList.CHANGE_TASKS_RUNNING);
                    ProjectsList.this.sendBroadcast(intent_tasks);
                    ProjectsList.this.startActivity(new Intent(ProjectsList.this, TasksRunning.class));
                    break;
                case R.id.action_report:
                    final Intent intent = new Intent(ProjectsList.this, GenerateReport.class);
                    intent.putExtra("Project", "Root");
                    ProjectsList.this.startActivity(intent);
                    break;
                case R.id.action_goToReports:
                    ProjectsList.this.startActivity(new Intent(ProjectsList.this, ReportsList.class));
                default:
                    break;
            }

            return true;
        });

        listView.setAdapter(this.adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            Log.i(ProjectsList.this.tag, "onItemClick");
            Log.d(ProjectsList.this.tag, "pos = " + position + ", id = " + id);

            final Intent intent = new Intent(ProjectsList.GO_FORWARD);
            intent.putExtra("posicio", position);
            ProjectsList.this.sendBroadcast(intent);

            if (ProjectsList.this.list_project.get(position).isProject()) {

                final GroupDetails gd = ProjectsList.this.list_project.get(position);

                final Intent intent_activity = new Intent(ProjectsList.this, ActivitiesList.class);
                intent_activity.putExtra("Title", gd.getName());
                ProjectsList.this.startActivity(intent_activity);

                // en aquesta classe ja es demanara la llista de fills

            }


        });

        FloatingActionButton fab = this.findViewById(R.id.add);

        fab.setOnClickListener(v -> {
            final Intent intent2 = new Intent (ProjectsList.this, CreateProject.class);
            intent2.putExtra("project_root", "TimeTracker");
            ProjectsList.this.startActivity(intent2);
        });

    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu){
        final MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.projects_list_menu, menu);
        return true;
    }


    AbsListView.MultiChoiceModeListener modeListener = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(final ActionMode mode, final int position, final long id, final boolean checked) {

            if(ProjectsList.this.UserSelection.contains(ProjectsList.this.list_project.get(position))){

                ProjectsList.this.UserSelection.remove(ProjectsList.this.list_project.get(position));
            }
            else{
                ProjectsList.this.UserSelection.add(ProjectsList.this.list_project.get(position));

            }

            mode.setTitle(ProjectsList.this.UserSelection.size() + " "+ ProjectsList.this.getString(R.string.items_selected));

            mode.invalidate();

        }

        @Override
        public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
            final MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.activities_list_context_menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
            if(ProjectsList.this.UserSelection.size()>1){
                final MenuItem item = menu.findItem(R.id.action_edit);
                item.setVisible(false);
                final MenuItem item2=menu.findItem(R.id.action_info);
                item2.setVisible(false);
                return true;
            }
            else{
                final MenuItem item = menu.findItem(R.id.action_edit);
                item.setVisible(true);
                final MenuItem item2 = menu.findItem(R.id.action_info);
                item2.setVisible(true);
                return true;
            }

        }

        @SuppressLint("RestrictedApi")
        @Override
        public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {

            switch (item.getItemId()){

                case R.id.action_info:
                    ProjectsList.this.openInfo(ProjectsList.this.UserSelection.get(0));
                    mode.finish();
                    return true;

                case R.id.action_edit:
                    ProjectsList.this.openEdit(ProjectsList.this.UserSelection.get(0));
                    mode.finish();
                    return true;

                case R.id.action_delete:
                    final MaterialDialog mDialog = new MaterialDialog.Builder(ProjectsList.this)
                            .setTitle(getString(R.string.DeleteQuestion))
                            .setMessage(getString(R.string.DeleteProject))
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.Delete), R.drawable.ic_delete, (dialogInterface, which) -> {
                                // Delete Operation
                                adapter.removeItems(UserSelection);
                                dialogInterface.dismiss();
                                mode.finish();
                            })
                            .setNegativeButton(getString(R.string.Cancel), R.drawable.ic_close,
                                    (dialogInterface, which) -> dialogInterface.dismiss())
                            .build();

                    // Show Dialog
                    mDialog.show();

            }
            return false;
        }

        @Override
        public void onDestroyActionMode(final ActionMode mode) {

            ProjectsList.this.UserSelection.clear();

        }
    };

    public void openSort(){

        final AlertDialog.Builder orderByDialog = new AlertDialog.Builder(this);
        orderByDialog.setTitle(getString(R.string.choose_order));
        orderByDialog.setCancelable(true);
        orderByDialog.setItems(R.array.orders_projects, (dialog, which) -> {
            switch (which){
                case 0:
                    Collections.sort(ProjectsList.this.list_project,GroupDetails.AlphaComparator);

                    ProjectsList.this.sendBroadcast(new Intent(ActivitiesList.ALPHA_COMPARATOR));
                    ProjectsList.this.adapter.notifyDataSetChanged();
                    break;
                case 1:
                    Collections.sort(ProjectsList.this.list_project,GroupDetails.RecentComparator);

                    ProjectsList.this.sendBroadcast(new Intent(ActivitiesList.RECENT_COMPARATOR));
                    ProjectsList.this.adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        });
        orderByDialog.show();

    }

    public void openInfo(final GroupDetails gd){

        final Intent intent = new Intent(this, InfoProject.class);

        intent.putExtra(InfoProject.NAME,gd.getName());
        intent.putExtra(InfoProject.DESCRIPTION,gd.getDescription());
        intent.putExtra(InfoProject.PROJECT_ROOT,gd.getRoot());
        intent.putExtra(InfoProject.START_DATE,gd.getStartDateString());
        intent.putExtra(InfoProject.END_DATE,gd.getEndDateString());
        intent.putExtra(InfoProject.DURATION,gd.getTimeSegString());
        intent.putExtra(InfoProject.NTASKS,gd.getNTasks());
        intent.putExtra(InfoProject.NSUBPROJECTS,gd.getNSubProjects());
        intent.putExtra(InfoProject.ROUTE,gd.getRoute());

        this.startActivity(intent);


    }

    public void openEdit(final GroupDetails gd){
        final Intent intent = new Intent (this, EditProject.class);
        intent.putExtra("id",gd.getId());

        intent.putExtra(InfoProject.NAME,gd.getName());
        intent.putExtra(InfoProject.DESCRIPTION,gd.getDescription());
        intent.putExtra(InfoProject.PROJECT_ROOT,gd.getRoot());
        intent.putExtra(InfoProject.START_DATE,gd.getStartDateString());
        intent.putExtra(InfoProject.END_DATE,gd.getEndDateString());
        intent.putExtra(InfoProject.DURATION,gd.getTimeSegString());
        intent.putExtra(InfoProject.NTASKS,gd.getNTasks());
        intent.putExtra(InfoProject.NSUBPROJECTS,gd.getNSubProjects());

        this.startActivity(intent);
    }


    private class Receptor extends BroadcastReceiver {
        /**
         * Nom de la classe per fer aparèixer als missatges de logging del
         * LogCat.
         *
         * @see Log
         */
        private final String tag = getClass().getCanonicalName();

        /**
         * Gestiona tots els intents enviats, de moment només el de la
         * acció TE_FILLS. La gestió consisteix en actualitzar la llista
         * de dades que s'està mostrant mitjançant el seu adaptador.
         *
         * @param context
         * @param intent
         * objecte Intent que arriba per "broadcast" i del qual en fem
         * servir l'atribut "action" per saber quina mena de intent és
         * i els extres per obtenir les dades a mostrar i si el projecte
         * actual és l'arrel de tot l'arbre o no
         *
         */
        @SuppressLint("RestrictedApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(this.tag, "onReceive");
            if (Objects.equals(intent.getAction(), TreeManagerService.SEND_ITEMS)) {
                boolean actualGroupisRoot = intent.getBooleanExtra(
                        "activitat_pare_actual_es_arrel", false);
                // obtenim la nova llista de dades d'activitat que ve amb
                // l'intent
                @SuppressWarnings("unchecked") final ArrayList<GroupDetails> groupDetailsList =
                        (ArrayList<GroupDetails>) intent
                                .getSerializableExtra("llista_dades_activitats");
                ProjectsList.this.adapter.clear();

                assert groupDetailsList != null;
                final GroupDetails actual = groupDetailsList.remove(groupDetailsList.size() - 1);

                for (final GroupDetails groupDetails : groupDetailsList) {
                    ProjectsList.this.adapter.add(groupDetails);
                }
                // això farà redibuixar el ListView
                ProjectsList.this.adapter.notifyDataSetChanged();
                actualGroupisRoot =true;
                Log.d(this.tag, "mostro els fills actualitzats");

            } else if (intent.getAction().equals(TreeManagerService.SEND_ARE_TASKS_RUNNING)) {

                final boolean close = intent.getBooleanExtra("running",false);

                if(close){
                    Log.d(this.tag, "parem servei");
                    ProjectsList.this.sendBroadcast(new Intent(ProjectsList.STOP_SERVICE));
                    ProjectsList.this.finish();
                }
                else{
                    final MaterialDialog mDialog = new MaterialDialog.Builder(ProjectsList.this)
                            .setTitle(ProjectsList.this.getString(R.string.AreTasksRunningTitle))
                            .setMessage(ProjectsList.this.getString(R.string.AreTasksRunning))
                            .setCancelable(false)
                            .setPositiveButton(ProjectsList.this.getString(R.string.AreTasksRunningYES), R.drawable.ic_stop, (dialogInterface, which) -> {
                                final Intent intent1 = new Intent (TasksRunning.STOP);
                                intent1.putExtra("id",-1);
                                ProjectsList.this.sendBroadcast(intent1);

                                dialogInterface.dismiss();
                                Log.d(Receptor.this.tag, "parem servei");

                                ProjectsList.this.sendBroadcast(new Intent(ProjectsList.STOP_SERVICE));
                                ProjectsList.this.finish();
                            })
                            .setNegativeButton(ProjectsList.this.getString(R.string.AreTasksRunningNO), R.drawable.ic_close, (dialogInterface, which) -> {
                                dialogInterface.dismiss();
                                final Intent intent_tasks = new Intent(ProjectsList.CHANGE_TASKS_RUNNING);
                                ProjectsList.this.sendBroadcast(intent_tasks);
                                ProjectsList.this.startActivity(new Intent(ProjectsList.this, TasksRunning.class));
                            })
                            .build();

                    // Show Dialog
                    mDialog.show();
                }


            }
        }
    }


    private ProjectsList.Receptor receptor;


    public static final String GIVE_ITEMS = "give_items";

    public static final String DELETE_PROJECT = "delete_project";

    public static final String SAVE_TREE = "save_tree";

    public static final String GO_FORWARD = "go_forward";

    public static final String GO_BACK = "go_back";

    public static final String STOP_SERVICE = "stop_service";

    public static final String CHANGE_TASKS_RUNNING = "change_tasks_running";

    public static final String ARE_TASKS_RUNNING = "are_tasks_running";


    @Override
    public final void onResume() {
        Log.i(this.tag, "onResume");

        final IntentFilter filter;
        filter = new IntentFilter();
        filter.addAction(TreeManagerService.SEND_ITEMS);
        filter.addAction(TreeManagerService.SEND_ARE_TASKS_RUNNING);
        this.receptor = new ProjectsList.Receptor();
        this.registerReceiver(this.receptor, filter);

        // Crea el servei GestorArbreActivitats, si no existia ja. A més,
        // executa el mètode onStartCommand del servei, de manera que
        // *un cop creat el servei* = havent llegit ja l'arbre si es el
        // primer cop, ens enviarà un Intent amb acció TE_FILLS amb les
        // dades de les activitats de primer nivell per que les mostrem.
        // El que no funcionava era crear el servei (aquí mateix o
        // a onCreate) i després demanar la llista d'activiats a mostrar
        // per que startService s'executa asíncronament = retorna de seguida,
        // i la petició no trobava el servei creat encara.
        this.startService(new Intent(this, TreeManagerService.class));

        super.onResume();
    }

    @Override
    public final void onPause() {
        Log.i(this.tag, "onPause");
        this.unregisterReceiver(this.receptor);
        super.onPause();
    }

    @Override
    public final void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        Log.i(this.tag, "onSaveInstaceState");
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public final void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.i(this.tag, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    public final void onStop() {
        Log.i(this.tag, "onStop");
        super.onStop();
    }
    @Override
    public final void onDestroy() {
        Log.i(this.tag, "onDestroy");
        super.onDestroy();
    }
    @Override
    public final void onStart() {
        Log.i(this.tag, "onStart");
        super.onStart();
    }
    @Override
    public final void onRestart() {
        Log.i(this.tag, "onRestart");
        super.onRestart();
    }
    @Override
    public final void onConfigurationChanged(@NonNull Configuration newConfig) {
        Log.i(this.tag, "onConfigurationChanged");
        if (Log.isLoggable(this.tag, Log.VERBOSE)) {
            Log.v(this.tag, newConfig.toString());
        }
    }
    @Override
    public final void onBackPressed() {
        Log.i(this.tag, "onBackPressed");
        this.sendBroadcast(new Intent (ProjectsList.ARE_TASKS_RUNNING));
    }


}
