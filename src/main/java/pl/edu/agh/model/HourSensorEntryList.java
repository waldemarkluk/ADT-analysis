package pl.edu.agh.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Piotr Konsek on 1/22/17.
 */
public class HourSensorEntryList {
    private Date date;
    private List<SensorEntry> sensorEntryList;

    public HourSensorEntryList(Date date, List<SensorEntry> sensorEntryList) {
        this.date = date;
        this.sensorEntryList = sensorEntryList;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<SensorEntry> getSensorEntryList() {
        return sensorEntryList;
    }

    public void setSensorEntryList(List<SensorEntry> sensorEntryList) {
        this.sensorEntryList = sensorEntryList;
    }
}
