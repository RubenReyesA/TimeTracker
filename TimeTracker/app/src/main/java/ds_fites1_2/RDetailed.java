package ds_fites1_2;

import java.util.ArrayList;
import java.util.List;

public class RDetailed extends Report {

    public RDetailed(Project rProject, Period rPeriod, String v) {
        super(rProject, rPeriod,v);
    }

    public final void writeReport() {
        final ESeparator rSeparator = new ESeparator();
        this.addtoListofElements(rSeparator);

        final ETitle rTitle = new ETitle("Informe detallat");
        this.addtoListofElements(rTitle);

        this.addtoListofElements(rSeparator);

        final ESubTitle rSubTitle1 = new ESubTitle("Periode");
        this.addtoListofElements(rSubTitle1);

        final ETableInfo rPeriodTable = new ETableInfo(4, 2);
        rPeriodTable.setContent(0, 0, "");
        rPeriodTable.setContent(0, 1, "Data");
        rPeriodTable.setContent(1, 0, "Desde");
        rPeriodTable.setContent(2, 0, "Fins a");
        rPeriodTable.setContent(3, 0, "Data Actual");
        rPeriodTable.setContent(1, 1, this.getRPeriodStartDateString());
        rPeriodTable.setContent(2, 1, this.getRPeriodEndDateString());
        rPeriodTable.setContent(3, 1, this.getRPeriodCurrentDateString());
        this.addtoListofElements(rPeriodTable);

        this.addtoListofElements(rSeparator);


        final List<Project> nProjects = this.getPeriodProjects(this.getRPeriod(), false);
        final int numberofProjects = 1
                + nProjects.size(); // 1 because headers and number of projects.

        final ESubTitle rSubTitle2 = new ESubTitle("Projectes de primer nivell");
        this.addtoListofElements(rSubTitle2);

        final ETableInfo rFirstLevelProjectsTable = new ETableInfo(
                numberofProjects, 4);
        rFirstLevelProjectsTable.setContent(0, 0, "");
        rFirstLevelProjectsTable.setContent(0, 1, "Data inici");
        rFirstLevelProjectsTable.setContent(0, 2, "Data final");
        rFirstLevelProjectsTable.setContent(0, 3, "Temps total");

        int i = 1;
        for (final Project p : nProjects) {
            rFirstLevelProjectsTable.setContent(i, 0, p.getGName());
            rFirstLevelProjectsTable.setContent(i, 1, p.getGStartDateString());
            rFirstLevelProjectsTable.setContent(i, 2, p.getGEndDateString());
            rFirstLevelProjectsTable.setContent(i, 3, Report.formatSeconds(
                    p.getGDuration()));

            i++;
        }

        this.addtoListofElements(rFirstLevelProjectsTable);

        this.addtoListofElements(rSeparator);

        final ESubTitle rSubTitle3 = new ESubTitle("Subprojectes");
        this.addtoListofElements(rSubTitle3);

        final EText rTextSubprojects = new EText("S'inclouen en la següent taula "
                + "només els subprojectes que tinguin alguna tasca amb "
                + "algún interval dins del periode.");

        this.addtoListofElements(rTextSubprojects);

        final List<Project> nSubProjects = new ArrayList<Project>();
        for (final Project p1 : nProjects) {
            nSubProjects.addAll(p1.getSubProjects(this.getRPeriod(), true));

        }

        final int numberofSubProjects = 1 + nSubProjects.size();

        final ETableInfo rSubProjectsTable = new ETableInfo(numberofSubProjects, 5);

        rSubProjectsTable.setContent(0, 0, "");
        rSubProjectsTable.setContent(0, 1, "Dins de");
        rSubProjectsTable.setContent(0, 2, "Data inici");
        rSubProjectsTable.setContent(0, 3, "Data final");
        rSubProjectsTable.setContent(0, 4, "Temps total");

        i = 1;

        for (final Project p : nSubProjects) {
            rSubProjectsTable.setContent(i, 0, p.getGName());
            rSubProjectsTable.setContent(i, 1, p.getGFatherName());
            rSubProjectsTable.setContent(i, 2, p.getGStartDateString());
            rSubProjectsTable.setContent(i, 3, p.getGEndDateString());
            rSubProjectsTable.setContent(i, 4, Report.formatSeconds(p.getGDuration()));

            i++;
        }

        this.addtoListofElements(rSubProjectsTable);

        this.addtoListofElements(rSeparator);

        final ESubTitle rSubTitle4 = new ESubTitle("Tasques");
        this.addtoListofElements(rSubTitle4);

        final EText rTextTask = new EText("S'inclouen en la següent taula la "
                + "durada de totes tasques i el projecte al qual pertanyen");

        this.addtoListofElements(rTextTask);

        final List<Project> nAllProjects = new ArrayList<Project>();
        nAllProjects.add(this.getRProject());
        nAllProjects.addAll(nProjects);
        nAllProjects.addAll(nSubProjects);

        final List<Task> nTasks = new ArrayList<Task>();

        for (final Project p1 : nAllProjects) {
            nTasks.addAll(p1.getTasks(this.getRPeriod()));
        }

        final int numberofTasks = 1
                + nTasks.size(); // 1 because headers and number of tasks.
        final ETableInfo rTasksTable = new ETableInfo(numberofTasks, 5);

        rTasksTable.setContent(0, 0, "");
        rTasksTable.setContent(0, 1, "Projecte");
        rTasksTable.setContent(0, 2, "Data inici");
        rTasksTable.setContent(0, 3, "Data final");
        rTasksTable.setContent(0, 4, "Temps total");

        i = 1;

        for (final Task t : nTasks) {
            rTasksTable.setContent(i, 0, t.getGName());
            rTasksTable.setContent(i, 1, t.getGFatherName());
            rTasksTable.setContent(i, 2, t.getGStartDateString());
            rTasksTable.setContent(i, 3, t.getGEndDateString());
            rTasksTable.setContent(i, 4, Report.formatSeconds(t.getGDuration()));

            i++;
        }

        this.addtoListofElements(rTasksTable);

        this.addtoListofElements(rSeparator);


        final ESubTitle rSubTitle5 = new ESubTitle("Intervals");
        this.addtoListofElements(rSubTitle5);

        final EText rTextIntervals = new EText("S'inclouen en la següent "
                + "taula el temps d'inici, final i durada de tots els intervals"
                + "entre la data inicial i final especificades, i la tasca i "
                + "projecte al qual pertanyen.");

        this.addtoListofElements(rTextIntervals);


        final List<Interval> nIntervals = new ArrayList<Interval>();

        for (final Task t1 : nTasks) {
            nIntervals.addAll(t1.getTIntervals(this.getRPeriod()));
        }

        final int numberofIntervals = 1 + nIntervals.size();
        final ETableInfo rIntervalsTable = new ETableInfo(numberofIntervals, 6);

        rIntervalsTable.setContent(0, 0, "Tasca");
        rIntervalsTable.setContent(0, 1, "Projecte");
        rIntervalsTable.setContent(0, 2, "Número Interval");
        rIntervalsTable.setContent(0, 3, "Data inici");
        rIntervalsTable.setContent(0, 4, "Data final");
        rIntervalsTable.setContent(0, 5, "Durada");


        i = 1;

        for (final Interval itv : nIntervals) {
            rIntervalsTable.setContent(i, 0, itv.getITaskName());
            rIntervalsTable.setContent(i, 1, itv.getIProjectName());
            rIntervalsTable.setContent(i, 2, Integer.toString(
                    itv.getPositioninTaskList()));
            rIntervalsTable.setContent(i, 3, itv.getIStartDateString());
            rIntervalsTable.setContent(i, 4, itv.getIEndDateString());
            rIntervalsTable.setContent(i, 5, Report.formatSeconds(itv.getIDuration()));


            i++;
        }

        this.addtoListofElements(rIntervalsTable);

        this.addtoListofElements(rSeparator);


        final EText rFooter = new EText(this.getVersion());

        this.addtoListofElements(rFooter);

        final EText rProject = new EText("Project Name: "+this.getRProjectString());

        this.addtoListofElements(rProject);


    }

}
