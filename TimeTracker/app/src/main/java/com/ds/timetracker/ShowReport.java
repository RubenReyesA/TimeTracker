package com.ds.timetracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.Objects;

/*Aquesta activity implementa la funcionalitat de mostra el informe*/

public class ShowReport extends AppCompatActivity {

    private String name;
    private String path;
    private String type;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_show_reports);

        final Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);

        /*Agafem el nom del report*/
        this.name = this.getIntent().getStringExtra("name");
        Objects.requireNonNull(this.getSupportActionBar()).setTitle(name);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*Fem la vista web*/
        final WebView webView = this.findViewById(R.id.webview);
        final WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultFontSize(1);
        webSettings.setBuiltInZoomControls(true);


        this.path = this.getIntent().getStringExtra("path");
        this.type = this.getIntent().getStringExtra("type");

        /*Carregem la vista amb la ruta i el tipus d'informe que em adquirit al
        getStingExtra*/
        webView.loadUrl("file://" + this.path);


        final ExtendedFloatingActionButton ExtFAB = this.findViewById(R.id.shareFAB);

        /*Per compartir el informe*/
        ExtFAB.setOnClickListener(v -> {
            ShowReport.this.share();
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }

    /*Aquesta funció implementa com compartirem el informe*/
    private void share(){
        final Intent intent_share = new Intent (Intent.ACTION_SEND);

        intent_share.setType(this.type);
        intent_share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ this.path));
        intent_share.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.ShareReport));
        intent_share.putExtra(Intent.EXTRA_TEXT,getString(R.string.SendReport) + " " + this.name);

        this.startActivity(Intent.createChooser(intent_share,"Share File"));
    }
}