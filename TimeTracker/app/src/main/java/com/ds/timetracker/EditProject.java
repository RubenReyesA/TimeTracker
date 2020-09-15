package com.ds.timetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/*Aquesta activity implementa la funcionalitat de editar projectes*/

public class EditProject extends AppCompatActivity {

    public static final String EDIT = "edit_project";

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String PROJECT_ROOT = "project_root";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String DURATION = "duration";
    public static final String NTASKS = "ntasks";
    public static final String NSUBPROJECTS = "nsubprojects";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_edit_project);

        final Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setTitle(R.string.EditProject);
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

        /*Sol podem canviar el nom i la descripció*/
        name.setHint(intent.getStringExtra(EditProject.NAME));
        description.setHint(intent.getStringExtra(EditProject.DESCRIPTION));
        project_root.setText(intent.getStringExtra(EditProject.PROJECT_ROOT));
        start_date.setText(intent.getStringExtra(EditProject.START_DATE));
        end_date.setText(intent.getStringExtra(EditProject.END_DATE));
        duration.setText(intent.getStringExtra(EditProject.DURATION));
        ntasks.setText(intent.getStringExtra(EditProject.NTASKS));
        nsubprojects.setText(intent.getStringExtra(EditProject.NSUBPROJECTS));


        final ExtendedFloatingActionButton fab = this.findViewById(R.id.editProjectFAB);

        fab.setOnClickListener(v -> {
            final EditText name1 = EditProject.this.findViewById(R.id.NameProject);
            final EditText description1 = EditProject.this.findViewById(R.id.DescriptionProject);

            /*Aqui guardem els canvis quan el usuari pulsa OK */
            if(!name1.getText().toString().equals("")) {
                final Intent edit = new Intent(EditProject.EDIT);
                edit.putExtra("name", name1.getText().toString());
                edit.putExtra("description", description1.getText().toString());
                edit.putExtra("id", EditProject.this.getIntent().getIntExtra("id", -1));

                sendBroadcast(edit);
                onBackPressed();
            }
            /*Si el usuari es deixa el camp nom buit, l'avisem*/
            else{
                Toasty.warning(EditProject.this,R.string.ErrorEmptyName,Toasty.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }
}
