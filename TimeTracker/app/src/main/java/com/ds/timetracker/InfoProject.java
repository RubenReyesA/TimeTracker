package com.ds.timetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

/*Aquesta activity implementa la funcionalitat de mostra
la informació de un projecte*/

public class InfoProject extends AppCompatActivity {

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String PROJECT_ROOT = "project_root";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String DURATION = "duration";
    public static final String NTASKS = "ntasks";
    public static final String NSUBPROJECTS = "nsubprojects";
    public static final String ROUTE = "route";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_info_project);


        final Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setTitle(R.string.InfoProject);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Intent intent = this.getIntent();

        /*Aqui agafem el nom, descripció, projec_root, data d'inici, data final,
         durada actual, nombre de tascas i nombre de subprojectes*/
        final EditText name = this.findViewById(R.id.NameProject);
        final EditText description = this.findViewById(R.id.DescriptionProject);
        final EditText project_root = this.findViewById(R.id.ProjectProjectRoot);
        final EditText start_date = this.findViewById(R.id.StartDateProject);
        final EditText end_date = this.findViewById(R.id.EndDateProject);
        final EditText duration = this.findViewById(R.id.DurationProject);
        final EditText ntasks = this.findViewById(R.id.NumberTasks);
        final EditText nsubprojects = this.findViewById(R.id.NumberSubProjects);
        final EditText route = this.findViewById(R.id.FullPath);

        name.setText(intent.getStringExtra(InfoProject.NAME));
        description.setText(intent.getStringExtra(InfoProject.DESCRIPTION));
        project_root.setText(intent.getStringExtra(InfoProject.PROJECT_ROOT));
        start_date.setText(intent.getStringExtra(InfoProject.START_DATE));
        end_date.setText(intent.getStringExtra(InfoProject.END_DATE));
        duration.setText(intent.getStringExtra(InfoProject.DURATION));
        ntasks.setText(intent.getStringExtra(InfoProject.NTASKS));
        nsubprojects.setText(intent.getStringExtra(InfoProject.NSUBPROJECTS));
        route.setText(intent.getStringExtra(InfoProject.ROUTE));
    }

    /*Fem com al editProject però sense dona la opció al client de modificar res*/
    @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }

}