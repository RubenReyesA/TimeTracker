package com.ds.timetracker;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.Objects;

public class Settings extends AppCompatActivity {

    ConfigApp actual = new ConfigApp();

    private String code = "";
    private int delay = -1;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_settings);


        final Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setTitle(R.string.Settings);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Spinner spinner = this.findViewById(R.id.LanguagesSpinner);
        final SeekBar seekBar = this.findViewById(R.id.seekBarDelaySeg);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
                final TextView textView = Settings.this.findViewById(R.id.TextDelaySeg);
                final String to_show = (progress+1)+" "+ Settings.this.getString(R.string.seg);
                textView.setText(to_show);
                Settings.this.actual.setDelaySeg(progress+1);
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {

            }
        });


        final Button deleteALL = this.findViewById(R.id.DeleteALLbtn);

        deleteALL.setOnClickListener(v -> {
            final MaterialDialog mDialog = new MaterialDialog.Builder(Settings.this)
                    .setTitle(Settings.this.getString(R.string.DeleteALLTitle))
                    .setMessage(Settings.this.getString(R.string.DeleteALLmsg))
                    .setCancelable(false)
                    .setPositiveButton(Settings.this.getString(R.string.DeleteALLYES), R.drawable.ic_delete,
                            (dialogInterface, which) -> {
                        final Intent intent = new Intent (Settings.DELETE_ALL);
                        Settings.this.sendBroadcast(intent);
                        dialogInterface.dismiss();

                        final Intent i = new Intent (ProjectsList.STOP_SERVICE);
                        i.putExtra("save", false);
                        Settings.this.sendBroadcast(i);
                    })
                    .setNegativeButton(Settings.this.getString(R.string.DeleteALLNO), R.drawable.ic_close,
                            (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();

            // Show Dialog
            mDialog.show();
        });



    }

    public static final String GIVE_CONFIG = "give_config";
    public static final String SEND_NEW_CONFIG = "send_new_config";
    public static final String DELETE_ALL = "delete_all";

    @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }

    public class Receptor extends BroadcastReceiver {

        @Override
        public final void onReceive(Context context,
                                    Intent intent) {


            if (Objects.equals(intent.getAction(), TreeManagerService.SEND_CONFIG)) {

                Settings.this.actual = (ConfigApp) intent.getSerializableExtra("config");

                assert Settings.this.actual != null;
                Settings.this.delay = Settings.this.actual.getDelaySeg();

                final SeekBar seekBar = Settings.this.findViewById(R.id.seekBarDelaySeg);
                seekBar.setProgress(Settings.this.actual.getDelaySeg()-1);

            }
        }
    }

    private Settings.Receptor receptor;

    @Override
    public final void onResume() {

        final IntentFilter filter;
        filter = new IntentFilter();
        filter.addAction(TreeManagerService.SEND_CONFIG);
        this.receptor = new Settings.Receptor();
        this.registerReceiver(this.receptor, filter);

        this.sendBroadcast(new Intent (Settings.GIVE_CONFIG));

        super.onResume();
    }

    @Override
    public final void onPause() {

        this.unregisterReceiver(this.receptor);

        super.onPause();
    }

    @Override
    public final void onBackPressed(){

        if(this.actual.getDelaySeg() != this.delay) {


            final Intent intent = new Intent(Settings.SEND_NEW_CONFIG);

            final Spinner spinner = this.findViewById(R.id.LanguagesSpinner);
            final SeekBar seekBar = this.findViewById(R.id.seekBarDelaySeg);

            intent.putExtra("delay", seekBar.getProgress() + 1);

            this.sendBroadcast(intent);
        }

        super.onBackPressed();
    }

}