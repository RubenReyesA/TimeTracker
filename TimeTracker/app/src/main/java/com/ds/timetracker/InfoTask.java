package com.ds.timetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

/*Aquesta activity implementa la funcionalitat de mostra
la informació de una tasca*/

public class InfoTask extends AppCompatActivity {

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String PROJECT_ROOT = "project_root";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String DURATION = "duration";
    public static final String NINTERVALS = "nintervals";
    public static final String ROUTE = "route";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_info_task);


        final Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setTitle(R.string.InfoTask);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Intent intent = this.getIntent();

        /*Aqui agafem el nom, descripció, projec_root, data d'inici, data final,
         durada actual, el nombre d'intervals i el arbre de la tasca (ruta)*/
        final EditText name = this.findViewById(R.id.NameTask);
        final EditText description = this.findViewById(R.id.DescriptionTask);
        final EditText project_root = this.findViewById(R.id.ProjectTaskRoot);
        final EditText start_date = this.findViewById(R.id.StartDateTask);
        final EditText end_date = this.findViewById(R.id.EndDateTask);
        final EditText duration = this.findViewById(R.id.DurationTask);
        final EditText nintervals = this.findViewById(R.id.NumberIntervalsTask);
        final EditText route = this.findViewById(R.id.FullPath);

        name.setText(intent.getStringExtra(InfoTask.NAME));
        description.setText(intent.getStringExtra(InfoTask.DESCRIPTION));
        project_root.setText(intent.getStringExtra(InfoTask.PROJECT_ROOT));
        start_date.setText(intent.getStringExtra(InfoTask.START_DATE));
        end_date.setText(intent.getStringExtra(InfoTask.END_DATE));
        duration.setText(intent.getStringExtra(InfoTask.DURATION));
        nintervals.setText(intent.getStringExtra(InfoTask.NINTERVALS));
        route.setText(intent.getStringExtra(InfoTask.ROUTE));
    }


    /*Fem com al editTask però sense dona la opció al client de modificar res*/
    @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }

}