package ds_fites1_2;

import java.util.List;

public class RBasic extends Report {

    public RBasic(Project rProject, Period rPeriod, String v) {
        super(rProject, rPeriod,v);
    }

    public final void writeReport() {
        final ESeparator rSeparator = new ESeparator();
        this.addtoListofElements(rSeparator);

        final ETitle rTitle = new ETitle("Informe breu");
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
                + nProjects.size(); // 1 per les capçaleres + nº de projectes.

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

        final EText rFooter = new EText(this.getVersion());

        this.addtoListofElements(rFooter);

        final EText rProject = new EText("Project Name: "+this.getRProjectString());

        this.addtoListofElements(rProject);

    }
}
