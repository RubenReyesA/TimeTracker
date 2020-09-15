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
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ReportsList extends AppCompatActivity {
    private final String tag = getClass().getSimpleName(); // Para los logs

    private final List<ReportsDetails> UserSelection = new ArrayList<>();
    private final ArrayList<ReportsDetails> list_Reports = new ArrayList<>();
    private final ItemReportsListAdapter adapter = new ItemReportsListAdapter(this, this.list_Reports);

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_reports_list);

        ListView listView = this.findViewById(R.id.listViewReports);

        listView.setNestedScrollingEnabled(true);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this.modeListener);
        listView.setEmptyView(this.findViewById(R.id.emptyReports));

        final Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setTitle(getString(R.string.Reports));

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView.setAdapter(this.adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            final ReportsDetails actual = ReportsList.this.list_Reports.get(position);

            final File file = new File(actual.getPath());
            if(file.exists()) {

                final Intent intent = new Intent(ReportsList.this, ShowReport.class);

                intent.putExtra("name", actual.getName());
                intent.putExtra("path", actual.getPath());
                intent.putExtra("type", actual.getType());

                ReportsList.this.startActivity(intent);
            }
            else{
                Toasty.warning(ReportsList.this, getString(R.string.ReportsDelete)).show();
                final MaterialDialog mDialog = new MaterialDialog.Builder(ReportsList.this)
                        .setTitle(getString(R.string.DeleteQuestion))
                        .setMessage(getString(R.string.DeleteEntry))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.Delete), R.drawable.ic_delete, (dialogInterface, which) -> {
                            // Delete Operation
                            final ArrayList<ReportsDetails> delete = new ArrayList<>();
                            delete.add(actual);
                            ReportsList.this.adapter.removeItems(delete);
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton(getString(R.string.Cancel), R.drawable.ic_close,
                                (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();

                // Show Dialog
                mDialog.show();
            }
        });
    }


    AbsListView.MultiChoiceModeListener modeListener = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(final ActionMode mode, final int position, final long id, final boolean checked) {

            if(ReportsList.this.UserSelection.contains(ReportsList.this.list_Reports.get(position))){

                ReportsList.this.UserSelection.remove(ReportsList.this.list_Reports.get(position));
            }
            else{
                ReportsList.this.UserSelection.add(ReportsList.this.list_Reports.get(position));

            }

            mode.setTitle(ReportsList.this.UserSelection.size() + " " + ReportsList.this.getString(R.string.items_selected));

            mode.invalidate();

        }

        @Override
        public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
            final MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.reports_list_context_menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
            return false;
        }

        @SuppressLint("RestrictedApi")
        @Override
        public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {

            switch (item.getItemId()){
                case R.id.action_delete:
                    final MaterialDialog mDialog = new MaterialDialog.Builder(ReportsList.this)
                            .setTitle(getString(R.string.DeleteQuestion))
                            .setMessage(getString(R.string.DeleteFile))
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.Delete), R.drawable.ic_delete, (dialogInterface, which) -> {
                                // Delete Operation
                                ReportsList.this.delete(mode);
                                dialogInterface.dismiss();
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

            ReportsList.this.UserSelection.clear();

        }
    };


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
         * @param intent  objecte Intent que arriba per "broadcast" i del qual en fem
         *                servir l'atribut "action" per saber quina mena de intent és
         *                i els extres per obtenir les dades a mostrar i si el projecte
         *                actual és l'arrel de tot l'arbre o no
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(this.tag, "onReceive");
            if (Objects.equals(intent.getAction(), TreeManagerService.SEND_REPORTS)) {

                final ArrayList<ReportsDetails> reportsDetailsArrayList =
                        (ArrayList<ReportsDetails>) intent
                                .getSerializableExtra("llista_reports");
                ReportsList.this.adapter.clear();


                assert reportsDetailsArrayList != null;
                for (final ReportsDetails reportsDetails : reportsDetailsArrayList) {
                    ReportsList.this.adapter.add(reportsDetails);
                }
                // això farà redibuixar el ListView
                ReportsList.this.adapter.notifyDataSetChanged();
                Log.d(this.tag, "mostro els fills actualitzats");
            } else {
                // no pot ser
                assert false : "intent d'acció no prevista";
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }


    private ReportsList.Receptor receptor;

    public static final String GIVE_REPORTS = "give_reports";

    @Override
    public final void onResume() {
        Log.i(this.tag, "onResume");

        final IntentFilter filter;
        filter = new IntentFilter();
        filter.addAction(TreeManagerService.SEND_REPORTS);
        this.receptor = new ReportsList.Receptor();
        this.registerReceiver(this.receptor, filter);
        this.sendBroadcast(new Intent(ReportsList.GIVE_REPORTS));

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

    private void delete(final ActionMode mode) {
        this.adapter.removeItems(this.UserSelection);
        mode.finish();
    }
}
