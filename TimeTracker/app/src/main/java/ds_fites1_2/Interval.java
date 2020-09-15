package ds_fites1_2;

import android.util.Log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// This class implements the observer pattern (observer)
public class Interval implements PropertyChangeListener, Serializable {

    private int iID;
    private Date iStartDate;
    private Date iEndDate;
    private Task iTask;
    private int iDuration;
    private boolean isRunning;
    static final String PATTERN = "d/MM/yyyy, HH:mm:ss";
    static final Locale LOCATION = new Locale("es", "ES");

    private static final int HOUR2SEG = 3600;
    private static final int MIN2SEG = 60;
    private static final int DECIMAL = 10;

    // Method that transforms a time given in seconds into the hh:mm:ss format
    public static final String formatSeconds(int timeInSeconds) {
        final int hours = timeInSeconds / Interval.HOUR2SEG;
        final int secondsLeft = timeInSeconds - hours * Interval.HOUR2SEG;
        final int minutes = secondsLeft / Interval.MIN2SEG;
        final int seconds = secondsLeft - minutes * Interval.MIN2SEG;

        String formattedTime = "";
        if (hours < Interval.DECIMAL) {
            formattedTime += "0";
        }
        formattedTime += hours + ":";

        if (minutes < Interval.DECIMAL) {
            formattedTime += "0";
        }
        formattedTime += minutes + ":";

        if (seconds < Interval.DECIMAL) {
            formattedTime += "0";
        }
        formattedTime += seconds;

        return formattedTime;
    }

    public Interval() {
    }

    public Interval(Task t) {
        iID = IDsGenerator.Instance(0).getId();
        iStartDate = new Date();
        iEndDate = null;
        iTask = t;
        iDuration = 0;
        isRunning =false;
    }

    public final int getiID() { return iID;}

    public final Date getIStartDate() {
        return iStartDate;
    }

    public final Date getIEndDate() {
        return iEndDate;
    }

    public final Task getITask() {
        return iTask;
    }

    public final int getIDuration() {
        return iDuration;
    }

    public final boolean isRunning(){return isRunning;}

    public final void setiIsRunning(final boolean b){
        isRunning =b;}

    // Method of the observer pattern
    public final void propertyChange(PropertyChangeEvent evt) {

        assert evt.getPropertyName().equals("date");
        iEndDate = (Date) evt.getNewValue();
        iDuration += Clock.instance(0).getDelaySeg();
        iTask.propagateChanges(iStartDate, iEndDate);
        Log.i("interval", "propertyChange: done!!");
    }

    // Method that checks if the period passed
    // by parameter coincides with that of interval
    public final boolean comparePeriod(
            Date startReport, Date endReport) {

        if (iStartDate == null && iEndDate == null) {
            return false;
        } else {
            return ((iEndDate.compareTo(startReport) > 0)
                    && (iStartDate.compareTo(endReport) < 0));
        }
    }

    public final String getITaskName() {
        return iTask.getGName();
    }

    public final String getIProjectName() {
        return iTask.getGFatherName();
    }

    public final int getPositioninTaskList() {
        return iTask.lookForInterval(this);
    }

    public final String getIStartDateString() {
        if(iStartDate ==null){
            return "---";
        }
        else {
            return new SimpleDateFormat(Interval.PATTERN, Interval.LOCATION).format(this.iStartDate);
        }
    }

    public final String getIEndDateString() {
        if(iEndDate ==null){
            return "---";
        }
        else {
            return new SimpleDateFormat(Interval.PATTERN, Interval.LOCATION).format(this.iEndDate);
        }
    }

    public final String getIDurationString(){
        return Interval.formatSeconds(iDuration);
    }
}
