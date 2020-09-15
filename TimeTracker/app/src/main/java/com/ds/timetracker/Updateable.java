package com.ds.timetracker;


/**
 * Interface que implementen aquelles classes que necessiten actualitzar
 * periòdicament el seu contingut mitjançant el mecanisme que proporciona un
 * objecte de classe {@link Updater} derivada de {@link java.util.logging.Handler}.
 * <p>
 * El que fa aquesta interface és exigir que les classes que la implementen
 * tinguin un mètode <code>actualitza</code>.
 *
 * @author joans
 * @version 24 gener 2012
 */
public interface Updateable {

    /**
     * Conté el codi que actualitza el contingut de la interfase d'usuari que es
     * es mostra per part de la Activity que implementa aquesta interface.
     */
    void update();
}
