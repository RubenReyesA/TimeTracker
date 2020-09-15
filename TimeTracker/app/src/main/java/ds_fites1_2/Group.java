package ds_fites1_2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

// This class implements the Composite pattern
// Its paper in the pattern is "Component"
public abstract class Group implements Serializable {

    private int gID;
    private String gName;
    private String gDescription;
    private Date gStartDate;
    private Date gEndDate;
    private int gDuration;
    private Group gFather;
    static final String PATTERN = "d/MM/yyyy, HH:mm:ss";
    static final Locale LOCATION = new Locale("es", "ES");
    private static final Logger logger = LoggerFactory.getLogger(Group.class);

    private boolean running;

    public boolean isRunning() {
        return running;
    }

    public void setisRunning(final boolean b){
        running =b;}

    private static final int HOUR2SEG = 3600;
    private static final int MIN2SEG = 60;
    private static final int DECIMAL = 10;

    // Method that transforms a time given in seconds into the hh:mm:ss format
    public static final String formatSeconds(int timeInSeconds) {
        final int hours = timeInSeconds / Group.HOUR2SEG;
        final int secondsLeft = timeInSeconds - hours * Group.HOUR2SEG;
        final int minutes = secondsLeft / Group.MIN2SEG;
        final int seconds = secondsLeft - minutes * Group.MIN2SEG;

        String formattedTime = "";
        if (hours < Group.DECIMAL) {
            formattedTime += "0";
        }
        formattedTime += hours + ":";

        if (minutes < Group.DECIMAL) {
            formattedTime += "0";
        }
        formattedTime += minutes + ":";

        if (seconds < Group.DECIMAL) {
            formattedTime += "0";
        }
        formattedTime += seconds;

        return formattedTime;
    }


    public Group() {

    }

    public Group(String name, String desc) {
        gID = IDsGenerator.Instance(0).getId();
        gName = name;
        gDescription = desc;
        gStartDate = null;
        gEndDate = null;
        gDuration = 0;
        gFather = null;
    }

    public final int getgID() { return gID;}

    public final String getGName() {
        return gName;
    }

    public final String getGFatherName() {
        if (gFather != null) {
            return gFather.getGName();
        } else {
            Group.logger.warn("This node has not father node. Check it!");
            return "";
        }
    }

    public final Group getGFather() {
        return this.gFather;
    }

    public final String getGDescription() {
        return gDescription;
    }

    public final int getGDuration() {
        return gDuration;
    }

    public final String getGDurationString() {
        return Group.formatSeconds(gDuration);
    }

    public final Date getGStartDate() {
        return gStartDate;
    }

    public final String getGStartDateString() {
        if(gStartDate ==null){
            return "---";
        }
        else {
            return new SimpleDateFormat(Group.PATTERN, Group.LOCATION).format(gStartDate);
        }
    }

    public final void setGDuration (final int d){
        gDuration =d;
    }

    public final void setGStartDate(Date d) {
        gStartDate = d;
    }

    public final Date getGEndDate() {
        return gEndDate;
    }

    public final String getGEndDateString() {
        if(gStartDate ==null){
            return "---";
        }
        else {
            return new SimpleDateFormat(Group.PATTERN, Group.LOCATION).format(gEndDate);
        }
    }


    public final void setGEndDate(Date d) {
        gEndDate = d;
    }

    public final void setGFather(Group gF) {
        gFather = gF;
    }

    // Method that propagates its elapsed time to its father Projects
    public final void propagateChanges(
            Date startDate, Date endDate) {

        gDuration += Clock.instance(0).getDelaySeg();
        gEndDate = endDate;

        if (gStartDate == null) {
            gStartDate = startDate;
        }

        if (gFather != null) {
            gFather.propagateChanges(startDate, endDate);
        } else {
            System.out.println("--------------------------------"
                    + "---------------------------------");
            System.out.printf("%-33s", " ");
            System.out.printf("%-26s", "Starting date");
            System.out.printf("%-26s", "End date");
            System.out.printf("%-26s", "Total time");
            System.out.println();
            accept(new Print());
            System.out.println("---------------------------"
                    + "--------------------------------------");
        }
    }

    // Method that propagates its elapsed time to its father Projects
    public final void propagateDurationChanges(int duration) {

        gDuration -= duration;

        if (gFather != null) {
            gFather.propagateDurationChanges(duration);
        } else {
            System.out.println("--------------------------------"
                    + "---------------------------------");
            System.out.printf("%-33s", " ");
            System.out.printf("%-26s", "Starting date");
            System.out.printf("%-26s", "End date");
            System.out.printf("%-26s", "Total time");
            System.out.println();
            accept(new Print());
            System.out.println("---------------------------"
                    + "--------------------------------------");
        }
    }

    public final void propagateStartDate(Date oldDate, Date newDate) {

        if(gStartDate == oldDate){
            gStartDate =newDate;
        }
    }

    public final void propagateEndDate(Date oldDate, Date newDate) {

        if(gEndDate == oldDate){
            gEndDate =newDate;
        }
    }

    public final void propagateRunning (final boolean b){
        running =b;
        if (gFather != null) {
            gFather.propagateRunning(b);
        }
    }


    public abstract void accept(Visitor v);

    // Method that checks if the period passed
    // by parameter coincides with that of group
    public final boolean comparePeriod(
            Date startReport, Date endReport) {

        System.out.println(startReport.toString() + endReport);
        System.out.println(this.gStartDate);
        System.out.println(this.gName);

        if (gStartDate == null && gEndDate == null) {
            Group.logger.info("This node has not StartDate nor EndDate");
            return false;
        } else {
            return ((gEndDate.compareTo(startReport) > 0)
                    && (gStartDate.compareTo(endReport) < 0));
        }
    }


    public static Comparator<Group> AlphaComparator = new Comparator<Group>() {
        @Override
        public int compare(final Group o1, final Group o2) {
            return o1.getGName().compareTo(o2.getGName());
        }
    };

    public static Comparator<Group> RecentComparator = new Comparator<Group>() {
        @Override
        public int compare(final Group o1, final Group o2) {
            return o1.getgID()-o2.getgID();
        }
    };

    public static Comparator<Group> TypeComparator = new Comparator<Group>() {
        @Override
        public int compare(final Group o1, final Group o2) {
            final String o1s;
            final String o2s;
            if(o1 instanceof Project){
                o1s="true";
            }
            else{
                o1s="false";
            }

            if(o2 instanceof Project){
                o2s="true";
            }
            else{
                o2s="false";
            }

            return o1s.compareTo(o2s);
        }
    };


    public final String calculateRoute(){
        final ArrayList<String> path = new ArrayList<>();

        path.add(gName);

        Group actual = this;

        while(actual.gFather!=null){
            path.add(actual.getGFatherName());
            actual = actual.getGFather();
        }

        final StringBuilder stringBuilder = new StringBuilder();

        Collections.reverse(path);
        path.remove(0);

        stringBuilder.append("|-> ");

        int i=0;

        for (final String s : path){
            stringBuilder.append(s);
            if(i!=path.size()-1){
                stringBuilder.append(" -> ");
                i++;
            }
        }

        return stringBuilder.toString();
    }


    public void setgName(final String name){
        gName =name;
    }

    public void setgDescription(final String description){
        gDescription = description;
    }


}
