package com.ds.timetracker;

import java.io.Serializable;

import ds_fites1_2.IDsGenerator;

/**
 * Aquesta classe implementa la configuració de la app, que es desa en Context.MODE_PRIVATE
 * i que després es carrega a l'inici del servei (app).
 */

public class ConfigApp implements Serializable {

    /**
     * Emmagatzema el delay del rellotge i l'últim ID assignat.
     */

    private int delaySeg;
    private int ID_status;

    public ConfigApp() {
        delaySeg = 1;
        ID_status =0;
    }

    // GETTERS I SETTERS //

    public int getDelaySeg() {
        return this.delaySeg;
    }

    public void setDelaySeg(final int delaySeg) {
        this.delaySeg = delaySeg;
    }

    public int getID_status() {
        return this.ID_status;
    }

    public void setID_status(final int ID_status) {
        this.ID_status = ID_status;
    }

    public void setInstanceIDs(){
        IDsGenerator.Instance(this.ID_status);
    }

}
