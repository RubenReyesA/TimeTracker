package com.ds.timetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/**
 * Aquesta classe implementa la interfície de l'activity "CreateProject".
 */

public class CreateProject extends AppCompatActivity {

    /**
     * String usat per enviar l'intent de crear projecte al servei {@link TreeManagerService}
     */

    public static final String CREATE_PROJECT = "create_project";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_create_project);

        /**
         * Estableix la toolbar i activa la fletxa de "back".
         */

        final Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setTitle(R.string.CreateProject);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        /**
         * Crea l'intent a enviar
         */
        final Intent intent = this.getIntent();

        /**
         * Assignem el titol del projecte pare a l'interficie (edit_text)
         */
        final EditText project_root = this.findViewById(R.id.ProjectTaskRoot);
        project_root.setText(intent.getStringExtra("project_root"));

        final ExtendedFloatingActionButton create_fab = this.findViewById(R.id.createProjectFAB);

        create_fab.setOnClickListener(v -> {

            /**
             * Definim la funció de "click" del botó "Create". Aquesta funció comprova
             * que el nom del projecte no es buit i envia l'intent.
             */

            final EditText name = CreateProject.this.findViewById(R.id.NameTask);
            final EditText description = CreateProject.this.findViewById(R.id.DescriptionTask);

            if(!name.getText().toString().equals("")) {
                final Intent intent_create = new Intent(CreateProject.CREATE_PROJECT);
                intent_create.putExtra("name", name.getText().toString());
                intent_create.putExtra("description", description.getText().toString());

                CreateProject.this.sendBroadcast(intent_create);
                CreateProject.this.onBackPressed();
            }
            else{
                Toasty.warning(CreateProject.this,R.string.ErrorEmptyName,Toasty.LENGTH_LONG).show();
            }

        });

    }

    /**
     * Estableix la funcionalitat de la fletxa de "back".
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }


}
