package ds_fites1_2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// This class implements the Composite pattern
// Its paper in the pattern is "leaf"
public class Task extends Group {

    private final List<Interval> tIntervals;
    private Interval tLastInterval;
    private static final Logger logger = LoggerFactory.getLogger(Task.class);


    private boolean invariant() {

        if (getGName().isEmpty()) {
            return false;
        }
        if (getGDescription().isEmpty()) {
            return false;
        }
        return getGDuration() >= 0;
    }

    public Task(String name, String desc) {
        super(name, desc);
        tIntervals = new ArrayList<Interval>();
        tLastInterval = null;
        setisRunning(false);
        assert this.invariant();
    }

    // This method inicializes a task and creates a new interval
    public final void start() {

        if(!this.isRunning()) {
            assert this.invariant();

            final int size = this.tIntervals.size();
            final Interval i = new Interval(this);
            i.setiIsRunning(true);
            // We just add the interval created
            // into the list of listeners (Observer pattern)
            Clock.instance(0).addPropertyChangeListener(i);
            tIntervals.add(i);
            tLastInterval = i;
            propagateRunning(true);

            assert (tLastInterval != null && this.tIntervals.size() == size + 1);
            assert this.invariant();

            Task.logger.info("Task <" + getGName()
                    + "> has been started succesfully.");
        }
    }

    // This method stops a task
    public final void stop() {
        if(this.isRunning()) {
            assert this.invariant();
            assert (this.tLastInterval != null);

            // We remove the interval from the list of listeners (Observer pattern)
            Clock.instance(0).removePropertyChangeListener(this.tLastInterval);
            this.tLastInterval.setiIsRunning(false);
            propagateRunning(false);
            assert this.invariant();
            Task.logger.info("Task <" + getGName()
                    + "> has been stopped succesfully.");
        }
    }

    public final List<Interval> getTIntervals() {
        return tIntervals;
    }

    public final List<Interval> getTIntervals(Period p) {
        assert this.invariant();
        assert (p.getPStartDate() != null && p.getPEndDate() != null);

        final List<Interval> lIntervals = new ArrayList<Interval>();

        final Date startReport = p.getPStartDate();
        final Date endReport = p.getPEndDate();

        for (final Interval t1 : tIntervals) {

            if (t1.comparePeriod(startReport, endReport)) {

                lIntervals.add(t1);
            }
        }

        assert this.invariant();
        return lIntervals;
    }

    public final void accept(Visitor v) {
        assert this.invariant();
        assert (v != null);
        v.visitTask(this);
        assert this.invariant();
    }

    public final int lookForInterval(Interval i) {
        assert (i != null);
        return this.tIntervals.indexOf(i) + 1;
    }


    public final int countIntervals(){
        return tIntervals.size();
    }


    public void add(final Interval i){
        tIntervals.add(i);
    }
}
