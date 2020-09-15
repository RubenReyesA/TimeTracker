package com.ds.timetracker;


import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import ds_fites1_2.Group;
import ds_fites1_2.Project;
import ds_fites1_2.Task;


/**
 * Conté les dades d'una activitat (projecte o tasca) que poden ser mostrades
 * per la interfase d'usuari. <code>GestorArbreActivitats</code> en fa una
 * llista amb les dades de les activitats filles del projecte actual, i l'envia
 * a la Activity <code>LlistaActivitatsActivity</code> per que la mostri.
 * <p>
 * Com que és una classe sense funcionalitat, només és una estructura de dades,
 * així que faig els seus atributs públics per simplificar el codi.
 * <p>
 * Aquesta classe simplifica el passar les dades de projectes i tasques a la
 * Activity corresponent que les visualitza. Si passéssim directament la llista
 * d'activitats filles, com que es fa per serialització, s'acaba enviat tot
 * l'arbre, ja que els fills referencien als pares. El problema es tal, que amb
 * un arbre mitjà es perd tota la "responsiveness".
 *
 * @author joans
 * @version 6 febrer 2012
 */
public class GroupDetails implements Serializable {
    /**
     * Ho demana el Checkstyle, però no he mirat per a què deu servir.
     */
    private static final long serialVersionUID = 1L;

    // getters.

    private final Date startDate;
    private final String startDateString;

    private final Date endDate;
    private final String endDateString;

    private final int timeSeg;
    private final String timeSegString;

    private final String name;

    private final String description;

    private final int id;

    private final boolean isProject;

    private final boolean isTask;

    private final boolean isRunning;


    private final int nTasks;
    private final int nSubProjects;
    private final int nIntervals;

    private final String root;

    private final String route;

    /**
     * Extreu les dades de la activitat passada per paràmetre i les copia als
     * atributs propis.
     *
     * @param group
     *            Tasca o projecte.
     */
    public GroupDetails(Group group) {


        this.startDate = group.getGStartDate();
        this.startDateString = group.getGStartDateString();
        this.endDate = group.getGEndDate();
        this.endDateString = group.getGEndDateString();

        this.timeSeg = group.getGDuration();
        this.timeSegString = group.getGDurationString();
        this.name = group.getGName();
        this.description = group.getGDescription();
        this.id =group.getgID();

        this.root =group.getGFatherName();

        this.isRunning = group.isRunning();

        this.route = group.calculateRoute();

        if (group.getClass().getName().endsWith("Project")) {
            this.isProject = true;
            this.isTask = false;
            this.nIntervals =0;
            this.nTasks =((Project) group).countTasks();
            this.nSubProjects =((Project) group).countSubProjects();
        } else {
            this.isProject = false;
            this.isTask = true;
            this.nIntervals =((Task) group).countIntervals();
            this.nTasks =0;
            this.nSubProjects =0;
        }
    }

    /**
     * Converteix una part de les dades d'un objecte DadesActivitat a un String,
     * que serà el que es mostrarà a la interfase d'usuari, ja que els
     * <code>ListView</code> mostren el que retorna aquest mètode per a cada un
     * dels seus elements. Veure
     * {@link ActivitiesList.Receptor#onReceive}.
     *
     * @return nom i durada de la activitat, en format hores, minuts i segons.
     */


    // Getters

    public final String getRoute() {
        return this.route;
    }

    public final String getStartDateString() {
        return this.startDateString;
    }

    public final String getEndDateString() {
        return this.endDateString;
    }

    public final String getTimeSegString() {
        return this.timeSegString;
    }

    public final String getName() {
        return this.name;
    }

    public final String getDescription() {
        return this.description;
    }

    public final int getId(){return this.id;}

    public final boolean isProject() {
        return this.isProject;
    }

    public final boolean isTask() {
        return this.isTask;
    }

    public final boolean isRunning() {
        return this.isRunning;
    }

    public final String getNTasks(){ return Integer.toString(this.nTasks);}
    public final String getNSubProjects(){ return Integer.toString(this.nSubProjects);}
    public final String getNIntervals(){ return Integer.toString(this.nIntervals);}

    public final String getRoot(){ return this.root;}



    public static Comparator<GroupDetails> AlphaComparator = (o1, o2) -> o1.getName().compareTo(o2.getName());

    public static Comparator<GroupDetails> RecentComparator = (o1, o2) -> o1.getId() - o2.getId();

    public static Comparator<GroupDetails> TypeComparator = (o1, o2) -> {
        final String o1s;
        final String o2s;
        if(o1.isProject()){
            o1s="true";
        }
        else{
            o1s="false";
        }

        if(o2.isProject()){
            o2s="true";
        }
        else{
            o2s="false";
        }

        return o1s.compareTo(o2s);
    };
}
