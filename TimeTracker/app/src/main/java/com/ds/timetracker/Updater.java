package com.ds.timetracker;

import android.os.Handler;
import android.util.Log;


/**
 * Periòdicament invoca el mètode <code>actualitza</code> d'un objecte que es
 * subscriu a un objecte d'aquesta classe. Té un propòsit semblant doncs al que
 * es podria aconseguir amb <code>Timer</code> combinat amb
 * <code>TimerTask</code>, però que no són compatibles amb l'Android.
 * <p>
 * El mecanisme per aconseguir-ho està molt ben explicat a l'article
 * <em>Updating the UI from a Timer</em> que es troba a la documentació oficial
 * d'Android Resources -> Articles,
 * <p>
 * {@link http://developer.android.com/resources/articles/timed-ui-updates.html}
 * <p>
 * Una extensió al respecte és la possibilitat de parar i engegar la crida
 * periòdica al mètode <code>actualitza</code>.
 * <p>
 * El fem servir per dues coses:
 * <ul>
 * <li>per comptar el temps a la classe <code>Clock</code></li>
 * <li>per actualitzar la interfase d'usuari a les classes
 * <code>LlistaActivitatsActivity</code> i
 * <code>LlistaIntervalsActivity</code></li>
 * </ul>
 *
 * @author joans
 * @version 6 febrer 2012
 */
public class Updater extends Handler {

    /**
     * Nom de la classe per fer aparèixer als missatges de logging del LogCat.
     *
     * @see Log
     */
    private final String tag = getClass().getSimpleName();

    /**
     * Període d'actualització, en milisegons.
     */
    private final long delayMillis;

    /**
     * Només quan és <code>true</code> funciona el mecanisme d'actualització, és
     * a dir, s'invoca el mètode <code>actualitza</code> de l'objecte
     * <code>actualitzable</code>. Els métodes <code>engega</code> i
     * <code>para</code> alternen el seu valor.
     */
    private boolean isWorking;

    /**
     * Consultar l'article <em>Updating the UI from a Timer</em> referenciat a
     * {@link Updater}.
     */
    private final Handler mHandler = new Handler();

    /**
     * Objecte del que invocarem periòdicament el mètode <code>actualitza</code>
     * . La seva classe ha d'implementar la interfase {@link Updateable}.
     */
    private final Updateable updateable;

    /**
     * Serveix per identificar l'objecte actualitzat en els missatges de
     * logging.
     */
    private final String owner;

    /**
     * Crea l'objecte actualitzador però encara no el posa en marxa.
     *
     * @param ac
     *            objecte a actualitzar periòdicament
     * @param d
     *            període en milisegons
     * @param str
     *            identificador de l'objecte actualitzat pels missatges de
     *            logging
     */
    public Updater(Updateable ac, long d,
                   String str) {
        isWorking = false;
        delayMillis = d;
        updateable = ac;
        owner = str;
    }

    /**
     * Inicia el cicle d'actualitzacions al cap de {@link #delayMillis}
     * milisegons i amb aquest període.
     */
    public final void start() {
        if (!this.isWorking) { // si no estava ja engegat
            this.isWorking = true;
            this.mHandler.postDelayed(this.mUpdateTimeTask, this.delayMillis);
            // així comença el cicle d'enviar i gestionar callbacks
            Log.d(this.tag, "handler de " + this.owner + " engegat");
        } else {
            Log.d(this.tag, "engegat handler de " + this.owner
                    + " que ja ho estava");
        }
    }

    /**
     * Consultar l'article <em>Updating the UI from a Timer</em> referenciat a
     * {@link Updater}.
     */
     private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            Log.d(Updater.this.tag, "entro a run() d'actualitzador de " + Updater.this.owner);
            // continuem el cicle
            if (Updater.this.isWorking) {
                Log.d(Updater.this.tag, "i actualitzo");
                Updater.this.mHandler.removeCallbacks(Updater.this.mUpdateTimeTask);
                Updater.this.updateable.update();
                Updater.this.mHandler.postDelayed(Updater.this.mUpdateTimeTask, Updater.this.delayMillis);
            } else {
                Log.d(Updater.this.tag, "pero no faig res");
            }
        }
    };

    /**
      * Momentàniament atura el cicle d'actualitzacions, fins a una nova
      * invocació de {@link #start()}.
      */
    public final void stop() {
        if (this.isWorking) { // si ja estava engegat
            this.isWorking = false;
            this.mHandler.removeCallbacks(this.mUpdateTimeTask);
            Log.d(this.tag, "handler pausat");
        } else {
            Log.d(this.tag, "pausat el handler que ja ho estava");
        }
    }

}
