package pl.edu.agh.model;

import com.sun.tools.javac.util.Pair;

import java.util.Date;
import java.util.List;

/**
 * Created by Wiktor on 2017-01-21.
 */
public class AnomalyReport {

    // można zastąpić wewnętrzną reprezentacją, żeby nie przekazywać setki razy id sensora dla zapytania o konkretny sensor?
    private List<SensorEntry> entries;
    private List<Date> anomalies;
    private List<Pair<Date, Date>> anomaliesDates;

    public List<Date> getAnomalies() {
        return anomalies;
    }

    public void setAnomalies(List<Date> anomalies) {
        this.anomalies = anomalies;
    }

    public List<SensorEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<SensorEntry> entries) {
        this.entries = entries;
    }

    public List<Pair<Date, Date>> getAnomaliesDates() {
        return anomaliesDates;
    }

    public void setAnomaliesDates(List<Pair<Date, Date>> anomaliesDates) {
        this.anomaliesDates = anomaliesDates;
    }
}
