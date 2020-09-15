package ds_fites1_2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// This class implements the Composite pattern
// Its paper in the pattern is "Composite"
public class Project extends Group {

    private final List<Group> pGroupList;
    private static final Logger logger = LoggerFactory.getLogger(Project.class);


    public Project(String name, String desc) {

        super(name, desc);
        pGroupList = new ArrayList<Group>();
        setisRunning(false);
    }

    public final void addGroup(Group g) {

        pGroupList.add(g);
        g.setGFather(this);
        Project.logger.info("Task or subProject <"
                + g.getGName() + "> has been linked succesfully to project <"
                + getGName() + ">.");

    }

    public final List<Group> getPGroupList() {
        return pGroupList;
    }

    public final void accept(Visitor v) {
        v.visitProject(this);
    }

    public final List<Project> getSubProjects(
            Period p, boolean isRecursive) {

        final List<Project> lSubProjects = new ArrayList<Project>();

        final Date startReport = p.getPStartDate();
        final Date endReport = p.getPEndDate();

        for (final Group g1 : pGroupList) {

            if ((g1 instanceof Project)
                    && (g1.comparePeriod(startReport, endReport))) {

                lSubProjects.add((Project) g1);
                    if (isRecursive) {
                        lSubProjects.addAll(
                                ((Project) g1).getSubProjects(p, isRecursive));
                    }
            }

        }


        return lSubProjects;
    }

    public final List<Task> getTasks(Period p) {

        final List<Task> lTasks = new ArrayList<Task>();

        final Date startReport = p.getPStartDate();
        final Date endReport = p.getPEndDate();

        for (final Group g1 : pGroupList) {

            if ((g1 instanceof Task)
                    && (g1.comparePeriod(startReport, endReport))) {

                lTasks.add((Task) g1);
            }
        }
        return lTasks;
    }



    public final int countTasks(){
        int count = 0;

        for (final Group g : getPGroupList()){
            if(g instanceof Task){
                count++;
            }
        }

        return count;
    }

    public final int countSubProjects(){
        int count = 0;

        for (final Group g : getPGroupList()){
            if(g instanceof Project){
                count++;
            }
        }

        return count;
    }


}
