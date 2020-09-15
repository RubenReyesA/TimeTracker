package ds_fites1_2;

import java.util.ArrayList;
import java.util.List;

public abstract class Report {

    private final Project rProject;
    private final Period rPeriod;
    private final List<Elements> rElements;
    static final int HOUR2SEG = 3600;
    static final int MIN2SEG = 60;
    private final String version;

    public Report(Project project, Period period, final String ver) {
        rProject = project;
        rPeriod = period;
        rElements = new ArrayList<Elements>();
        version =ver;
    }


    public final Project getRProject() {
        return rProject;
    }

    public final String getRProjectString() {
        return rProject.getGName();
    }

    public final Period getRPeriod() {
        return rPeriod;
    }

    public final String getVersion() {return version;}

    public final String getRPeriodStartDateString() {
        return rPeriod.getPStartDateString();
    }

    public final String getRPeriodEndDateString() {
        return rPeriod.getPEndDateString();
    }

    public final String getRPeriodCurrentDateString() {
        return rPeriod.getPCurrentDateString();
    }

    public final List<Project> getPeriodProjects(
            Period p, boolean r) {
        return rProject.getSubProjects(p, r);
    }

    public abstract void writeReport();

    public final void buildReport(Format f) {
        for (final Elements e : this.rElements) {
            e.accept(f);
        }

        f.closeWriter();
    }

    public static final String formatSeconds(int timeInSeconds) {
        final int hours = timeInSeconds / Report.HOUR2SEG;
        final int secondsLeft = timeInSeconds - hours * Report.HOUR2SEG;
        final int minutes = secondsLeft / Report.MIN2SEG;
        final int seconds = secondsLeft - minutes * Report.MIN2SEG;

        String formattedTime = "";
        formattedTime += hours + "h ";
        formattedTime += minutes + "m ";
        formattedTime += seconds + "s";

        return formattedTime;
    }

    public final void addtoListofElements(Elements e) {
        rElements.add(e);
    }
}
