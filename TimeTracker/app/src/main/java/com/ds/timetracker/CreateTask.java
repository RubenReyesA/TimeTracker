package com.ds.timetracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/**
 * Aquesta classe implementa la interfície de l'activity "CreateTask".
 */

public class CreateTask extends AppCompatActivity {

    /**
     * String usat per enviar l'intent de crear projecte al servei {@link TreeManagerService}
     * Ints usats per generar la data.
     */

    public static final String CREATE_TASK = "create_task";
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_create_task);

        /**
         * Estableix la toolbar i activa la fletxa de "back".
         */
        final Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setTitle(R.string.CreateTask);
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

        final ExtendedFloatingActionButton create_fab = this.findViewById(R.id.createTaskFAB);

        create_fab.setOnClickListener(v -> {
            /**
             * Definim la funció de "click" del botó "Create". Aquesta funció comprova
             * que el nom de la tasca no es buit i envia l'intent.
             */

            final EditText name = CreateTask.this.findViewById(R.id.NameTask);
            final EditText description = CreateTask.this.findViewById(R.id.DescriptionTask);

            if(!name.getText().toString().equals("")) {
                final Intent intent_create = new Intent(CreateTask.CREATE_TASK);
                intent_create.putExtra("name", name.getText().toString());
                intent_create.putExtra("description", description.getText().toString());

                CreateTask.this.sendBroadcast(intent_create);
                CreateTask.this.onBackPressed();
            }
            else{
                Toasty.warning(CreateTask.this,R.string.ErrorEmptyName,Toasty.LENGTH_LONG).show();

            }

        });

        final Switch sPreprogramada = this.findViewById(R.id.SwitchPreprogramada);

        sPreprogramada.setOnCheckedChangeListener((buttonView, isChecked) -> {
            /**
             * Definim la funció de "click" de la switch quan es diu que es preprogramada.
             * Aquesta funció mostra els nous elements.
             */
            final TextView tw = CreateTask.this.findViewById(R.id.FechaPreprogramada);
            final EditText et = CreateTask.this.findViewById(R.id.DatePreprogramada);

            if(isChecked){
                tw.setVisibility(View.VISIBLE);
                et.setVisibility(View.VISIBLE);
                et.setEnabled(true);
            }
            else{
                tw.setVisibility(View.GONE);
                et.setVisibility(View.GONE);
                et.setEnabled(false);
            }
        });


        final EditText et = this.findViewById(R.id.DatePreprogramada);

        et.setOnClickListener(v -> {
            /**
             * Definim la funció de "click" de la data quan es diu que es preprogramada.
             * Aquesta funció mostra el datePicker i escriu al EditText la data.
             */
            // Get Current Date
            Calendar c = Calendar.getInstance();
            CreateTask.this.mYear = c.get(Calendar.YEAR);
            CreateTask.this.mMonth = c.get(Calendar.MONTH);
            CreateTask.this.mDay = c.get(Calendar.DAY_OF_MONTH);


            final DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTask.this,
                    (view, year, monthOfYear, dayOfMonth) -> {

                        final String toSet = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        et.setText(toSet);

                    }, CreateTask.this.mYear, CreateTask.this.mMonth, CreateTask.this.mDay);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();

        });

        final Switch sLimitada = this.findViewById(R.id.SwitchLimitada);

        sLimitada.setOnCheckedChangeListener((buttonView, isChecked) -> {
            /**
             * Definim la funció de "click" de la data quan es diu que es limitada.
             * Aquesta funció mostra els nous elements.
             */
            final TextView tw = CreateTask.this.findViewById(R.id.TextLimitada);
            final EditText et1 = CreateTask.this.findViewById(R.id.TimeLimitada);

            if(isChecked){
                tw.setVisibility(View.VISIBLE);
                et1.setVisibility(View.VISIBLE);
                et1.setEnabled(true);
            }
            else{
                tw.setVisibility(View.GONE);
                et1.setVisibility(View.GONE);
                et1.setEnabled(false);
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
