package ds_fites1_2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Period {

    private final Date pStartDate;
    private final Date pEndDate;
    private final Date pCurrentDate;
    static final String PATTERN = "d/MM/yyyy, HH:mm:ss";
    static final Locale LOCATION = new Locale("es", "ES");

    public Period(Date startDate, Date endDate) {
        pStartDate = startDate;
        pEndDate = endDate;
        pCurrentDate = new Date();
    }

    public final Date getPStartDate() {
        return pStartDate;
    }

    public final Date getPEndDate() {
        return pEndDate;
    }

    public final Date getPCurrentDate() {
        return pCurrentDate;
    }

    public final String getPStartDateString() {
        return new SimpleDateFormat(Period.PATTERN, Period.LOCATION).format(pStartDate);
    }

    public final String getPEndDateString() {
        return new SimpleDateFormat(Period.PATTERN, Period.LOCATION).format(pEndDate);
    }

    public final String getPCurrentDateString() {
        return new SimpleDateFormat(Period.PATTERN, Period.LOCATION).format(
                pCurrentDate);
    }
}
