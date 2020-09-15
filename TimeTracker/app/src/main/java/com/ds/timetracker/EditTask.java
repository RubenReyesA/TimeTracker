package com.ds.timetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Objects;

/*Aquesta activity implementa la funcionalitat de editar tasques*/

public class EditTask extends AppCompatActivity {

    public static final String EDIT = "edit_task";

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String PROJECT_ROOT = "project_root";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String DURATION = "duration";
    public static final String NINTERVALS = "nintervals";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_edit_task);


        final Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setTitle(R.string.EditTask);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Intent intent = this.getIntent();

        /*Aqui agafem el nom, descripció, projec_root, data d'inici, data final,
         durada actual i nombre d'interavals de la tassca*/

        final EditText name = this.findViewById(R.id.NameTask);
        final EditText description = this.findViewById(R.id.DescriptionTask);
        final EditText project_root = this.findViewById(R.id.ProjectTaskRoot);
        final EditText start_date = this.findViewById(R.id.StartDateTask);
        final EditText end_date = this.findViewById(R.id.EndDateTask);
        final EditText duration = this.findViewById(R.id.DurationTask);
        final EditText nintervals = this.findViewById(R.id.NumberIntervalsTask);

        /*Sol podem canviar el nom i la descripció*/
        name.setHint(intent.getStringExtra(EditTask.NAME));
        description.setHint(intent.getStringExtra(EditTask.DESCRIPTION));
        project_root.setText(intent.getStringExtra(EditTask.PROJECT_ROOT));
        start_date.setText(intent.getStringExtra(EditTask.START_DATE));
        end_date.setText(intent.getStringExtra(EditTask.END_DATE));
        duration.setText(intent.getStringExtra(EditTask.DURATION));
        nintervals.setText(intent.getStringExtra(EditTask.NINTERVALS));


        final ExtendedFloatingActionButton fab = this.findViewById(R.id.editTaskFAB);

        fab.setOnClickListener(v -> {
            final EditText name1 = EditTask.this.findViewById(R.id.NameTask);
            final EditText description1 = EditTask.this.findViewById(R.id.DescriptionTask);

            /*Aqui guardem els canvis quan el usuari pulsa OK */
            final Intent edit = new Intent(EditTask.EDIT);
            edit.putExtra("name", name1.getText().toString());
            edit.putExtra("description", description1.getText().toString());
            edit.putExtra("id", EditTask.this.getIntent().getIntExtra("id",-1));

            EditTask.this.sendBroadcast(edit);
            EditTask.this.onBackPressed();
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }
}
