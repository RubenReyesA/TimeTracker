package com.ds.timetracker;


import java.io.Serializable;
import java.util.Date;

import ds_fites1_2.Interval;


/**
 * Conté les dades d'un interval que poden ser mostrades per la interfase
 * d'usuari. <code>GestorArbreActivitats</code> en fa una llista dels de la
 * tasca pare actual i l'envia a la Activity
 * <code>LlistaIntervalsActivity</code> per que la mostri.
 * <p>
 * Aquesta classe simplifica el passar les dades d'interval a la Activity
 * corresponent que les visualitza. Veure {@link GroupDetails} per saber
 * perquè.
 *
 * @author joans
 * @version 6 febrer 2012
 */
public class IntervalDetails implements Serializable {

    /**
     * Necessari segons checkstyle.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Data inicial d'un interval.
     * @see Interval
     */
    private final Date startDate;
    private final String startDateString;

    /**
     * Data final d'un interval.
     * @see Interval
     */

    private final Date endDate;
    private final String endDateString;

    /**
     * Durada d'un interval.
     * @see Interval
     */

    private final int timeSeg;
    private final String timeSegString;

    private final int id;

    private final boolean isRunning;

    public IntervalDetails(Interval inter) {
        this.startDate = inter.getIStartDate();
        this.startDateString = inter.getIStartDateString();

        this.endDate = inter.getIEndDate();
        this.endDateString = inter.getIEndDateString();

        this.timeSeg = inter.getIDuration();
        this.timeSegString = inter.getIDurationString();

        this.id =inter.getiID();

        this.isRunning =inter.isRunning();
    }

    // Getters

    public final String getStartDateString() {
        return this.startDateString;
    }

    public final String getEndDateString() {
        return this.endDateString;
    }

    public final String getTimeSegString() {
        return this.timeSegString;
    }

    public final int getId(){return this.id;}

    public final boolean isRunning(){return this.isRunning;}

}
