package com.ds.timetracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;


/**
 * Aquesta classe implementa la interfície de l'activity "About".
 */

public class About extends AppCompatActivity {

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_about);

        /**
         * Estableix la toolbar i activa la fletxa de "back".
         */
        final Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(
                this.getSupportActionBar()).setTitle(R.string.About);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Estableix la funcionalitat de la fletxa de "back".
     * @return true
     */
    @Override
    public final boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }
}
