package ds_fites1_2;
// import java.io.*;
import android.util.Log;

import com.ds.timetracker.Updateable;
import com.ds.timetracker.Updater;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

/**
 * Actualitzacio Setembre 2016 :
 *
 * He provat d'utilitzar un rellotge "normal i corrent", el que fem per la fita 1 i
 * que esta basat o conte un Thread de Java, ja sigui en la classe Timer, TimerTask,
 * o en la versio més simple, que es tenir un rellotge sense practicament funcionalitat
 * i alhora una classe GeneradorTicks que es la que realment es un Thread i va
 * actualitzant el rellotge cada segon.
 *
 * Amb aquest tipus de rellotge la aplicacio Android segueix funcionant en l'emulador
 * de l'Android Studio.
 *
 * No obstant, prefereixo la implementacio original perque evitar que el Thread de
 * GeneradorTicks estigui viu sempre. Amb la implementacio original podem parar
 * el mecanisme d'actualitzacio (de fet ho fem). No obstant, deixo incloses al
 * projecte el codi de les classes Rellotge i GeneradorTicks per si cas, nomes
 * que excloses del path de compilacio (arxiu build.gradle).
 *
 */
public final class Clock implements Updateable {

    private final Updater updateClock;
	private final PropertyChangeSupport support;
	private final int delayMillis;
	private final int delaySeg;
	private static Clock theInstance;
	public static final int SEG2MILSEG = 1000;

	private Clock(int delay) {
		this.setHora(new Date());
		delayMillis = delay * Clock.SEG2MILSEG;
		delaySeg = delay;
		updateClock = new Updater(this, delayMillis, "clock");
		support = new PropertyChangeSupport(this);

	}
	public static Clock instance(int delay) {
		if (Clock.theInstance == null) {
			Clock.theInstance = new Clock(delay);
		}
		return Clock.theInstance;
	}

	private Date time;

	public Date getTime() {
		return this.time;
	}
	public int getDelaySeg() { return this.delaySeg; }

	public void setHora(Date date) {
		time = date;
	}

	public void addPropertyChangeListener(
			PropertyChangeListener pcl) {
		this.support.addPropertyChangeListener(pcl);
		Log.i("Clock", "addPropertyChangeListener: DONE");
	}

	public void removePropertyChangeListener(
			PropertyChangeListener pcl) {
		this.support.removePropertyChangeListener(pcl);
		Log.i("Clock", "removePropertyChangeListener: DONE");

	}
	private void tick() {
		final Date old = this.getTime();
		this.setHora(new Date());
		support.firePropertyChange(
				new PropertyChangeEvent(this,
						"date", old, this.time));
	}

    @Override
    public void update() {
		this.tick();
    }
    public void start() {
		this.updateClock.start();
    }
    public void stop() {
		this.updateClock.stop();
    }

}
