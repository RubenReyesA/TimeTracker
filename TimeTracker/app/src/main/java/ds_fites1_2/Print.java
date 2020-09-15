package ds_fites1_2;

import java.text.SimpleDateFormat;
import java.util.Locale;

// Class that implements the Visitor pattern
public class Print implements Visitor {

    static final String PATTERN = "d/MM/yyyy, HH:mm:ss";
    static final Locale LOCATION = new Locale("es", "ES");
    static final int HOUR2SEG = 3600;
    static final int MIN2SEG = 60;
    static final int DECIMAL = 10;

    // Method that transforms a time given in seconds into the hh:mm:ss format
    public static final String formatSeconds(int timeInSeconds) {
        final int hours = timeInSeconds / Print.HOUR2SEG;
        final int secondsLeft = timeInSeconds - hours * Print.HOUR2SEG;
        final int minutes = secondsLeft / Print.MIN2SEG;
        final int seconds = secondsLeft - minutes * Print.MIN2SEG;

        String formattedTime = "";
        if (hours < Print.DECIMAL) {
            formattedTime += "0";
        }
        formattedTime += hours + ":";

        if (minutes < Print.DECIMAL) {
            formattedTime += "0";
        }
        formattedTime += minutes + ":";

        if (seconds < Print.DECIMAL) {
            formattedTime += "0";
        }
        formattedTime += seconds;

        return formattedTime;
    }

    @Override
    public final void visitProject(Project p) {

        if ((p.getGStartDate() == null) || (p.getGEndDate() == null)) {
            System.out.printf("%-85s", p.getGName());
            System.out.printf("%-33s", Print.formatSeconds(p.getGDuration()));
            System.out.println();
        } else {
            System.out.printf("%-33s", p.getGName());
            System.out.printf("%-26s", new SimpleDateFormat(
                    Print.PATTERN, Print.LOCATION).format(p.getGStartDate()));
            System.out.printf("%-26s", new SimpleDateFormat(
                    Print.PATTERN, Print.LOCATION).format(p.getGEndDate()));
            System.out.printf("%-26s", Print.formatSeconds(p.getGDuration()));
            System.out.println();
        }

        // This for() allows us to apply the
        // visitor to all groups in the groupList
        for (final Group g : p.getPGroupList()) {
            g.accept(this);
        }

    }

    @Override
    public final void visitTask(Task t) {

        if ((t.getGStartDate() == null) || (t.getGEndDate() == null)) {
            System.out.printf("%-85s", t.getGName());
            System.out.printf("%-33s", Print.formatSeconds(t.getGDuration()));
            System.out.println();
        } else {
            System.out.printf("%-33s", t.getGName());
            System.out.printf("%-26s", new SimpleDateFormat(
                    Print.PATTERN, Print.LOCATION).format(t.getGStartDate()));
            System.out.printf("%-26s", new SimpleDateFormat(
                    Print.PATTERN, Print.LOCATION).format(t.getGEndDate()));
            System.out.printf("%-26s", Print.formatSeconds(t.getGDuration()));
            System.out.println();
        }
    }
}
