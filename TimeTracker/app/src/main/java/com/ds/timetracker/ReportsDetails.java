package com.ds.timetracker;


import java.io.Serializable;

import ds_fites1_2.IDsGenerator;



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
public class ReportsDetails implements Serializable {
    /**
     * Ho demana el Checkstyle, però no he mirat per a què deu servir.
     */
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private final String path;
    private final String type;

    public ReportsDetails(String name, String path, String type) {
        id = IDsGenerator.Instance(0).getId();
        this.name = name;
        this.path = path;
        this.type = type;
    }

    // Getters


    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPath() {
        return this.path;
    }

    public String getType() {
        return this.type;
    }
}
