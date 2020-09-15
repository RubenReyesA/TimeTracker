package com.ds.timetracker;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class GenerateReport extends AppCompatActivity {

    private int mYear, mMonth, mDay, mHour, mMinute;
    private final String mSeg = "00";
    private EditText StartReport;
    private EditText EndReport;
    private EditText StartReportTime;
    private EditText EndReportTime;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ds_activity_generate_report);

        this.StartReport = this.findViewById(R.id.StartReport);
        this.StartReportTime = this.findViewById(R.id.StartReportTime);
        this.EndReport = this.findViewById(R.id.EndReport);
        this.EndReportTime = this.findViewById(R.id.EndReportTime);


        Toolbar toolbar = this.findViewById(R.id.appbar);
        this.setSupportActionBar(toolbar);

        Objects.requireNonNull(this.getSupportActionBar()).setTitle(R.string.GenerateReports);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        final EditText ProjectReport = this.findViewById(R.id.ProjectReport);
        ProjectReport.setText(this.getIntent().getStringExtra("Project"));

        Spinner DateReport = this.findViewById(R.id.DateReport);

        DateReport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                final String option = DateReport.getSelectedItem().toString();
                final List<String> options_array = Arrays.asList(GenerateReport.this.getResources().getStringArray(R.array.dateReports));

                if(option.equals(options_array.get(options_array.size()-1))){
                    GenerateReport.this.StartReport.setEnabled(true);
                    GenerateReport.this.StartReportTime.setEnabled(true);
                    GenerateReport.this.EndReport.setEnabled(true);
                    GenerateReport.this.EndReportTime.setEnabled(true);
                }
                else{
                    GenerateReport.this.StartReport.setEnabled(false);
                    GenerateReport.this.StartReportTime.setEnabled(false);
                    GenerateReport.this.EndReport.setEnabled(false);
                    GenerateReport.this.EndReportTime.setEnabled(false);
                    GenerateReport.this.setDatesReports(options_array.indexOf(option));
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        });

        this.StartReport.setOnClickListener(v -> {
            // Get Current Date
            Calendar c = Calendar.getInstance();
            GenerateReport.this.mYear = c.get(Calendar.YEAR);
            GenerateReport.this.mMonth = c.get(Calendar.MONTH);
            GenerateReport.this.mDay = c.get(Calendar.DAY_OF_MONTH);


            final DatePickerDialog datePickerDialog = new DatePickerDialog(GenerateReport.this,
                    (view, year, monthOfYear, dayOfMonth) -> {

                        final String toSet = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        GenerateReport.this.StartReport.setText(toSet);

                    }, GenerateReport.this.mYear, GenerateReport.this.mMonth, GenerateReport.this.mDay);

            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();

        });


        this.EndReport.setOnClickListener(v -> {
            // Get Current Date
            Calendar c = Calendar.getInstance();
            GenerateReport.this.mYear = c.get(Calendar.YEAR);
            GenerateReport.this.mMonth = c.get(Calendar.MONTH);
            GenerateReport.this.mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(GenerateReport.this,
                    (view, year, monthOfYear, dayOfMonth) -> {

                        final String toSet = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        GenerateReport.this.EndReport.setText(toSet);

                    }, GenerateReport.this.mYear, GenerateReport.this.mMonth, GenerateReport.this.mDay);
            try {
                datePickerDialog.getDatePicker().setMinDate((new SimpleDateFormat("dd/MM/yyyy")).parse(GenerateReport.this.StartReport.getText().toString()).getTime());
            } catch (final ParseException e) {
                e.printStackTrace();
            }
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();

            try {
                final Date date = new SimpleDateFormat("dd/MM/yyyy").parse(GenerateReport.this.EndReport.getText().toString());
            }catch (final Exception e){
                Log.d("ERROR:", Objects.requireNonNull(e.getMessage()));
            }

        });


        this.StartReportTime.setOnClickListener(v -> {
            // Get Current Time
            Calendar c = Calendar.getInstance();
            GenerateReport.this.mHour = c.get(Calendar.HOUR_OF_DAY);
            GenerateReport.this.mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            final TimePickerDialog timePickerDialog = new TimePickerDialog(GenerateReport.this,
                    (view, hourOfDay, minute) -> {

                        final String toSet = hourOfDay + ":" + minute + ":" + GenerateReport.this.mSeg;
                        GenerateReport.this.StartReportTime.setText(toSet);

                    }, GenerateReport.this.mHour, GenerateReport.this.mMinute, true);
            timePickerDialog.show();
        });


        this.EndReportTime.setOnClickListener(v -> {
            // Get Current Time
            Calendar c = Calendar.getInstance();
            GenerateReport.this.mHour = c.get(Calendar.HOUR_OF_DAY);
            GenerateReport.this.mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            final TimePickerDialog timePickerDialog = new TimePickerDialog(GenerateReport.this,
                    (view, hourOfDay, minute) -> {

                        final String toSet = hourOfDay + ":" + minute + ":" + GenerateReport.this.mSeg;
                        GenerateReport.this.EndReportTime.setText(toSet);

                    }, GenerateReport.this.mHour, GenerateReport.this.mMinute, true);
            timePickerDialog.show();
        });


        final ExtendedFloatingActionButton fab_create = this.findViewById(R.id.createReportFAB);
        fab_create.setOnClickListener(v -> {
            final EditText name = GenerateReport.this.findViewById(R.id.NameReport);
            if(!name.getText().toString().equals("")) {
                generate_report();
            }
            else{
                Toasty.warning(GenerateReport.this,R.string.ErrorEmptyName,Toasty.LENGTH_LONG).show();
            }
        });

    }

    public Date addORsubDaystoDate(final Date date, final int days){
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR,days);

        return calendar.getTime();
    }

    public void setDatesReports(final int index){
        this.StartReportTime.setText(R.string.TimeStart);
        this.EndReportTime.setText(R.string.TimeFinish);

        final Date today = new Date();

        final SimpleDateFormat f_date = new SimpleDateFormat("dd/MM/yyyy");
        final SimpleDateFormat f_time = new SimpleDateFormat("HH:mm:ss");
        final SimpleDateFormat f_month = new SimpleDateFormat("d");
        final SimpleDateFormat f_year = new SimpleDateFormat("D");
        final SimpleDateFormat f_day = new SimpleDateFormat("EEEE");


        switch (index){
            case 0:
                this.StartReport.setText(f_date.format(today));
                this.EndReport.setText(f_date.format(today));
                break;
            case 1:
                final Date Yesterday = this.addORsubDaystoDate(today,-1);
                this.StartReport.setText(f_date.format(Yesterday));
                this.EndReport.setText(f_date.format(Yesterday));
                break;

            case 2:
                Date monday;
                Date sunday = today;

                int i=-1;
                while(!f_day.format(sunday).equals("Sunday")){
                    sunday= this.addORsubDaystoDate(today,i);
                    i=i-1;
                }

                monday=sunday;

                while(!f_day.format(monday).equals("Monday")){
                    monday= this.addORsubDaystoDate(today,i);
                    i=i-1;
                }

                this.StartReport.setText(f_date.format(monday));
                this.EndReport.setText(f_date.format(sunday));
                break;

            case 3:
                final Date oneWeek = this.addORsubDaystoDate(today,-6);
                this.StartReport.setText(f_date.format(oneWeek));
                this.EndReport.setText(f_date.format(today));
                this.EndReportTime.setText(f_time.format(today));
                break;

            case 4:
                Date first_day_month = this.addORsubDaystoDate(today,-Integer.parseInt(f_month.format(today)));
                final Date last_day_month = this.addORsubDaystoDate(today,-Integer.parseInt(f_month.format(today)));

                while(!f_month.format(first_day_month).equals("1")){
                    first_day_month= this.addORsubDaystoDate(first_day_month,-1);
                }
                this.StartReport.setText(f_date.format(first_day_month));
                this.EndReport.setText(f_date.format(last_day_month));

                break;

            case 5:
                final Date first_day_this_month = this.addORsubDaystoDate(today,-Integer.parseInt(f_month.format(today))+1);

                this.StartReport.setText(f_date.format(first_day_this_month));
                this.EndReport.setText(f_date.format(today));
                this.EndReportTime.setText(f_time.format(today));

                break;

            case 6:
                Date first_day_year = this.addORsubDaystoDate(today,-Integer.parseInt(f_year.format(today)));
                final Date last_day_year = this.addORsubDaystoDate(today,-Integer.parseInt(f_year.format(today)));

                while(!f_month.format(first_day_year).equals("1")){
                    first_day_year= this.addORsubDaystoDate(first_day_year,-1);
                }
                this.StartReport.setText(f_date.format(first_day_year));
                this.EndReport.setText(f_date.format(last_day_year));

                break;

            case 7:
                final Date first_day_this_year = this.addORsubDaystoDate(today,-Integer.parseInt(f_year.format(today))+1);

                this.StartReport.setText(f_date.format(first_day_this_year));
                this.EndReport.setText(f_date.format(today));
                this.EndReportTime.setText(f_time.format(today));
                break;

            default:
                break;

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }


    public static final String GENERATE_REPORT = "generate_report";
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    // Check whether the external storage is mounted or not.
    public static boolean isExternalStorageMounted() {

        final String dirState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(dirState);
    }

    // Get public external storage base directory.
    public static String getPublicExternalStorageBaseDir(final String dirType)
    {
        String ret = "";
        if(GenerateReport.isExternalStorageMounted()) {
            final File file = Environment.getExternalStoragePublicDirectory(dirType);
            ret = file.getAbsolutePath();
        }
        return ret;
    }

    // This method is invoked after user click buttons in permission grant popup dialog.
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == GenerateReport.REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION)
        {
            final int grantResultsLength = grantResults.length;
            if(grantResultsLength > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toasty.info(this.getApplicationContext(), R.string.GrantedPermission, Toast.LENGTH_LONG).show();
            }else
            {
                Toasty.warning(this.getApplicationContext(), R.string.DeniedPermission, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void generate_report(){

        final EditText editTextName = this.findViewById(R.id.NameReport);
        final EditText editTextStartDate = this.findViewById(R.id.StartReport);
        final EditText editTextStartDateTime = this.findViewById(R.id.StartReportTime);
        final EditText editTextEndDate = this.findViewById(R.id.EndReport);
        final EditText editTextEndDateTime = this.findViewById(R.id.EndReportTime);

        final Spinner spinnerFormat = this.findViewById(R.id.FormatReport);
        final Spinner spinnerType = this.findViewById(R.id.TypeReport);


        final String date_start_compose = editTextStartDate.getText() + " " + editTextStartDateTime.getText();
        final String date_end_compose = editTextEndDate.getText() + " " + editTextEndDateTime.getText();

        final Intent intent = new Intent (GenerateReport.GENERATE_REPORT);
        intent.putExtra("name",editTextName.getText().toString());
        intent.putExtra("date1",date_start_compose);
        intent.putExtra("date2", date_end_compose);
        intent.putExtra("format", spinnerFormat.getSelectedItem().toString());
        intent.putExtra("type", spinnerType.getSelectedItem().toString());

        if (GenerateReport.isExternalStorageMounted()) {

            // Check whether this app has write external storage permission or not.
            final int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            // If do not grant write external storage permission.
            if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                // Request user to grant write external storage permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GenerateReport.REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            } else {
                // Create folders /storage/emulated/0/Documents/TimeTracker
                final String publicDcimDirPath = GenerateReport.getPublicExternalStorageBaseDir(Environment.DIRECTORY_DOCUMENTS) + "/TimeTracker";

                final File dir = new File(publicDcimDirPath);
                dir.mkdirs();

                intent.putExtra("publicDcimDirPath",publicDcimDirPath);

                this.sendBroadcast(intent);
                this.finish();
            }
        }
    }

}
